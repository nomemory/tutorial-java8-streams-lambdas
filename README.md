# Streams & Lambdas

![streams-lambdas-main](media/streams-lambdas-main.jpg)

**half-life-3-confirmed**

<sup>This tutorial assumes the reader has a good grasp of the Java Programming language features: Interfaces, Anonymous Classes, Collectins API, etc.</sup>

## Introduction

Before jumping into conclusions and start bragging about how Streams and Lambdas are going to suddenly solve all of our dev problems, let me start by telling you that you can continue writing excellent Java code without using any of those features. We did that before Java 8, didn't we ?

Another important aspect is that you shouldn't jump directly into (re)writing everything in a "functional" style, just because it's "nice". 

Streams and Lambdas are an important addition to the Java language, but they are adding a little bit of overhead (actually, depending on the context, it can be more than *a bit*). For example, a classic `for` loop will be more efficient than using a `Stream` to iterate over an array. And no matter how the JVM will evolve in the future, things will probably remain this way.

Using Lambdas and Streams is not about gaining small performance advantages in terms of CPU or memory utilisation (actually they induce *penalties*), but about writing code that is more *short*, *readable*, *concise* and *easier to debug*. If performance is important, please don't replace every `for` with [`IntStream.range()`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/IntStream.html#range-int-int-) just because it's *hip*. 

// rant off 

To prove my points let's begin by doing something we enjoy: write code and solve an exercise.

We will write a simple method that takes a `List<Employee>` as input, then groups every employee by his/her department, resulting in a `Map<String, List<Employee>>`. 

The `key` represents the department, while the `value` is a `List<>` of every employee that works in that deprtament. 

For reference the `Employee` might class looks like:

```java
// If this your first time you those annotations
// Please check: `Project Lombok`
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

Without using any of the Streams API we can write something like this:

```java
public static Map<String, List<Employee>> groupByDepartments(List<Employee> employees) {
    Map<String, List<Employee>> result = new HashMap<>();
    for(Employee employee : employees) {
        // If it's the first time we encounter the department we initialize the List<Employee>
        // Note: `putIfAbsent` method was also introduced in Java 8
        result.putIfAbsent(employee.getDepartment(), new LinkedList<>());
        result.get(employee.getDepartment()).add(employee);
    }
    return result;
}
```

This doesn't look too bad, and the code is quite straight-forward. We can live with that. At least we did.

But what if tell you `groupingBy` is a thing that is "built-in" in the Stream API and everything becomes a one-liner:

```java
public static Map<String, List<Employee>> groupByDepartmentsF(List<Employee> employees) {
    return employees.stream().collect(groupingBy(Employee::getDepartment)); // Employee:getDepartment is a lambda
}
```

Normally I don't recommend people on using "condensed" one-liners, but in this case it's almost like reading *English*. The code speaks by itself. 

Let's make things more "difficult".

The new requirement is to write a method that takes a `List<Employee>` and returns a `Map<String, Long>` counting how many employees each departments has. The `key` will represent the Department, while the `value` will represent the number of employees working in that department.

```
// The map will look like this:
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

<sub>Before *jumping into the Stream*</sub>

<sup>Lambda, Λ, λ (uppercase Λ, lowercase λ) is the 11th letter of the Greek alphabet...</sup>

Lambda is also a **concise** representation of an **anonymous** **function** that can be **passed around**.
* concise → no need to write boilerplate code (remember *Anonymous Classes...*);
* anonymous → the lambda doesn’t have a name like methods have;
* function → just like a function it has a body, a return type, and list of parameters;
* passed around → the lambda can be passed as parameter or referenced by a variable.

**Bad News**:
* Lambdas technically don't let you do anything that you couldn't do prior to **Java 8**. 

**Good news**:
* You are no longer required to write long and tedious declarations. Shorter code ceremony, same features.

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

Collections.sort(employees, Comparator.comparing(Employee::getSalary)) // Employee::getSalary is also a lambda!
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

