import SetOperations.SetOperations
//import _root_.SetOperations.SetOperations.Union

import scala.collection.mutable
import scala.collection.mutable.Set
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer


object SetOperations {
  private val bindings: mutable.Map[String, Any] = mutable.Map[String, Any]()
  private val macroMap: mutable.Map[String, SetOperations] = mutable.Map[String, SetOperations]()
  private val scopeBindings: mutable.Map[String, mutable.Map[String, Any]] = mutable.Map[String, mutable.Map[String, Any]]("global" -> bindings)
  // Default scope is "global".
  private val curScope: ListBuffer[String] = ListBuffer[String]("global")
  private val exceptionStack: mutable.Stack[Any] = mutable.Stack[Any]()
  type A = Either[SetOperations, mutable.Set[Any]]
  type B = Either[SetOperations, Any]

  enum SetOperations:
    case ValueOf(varName: Any)
    case Variable(varName: String)
    case Assign(varName: String, element: SetOperations)
    case Insert[B](varName: String, element: B)
    case CreateSet(elements: Any*)
    case Delete(varName: String, element: SetOperations)
    case Union[A](setName1: A, setName2: A)
    case Intersection[A](setName1: A, setName2: A)
    case Difference[A](setName1: A, setName2: A)
    case Symmetric_difference[A](setName1: A, setName2: A)
    case Product[A](setName1: A, setName2: A)
    case CheckElement(setName: SetOperations, element: SetOperations)
    case SetMacro(macroName: String, macroValue: SetOperations)
    case GetMacro(macroName:String)
    case Scope(scopeName: String, varName: String, element: SetOperations)
    case getSetFromScope(scopeName: String, setName: String)
    case getScope(scopeName: String)
    case Remove(varName: String)

    private def innerEval[A](statement: A): Any = { //SetOperations,nothing: Nothing
      try{
        statement.asInstanceOf[SetOperations].eval
      }
      catch {
        case e:Throwable =>
          exceptionStack.push(e.getMessage)
          statement
      }
    }

    def eval: Any =
      this match
        case ValueOf(varName) => varName

        case Variable(varName) =>
          if (scopeBindings(curScope.head).contains(varName))
            scopeBindings(curScope.head)(varName.asInstanceOf[String])
          else
            Variable(varName)

        case Assign(varName, element) =>
//          if (element.isInstanceOf[])
          scopeBindings(curScope.head) += (varName -> element.eval)
          scopeBindings(curScope.head)(varName)

        case CreateSet(elements*) =>
          val new_bindings = mutable.Set[Any](elements *)
          new_bindings

        case Insert(varName, element) =>
          // If binding is available for varName, add element to the set
          // Using curScope.head to get in which scope operation is being performed into.
          val newElement = mutable.Stack[Any]()
          if (element.isInstanceOf[SetOperations]) {
             newElement.push(element.asInstanceOf[SetOperations].eval)
          }
          else{
            newElement.push(element)
          }
          // See more in Scope.
          if (scopeBindings(curScope.head).contains(varName))
            val newsomething = newElement.pop()
            val varName1 = scopeBindings(curScope.head)(varName)
            val new_bindings = mutable.Stack[Any]()
            varName1 match {
              case tuple: Tuple => new_bindings.push(tuple.toList.toSet
                + newsomething)
              case value: mutable.Set[Any] => new_bindings.push(value.+(newsomething))
              case _ => new_bindings.push(varName1.asInstanceOf[mutable.Set[Any]]+newsomething)
            }
            scopeBindings(curScope.head) += (varName -> new_bindings.head)
            new_bindings.pop()
          // Else create a new set and add element to the set
          else
            Insert(varName, newElement.pop())

        case Union(setName1, setName2) =>
          val set1 = innerEval(setName1)
          val set2 = innerEval(setName2)
          set1 match {
            case value: mutable.Set[Any] if set2.isInstanceOf[mutable.Set[Any]] =>
              val retSet = value.union(set2.asInstanceOf[mutable.Set[Any]])
              retSet
            case operations: SetOperations if set2.isInstanceOf[SetOperations] => // || set2.isInstanceOf[SetOperations]
              Union(operations, set2.asInstanceOf[SetOperations]) //.asInstanceOf[Either[SetOperations, mutable.Set[Any]]]
            case operations: SetOperations =>
              Union(operations, set2.asInstanceOf[mutable.Set[Any]])
            case _ => set2 match {
              case operations: SetOperations =>
                Union(set1.asInstanceOf[mutable.Set[Any]], operations)
              case _ =>
            }
          }

