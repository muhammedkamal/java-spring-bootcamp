# Week 4, Session 4A: Modern Java, Maven and JUnit

Slides: `../Session07_Modern_Java_Maven_and_JUnit.pptx`

## Run a file

```bash
cd files/session-07
java demos/Lambdas.java
```

## Demos

| File | Topic |
| --- | --- |
| `Lambdas.java` | Anonymous class vs lambda, `forEach`, `removeIf`, effectively final |
| `FunctionalInterfaces.java` | `Predicate`, `Function`, `Supplier`, `Consumer`, your own `@FunctionalInterface` |
| `MethodReferences.java` | The four shapes: static, object, any object of a type, constructor |
| `StreamsBasics.java` | `filter`, `map`, `sorted`, `distinct`, laziness, a stream can be used once |
| `StreamsCollect.java` | `joining`, `groupingBy`, `counting`, `partitioningBy`, `toMap` |
| `OptionalDemo.java` | `Optional` as a return type: `map`, `orElse`, `orElseThrow` |
| `Records.java` | Record, compact constructor, equals and hashCode for free |
| `PatternMatching.java` | `instanceof` patterns, `switch` patterns, `sealed`, text blocks |
| `maven-demo/` | A Maven project with a JUnit 5 test class |

## Maven and JUnit

`maven-demo/` and the Library v2 projects need Maven (or the Maven panel in IntelliJ). From the project folder:

```bash
mvn test
```

Use `./mvnw test` when the project has the wrapper. Open the folder in IntelliJ and it imports `pom.xml` by itself.

## Labs

| Lab | Task |
| --- | --- |
| 1 | Streams warm-up: replace loops with pipelines (single file) |
| 2 | Order report with records and streams: total per customer, top products (single file) |
| 3 | Library v2: Maven project with records, `Optional`, streams and 15 or more JUnit tests |

Starters are in `lab/starter/`, reference answers in `lab/solutions/`. Labs 1 and 2 run with `java LabXxx.java` and print the expected output in the header comment. Lab 3 lives in `lab/starter/library-v2/`: `pom.xml` and the records are ready, the `Library` methods are `TODO`, and `LibraryTest` has 4 tests. Write at least 11 more.

## Before the next session

1. Finish Labs 1 to 3. Open a pull request for Library v2 and ask your partner to review it.
2. Make sure Docker Desktop runs (`docker ps`). Session 8 starts PostgreSQL with Docker.
3. Solve on LeetCode: Group Anagrams, Top K Frequent Elements. Solve 5 problems from LeetCode SQL 50.
