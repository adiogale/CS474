import ClassOperations.ClassDefinition.{ClassDef, Constructor, Fields, Implements, Method}
import SetOperations.SetOperations.*
import SetOperations.SetOperations

import scala.collection.mutable

object ClassOperations {
    private val classMapping: mutable.Map[String, mutable.Map[String, Any]] = mutable.Map[String, mutable.Map[String, Any]]()
    private val objectMapping: mutable.Map[String, mutable.Map[String, Any]] = mutable.Map[String, mutable.Map[String,Any]]()
    private val abstractClassMapping: mutable.Map[String, mutable.Map[String, Any]] = mutable.Map[String, mutable.Map[String, Any]]()
    private val interfaceMapping: mutable.Map[String, mutable.ArrayBuffer[String]] = mutable.Map[String, mutable.ArrayBuffer[String]]()

    enum ClassDefinition:
      case ClassDef(className: String, fields: ClassDefinition, constructor: ClassDefinition, interfaces: ClassDefinition.Implements,
                    extendsAbstractClass: ClassDefinition.ExtendAbstractClass, method: ClassDefinition.Method *) //constructor: ClassDefinition,
      case Fields(fields: String*)
      case Constructor(fields: Tuple*)
      case Method(methodName: String, operations: SetOperations*)
      case NewObject(objectName: String, className: String)
      case Object(objectName: String)
      case CallMethod(methodName: String, objectName: String)
      case Extend(class1: String, class2: String)
      case InnerClass(outerClassName: String, classDef: ClassDefinition.ClassDef)
//      case InnerClassObject(outerObjectName: String, innerObjectName: String, innerClassName: String)
      case AbstractMethods(methodName: String*)
      case Interface(interfaceName: String, methods: ClassDefinition.AbstractMethods)
      case Implements(interfaceNames: String*)
      case ExtendAbstractClass(className: String = "")
      case AbstractClass(className: String, fields: ClassDefinition, constructor: ClassDefinition, interfaces: ClassDefinition.Implements,
                         abstractMethods: ClassDefinition.AbstractMethods, method: ClassDefinition.Method*)
      case ExtendInterface(interfaceName: String, extendedInterface:String)
      case GetInterface(interfaceName: String)
      case ExtendAbstrctClass(className1: String, className2: String)

