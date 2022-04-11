# CS474 - Aditya Ogale - 678453407
Homework 4 of course CS474 - OOLE

## Run tests
Code is written in ```src/main/scala/ExceptionOperations.scala```. The tests are hosted in file ```src/test/scala/ExceptionOperationTest.scala```. To run the tests, click on the icon on left panel on line where class is defined or right click on white space and click run tests.

## Imports
To import the package, add following import statement.
```
import ExceptionOperations.ExceptionClass.*
```

## Methods and the functionality
1. eval:
    - Use this method to evaluate the ClassDefinition expression.
 
2. Condition(lhs: SetOperations, rhs: SetOperations)
    - Equates LHS and RHS and returns if both are equal, false otherwise.
    - LHS and RHS are both SetOperations. It will only equate the 2 expressions. It can not evaluate greater than or less than for the 2 sets.
    
    ```
    Assign("x", CreateSet(1, 2, 3)).eval
    Assign("y", CreateSet(1, 2, 3)).eval
    // This condition case will return true.
    Condition(Variable("x"), Variable("y")).eval
    ```
    

3. IfElse(condition: Condition, thenExp: List[Any], elseExp: List[Any]):
    - This is the if-else construct. it evaluates the condition and decides which of the 2 to evaluate: thenExp or elseExp.
    - If condition is true, thenExp is evaluated.
    - If condition is false, elseExp is evaluated.
    - Both the thenExp and elseExp accept List of Any type, although will only evaluate if the passed element is of type SetOperations, ClassDefinition or ExceptionClass. It will not evaluate any other type but will not throw any error either.
    - For nested if-else, Use If construct in theExp or elseExp block. It will evaluate under ExceptionClass type.
    
    ```
    Assign("x", CreateSet(1, 2, 3)).eval
    Assign("y", CreateSet(1, 2, 3)).eval
    // This will evaluate thenExp as condition is true.
    IfElse(Condition(Variable("x"), Variable("y")),
      List[Any](Insert("y", ValueOf(10002)), Insert("x", ValueOf(20002))),
      List[Any](Insert("y", ValueOf(4)), Insert("x", ValueOf(6)))).eval
    ```
    

4. ExceptionClassDef(classDef: String, reason: String):
    - Creates the binding of className corresponding to its reason.
    - Returns the binding of the className. 
    
    ```
    // This will craete mapping of class c1 with the given reason.
    ExceptionClassDef("c1", "Termination due to exhaustion").eval
    ```
    

5. Try(tryExp: Any*):
    - Accepts multiple inputs and tries to evaluate them.
    - Although it accepts Any, only SetOperations, ClassDefinition and ExceptionClass are evaluated. Any other type will not be evaluated but will not throw any error.
    - This case can not be used as a stand-alone case. It has to be used in TryCatch case.
    
    ```
    // This will give error. Following code-snippet is for illustation purposes only.
    Try(Insert("x", ValueOf(98)), Insert("x", ValueOf(1002))).eval
    ```
    

6. Catch(catchExp: Any*):
    - Accepts multiple inputs and tries to evaluate them if Throw is called in Try block.
    - Although it accepts Any, only SetOperations, ClassDefinition and ExceptionClass are evaluated. Any other type will not be evaluated but will not throw any error.
    - This case can not be used as a stand-alone case. It has to be used in TryCatch case.
    
    ```
    // This will give error. Following code-snippet is for illustation purposes only.
    Catch(Insert("x", ValueOf(98)), Insert("x", ValueOf(1002))).eval
    ```
    

7. TryCatch(tryExp: ExceptionOperations.ExceptionClass.Try, catchExp: ExceptionOperations.ExceptionClass.Catch):
    - Accepts a Try expression and Catch expression. Evaluates the Try expression first. If executed with no exceptions, Catch block will not be evaluated.
    - For control to move to Catch block, a ThrowExpression must be used in Try block. 
    - If ThrowExpression is used in Try block, then all subsequent statements are skipped and it will be moved to catch.
    - Catch block prints the reason of the exception by default and then evaluates the catchExp. At the end, it will return the reason of the exception.
    - This case can be nested inside each other. The control will always move to the catch block.
    
    ```
    // try-catch block. It will try to evaluate Try block. If Throw is used in it, control will move to Catch.
    TryCatch(Try(Insert("x", ValueOf(98)), Insert("x", ValueOf(1002))),
      Catch(Assign("x", CreateSet(-1)),Assign("y", CreateSet(9,6,3)), Assign("z", CreateSet(8,9,0)))).eval
    ```
    

8. ThrowExpression(className: String, reason: String):
    - This should not be used stand-alone, although it will not throw any exception.
    - This case replaces the reason of the given class and then it will move control to catch block.
    
    ```
    // This case will not yield any exception but it is only for illustration purposes only.
    ThrowExpression("c1", "avada kedavra!").eval
    // try-catch block. It will try to evaluate Try block. If Throw is used in it, control will move to Catch.
    TryCatch(Try(ThrowExpression("c1", "avada kedavra!")),
      Catch(Assign("x", CreateSet(-1)),Assign("y", CreateSet(9,6,3)), Assign("z", CreateSet(8,9,0)))).eval
    ```
    

9. getReasonOfException(className: String):
    - This case is for testing purposes only.
    - Accepts className and returns the reason of the exception class.
    
    ```
    // Returns the reason of class c1.
    getReasonOfException("c1").eval
    ```
    