        case Intersection(setName1, setName2) =>
          val set1 = innerEval(setName1)
          val set2 = innerEval(setName2)
          set1 match {
            case value: mutable.Set[Any] if set2.isInstanceOf[mutable.Set[Any]] =>
              val retSet = value.intersect(set2.asInstanceOf[mutable.Set[Any]])
              retSet
            case operations: SetOperations if set2.isInstanceOf[SetOperations] => // || set2.isInstanceOf[SetOperations]
              Intersection(operations, set2.asInstanceOf[SetOperations]) //.asInstanceOf[Either[SetOperations, mutable.Set[Any]]]
            case operations: SetOperations =>
              Intersection(operations, set2.asInstanceOf[mutable.Set[Any]])
            case _ => set2 match {
              case operations: SetOperations =>
                Intersection(set1.asInstanceOf[mutable.Set[Any]], operations)
              case _ =>
            }
          }

        case Difference(setName1, setName2) =>
          val set1 = innerEval(setName1)
          val set2 = innerEval(setName2)
          set1 match {
            case value: mutable.Set[Any] if set2.isInstanceOf[mutable.Set[Any]] =>
              val retSet = value.diff(set2.asInstanceOf[mutable.Set[Any]])
              retSet
            case operations: SetOperations if set2.isInstanceOf[SetOperations] => // || set2.isInstanceOf[SetOperations]
              Difference(operations, set2.asInstanceOf[SetOperations]) //.asInstanceOf[Either[SetOperations, mutable.Set[Any]]]
            case operations: SetOperations =>
              Difference(operations, set2.asInstanceOf[mutable.Set[Any]])
            case _ => set2 match {
              case operations: SetOperations =>
                Difference(set1.asInstanceOf[mutable.Set[Any]], operations)
              case _ =>
            }
          }

        case Symmetric_difference(setName1, setName2) =>
          // = (A-B)U(B-A)

          val set1 = innerEval(setName1)
          val set2 = innerEval(setName2)
          set1 match {
            case value: mutable.Set[Any] if set2.isInstanceOf[mutable.Set[Any]] =>
              //            (setName1.eval.asInstanceOf[mutable.Set[Any]].diff(setName2.eval.asInstanceOf[mutable.Set[Any]])).
              //              union(setName2.eval.asInstanceOf[mutable.Set[Any]].diff(setName1.eval.asInstanceOf[mutable.Set[Any]]))
              val retSet = value.diff(set2.asInstanceOf[mutable.Set[Any]]).union(set2.asInstanceOf[mutable.Set[Any]].diff(value))
              retSet
            case operations: SetOperations if set2.isInstanceOf[SetOperations] => // || set2.isInstanceOf[SetOperations]
              Symmetric_difference(operations, set2.asInstanceOf[SetOperations]) //.asInstanceOf[Either[SetOperations, mutable.Set[Any]]]
            case operations: SetOperations =>
              Symmetric_difference(operations, set2.asInstanceOf[mutable.Set[Any]])
            case _ => set2 match {
              case operations: SetOperations =>
                Symmetric_difference(set1.asInstanceOf[mutable.Set[Any]], operations)
              case _ =>
            }
          }

        case Product(setName1, setName2) =>
          //Creates a set of tuples of all possible combinations of the sets
          val set1 = innerEval(setName1)
          val set2 = innerEval(setName2)
          set1 match {
            case value: mutable.Set[Any] if set2.isInstanceOf[mutable.Set[Any]] =>
              val new_binding = mutable.Set[Any]()
              for (i <- value)
                for (j <- set2.asInstanceOf[mutable.Set[Any]])
                  new_binding += ((i, j))
              new_binding
            case operations: SetOperations if set2.isInstanceOf[SetOperations] => // || set2.isInstanceOf[SetOperations]
              Product(operations, set2.asInstanceOf[SetOperations]) //.asInstanceOf[Either[SetOperations, mutable.Set[Any]]]
            case operations: SetOperations =>
              Product(operations, set2.asInstanceOf[mutable.Set[Any]])
            case _ => set2 match {
              case operations: SetOperations =>
                Product(set1.asInstanceOf[mutable.Set[Any]], operations)
              case _ =>
            }
          }


