# CS474 - Aditya Ogale - 678453407
Homework 2 of course CS474 - OOLE

- Can a class/interface inherit from itself?
    - A class can not extend itself in Java. In my current implementation, I have applied a check with the same. It will show error message.
- Can an interface inherit from an abstract class with all pure methods?
    - No. In my implementtion, Interface can not inherit a class. If tried to do so, it will show an error.
- Can an interface implement another interface?
    - No. In my implementtion, Interface can not implement an interface. If tried to do so, it will show an error.
- Can a class implement two or more different interfaces that declare methods with exactly the same signatures?
    - Yes, a class can implement two two interfaces with the same method signature but that method should be implemented only once in the class. 
    - Since return type of a method is not defined in my implementation, a set will be created of all the methods defined in all the interfaces and then check if the those methods are implemented or not. If not, error message will be shown.
- Can an abstract class inherit from another abstract class and implement interfaces where all interfaces and the abstract class have methods with the same signatures?
    - Yes, since the signature is same, the concrete class must have implementation of the method only once. In my implementation, it is the same.
- Can an abstract class implement interfaces?
    - Yes. If the methods of the interface are not defined, whichever class inherits the abstract must have the implementation of remaining methods.
    - In the current implementation, all the abstract methods are clubbed together and checked if the methods are implemented or not.
- Can a class implement two or more interfaces that have methods whose signatures differ only in return types? 
    - If two interfaces contain a method with the same signature but different return types, then we can not implement both the interfaces at the same time.
    - In the current implementation, return types for the methods are not defined. 
- Can an abstract class inherit from a concrete class?
    - Yes. In the current implementation as well, the abstract class can extend concrete class. 
- Can an abstract class/interface be instantiated as anonymous concrete classes?
    - No. Interface or abstract class can not be instantiated. 
    - In the current implementation, it will show error message.
