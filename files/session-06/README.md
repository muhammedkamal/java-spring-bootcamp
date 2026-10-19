# Week 3, Session 3B: Exceptions, Collections and Generics

Slides: `../Session06_Exceptions_Collections_and_Generics.pptx`

## Run a file

```bash
cd files/session-06
java demos/TryCatch.java
```

## Demos

| File | Topic |
| --- | --- |
| `TryCatch.java` | `try` / `catch` / `finally`, multi-catch, the program keeps going |
| `CheckedUnchecked.java` | Checked vs unchecked, `throw` vs `throws` |
| `TryWithResources.java` | `AutoCloseable`, closing order |
| `CustomExceptions.java` | `InsufficientFundsException` with extra data |
| `ListsDemo.java` | `ArrayList`: add, get, set, remove, `removeIf`, `List.of` |
| `RemoveWhileLooping.java` | Predict the output: `ConcurrentModificationException` and two fixes |
| `SetsAndMaps.java` | `HashSet`, `HashMap`, `getOrDefault`, word count, `TreeMap`, `entrySet` |
| `HashCodeMatters.java` | Why a `HashSet` needs `hashCode` as well as `equals` |
| `Sorting.java` | `Comparable` vs `Comparator`, `Comparator.comparing` preview |
| `GenericsDemo.java` | `Box<T>`, `Pair<K, V>`, generic methods, bounded types |

## Labs

Starters are in `lab/starter/`. Each starter has the expected output in its header comment.

| Lab | Task |
| --- | --- |
| 1 | Safe parsing and dividing: catch, rethrow, which `catch` goes first |
| 2 | `BankAccount` with a checked `InsufficientFundsException` and a safe `transfer` |
| 3 | Word frequency with `HashMap`, top 3 words |
| 4 | Student roster: remove duplicates with a set, sort with a `Comparator`, group by grade |
| 5 | Generic `Pair<A, B>`, `swap`, `minMax`, `SimpleStack<T>` |
| 6 | Library System v1: collections, custom exceptions, menu UI (Week 3 deliverable) |

Reference answers are in `lab/solutions/`. Try first, then compare.

## Before the next session

1. Finish Labs 1 to 5. Commit each one.
2. Lab 6 is the Week 3 deliverable: Library System v1 in your `library-system` repo, with a README, sample output and 5 or more commits. Open a pull request and ask your partner to review it.
3. Solve on LeetCode: Valid Parentheses, Majority Element. Solve 5 HackerRank Java Data Structures problems.
4. Quiz 3 covers inheritance, interfaces, exceptions, collections and generics.
