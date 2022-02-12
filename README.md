# CS474 - Aditya Ogale - 678453407
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
    // Returns 2
    ValueOf(2).eval
    ```

3. Variable(varName: String):
    - Returns value of the variable "varName" as assigned previously.
    - If not assigned, returns 0.
    ```
    // Returns already assigned value of variable x. If not assigned, returns 0.
    Variable("x").eval
    ```

4. Assign(varName: String, element: SetOperations):
    - Assigns output of SetOperation element to the variable varName.
    - Refer to following code snippet as an example. The code assigns already assigned value of "y" to new variable "x".
    ```
    // Assigns value of "y" to new variable "x".
    Assign("x", Variable("y")).eval
    ```
    
5. CreateSet(elements: Any*):
    - Creates and returns a new set of values passed as a parameter. 
    - There is **no need to call ValueOf for each element**
    ```
    // Creates a new Set and assigns the set to "x".
    Assign("x", CreateSet(1,2)).eval
    ```
    
6. Insert(varName: String, element: SetOperations):
    -  Inserts the given element in given varName. 
    -  If set of varName is present, element is inserted in the set. If not, a set is created and added to it.
    ```
    // Insert 3 in set "x"
    Insert("x", ValueOf(3)).eval
    ```

7.Delete(varName: String, element: SetOperations):
    - Deletes the element from set named varName.
    ```
    // Delete 2 from set "x"
    Delete("x", ValueOf(2)).eval
    ```
    
8. Union(setName1: SetOperations, setName2: SetOperations):
    - Returns union of setName1 and setName2.
    ```
    //Union of set1 and set2
    Union(CreateSet(1,2), CreateSet(3,4)).eval
    ```
    
9. Intersection(setName1: SetOperations, setName2: SetOperations):
    - Returns elements present in both of the sets.
    ```
    // Intersection of set1, set2.
    Intersection(CreateSet(1,2,3), CreateSet(2,3,4)).eval
    //Returns HashSet(2,3)
    
10. Difference(setName1: SetOperations, setName2: SetOperations):
    - Returns set of elements that are in set1, except elements present in set2.
    - e.g. A={1,2,3} B={3,4} => A-B = {1,2}
    ```
    //Difference of 2 sets
    Difference(Variable("x"), CreateSet(2,3,4)).eval
    ```

11. Symmetric_difference(setName1: SetOperations, setName2: SetOperations):
    - Returns set of elements that are in set1, except elements present in set2 UNION elements in set2 that are not in set1.
    - e.g. A={1,2,3} B={3,4} => Symmetric_difference(A,B) = {1,2,4}
    ```
    //Difference of 2 sets
    Symmetric_difference(Variable("x"), CreateSet(2,3,4)).eval
    ```

12. Product(setName1: SetOperations, setName2: SetOperations)
    - Returns set of tuples with all possible combinations between the set elements.
    - e.g. A= {1,2} B={3,4} AXB = {(1,3),(2,3),(1,4),(2,4)}
    ```
    //Product of 2 sets
    Product(CreateSet(1,2), Insert("x",4)).eval
    ```
    
13. CheckElement(setName: SetOperations, element: SetOperations)
    - Checks if element is present in the set.
    - Return "Variable Name is not a set" if **setName** is not present.
    
14. SetMacro(macroName: String, macroValue: SetOperations)
    - Creates a Macro with given macroName and macroValue (will support a set operation only).
    ```
    SetMacro("insert", Insert("x", ValueOf(varName))).eval
    ```
    
15. GetMacro(macroName:String)
    - Returns value of given macroName. Can use instead of the desired value.
    ```
    GetMacro("insert").eval
    ```
    
16. Scope(scopeName: String, varName: String, element: SetOperations)
    - Creates a scope outside of which the elements will not be visible.
    - Default is "global".
    - While in execution, scope will be changed to scopeName, but after execution, it returns to default "global".
    ```
    // Creates a scope scope1 and creates a binding of x to a set (5,6)
    Scope("scope1", "x", CreateSet(5,6)).eval
    ```
    
17. getSetFromScope(scopeName: String, setName: String)
    - Will return the set or variable from given scope scopeName.
    ```
    //Returns x from scope1.
    getSetFromScope("scope2", "x").eval
    ```
    
  