*This lambda is a function with no input parameters and returns a string: “Example1”. The return statement is implicit (we don't need to write it). The equivalent method is:* `public void something() { return “Example1”; }`

* :white_check_mark: `() -> { return “Example1”; } ` → 

*This is the same lambda method as above, but instead of the implicit return statement we are using an explicit one. For brevity I prefer the initial version.*

* :x: `() ->  String s = “abc” ; s + s;` →

*The above lambda is "invalid". The rule states that if the lambda body is a block of statements - we need to include brackets. Brackets can be omitted only if the lambda is a one-liner.*

* :x: `() ->  { String s = “abc” ; s + s; }` →

*This lambda is still invalid because it's a block of statements but it doesn't have a return statement.*

* :white_check_mark: `() ->  { String s = “abc” ; return s + s; }` →

*This is a valid lambda that (always) returns the string:* `"abcabc"`.* 

* :white_check_mark: `(List<String> list) -> list.isEmpty()` →

*This is valid lambda. In most of the cases there's no need to specify the type of the input parameters, as the type is inferred from the context. You could've ommited the types by simply writing: `(list) -> list.isEmpty`*

* :white_check_mark: `() -> new Apple(10)`

* :white_check_mark: `(Message msg) -> { System.out.println(msg.getHeader()); }`

* :white_check_mark: `(Integer a, Integer b) -> a * b;`


### `@FunctionalInterface`

**Q** :question: So lambdas are anonymous functions with a body but without a name ! How and where do we use them ?

 * We just pass them around. Lambdas can be parameters for functions, constructors and they can be kept in variables! |
 
**Q** :question: Oh wait, Java is strongly typed. Is “Lambda” a new type ?

* Well… no. For now, it suffices to understand that a lambda expression can be assigned to a variable or passed to a method expecting a functional interface as argument, provided the lambda expression has the same signature as the abstract method of the **Functional Interface**.

To be more clear, **Functional Interface**s are interfaces that specify exactly one abstract method and can be marked with the `@FunctionalInterface`. 

<sup>(Note: This annotation is not mandatory, but it's useful for compile-time checks. Basically if we don't respect the "one abstract method rule" and `@FunctionalInterface` was used, the code won't compile. Use it with trust: it will make your code more readable, and it will protect the codebase by not allowing "rogue" developers to add more abstract methods to your functional interfaces).</sup>

The most obvious examples of `@FunctionalInterface`s from the Java API are:

```java
@FunctionalInterface
public interface Comparator<T> {
    // The one and only abstract method from the interface
	int compare(T o1, T o2);
}

// That's why we can write:
Comparator<T> comp = (T t1, T t2) -> { /*...*/ }
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

Actually there's more than that. The [`java.utill.function`](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html) package is nice enough to define Functional Interfaces for us so we can easily juggle with the lambdas in our code. The most important ones are `Predicate<T>`, `Function<T1, T2>`, `Consumer<T>`, `Supplier<T>` and `BiFunction<T1, T2, T3>`. Those interfaces represent the type we are going to use when referencing lambda methods.

Nobody restricts us to define our own `@FunctionalInterface`s as long as we keep in mind that they need to contain exactly one abstract method. 

Creating our own functional interfaces is not uncommon, but because all the interfaces defined in [`java.utill.function`](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html) are generic we should them re-use them as much as possible. 

Examples:

```java

// References a method that:
// - Has one input parameter (T)
// - Returns void
// 
// (T) -> void
Consumer<String> printUpperCase = (str) -> str.toLowerCase();

// References a method that:
// - Has one input parameter (T)
// - Returns a value (R) (R and T can be identifical)
// 
// (T1) -> return (T2);
Function<String, Integer> countVocals =
                (str) -> str.replaceAll("[^aeiouAEIOU]","").length();

// References a method that:
// - Has one input parameter (T)
// - Returns a Boolean
// 
// (T) -> return Boolean;
Predicate<String> containsComma = (str) -> str.contains(",");

// References a method that:
// - Has no input parameters
// - Returns a value (R)
//
// () -> R
Supplier<Double> randomSupplier = () -> Math.random();


// Reference a method that:
// - Has two input parameters (T1, T2)
// - Returns void
//
// (T1, T2) -> void
BiConsumer<Integer, Integer> printSum = 
    (i1, i2) -> { 
        System.out.printf("%d+%d=%d", i1, i2, i1+i2); 
    };

// References a method that:
// - Has two input parameters: (T1) and (T2);
// - Returns a value (R)
//
// (T1, T2) -> return (R);
BiFunction<String, Integer, String> repeatNTimes=
                (str, times) -> new String(new char[times]).replace("\0", str);

                
// References a method that:
// - Has two input parameters (T1) and (T2)
// - Returns a boolean value
//
// (T1, T2) -> Boolean
BiPredicate<Integer, Integer> biggerThan = (i1, i2) -> (i1 > i2);
```

If you look closer at the example everything should start making sense now. `containsComma`, `printUpperCase`, `countVocals`, `repeatNTimes`, etc are all "variables", but they are no longer used to store data. Or at least not that the type of data we were accustomed to. Rather, they "store behavior", "behavior" that can be passed around your code and played with.

If we want to actually call the "behavior"/"functionality" associated with a Lambda method we just need to invoke the single abstract method that the `@FunctionalInterface` defines.

The only abstract method from the `Function<T>` interface is called `apply()`:

```java
Function<String, Integer> countVocals = (str) -> str.replaceAll("[^aeiouAEIOU]","").length();
int numVocals = countVocals.apply("ABC");
```

The only abstract method from the `Predicate` class is called `test()`:

```java
Predicate<String> containsComma = (str) -> str.contains(",");
boolean hasComma = containsComma.test("ab,c");
```

It's also important to note that a `@FunctionalInterface` cand have as many `default` methods as necesarry. There's no limit on that. For example the `Function<T>` interface has three additional `default` methods: `andThen()`, `compose()` and `identity()`. 

`compose()` and `andThen()` are methods used for function composition. They are quite helfpul if we want to chain a series of lambdas:

```java
Function<String, String> putInBrackets = (s) -> "[" + s + "]";
Function<String, String> putInQuotes = (s) -> "\"" + s + "\"";

// Equivalent to putInQuotes(putInBrackets("ABC"))
// g(f(x)) <=> (gof)(x)
String s1 = putInBrackets.andThen(putInQuotes).apply("ABC");
System.out.println(s1); // Output: "[ABC]"

// Equivalent to putInBrackets(putInQuotes("ABC"))
// f(g(x)) <=> (fog)(x)
String s2 = putInBrackets.compose(putInQuotes).apply("ABC");
System.out.println(s2); // Output: ["ABC"]

// Notice the difference
```

Let's go further and imagine ourselves we are building a class `EmailComposer` that compose email. Each email has:

* A greeting: "Dear, ", "Hi, "
* A pleasantry: "I hope your are well."
* A body (content) - this represents the actual message;
* A sign-off: "Best wishes", "Warm Regards".

Our task is to write the `EmailComposer` using `andThem()` and `compose()`. With a little bit of stretch, and only to prove a point, we can come up with the following solution (I am not kidding, you don't have to do it like this):

```java
public class EmailComposer {

    private static final String GREETING = "Hello,";
    private static final String PLEASANTRY = "We hope you are well.";
    private static final String SIGN_OFF = "Warm regards,\nThe team.";

    private String body;

    public EmailComposer(String body) {
        this.body = body;
    }

    public static Function<String, String> prepend(String msg) {
        return (body) -> msg + "\n\n" + body;
    }

    public static Function<String, String> append(String msg) {
        return (body) -> body + "\n\n" + msg;
    }

    // Chaining functions !! HERE
    public String composeEmail() {
        return prepend(GREETING)
                .compose(prepend(PLEASANTRY))
                .andThen(append(SIGN_OFF))
                .apply(this.body);
    }

    public static void main(String[] args) {
        EmailComposer ec = new EmailComposer("This is a test email.");
        System.out.println(ec.composeEmail());
    }
}
```

### Referencing existing method with lambda using `::` notation

The `::` notation is mainly used to reference existing methods, static methods or even constructors. 

So for example if we want to sort a `String[]` using the `compareToIgnoreCase()` method from the [String class](docs.oracle.com/javase/8/docs/api/java/lang/String.html) we would write something like this:

```java
String[] stringArray = { "Andrei", "ion", "Sara", "Avraham", "Steven", "deborah", "michael" };
Arrays.sort(stringArray, (s1, s2) -> s1.compareToIgnoreCase(s2));
```

Instead of writing a lambda that uses the `compareToIgnoreCase()` explicitily we can reference the method directly just like in the following example:

```java
 String[] stringArray = { "Andrei", "ion", "Sara", "Avraham", "Steven", "deborah", "michael" };
Arrays.sort(stringArray, String::compareToIgnoreCase);
```

In the above example `s1` will become the object on which we call the method `String::compareToIgnoreCase`, while `s2` represents the the input parameter of the method. The nice part is we don't have to explicitily write them.

Other examples:

```java
Consumer<String> print1 = (str) -> System.out.println(str);
// Is Equivalent to
Consumer<String> print2 = System.out::println;

// OR

BiFunction<String, String, String> concater1 = (source, str) -> source.concat(str);
// Is Equivalent to
BiFunction<String, String, String> concater2 = String::concat;

// OR

BiPredicate<Object, Object> biPred1 = (o1, o2) -> Objects.deepEquals(o1, o2);
// Is Equivalent to
BiPredicate<Object, Object> biPred2 = Objects::deepEquals;
```

Constructors can be referenced just as well:

```java
Function<String, StringBuilder> sbCreate1 = (str) -> new StringBuilder(str);
// Is Equivalent to
Function<String, StringBuilder> sbCreate2 = StringBuilder::new;

// OR

Supplier<StringBuilder> sbNaCreate1 = () -> new StringBuilder();
// Is Equivalent to
Supplier<StringBuilder> sbNaCreate2 = StringBuilder::new;
```

### Lambdas and Scope

Important Rule: Lambdas are **not** syntactic sugar for *Anonymous Inner Classes*, even if they seem to be similar. What differentiates the two is how `scope` works:

* When we create an Inner Class, we create a new scope. `this` in the context of an inner class is a reference to the newly created instance;

* When we create a Lambda method, we "inherit" the enclosing scope. `this` in the context of a lambda references the enclosing instance.

Let's run the following example:

```java
public class ScopeExperiment {

    private String value = "Enclosing Scope";

    public void experiment() {
        Runnable rAIC = new Runnable() {
            private String value = "Local Scope";
            @Override
            public void run() {
                System.out.println(this.getClass().getName());
                System.out.println(this.value);
            }
        };
        rAIC.run();

        Runnable rLam = () -> {
            System.out.println(this.getClass().getName());
            System.out.println(this.value);
        };
        rLam.run();
    }

    public static void main(String[] args) {
        new ScopeExperiment().experiment();
    }
}
```

The output will be:

```
net.andreinc.jlands.generic.ScopeExperiment$1
Local Scope
net.andreinc.jlands.generic.ScopeExperiment
Enclosing Scope
```

Remember that Lambdas don't have their own concept of `this`. All they do is to "inherit" their enclosing scope.

So if we change the signature of the `experiment()` method to `public static void experiment()` the code won't compile, all because of the lambda not being able to reference `this` from the static context. This doesn't affect inner classes:

```java
// DOES NOT COMPILE
public static void experiment() {
    Runnable rAIC = new Runnable() {
        private String value = "Local Scope";
        @Override
        public void run() {
            System.out.println(this.getClass().getName());
            System.out.println(this.value);
        }
    };
    rAIC.run();

    // Because we are in a static context there's no reference to 
    // this in the enclosing scope - code won't compile
    Runnable rLam = () -> {
        System.out.println(this.getClass().getName());
        System.out.println(this.value);
    };
    rLam.run();
}

// DOES COMPILE
// The Abstract Inner Class has it's "own this"
public static void experiment() {
    Runnable rAIC = new Runnable() {
        private String value = "Local Scope";
        @Override
        public void run() {
            System.out.println(this.getClass().getName());
            System.out.println(this.value);
        }
    };
    rAIC.run();
}
```

PS: Careful with this subtle difference!

#### Partial Function Application

To makes thing even more complicated we can have lambdas generating other lambdas by partially initializing them. 

Basically we can "translate" a lambda `g(x, y, z)` into a new lambda `f(y, z)` by binding `x` to a supplied value, so that `f(y,z)` is equivalent to `g(fixed_value, y, z)`. 

Partial Function Application refers to the process of fixing a number of arguments to a function, producing a new function with a smaller arity (*the number of arguments expected by a function*).

Let's define our own custom `@FunctionInterface` called `F3`:

```java
@FunctionalInterface
interface F3<T1, T2, T3, R> {
    R apply(T1 x, T2 y, T3 z);
}
```

`F3` is a function that accepts 3 parameters (`T1`, `T2`, `T3`) and returns a value, `R`. I find it perfect to reference an email generator:

```java
 F3 <String, String, String, String> emailGen =
                (name, company, domain) ->
                        name + "@" + company + "." + domain;

String luke = emailGen.apply("luke", "gmail", "com");
System.out.println(luke); // Output: luke@gmail.com
```

But if we want to generate only emails for a certain corporation (`"corp.net"`) we can partially intialize our initial `emailGen`erator by binding values to (`company`=`"corp"`) and (`domain`=`"net"`):

At this point `emailGen(name, company, domain)` becomes `corpEmailGen(name)`, a new lambda with reduced arity compared to the initial one:

```java

// We use the initial lambda `emailGen` to generate a new lambda `corpEmailGen` by partially initializing the function.
Function<String, String> corpEmailGen = (name) -> emailGen.apply(name, /* company= */ "corp", /* domain= */ "net");

String mikeCorp = corpEmailGen.apply("mike"); // Output: mike@corp.net
String lukeCorp = corpEmailGen.apply("luke"); // Output: luke@corp.net
```



### Currying

Currying is a technique of translating the evaluation o a function that takes multiple arguments into evaluating a sequence of functions, each with **a single argument**.

Currying can be seen as type of partial function application, they are related but certainly not the same. The biggest differences that curried functions return at every step another method with smaller arity that the previous one, while partially applied functions returnt the value instantly.

For example, to build the `emailGen` from the previous example we won't need to define our own `@FunctionalInterface`, but rather we can use the following pattern:

```java
Function<String, Function<String, Function<String, String>>> emailGen =
                (name) -> {
                    return (company) -> {
                        return (domain) -> {
                            // `name` and `company` are available from the enclosing scope
                            return name + "@" + company + "." + domain;
                        };
                    };
                };
```               

By excluding the explicit `return` statements we can come up with a shorther, more readable version for `emailGen`:

```java
Function<String, Function<String, Function<String, String>>> emailGen =
        name -> company -> domain -> name + "@" + company + "." + domain;
```

`emailGen` is curried because it can be considered as a sequence of functions taking a single argument as input parameter.

```java
// Intermediary Step 1
Function<String, Function<String, String>> withDomainDotNet = emailGen.apply("net");

// Intermediary Step 2
Function<String, String> withDomainNetAndCorp = withDomainDotNet.apply("corp");

// Final step:
String mikesEmail = withDomainNetAndCorp.apply("mike");
System.out.println(mikesEmail); // Output: mike@corp.net
```

Or writing the sequence of functions directly:

```java
String tomsEmail = emailGen.apply("net").apply("corp").apply("tom");
System.out.println(tomsEmail); // Output: tom@corp.net
```        

Java supports currying, but you can feel the language was not designed with this in mind. 

It doesn't come by default, and having to write something like `Function<String, Function<String, Function<String, String>>>` only to reference a curried method with 3 input parameters is ugly. 

Currying is useful when we decide we want to write our code in a purely functional way. Then it makes sense to define methods like this and pass them to higher order functions. 

Is Java the right language to write functional code ?

#### Example: Writing our own forEach method using lambdas

How many times we had to write code that was iterating over an array or a colllection, check if the elements match a certain condition and do something with those elements.

What if encapsulate the condition in a `Predicate<T>` and what we do with the elements in a `Consumer<T>`? What if we can then use those methods to "inject" behavior in the higher level `forEach` method we are going to implement?

The siganture for our own `forEach` method can look like:

```java
public static <T> void forEach(Iterable<T> elements, Predicate<T> condition, Consumer<T> consumer) {
    for(T element : elements) {
        if (condition.test(element)) { // Condition is not yet known -> the behavior will be received as an input parameter
            consumer.accept(element); // What we do with the element is not yet known -> the behavior will be received as input parameter
        }
    }
}
```    

Our method is generic enough now to be re-used in different scenarios:

```java
List<Integer> integers = Arrays.asList(100, 50, 200, 300, 70, 30, 20, 500);
List<String> names = Arrays.asList("Tom", "Kim", "Deb", "Mike", "Tony", "Tim");


// We create a curried function to define specialized `Predicate<T>`s
Function<Integer, Predicate<Integer>> biggerThan =
                n -> el -> (el > n);

// Print on the console elements bigger than 10
forEach(integers, biggerThan.apply(10), System.out::println);

// OR
// Print on the console elements bigger than 200
forEach(integers, biggerThan.apply(200), System.out::println);

// OR
// Print on the console all the strings that contain T
forEach(names, curry(String::contains, "T"), System.out::println);

// OR
// Print on the console all strings that have size == 3
forEach(names, s -> s.length() == 3, System.out::println);
```        

At this point you might wonder what the `curry` method means. Well the bad part is that there's no standard implementation in Java API, but for the sake of our `forEach` example I've written one by myself. Don't ask me why:

```java
public static <T1, T2> Predicate<T1> curry (BiPredicate<T1, T2> biPredicate, T2 el) {
    return t1 -> biPredicate.test(t1, el);
}
```
