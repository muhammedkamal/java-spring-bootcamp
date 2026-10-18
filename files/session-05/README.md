# Week 3, Session 3A: Inheritance, Polymorphism and Interfaces

Slides: `../Session05_Inheritance_Polymorphism_and_Interfaces.pptx`

## Run a file

```bash
cd files/session-05
java demos/Inheritance.java
```

Each demo file has its main class first, then the helper classes. `java File.java` runs the first class.

## Demos

| File | Topic |
| --- | --- |
| `Inheritance.java` | `extends`, `super(...)`, `protected`, what a subclass adds |
| `SuperAndOverride.java` | `@Override`, `super.method()`, overriding `toString` |
| `ConstructorChain.java` | Predict the output: constructors run parent first |
| `Polymorphism.java` | Array of a parent type, dynamic dispatch, `instanceof` pattern, casting |
| `AbstractClasses.java` | `abstract` class and methods, `Shape` with `Circle` and `Rectangle` |
| `Interfaces.java` | `interface`, `implements`, several interfaces, `default` methods |
| `EnumDemo.java` | `enum` with fields, `switch` on an enum, `values()`, `valueOf()` |
| `Composition.java` | HAS-A vs IS-A, handing a part in through the constructor |
| `OpenClosed.java` | SOLID intro: replace an if/else chain with an interface |

## Labs

Starters are in `lab/starter/`. Each starter has the expected output in its header comment.

| Lab | Task |
| --- | --- |
| 1 | `Shape` hierarchy: `Circle`, `Rectangle`, `Square`, `Triangle`, largest area |
| 2 | `PaymentMethod` interface with `CardPayment` and `WalletPayment`, checkout with fallback |
| 3 | Payroll: abstract `Employee`, salaried, hourly with overtime, `Manager`, `Department` enum |
| 4 | Library System domain model: `Book`, `Member`, `Loan`, `BookStatus`, `MemberType` (individual project starts) |

Reference answers are in `lab/solutions/`. Try first, then compare.

## Before the next session

1. Finish Labs 1 to 3. Commit each one.
2. Lab 4 is the start of your individual project. Create a new repo `library-system`, add the model and a README.
3. Solve on LeetCode: Valid Anagram, Contains Duplicate.
4. For each class in Lab 3, say out loud whether it IS-A or HAS-A the others.
