# Streams & Lambdas

![streams-lambdas-main](media/streams-lambdas-main.jpg)

**half-life-3-confirmed**

<sup>This tutorial assumes the reader has a good grasp of the Java Programming language features: Interfaces, Anonymous Classes, Collectins API, etc.</sup>

## Introduction

Before jumping into conclusions and start bragging about how Streams and Lambdas are going to suddenly solve all of our developers problems let me start by telling you that you can continue writing excellent Java code without using any of those features. We did that before Java 8, didn't we ?

Also it's important to have in mind that the more "functional" you write your code small performance penalties will be inherent. It's quite contextual, but in most of the cases using a classic `for loop` instead of adding the small overhead of the Streams API will be more efficient.

Using Lambdas and Streams is not about gaining small performance advantages in terms of CPU or memory utilisation (it's the other way around), but about writing code that is more *short*, *readable*, *concise* and easier to debug.

Let's begin by doing some code-work. 

We will write a simple method that takes a `List<Employee>` as input and then groups every employee by his/her department, resulting in a `Map<String, List<Employee>>`. The `key` represents the department, while the `value` is a `List<>` of every employee that works in that deprtament. 

For reference the `Employee` might class looks like:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    public Long id;
    public String name;
    public String department;
    public Long managerId;
    public BigDecimal salary;
}
```

Without using any of the Streams API we can write something like this (`putIfAbsent` method was also introduced in Java 8):

```java
public static Map<String, List<Employee>> groupByDepartments(List<Employee> employees) {
    Map<String, List<Employee>> result = new HashMap<>();
    for(Employee employee : employees) {
        // If it's the first time we encounter the department we initialize the List<Employee>
        result.putIfAbsent(employee.getDepartment(), new LinkedList<>());
        result.get(employee.getDepartment()).add(employee);
    }
    return result;
}
```

This doesn't look too bad, and the code is quite straight-forward. We can live with that. At least we did.

But what if tell you `groupingBy` is a thing that is "built-in" in the Stream API and everything becomes a one-liner (that's not even hackish and with a stretch is as readble as plain English):

```java
public static Map<String, List<Employee>> groupByDepartmentsF(List<Employee> employees) {
    return employees.stream().collect(groupingBy(Employee::getDepartment)); // Employee:getDepartment is a lambda
}
```

Now we want to go even further with our exercise. The new requirement is to write a method that takes a `List<Employee>` and returns a `Map<String, Long>` describing how many employees each departments has. The `key` will represent the Department, while the `value` will represent the number of employees working in that department.

```
{Customer Service=67, Staffing=61, Licenses=58, Financial=65, ...}
```

I will let you write the imperative implementation by yourself as an exercise, but the functional implementation looks as simple as:

```java
public static Map<String, Long> groupAndCountDepartments(List<Employee> employees) {
    return employees.stream()
                    .map(Employee::getDepartment) // Employee:getDepartment is a lambda
                    .collect(groupingBy(identity(), counting()));
}
```

Is the code more concise and readable ? Let's be honest to ourselves, it isn't very readable if this is the first interaction with those "weird concepts and syntax", but after a short initial investment the benefits will become more and more obvious.


## Lambdas


### What is Lambda ?


<sup>Lambda, Λ, λ (uppercase Λ, lowercase λ) is the 11th letter of the Greek alphabet... Also a mandatory concept to understand before jumping into Streams.</sup>

Lambda is also a **concise** representation of an **anonymous** **function** that can be **passed around**.
* concise → no need to write boilerplate code (remember *Anonymous Classes...*);
* anonymous → the lambda doesn’t have a name like methods have;
* function → just like a function it has a body, a return type, and list of parameters;
* passed around → the lambda can be passed as parameter or referenced by a variable.

**Bad News**:
* Lambdas technically don't let you do anything that you couldn't do prior to **Java 8**. In a way lambdas are nothing more than syntactic sugar. 

**Good news**:
* You are no longer required to write long and tedious declarations (remember *Anonymous Classes...*). 

For example in order to sort the a `List<Employees` by their salary we are no longer required to write a `Comparator<Employee>` using an anonymous class:

```java
// Anonymous Class Example
// Don't forget to null check
Comparator<Employee> bySalary = new Comparator<Employee>() {
    @Override
    public int compare(Employee e1, Employee e2) {
        return e1.getSalary().compareTo(e2.getSalary());
    }
};

Collections.sort(employees, bySalary);
```

We can use a lambdas instead:

```java
// Lambda Example
// Don't forget to null check

Comparator<Employee> bySalary = (e1, e2) -> e1.getSalary().compareTo(e2.getSalary());
Collections.sort(employees, bySalary);

//OR

Collections.sort(employees, (e1, e2) -> e1.getSalary().compareTo(e2.getSalary());

// OR

Collections.sort(employees, Comparator.comparing(Employee::getSalary));
```

For creating something as simple as a `Runnable` we will never have to write:

```java
// Anonymous Runnable
Runnable runnable1 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Running!");
    }
};
runnable1.run();
``` 

A simple lambda expression will do:

```java
 // Lambda Runnable
