# Online Quiz System (PS No. 3)

Console quiz application in Java.
Rungta International Skills University, School of Computer Science and Engineering.

## Problem
Questions are loaded from `quiz.txt` into a **Queue** and asked one by one. Each answered question is pushed on a **Stack**, so the player can review answers (latest first). Scores are saved to `scores.txt` and shown on a leaderboard sorted by score (**array + bubble sort**).

## Concepts used
Queue (`LinkedList`), Stack (`ArrayDeque` push/iterate), Array, Sorting, Methods, File Handling.

## Files
| File | Purpose |
|---|---|
| `Main.java` | Whole program (single Main class) |
| `quiz.txt` | Question bank, one per line: `question\|A\|B\|C\|D\|answer` (answer key: Q1-B, Q2-A, Q3-C) |
| `scores.txt` | Saved scores, one per line: `name,score,total` |

## Run
```
javac Main.java
java Main
```
Keep `quiz.txt` and `scores.txt` in the folder you run from.

## Menu
1. Start quiz  2. Review answers (latest first)  3. Leaderboard  4. Exit (saves scores)

## Rules
- Only A, B, C or D is accepted (case-insensitive).
- Any other input prints `Invalid option. Enter A, B, C or D.`, is not counted, and the same question is asked again.
- Scores are loaded at start and saved on exit.

## Test cases (all verified)
| # | Scenario | Result |
|---|---|---|
| 1 | Riya answers B, A, C | `Quiz over. Riya scored 3/3.` |
| 2 | Only Q1 in quiz.txt, Aman answers D | `Wrong! Correct answer: B` / `Quiz over. Aman scored 0/1.` |
| 3 | Answer E | `Invalid option. Enter A, B, C or D.` and Q1 is asked again |
| 4 | Riya has 3/3 saved; Aman answers B, A, D; review; leaderboard | `Aman scored 2/3`; review Q3, Q2, Q1; `1. Riya - 3/3`, `2. Aman - 2/3` |

For test case 4, `scores.txt` should contain `Riya,3,3` before running. For test case 2, keep only the first line in `quiz.txt`.
