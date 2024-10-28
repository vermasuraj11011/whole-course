## Blocking and non-blocking languages

## Blocking
Blocking means that the execution of further instructions in the program is halted until the operation has been completed.
Blocking is used to manage the flow of data between two functions. If the flow of data is heavy, then the program will be
blocked until the data is fully transferred.

## Non-blocking
Non-blocking means that the execution of further instructions in the program continues immediately without waiting for the
operation to complete. Non-blocking is used to manage the flow of data between two functions. If the flow of data is heavy,
then the program will not be blocked until the data is fully transferred.

## Blocking languages
Blocking languages are those languages that are synchronous in nature. In these languages, the execution of the program is
done line by line. The next line of code is executed only after the current line of code has been executed. This means that
the program is blocked until the current line of code has been executed. Examples of blocking languages are C, C++, Java,
Python, etc.

## Non-blocking languages
Non-blocking languages are those languages that are asynchronous in nature. In these languages, the execution of the program
is done in parallel. This means that the program is not blocked until the current line of code has been executed. Examples of
non-blocking languages are JavaScript, Node.js, etc.

## Event driven architecture
Event-driven architecture is a software architecture that is based on events. In this architecture, the flow of the program
is controlled by events. An event is a signal that is generated by a component of the system. When an event is generated, the
system responds to it by executing a specific piece of code. This piece of code is called an event handler. Event-driven
architecture is used to build responsive and scalable applications. Examples of event-driven architecture are web
applications, mobile applications, etc.

## Difference between blocking and non-blocking languages
The main difference between blocking and non-blocking languages is the way they handle the flow of data. In blocking languages,
the flow of data is managed synchronously, while in non-blocking languages, the flow of data is managed asynchronously. This
means that in blocking languages, the program is blocked until the data is fully transferred, while in non-blocking languages,
the program is not blocked until the data is fully transferred. This makes non-blocking languages more efficient and
responsive than blocking languages.

## solution
blocking uses multi-threading to handle multiple tasks concurrently, while non-blocking uses asynchronous programming to
handle multiple tasks concurrently. In blocking, the program is blocked until the task is completed, while in non-blocking,
the program is not blocked until the task is completed. This makes non-blocking more efficient and responsive than blocking.

blocking -> multi-threading, synchronous programming

non-blocking -> asynchronous programming, observer pattern, promises, callbacks

## Difference between asynchronous and multi-threading
Asynchronous and multi-threading are two different ways of handling concurrent operations in a program. Asynchronous is a
programming model that allows the program to execute multiple tasks concurrently without blocking the main thread. In
asynchronous programming, the program is not blocked until the task is completed. Multi-threading is a programming model that
allows the program to execute multiple tasks concurrently by creating multiple threads. In multi-threading, the program is
blocked until the task is completed. The main difference between asynchronous and multi-threading is that in asynchronous
programming, the program is not blocked until the task is completed, while in multi-threading, the program is blocked until
the task is completed.


## non-blocking code example
```javascript
console.log('Start');

setTimeout(() => {
  console.log('2 Second Timer');
}, 2000);

setTimeout(() => {
  console.log('0 Second Timer');
}, 0);

console.log('End');
```

## blocking code example
```java
public class Main {

    public static void main(String[] args) {
        System.out.println("Start");
        
        temp(a);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("2 Second Timer");

        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("0 Second Timer");

        System.out.println("End");
    }
}
```

## How asynchronous programming achieve in the scala
In Scala, asynchronous programming can be achieved using the Future and Promise classes. The Future class represents a
computation that will be completed in the future, while the Promise class represents a value that will be produced in the
future. The Future class provides methods to handle the completion of the computation, such as onComplete, onSuccess, and
onFailure. The Promise class provides methods to complete the computation, such as success and failure. By using the Future
and Promise classes, you can write asynchronous code that is non-blocking and responsive.

## scala 
    - functional and object-oriented programming language
    - asynchronous coding using Future and Promise classes (but by nature it is blocking)
    - scala function is first class citizen (means it is an object), we can assing it to a variable, pass it as an argument, return it from a function.
    - Operator overloading is also possible in scala.


### Create executable class in scala

```scala
// class having main method
object Main {
  def main(args: Array[String]): Unit = {
    println("Hello, World!")
  }
}

// class which extends App trait
object Main extends App {
  println("Hello, World!")
}

// class with executable code
@main def hello(): Unit = {
  println("Hello, World!")
}
```