      def eval: Any =
        this match {

//          case ExtendAbstractClass(className1, className2) =>

          case ExtendInterface(interfaceName, extendedInterface) =>
            if(interfaceMapping.contains(extendedInterface)) {
              val setMethods = interfaceMapping(interfaceName).concat(interfaceMapping(extendedInterface)).toSet
              val temp: mutable.ArrayBuffer[String] = mutable.ArrayBuffer[String]()
              for (method <- setMethods) {
                temp += method
              }
              interfaceMapping(interfaceName) = temp
            }
            else if(classMapping.contains(extendedInterface)) {
              return "Can not extend a class"
            }
            else if (abstractClassMapping.contains(extendedInterface)) {
              return "Can not extend abstract class"
            }
            else{
              return "Error extending interfaces"
            }

          case GetInterface(interfaceName: String) =>
            interfaceMapping(interfaceName)

          case ClassDef(className, fields, constructor, interfaces, extendsAbstractClass, methods*) =>
              // FieldList is created
              val fieldList = fields.eval.asInstanceOf[mutable.Set[String]]
              // methodMap is created
              val methodMap: mutable.Map[String, mutable.Set[SetOperations]] = mutable.Map[String, mutable.Set[SetOperations]]()
              // Got set of the methods
              val methodSet = mutable.Set[ClassDefinition](methods *)
              for (method <- methodSet) {
                // For each method from the above set, map of method name and its function is created.
                methodMap += method.eval.asInstanceOf[(String, mutable.Set[SetOperations])]
              }
              val interfaceMethods: mutable.Set[String] = mutable.Set[String]()
              val interfaceImpl = interfaces.eval.asInstanceOf[Seq[String]]
              for (interface <- interfaceImpl) {
                val interfaceDefinedMethods = interfaceMapping(interface)
                for (methodName <- interfaceDefinedMethods) {
                  interfaceMethods += methodName
                }
              }
              val classAbsName = extendsAbstractClass.eval.asInstanceOf[String]
              val flag: mutable.Stack[Boolean] = mutable.Stack[Boolean](false)
              if(classAbsName != "") {
                val absMethodsInAbsClass = abstractClassMapping(classAbsName)("abstractMethods").asInstanceOf[mutable.ArrayBuffer[String]]
                for(absMethod <- absMethodsInAbsClass) {
                  interfaceMethods += absMethod
                }
                flag.pop()
                flag.push(true)
              }

              for (methodInInterface <- interfaceMethods) {
                if (methodMap.contains(methodInInterface)) {
                  interfaceMethods -= methodInInterface
                }
              }
              if (interfaceMethods.size > 0) {
                "Implement all methods from all the interfaces and abstract methods from extended abstract class. Methods not implemented: " + interfaceMethods.toString
              }
              else {
                if (flag.top == false) {
                  // Mapped flag (which is for inheritance), fields, Constructor and methods to respective fields.
                  val mapping = mutable.Map[String, Any]("flag" -> flag.pop(), "fields" -> fieldList, "methods" -> methodMap,
                    "constructor" -> mutable.Set[ClassDefinition](constructor))
                  // Use this map for classMapping.
                  classMapping += (className -> mapping)
                  (className, classMapping(className))
                }
                else {
                  val methodMapAbs = abstractClassMapping(classAbsName)("methods").asInstanceOf[mutable.Map[String, mutable.Set[SetOperations]]]
                  for ((methodAbs -> methodAbsDef) <- methodMapAbs) {
                    if (!methodMap.contains(methodAbs)) {
                      methodMap += (methodAbs -> methodAbsDef)
                    }
                  }
                  val mapping = mutable.Map[String, Any]("flag" -> flag.pop(),
                    "fields" -> fieldList.union(abstractClassMapping(classAbsName)("fields").asInstanceOf[mutable.Set[String]]),
                    "methods" -> methodMap,
                    "constructor" -> mutable.Set[ClassDefinition](constructor).asInstanceOf[mutable.Set[ClassDefinition]]
                      .union(abstractClassMapping(classAbsName)("constructor").asInstanceOf[mutable.Set[ClassDefinition]])
                  )
                  // Use this map for classMapping.
                  classMapping += (className -> mapping)
                  (className, classMapping(className))
                }
              }

          case ExtendAbstractClass(className) =>
            println(className)
            className

          case Fields(fields*) =>
            // Return binding of set of all fields passed
            val new_bindings = mutable.Set[String](fields *)
            new_bindings

          case Method(methodName, operations*)  =>
            // Returns a tuple of methodName and set of operations in the method
            (methodName -> mutable.Set[SetOperations](operations *))

          case NewObject(objectName, className) =>
            // If classmapping does not contain class name, returns error message
            if(!classMapping.contains(className)) {
              if(abstractClassMapping.contains(className)) {
                return "Can not initialize abstract class"
              }
              else if(interfaceMapping.contains(className)) {
                return "Can not initialize interface"
              }
              return "Error creating class"
            }
            // Map for object properties created
            val objectMap: mutable.Map[String, Any] = mutable.Map[String,Any]()
            // Adding className to object map
            objectMap += ("class" -> className)
            // Creating map of fields
            val fields: mutable.Map[String, Any] = mutable.Map[String,Any]()
            // Creating tuple list of fields
            val fieldTupleList = mutable.Set[Tuple]()
            // Iterating over constructors the class may have (due to inheritance)
            for (fieldTuple <- classMapping(className)("constructor").asInstanceOf[mutable.Set[ClassDefinition]]) {
              // Evaluating each tuple field.
              val eachConstructorEval = fieldTuple.eval
              // Will add each tuple to the fields.
              for (tuple <- eachConstructorEval.asInstanceOf[mutable.Set[Tuple]]){
                fieldTupleList += tuple
              }
            }
            // Getting list of all the fields in the class.
            val fieldListInClass = classMapping(className)("fields").asInstanceOf[mutable.Set[Any]]
            // Iterating over the fields to initialize to 0.
            for (field <- fieldListInClass) {
              fields += (field.asInstanceOf[String] -> 0)
            }
            // Adding each field->its value tuple to the fields map.
            for ((field, value) <- fieldTupleList.asInstanceOf[mutable.Set[Tuple]])
              if (classMapping(className)("fields").asInstanceOf[mutable.Set[Any]].contains(field)) {
                fields(field.asInstanceOf[String]) = value.asInstanceOf[SetOperations].eval
              }
            // Adding fields to the object map
            objectMap += ("fields" -> fields)
            // Addign object map corresponding to the object name.
            objectMapping += (objectName -> objectMap)
            objectMapping(objectName)


          case Constructor(fields*) =>
            // Returns set of all fields
            mutable.Set[Tuple](fields *)

          case Object(objectName) =>
            // Returns whole object.
            objectMapping(objectName)

          case CallMethod(methodName, objectName) =>
            //Gets className from object
            val className = objectMapping(objectName)("class").asInstanceOf[String]
            // Getting all the methods from the class.
            val methods = classMapping(className)("methods").asInstanceOf[mutable.Map[String, SetOperations]]
            // Getting all the fields from the class
            val fieldList = classMapping(className)("fields").asInstanceOf[mutable.Set[Any]]
            // Assigns the value of all the fields to respective variables for later use.
            for (field <- fieldList) {
              Assign(field.asInstanceOf[String],
                ValueOf(objectMapping(objectName)("fields").asInstanceOf[mutable.Map[String, Any]](field.asInstanceOf[String]))).eval
            }
            // Gets set operations from the method.
            val setOperations = methods(methodName).asInstanceOf[mutable.Set[SetOperations]]
            // Evaluating the operations.
            for (setOp <- setOperations)
              setOp.eval
            // Creating a new fields map
            val newFieldMap = mutable.Map[String, Any]()
            // For all fields, initialising the map.
            for (field <- fieldList) {
              newFieldMap += (field.asInstanceOf[String] -> Variable(field.asInstanceOf[String]).eval)
            }
            // adding the fields to the object map after the method is complete.
            for (field <- fieldList) {
              objectMapping(objectName)("fields").asInstanceOf[mutable.Map[String, Any]](field.asInstanceOf[String]) = newFieldMap(field.asInstanceOf[String])
            }

          //Class1 extends Class2
          case Extend(class1, class2) =>
            val absFlag: mutable.Stack[Boolean] = mutable.Stack[Boolean](false)
            if (!classMapping.contains((class1)) && abstractClassMapping.contains((class1))) {
              if(abstractClassMapping.contains(class2)){
                absFlag.pop()
                absFlag.push(true)
              }
            }
            else if(!classMapping.contains((class1)) && interfaceMapping.contains((class1))){
              return "Can not extend interface"
            }
            else if(!classMapping.contains(class1)) {
              return "Error extending class"
            }
            if (absFlag.top == true) {
              if(abstractClassMapping(class1)("flag") == false) {
                // Setting flag true for future reference.
                abstractClassMapping(class1)("flag") = true
                // Assigning union of all the fields to the subclass fields.
                abstractClassMapping(class1)("fields") = abstractClassMapping(class1)("fields").asInstanceOf[mutable.Set[String]]
                  .union(abstractClassMapping(class2)("fields").asInstanceOf[mutable.Set[String]])
                // To compare methods, creating 2 maps, methodMap1 for class1, methodMap2 for class2.
                val methodMap1 = abstractClassMapping(class1)("methods").asInstanceOf[mutable.Map[String, mutable.Set[SetOperations]]]
                val methodMap2 = abstractClassMapping(class2)("methods").asInstanceOf[mutable.Map[String, mutable.Set[SetOperations]]]
                // If the method from class 2 does not exist in class 1, adding it to class 1
                for ((methodNameC2, methodC2) <- methodMap2){
                  if(!methodMap1.contains(methodNameC2)) {
                    methodMap1 += (methodNameC2 -> methodC2)
                  }
                }
                abstractClassMapping(class1)("methods") = methodMap1
                // Adding all the constructors to the class 1.
                abstractClassMapping(class1)("constructor") = abstractClassMapping(class1)("constructor").asInstanceOf[mutable.Set[ClassDefinition]]
                  .union(abstractClassMapping(class2)("constructor").asInstanceOf[mutable.Set[ClassDefinition]])
                abstractClassMapping(class1)("abstractMethods") = abstractClassMapping(class2)("abstractMethods").asInstanceOf[mutable.ArrayBuffer[String]].concat(
                  abstractClassMapping(class2)("abstractMethods").asInstanceOf[mutable.ArrayBuffer[String]]
                )
              }
              else {
                "Can not extend multiple classes"
              }
            }
            else {
              // If flag is true, already inherited from some other class.
              if (classMapping(class1)("flag") == false) {
                // Setting flag true for future reference.
                classMapping(class1)("flag") = true
                // Assigning union of all the fields to the subclass fields.
                classMapping(class1)("fields") = classMapping(class1)("fields").asInstanceOf[mutable.Set[String]]
                  .union(classMapping(class2)("fields").asInstanceOf[mutable.Set[String]])
                // To compare methods, creating 2 maps, methodMap1 for class1, methodMap2 for class2.
                val methodMap1 = classMapping(class1)("methods").asInstanceOf[mutable.Map[String, mutable.Set[SetOperations]]]
                val methodMap2 = classMapping(class2)("methods").asInstanceOf[mutable.Map[String, mutable.Set[SetOperations]]]
                // If the method from class 2 does not exist in class 1, adding it to class 1
                for ((methodNameC2, methodC2) <- methodMap2) {
                  if (!methodMap1.contains(methodNameC2)) {
                    methodMap1 += (methodNameC2 -> methodC2)
                  }
                }
                classMapping(class2)("methods") = methodMap1
                // Adding all the constructors to the class 1.
                classMapping(class1)("constructor") = classMapping(class1)("constructor").asInstanceOf[mutable.Set[ClassDefinition]]
                  .union(classMapping(class2)("constructor").asInstanceOf[mutable.Set[ClassDefinition]])

              }
              else {
                "Can not extend multiple classes"
              }
            }

          case InnerClass(outerClassName, classDef) =>
            // Getting className and classDefinition
            val (className, classDf) = classDef.eval.asInstanceOf[Tuple]
            val classNm = className.asInstanceOf[String]
            // Removing from classMapping as it is inner class
            classMapping -= classNm
            // Adding a field to outerClass named "innerClass".
            classMapping(outerClassName) += ("innerClass" -> (className -> classDf))

          case AbstractMethods(methodNames*) =>
            val methods: mutable.ArrayBuffer[String] = mutable.ArrayBuffer[String]()
            for (method <- methodNames) {
              methods += (method.asInstanceOf[String])
            }
            methods

          case Interface(interfaceName, methodSeq) =>
            interfaceMapping += (interfaceName -> methodSeq.eval.asInstanceOf[mutable.ArrayBuffer[String]])
            println(interfaceMapping)

          case Implements(interfaceNames*) =>
            interfaceNames

          case AbstractClass(className, fields, constructor, interfaces, abstractMethods, methods*) =>
            // FieldList is created
            val fieldList = fields.eval
            // Abstract Methods
            val abstractMethodArray = abstractMethods.eval.asInstanceOf[mutable.ArrayBuffer[String]]
            // methodMap is created
            val methodMap: mutable.Map[String, mutable.Set[SetOperations]] = mutable.Map[String, mutable.Set[SetOperations]]()
            // Got set of the methods
            val methodSet = mutable.Set[ClassDefinition](methods *)
            for (method <- methodSet) {
              // For each method from the above set, map of method name and its function is created.
              methodMap += method.eval.asInstanceOf[(String, mutable.Set[SetOperations])]
            }
            val interfaceMethods: mutable.Set[String] = mutable.Set[String]()
            val interfaceImpl = interfaces.eval.asInstanceOf[Seq[String]]
            for (interface <- interfaceImpl) {
              val interfaceDefinedMethods = interfaceMapping(interface)
              for (methodName <- interfaceDefinedMethods) {
                interfaceMethods += methodName
              }
            }
            for (methodInInterface <- interfaceMethods) {
              if (methodMap.contains(methodInInterface)) {
                interfaceMethods -= methodInInterface
              }
            }
            if (interfaceMethods.size > 0) {
              for( absMethod <- interfaceMethods ){
                abstractMethodArray += absMethod
              }
            }
            println(abstractMethodArray)
            if (abstractMethodArray.size >0) {
              // Mapped flag (which is for inheritance), fields, Constructor and methods to respective fields.
              val mapping = mutable.Map[String, Any]("flag" -> false, "fields" -> fieldList, "methods" -> methodMap,
                "abstractMethods" -> abstractMethodArray, "constructor" -> mutable.Set[ClassDefinition](constructor))
              // Use this map for classMapping.
              abstractClassMapping += (className -> mapping)
              (className, abstractClassMapping(className))
            }
            else{
              "Abstract class needs at least one abstract method."
            }


//          case InnerClassObject(outerObjectName, innerObjectName, innerClassName) =>
//            val outerClassName = objectMapping(outerObjectName)("class").asInstanceOf[String]
//            classMapping += classMapping(outerClassName)("innerClass").asInstanceOf[mutable.Map[String, Any]]
        }

    @main def runIt: Unit = {
      import ClassOperations.ClassDefinition.*
    }

}