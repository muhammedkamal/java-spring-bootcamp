# Session 1 code pack: Your First Java Programs

Week 1, Session 1A (Wednesday). Matches the slides `Session01_Your_First_Java_Programs.pptx`.

## Run any file

From this folder, with JDK 25 installed:

```
java demos/Main.java          # compile + run in one step
```

Or the two-step way shown on the "Compile and run" slide:

```
cd demos
javac Main.java
java Main
```

In IntelliJ: copy a file into `src`, open it, and press Run (Shift+F10 / Ctrl+R).

## demos/ (used live in the session)

| File | Slide | Shows |
| --- | --- | --- |
| HelloCompact.java | Hello, World in Java 25 | Compact `void main()` (JDK 25+ only) |
| Main.java | The classic form | `public static void main` |
| Recipe.java | A program is a list of exact instructions | Statements run in order |
| Variables.java | A variable is a labeled box | Type, name, value |
| Types.java | The types you will use every day | int, long, double, boolean, char, String |
| VarAndFinal.java | Naming, var and final | Type inference and constants |
| Operators.java | Operators | Arithmetic, comparison, logical, shortcuts |
| IntegerDivision.java | Watch out: integer division | 5 / 2 = 2 and the average bug |
| Casting.java | Casting | Widening, narrowing, Math.round |
| StringsDemo.java | Strings | Methods and left-to-right joining |
| Greeting.java | Reading input with Scanner | nextLine, nextInt |
| ScannerTrap.java | The Scanner newline trap | Leftover Enter after nextInt |
| TipCalculator.java | Live coding | Full small program with printf |
| PredictOutput.java | Predict the output | Quiz answers in comments |

## lab/ (afternoon, 14:00-16:45)

`starter/` has a file per exercise with TODOs. `solutions/` has the reference answers: compare after you finish.

| Lab | Task | Check your answer |
| --- | --- | --- |
| 1 | Personal card in a box of `*` | Box lines are the same width |
| 2 | Celsius to Fahrenheit | 37 → 98.6 |
| 3 | BMI | 70 kg, 1.75 m → 22.86 |
| 4 | Seconds to h/min/s | 3725 → 1 h 2 min 5 s |
| 5 | Swap two variables | a = 8, b = 3 |
| 6 (stretch) | EGP change maker | 387 → 200, 100, 50, 20, 10, 5, 1 x2 |

Intern B: finish all six, then start the Java-for-C#-developers sheet. See [csharp-to-java-map.md](csharp-to-java-map.md).
