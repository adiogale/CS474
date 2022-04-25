# CS474 - Aditya Ogale - 678453407
Homework 5 of course CS474 - OOLE

## Run tests
Code is written in ```src/main/scala/ExceptionOperations.scala```. The tests are hosted in file ```src/test/scala/ExceptionOperationTest.scala```. To run the tests, click on the icon on left panel on line where class is defined or right click on white space and click run tests.

In this homework, I have implemented partial evaluation of the case classes and implemented optimizers as follows:

1. Partial Evaluation:
    - Insert: Insert will now NOT create a new set as it used to before and will instead return the partial evaluation of itself.
    - ``` Insert("x", ValueOf(3)).eval ``` will now return ```Insert("x", 3)``` instead of creating new set.
    
    - Union: ``` Union(Variable("x"), Variable("y")).eval``` will now return ```Union(Variable("x"), HashSet(1,2,3))``` if x is not yet defined.

    - Intersection: Similar to Union, ``` Intersection(Variable("x"), Variable("y")).eval``` will now return ```Intersection(Variable("x"), HashSet(1,2,3))``` if x is not yet defined.

    - Difference and Symmetric_Difference: Similarly, Difference and Symmetric_Difference will be partially evaluated.

    - NewObject: If a class is not yet defined, ``` NewObject("o1", "c1").eval ``` will not throw an error. Instead, ``` NewObject("o1", "c1") ``` will be returned.

    - Object: Similar to NewObject, Object will not throw error, and will return ``` Object("o1") ``` if o1 is not defined.

    - CallMethod: Similar to above two, CallMethod will be partially evaluated. If the method is not defined in the class, however, an error message will be returned as there is no chance of defining that method now as the class is already defined.

    -IfElse: IfElse is now partially evaluated by checking the condition. If condition is not fully evaluated, thenExp and elseExp are partially evaluated and IfElse construct is returned. 
    ```
    IfElse(Condition(Variable("i"), Variable("j")),List[Any](Insert("j", ValueOf(10002)), Insert("i", ValueOf(20002))),List[Any](Insert("j", ValueOf(4)))).eval
    // This will now return as following:
    // IfElse(Condition(mutable.HashSet(9, 3, 6),Variable("j")),List(Insert("j",10002), mutable.HashSet(9, 20002, 3, 6)),List(Insert("j",4)))
    
