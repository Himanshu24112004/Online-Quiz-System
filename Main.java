import java.io.*;
import java.util.*;

/**
 * Online Quiz System (console)
 * - Questions are loaded from quiz.txt into a Queue and asked one by one.
 * - Answered questions are pushed on a Stack so they can be reviewed (latest first).
 * - Scores are stored in scores.txt and shown on a leaderboard sorted by score.
 *
 * Concepts: Queue, Stack, Array, Sorting, Methods, File Handling
 */
public class Main {

    static final String QUIZ_FILE = "quiz.txt";
    static final String SCORE_FILE = "scores.txt";

    // ---------- Data classes ----------
    static class Question {
        int number;
        String text;
        String[] options = new String[4];
        char answer;
    }

    static class Attempt {
        Question question;
        char given;
    }

    static class ScoreEntry {
        String name;
        int score;
        int total;

        ScoreEntry(String name, int score, int total) {
            this.name = name;
            this.score = score;
            this.total = total;
        }
    }

    // ---------- Data structures ----------
    static Queue<Question> questionBank = new LinkedList<>();   // Queue of questions
    static Deque<Attempt> answeredStack = new ArrayDeque<>();   // Stack (push/pop at head)
    static List<ScoreEntry> scores = new ArrayList<>();

    static Scanner in = new Scanner(System.in);

    // ---------- Input helper ----------
    static String readLine() throws EOFException {
        if (!in.hasNextLine()) {
            throw new EOFException();
        }
        return in.nextLine().trim();
    }

    // ---------- File handling ----------
    // quiz.txt line format: question|optionA|optionB|optionC|optionD|answerLetter
    static void loadQuestions() {
        questionBank.clear();
        File f = new File(QUIZ_FILE);
        if (!f.exists()) {
            System.out.println("Warning: " + QUIZ_FILE + " not found. No questions loaded.");
            return;
        }
        try (Scanner fileScanner = new Scanner(f)) {
            int number = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] p = line.split("\\|");
                if (p.length != 6) {
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }
                Question q = new Question();
                q.number = ++number;
                q.text = p[0].trim();
                for (int i = 0; i < 4; i++) {
                    q.options[i] = p[i + 1].trim();
                }
                q.answer = Character.toUpperCase(p[5].trim().charAt(0));
                questionBank.add(q);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading " + QUIZ_FILE + ": " + e.getMessage());
        }
    }

    // scores.txt line format: name,score,total
    static void loadScores() {
        scores.clear();
        File f = new File(SCORE_FILE);
        if (!f.exists()) {
            return;
        }
        try (Scanner fileScanner = new Scanner(f)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] p = line.split(",");
                if (p.length != 3) {
                    continue;
                }
                try {
                    scores.add(new ScoreEntry(p[0].trim(),
                            Integer.parseInt(p[1].trim()),
                            Integer.parseInt(p[2].trim())));
                } catch (NumberFormatException ignored) {
                    // skip bad line
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading " + SCORE_FILE + ": " + e.getMessage());
        }
    }

    static void saveScores() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SCORE_FILE))) {
            for (ScoreEntry s : scores) {
                pw.println(s.name + "," + s.score + "," + s.total);
            }
        } catch (IOException e) {
            System.out.println("Error saving " + SCORE_FILE + ": " + e.getMessage());
        }
    }

    // ---------- Features ----------
    static boolean isValidAnswer(String s) {
        return s.length() == 1 && "ABCD".indexOf(Character.toUpperCase(s.charAt(0))) >= 0;
    }

    static void startQuiz() throws EOFException {
        if (questionBank.isEmpty()) {
            System.out.println("No questions available.");
            return;
        }
        System.out.print("Enter player name: ");
        String name = readLine();
        if (name.isEmpty()) {
            name = "Player";
        }
        name = name.replace(",", " ");

        answeredStack.clear();
        Queue<Question> queue = new LinkedList<>(questionBank); // fresh copy of the queue
        int total = queue.size();
        int score = 0;

        while (!queue.isEmpty()) {
            Question q = queue.poll();                           // dequeue next question
            char given = askQuestion(q);

            Attempt a = new Attempt();
            a.question = q;
            a.given = given;
            answeredStack.push(a);                               // push on stack

            if (given == q.answer) {
                System.out.println("Correct!");
                score++;
            } else {
                System.out.println("Wrong! Correct answer: " + q.answer);
            }
        }

        System.out.println("Quiz over. " + name + " scored " + score + "/" + total + ".");
        scores.add(new ScoreEntry(name, score, total));
    }

    // Keeps asking the same question until a valid answer (A-D) is given.
    static char askQuestion(Question q) throws EOFException {
        System.out.println();
        System.out.println("Q" + q.number + ". " + q.text);
        System.out.println("  A) " + q.options[0]);
        System.out.println("  B) " + q.options[1]);
        System.out.println("  C) " + q.options[2]);
        System.out.println("  D) " + q.options[3]);
        while (true) {
            System.out.print("Your answer: ");
            String s = readLine();
            if (isValidAnswer(s)) {
                return Character.toUpperCase(s.charAt(0));
            }
            System.out.println("Invalid option. Enter A, B, C or D.");
            // invalid answer is NOT counted; the question is asked again
        }
    }

    static void reviewAnswers() {
        if (answeredStack.isEmpty()) {
            System.out.println("No answers to review yet. Start a quiz first.");
            return;
        }
        System.out.println("--- Review (latest first) ---");
        // ArrayDeque iterator walks from the top of the stack (latest pushed) down.
        for (Attempt a : answeredStack) {
            System.out.println("Q" + a.question.number
                    + " | Your answer: " + a.given
                    + " | Correct: " + a.question.answer);
        }
    }

    // Bubble sort on an array, highest score first.
    static ScoreEntry[] sortByScore(List<ScoreEntry> list) {
        ScoreEntry[] arr = list.toArray(new ScoreEntry[0]);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j].score < arr[j + 1].score) {
                    ScoreEntry tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
        return arr;
    }

    static void showLeaderboard() {
        if (scores.isEmpty()) {
            System.out.println("Leaderboard is empty.");
            return;
        }
        System.out.println("--- Leaderboard ---");
        ScoreEntry[] sorted = sortByScore(scores);
        for (int i = 0; i < sorted.length; i++) {
            System.out.println((i + 1) + ". " + sorted[i].name + " - "
                    + sorted[i].score + "/" + sorted[i].total);
        }
    }

    static void printMenu() {
        System.out.println();
        System.out.println("===== ONLINE QUIZ SYSTEM =====");
        System.out.println("1. Start quiz");
        System.out.println("2. Review answers");
        System.out.println("3. Leaderboard");
        System.out.println("4. Exit");
        System.out.print("Choose option: ");
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        loadQuestions();   // file -> queue
        loadScores();      // file -> list

        try {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = readLine();
                switch (choice) {
                    case "1":
                        startQuiz();
                        break;
                    case "2":
                        reviewAnswers();
                        break;
                    case "3":
                        showLeaderboard();
                        break;
                    case "4":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid menu option. Choose 1-4.");
                }
            }
        } catch (EOFException e) {
            System.out.println();   // input ended
        }

        saveScores();      // save on exit
        in.close();
        System.out.println("Scores saved. Goodbye!");
    }
}