        case Delete(varName, element) =>
          scopeBindings(curScope.head)(varName).asInstanceOf[mutable.Set[Any]] -= element.eval.asInstanceOf[Any]
          scopeBindings(curScope.head)(varName)

        case CheckElement(varName, element) =>
          // Returns True if element present in varName. Else False.
          val tempVal = varName.eval
          if (tempVal.isInstanceOf[mutable.Set[Any]])
            if (tempVal.asInstanceOf[mutable.Set[Any]].contains(element.eval))
              true
            else false
          // If Variable name not present, returns "Variable Name is not a set"
          else
            "Variable Name is not a set"

        case SetMacro(macroName, macroValue) =>
          macroMap += (macroName -> macroValue)

        case GetMacro(macroName) =>
          if(macroMap.contains(macroName))
            macroMap(macroName).eval
          else
            GetMacro(macroName)

        case getSetFromScope(scopeName, setName) =>
          //Returns set from given scope.
          scopeBindings(scopeName)(setName)

        case getScope(scopeName)=>
          scopeBindings(scopeName)

        case Remove(varName) =>
          bindings -= varName

        case Scope(scopeName, varName, element) =>
          // If scope is not yet present, create one.
          if (!scopeBindings.contains(scopeName)){
            scopeBindings += (scopeName -> mutable.Map[String, Any]())
          }
          // Default scope is "global", change it to given scopeName.
          curScope.remove(0)
          curScope += scopeName

          // Perform the action in the given scopeName. If in scopeName, varNAme is present, add to it.
          if (scopeBindings(scopeName).contains(varName)) {
            val new_binding = scopeBindings(scopeName)(varName).asInstanceOf[mutable.Set[Any]].union(element.eval.asInstanceOf[mutable.Set[Any]])
            scopeBindings(scopeName)(varName) = new_binding
          }
          //Else create new one.
          else {
            scopeBindings(scopeName) += (varName -> element.eval.asInstanceOf[Any])
            scopeBindings(scopeName)(varName).asInstanceOf[mutable.Set[Any]]
          }
          //Restore to default global scope.
          curScope.remove(0)
          curScope += "global"



  @main def rinIf(): Unit =
    import SetOperations.*
    Assign("x", CreateSet(1,2,3)).eval
    println("***************************")
    println("Union")
    println(Union(Variable("x"), Variable("y")).eval)
    val exp = Union(Variable("x"), Variable("y")).eval
    Assign("y", CreateSet(3,4,5)).eval
    println(exp.asInstanceOf[SetOperations].eval)

    println("***************************")
    println("Intersection")
    Remove("y").eval
    val expInter = Intersection(Variable("x"), Variable("y")).eval
    println(expInter.asInstanceOf[SetOperations].eval)
    Assign("y", CreateSet(2, 3,4,5)).eval
    println(expInter.asInstanceOf[SetOperations].eval)

    println("***************************")
    println("Difference")
    Remove("y").eval
    val expDiff = Difference(Variable("x"), Variable("y")).eval
    println(expDiff.asInstanceOf[SetOperations].eval)
    Assign("y", CreateSet(2, 3,4,5)).eval
    println(expDiff.asInstanceOf[SetOperations].eval)

    println("***************************")
    println("Symmetric_difference")
    Remove("y").eval
    val expSym = Symmetric_difference(Variable("x"), Variable("y")).eval
    println(expSym.asInstanceOf[SetOperations].eval)
    Assign("y", CreateSet(2, 3,4,5)).eval
    println(expSym.asInstanceOf[SetOperations].eval)

    println("***************************")
    println("Product")
    Remove("y").eval
    Delete("x", ValueOf(1))
    val expProd = Product(Variable("x"), Variable("y")).eval
    println(expProd.asInstanceOf[SetOperations].eval)
    Assign("y", CreateSet(2, 3)).eval
    println(expProd.asInstanceOf[SetOperations].eval)

    println("***************************")
    Remove("y").eval
    val expIn = Insert("y", ValueOf(6)).eval
    println(expIn.asInstanceOf[SetOperations].eval)
    Assign("y", CreateSet(2, 3)).eval
    println(expIn.asInstanceOf[SetOperations].eval)
}