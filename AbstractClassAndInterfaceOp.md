# CS474 - Aditya Ogale - 678453407
Homework 2 of course CS474 - OOLE

## Run tests
Code is written in ```src/main/scala/ClassOperations.scala```. The tests are hosted in file ```src/test/scala/ClassOperationsTest.scala```. To run the tests, click on the icon on left panel on line where class is defined or right click on white space and click run tests.

## Imports
To import the package, add following import statement.
```
import ClassOperations.ClassDefinition.*
```

## Methods and the functionalit
1. eval:
    - Use this method to evaluate the ClassDefinition expression.
 
2. AbstractMethods(methodName: String*):
    - Creates and returns an ArrayBuffer[String] of all the abstract methods mentioned.
    - Will be used while creating Interface or an Abstract class.
    ```
    AbstractMethods("m1", "m2").eval
    ```
    
3. Interface(interfaceName: String, methods: ClassDefinition.AbstractMethods):
    - Creates a mapping of an interface name and its abstract methods. 
    - Can not define a field or a concrete method in current implementation.
    - Constructor is not defined for an interface.
    - Nested interface can not be implemented in the current implementation.
    ```
    //Creates interface i1 with methods m1 and m2.
    Interface("i1", AbstractMethods("m1", "m2")).eval
    ```
    
4. Implements(interfaceNames: String*):
    - Returns ArraySeq of the interfaces mentioned.
    - Will be used in ClassDef.
    ```
    // This class will implement interfaces i1 and i2.
    ClassDef("c7", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2,3)), ("a", CreateSet(5,6))), Implements("i1", "i2"), ExtendAbstractClass(),
      Method("m1", Insert("a", ValueOf(1)))).eval
    ```
    
5. ExtendAbstractClass(className: String = ""):
    - Returns the name of extended abstract class. 
    - Will be used in ClassDef like Implements.
    ```
    // This class will implement interfaces i1 and i2 and will extend abstract class abs1.
    ClassDef("c7", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2,3)), ("a", CreateSet(5,6))), Implements("i1", "i2"), ExtendAbstractClass("abs1"),
      Method("m1", Insert("a", ValueOf(1)))).eval
    ```
    
6. AbstractClass(className: String, fields: ClassDefinition, constructor: ClassDefinition, interfaces: ClassDefinition.Implements,
                 abstractMethods: ClassDefinition.AbstractMethods, method: ClassDefinition.Method*):
    - Like a ClassDef, fields, constructor, interfaces implemented and methods can be defined.
    - Since this is an abstract class, at least one abstract method has to be defined.
    ```
    // Abstract class named abs1 will be created with abstract method m3.
    AbstractClass("abs1", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2)), ("a", CreateSet(3,4))), Implements(), AbstractMethods("m3"),
      Method("m1", Insert("a", ValueOf(1))), Method("m2", Assign("b", CreateSet(8,9)))).eval
    ```
    
7. ExtendInterface(interfaceName: String, extendedInterface:String):
    - One interface can be extended by another interface.
    - This can be used multiple times for one interface i.e. one interface can extend multiple interfaces.
    ```
    Interface("i1", AbstractMethods("m1", "m2")).eval
    Interface("i2", AbstractMethods("m2", "m3")).eval
    // i1 will extend i2.
    ExtendInterface("i1", "i2").eval
    ```
    
8. GetInterface(interfaceName: String):
    - Returns all the methods in the interface.
    - Defined for testing purposes.
    ```
    // Will return all the methods in i3.
    GetInterface("i3").eval
    ```
