# Streams & Lambdas

![streams-lambdas-main](media/streams-lambdas-main.jpg)

[#half-life-3-confirmed](https://kotaku.com/tag/half-life-3)

## Introduction

Before jumping into conclusions and start bragging about how Streams and Lambdas are going to suddenly solve all our problems let's start by ... doing some code-work. 

We will write a simple method that takes a `List<Employee>` as input and then groups every employee by his/her department, resulting in a `Map<String, List<Employee>>`. 

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

Normally in the Java versions prior to Java 8 we would do something like:

```java
public static Map<String, List<Employee>> groupByDepartments(List<Employee> employees) {
    Map<String, List<Employee>> result = new HashMap<>();
    for(Employee employee : employees) {
        result.putIfAbsent(employee.getDepartment(), new LinkedList<>());
        result.get(employee.getDepartment()).add(employee);
    }
    return result;
}
```

This doesn't look too bad, and the code is quite straight-forward. We can live with that. At least we did.

But what if tell you `groupingBy` is a thing that is "built-in" in the Stream API and everything becomes:

```java
public static Map<String, List<Employee>> groupByDepartmentsF(List<Employee> employees) {
    return employees.stream()
                    .collect(groupingBy(Employee::getDepartment)); // Employee:getDepartment is a lambda
}
```

Now we want to go even further with our exercise. The new requirement is to write a method that takes a `List<Employee>` and returns a `Map<String, Long>` describing how many employees each departments has.

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

Lambda expressions can be considered an elegant way of "storing" and referencing behavior, while Streams API comes with a with functionality similar to SQL but much more powerful when it comes to using/re-using the behavior we are encapsulating in lambda expressions. 

## Lambdas

### What is Lambda ?

<sup>Lambda, Λ, λ (uppercase Λ, lowercase λ) is the 11th letter of the Greek alphabet... Also a mandatory concept to understand before jumping into Streams.</sup>

Lambda is also a **concise** representation of an **anonymous** **function** that can be **passed around**.
* concise → no need to write boilerplate code (remember *Anonymous Classes...*);
* anonymous → the lambda doesn’t have a name;
* function → just like a function it has a body, a return type, and list of parameters;
* passed around → the lambda can be passed as parameter or referenced by a variable.

**Bad News**:
* Lambdas technically don't let you do anything that you couldn't do prior to Java 8. 

**Good news**:
* You are no longer required to write long and tedious declarations (remember *Anonymous Classes...*). 

For example in order to sort the employees by their salary we are no longer to write a `Comparator<Employee>` using an anonymous class:

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

We can use a lambda instead:

```java
// Lambda Example
// Don't forget to null check
Comparator<Employee> bySalary = (e1, e2) -> e1.getSalary().compareTo(e2.getSalary());
Collections.sort(employees, bySalary);
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

`(Param1, Param2, Param2) -> {}`

| Lambda Expression | Is Valid ? | Comments |
| ----------------- | ---------- | -------- |
| `() -> {}` | :white_check_mark: | This lambda is a function with no input parameters and returns void. The equivalent method would look like: `public void run() {}` |
| `() -> “Example1”` | :white_check_mark: | This lambda is a function with no input parameters and returns a string: “Example1”. The equivalent method:
`public void something() { return “Example1”; }` |
| `() -> { return “Example1”; } ` | :white_check_mark: | This is the same lambda method as above, but instead we are using an explicit return statement. |







