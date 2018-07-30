# Streams & Lambdas

![streams-lambdas-main](media/streams-lambdas-main.jpg)

[#half-life-3-confirmed](https://kotaku.com/tag/half-life-3)

# Introduction

Before jumping into conclusions and start bragging about how Streams and Lambdas are going to suddenly solve all our problems let's start by ... doing some code-work.

We will write a method that takes a `List<Manager>` as input and then groups every manager by his/her department, resulting in a `Map<String, List<Manager>>`. 

For reference the `Manager` class looks like (*note: check [project lombok](https://projectlombok.org) if you want to know more about those annotation*):

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
private class Manager {
    private Long id;
    private String name;
    private String department;
}
```