Runnable runnable2 = () -> System.out.println("Running!");
runnable2.run();
```

### The structure of a Lambda 

As you can see in the previous examples the structure of a Lambda is as follows:

`(Param1, Param2, ..., ParamN) -> { /* do something ||&& return something */ }`

Writing lambda expressions involes some few basic rules. Skim through the next examples:

* :white_check_mark: `() -> {}` →

*This lambda is a function with no input parameters and returns void. The equivalent method would look like:* `public void run() {}`.

* :white_check_mark: `() -> “Example1”` →

*This lambda is a function with no input parameters and returns a string: “Example1”. The return statement is implicit (we don't need to write it). The equivalent method:* `public void something() { return “Example1”; }`

* :white_check_mark: `() -> { return “Example1”; } ` → 

*This is the same lambda method as above, but instead of the implicit return statement we are using an explicit one.*

* :x: `() ->  String s = “abc” ; s + s;` →

*If the lambda body is a block of statements, or the lambda has no value - we need to include brackets.*

* :x: `() ->  { String s = “abc” ; s + s; }` →

*This lambda is still invalid because it's a block of statements but it doesn't have a return statement.*

* :white_check_mark: `() ->  { String s = “abc” ; return s + s; }` →

*This is a valid lambda that returns the string:* `"abcabc"`.

* :white_check_mark: `(List<String> list) -> list.isEmpty()`

*This is valid lambda. In most of the cases there's no need to specify the type of the input parameters, as the type is inferred from the context. 

* :white_check_mark: `() -> new Apple(10)`

* :white_check_mark: `(Message msg) -> { System.out.println(msg.getHeader()); }`

* :white_check_mark: `(Integer a, Integer b) -> a * b;`


### `@FunctionalInterface`

**Q** :question: So lambdas are those small anonymous functions! But how and where do we use them ?

 * We just pass them around. Lambdas can be parameters for functions, constructors and they can be kept in variables! |
 
**Q** :question: Oh wait, Java is strongly typed. Is “Lambda” a new type ?

* Well… no. For now, it suffices to understand that a lambda expression can be assigned to a variable or passed to a method expecting a functional interface as argument, provided the lambda expression has the same signature as the abstract method of the **Functional Interface**.

To be more clear, **Functional Interface**s are interfaces that specify exactly one abstract method and can be marked with the `@FunctionalInterface`.

The most obvious examples from the Java API are:

```java
@FunctionalInterface
public interface Comparator<T> {
    // The one and only abstract method from the interface
	int compare(T o1, T o2);
}
...
```

Or:

```java
@FunctionalInterface
public interface Runnable {
    // The one and only abstract method from the interface
	void run();
}
...
```

The `java.utill.function` package is nice enough to define Functional Interfaces for us so we can easily juggle with the lambdas in our code. The most important ones are `Predicate<T>`, `Function<T1, T2>`, `Consumer<T>`, `Supplier<T>` and `BiFunction<T1, T2, T3>`.

Nobody restricts us to define our own `@FunctionalInterface`s as long as we keep in mind that they need to contain only abstract method. 

Creating our own functional interfaces is not uncommon, but because all the interfaces defined in `java.utill.function` are generic we should them re-use them as much as possible.

Example:

```java
// (T) -> return Boolean;
Predicate<String> containsComma = (str) -> str.contains(",");

// (T) -> void
Consumer<String> printUpperCase = (str) -> str.toLowerCase();

// (T1) -> return (T2);
// Magic method to count the vocals
Function<String, Integer> countVocals =
                (str) -> str.replaceAll("[^aeiouAEIOU]","").length();

// (T1, T2) -> return (T3);
// Magic-method to repeat a String. 
BiFunction<String, Integer, String> repeatNTimes=
                (str, times) -> new String(new char[times]).replace("\0", str);
```

If you look closer at the example everything should start making sense now. `containsComma`, `printUpperCase`, `countVocals`, `repeatNTimes` are all variables, but they are no longer used to store data. They "store behavior", "behavior" that can be passed around your code and played with.

If we want to actually call the "behavior"/"functionality" associated with a Lambda method we just need to invoke the single abstract method that the `@FunctionalInterface` defines.

The only abstract method from the `Function` class is called `apply()`:

```java
Function<String, Integer> countVocals = (str) -> str.replaceAll("[^aeiouAEIOU]","").length();
int numVocals = countVocals.apply("ABC");
```

The only abstract method from the `Predicate` class is called `test()`:

```java
Predicate<String> containsComma = (str) -> str.contains(",");
boolean hasComma = containsComma.test("ab,c");
```        

#### A lambda returning a lambda 

To makes thing even more complicated we can have lambdas generating other lambdas by partially initializing them. 

Check the following example:
```java
// Defines a lambda that returns partially initialized lambdas
Function<Integer, Function<Integer, Integer>> createAdder = (adder) -> 
                                                                (number) -> number + adder;

// Creates a function that adds 3 to a number
Function<Integer, Integer> add3 = createAdder.apply(3);
// Creates a function that adds 5 to a number                                                                 
Function<Integer, Integer> add5 = createAdder.apply(5);

// Use the methods
System.out.println(add3.apply(1)); // result: 4
System.out.prinltn(add5.apply(3)); // result: 8
```

#### Writing our own forEach method

The purpose of this exercise is to write our own `forEach` method:
* Traverse the `Iterable` collection;
* Check if the element passes a certain condition (think `Predicate`);
* Consums each element of the collection (think `Consumer`).

```java
public static void forEach(Iterable<String> iterable, Predicate<String> predicate, Consumer<String> consumer) {
    for(String s : iterable)
        if (predicate.test(s)) // The condition is passed down (input parameter)
            consumer.accept(s); // The behavior is passed down (input parameter)
}
```

```java
 List<String> list = new ArrayList<>(Arrays.asList("abc", "det", "delo", "itte"));

// Prints "det", "delo"
forEach(list, (s) -> s.startsWith("d"), System.out::println);

// Prints "abc", "det", "Delo", "itte"
Predicate<String> nonEmpty = (String s) -> s != null && s.length()!=0;
Consumer<String> printItToConsole = System.out::println;
forEach(list, nonEmpty, printItToConsole);
```


