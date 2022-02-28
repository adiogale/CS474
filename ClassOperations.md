# CS474 - Aditya Ogale - 678453407
Homework 1 of course CS474 - OOLE

## Imports
To import the package, add following import statement.
```
import ClassOperations.ClassDefinition.*
```

## Methods and the functionality
1. eval:
    - Use this method to evaluate the ClassDefinition expression.
 
2. ClassDef(className: String, fields: ClassDefinition, constructor: ClassDefinition, method: ClassDefinition.Method *):
    - Creates a class and returns a tuple of className and class definition.
    - Creates a class with given class name, set of fields, one constructor and as many methods as possible.
    ```
    // This will create a class c1, with fields x and y, with given constructor and methods.
    ClassDef("c1", Fields("x", "y"), Constructor(("c", CreateSet(1, 2)), ("y", ValueOf(2)), ("x", ValueOf(1, 3, 4, 2))),
      Method("m1", Assign("x", CreateSet(1, 2, 3))), Method("m2", Insert("x", ValueOf(9)))).eval
    ```
    
3. Fields(fields: String*):
    - This returns set of all the fields desired.
    - Refer to above example, Fields("x", "y") is called.
    
4. Constructor(fields: Tuple*):
    - This returns set of given tuples with format (field, value).
    ```
    // Constructor initializes c, y and x to given values. Beware, even if c is not present in class, it will not throw any error nor will it add the field.
    Constructor(("c", CreateSet(1, 2)), ("y", ValueOf(2)), ("x", ValueOf(1, 3, 4, 2)))
    ```
    
5. Method(methodName: String, operations: SetOperations*):
    - Returns key value pair of method name and set of all SetOperations.
    ```
    // This will return the m1 -> Assign("x", CreateSet(1,2,3)). This is non-parameterized method implementation. Parameterized is not implemented.
    Method("m1", Assign("x", CreateSet(1, 2, 3)))
    ```
    
6. NewObject(objectName: String, className: String):
    - Class with className must be defined beforehand.
    - Returns new object with objectName and className.
    - It will call the constructor internally and all methods will be available to the object with fields initialized.
    - Default value of all fields will be 0.
    ```
    // This will create object o1 of class c1.  
    NewObject("o1", "c1").eval
    ```
    
7. Object(objectName: String):
    - Return the whole object for the user to view.
    ```
    // This will return the object map of o1.
    Object("o1").eval
    ```
    
8. CallMethod(methodName: String, objectName: String):
    - NewObject must be called beforehand to create object.
    - Calls the method "methodName" for the object "objectName".
    - Will run all the SetOperations internally and change values of fields if necessary.
    - This will impact only the object called.
    ```
    // This will call method m1 of object o1.
    CallMethod("m1", "o1").eval
    ```
    
9. Extend(class1: String, class2: String):
    - Both classes must be defined beforehand.
    - Class1 extends class2. All fields and methods will be added to the class1.
    - If methods from both classes have same name, method in class 2 will be overridden and class1 method will be used.
    - Constructor of both the classes will be called to initialize all the fields. If a field has same name, value of constructor of class1 will be used.
    ```
    // c2 extends c1.
    Extend("c2", "c1").eval
    ```
10. InnerClass(outerClassName: String, classDef: ClassDefinition.ClassDef):
    - Creates a inner class inside outerClassName class. 
    - classDef will be evaluated and assigned inside key "innerClass" of the class map.
    ```
    // Will create class c2
    ClassDef("c2", Fields("z", "y"), Constructor(("z", CreateSet(1, 2)), ("y", ValueOf(2))),
        Method("m1", Assign("x", CreateSet(1, 2, 6))), Method("m3", Insert("x", ValueOf(8)))).eval
    // Will create an inner class c1 and the ClassDef will be assigned to "innerClass" of c2 class.
    InnerClass("c2", ClassDef("c1", Fields("x", "y"), Constructor(("c", CreateSet(1, 2)), ("y", ValueOf(2)), ("x", ValueOf(1, 3, 4, 2))),
        Method("m1", Assign("x", CreateSet(1, 2, 3))), Method("m2", Insert("x", ValueOf(9))))).eval
    ```
