# CS474 - Aditya Ogale - 678453407
Homework 5 of course CS474 - OOLE

## Run tests
Code is written in ```src/main/scala/ExceptionOperations.scala```. The tests are hosted in file ```src/test/scala/ExceptionOperationTest.scala```. To run the tests, click on the icon on left panel on line where class is defined or right click on white space and click run tests.

In this homework, I have implemented partial evaluation of the case classes and implemented optimizers as follows:

1. Partial Evaluation:
    - Insert: Insert will now NOT create a new set as it used to before and will instead return the partial evaluation of itself.
    - ``` Insert("x", ValueOf(3)).eval ``` will now return ```Insert("x", 3)``` instead of creating new set.
    
    - Union: ``` Union(Variable("x"), Variable("y")).eval``` will now return ```Union(Variable("x"), HashSet(1,2,3))```
