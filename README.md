# CS474
Homework 1 of course CS474 - OOLE

## Imports
To import the package, add following import statement.
```
import SetOperations.SetOperations.*
```

## Methods and the functionality
1. eval:
    - Use this method to evaluate the SetOperation expression.
 
2. ValueOf(varName: Any):
    - Returns the value of varName
    ```
    ValueOf(2).eval
    // Returns 2
    ```

3. Variable(varName: String):
    - Returns value of the variable "varName" as assigned previously.
    - If not assigned, returns 0.
    ```
    Variable("x").eval
    // Returns already assigned value of variable x. If not assigned, returns 0.
    ```

4. Assign(varName: String, element: SetOperations):
    - Assigns output of SetOperation element to the variable varName.
    - Refer to following code snippet as an example. The code assigns already assigned value of "y" to new variable "x".
    ```
    Assign("x", Variable("y")).eval
    // Assigns value of "y" to new variable "x".
    ```
    
5. CreateSet(elements: Any*):
    - Creates and returns a new set of values passed as a parameter. 
    - There is **no need to call ValueOf for each element**
    ```
    Assign("x", CreateSet(1,2)).eval
    // Creates a new Set and assigns the set to "x".
    ```
    
6. Insert(varName: String, element: SetOperations):
     -  

