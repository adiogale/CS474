import scala.collection.mutable
import scala.collection.mutable.Set
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer


object SetOperations {
  private val bindings: mutable.Map[String, Any] = mutable.Map[String, Any]("x" -> mutable.Set(3, 4))
  private val macroMap: mutable.Map[String, SetOperations] = mutable.Map[String, SetOperations]()
  private val scopeBindings: mutable.Map[String, mutable.Map[String, Any]] = mutable.Map[String, mutable.Map[String, Any]]("global" -> bindings)
  // Default scope is "global".
  private val curScope: ListBuffer[String] = ListBuffer[String]("global")

  enum SetOperations:
    case ValueOf(varName: Any)
    case Variable(varName: String)
    case Assign(varName: String, element: SetOperations)
    case Insert(varName: String, element: SetOperations)
    case CreateSet(elements: Any*)
    case Delete(varName: String, element: SetOperations)
    case Union(setName1: SetOperations, setName2: SetOperations)
    case Intersection(setName1: SetOperations, setName2: SetOperations)
    case Difference(setName1: SetOperations, setName2: SetOperations)
    case Symmetric_difference(setName1: SetOperations, setName2: SetOperations)
    case Product(setName1: SetOperations, setName2: SetOperations)
    case CheckElement(setName: SetOperations, element: SetOperations)
    case SetMacro(macroName: String, macroValue: SetOperations)
    case GetMacro(macroName:String)
    case Scope(scopeName: String, varName: String, element: SetOperations)
    case getSetFromScope(scopeName: String, setName: String)

    def eval: Any =
      this match
        case ValueOf(varName) => varName

        case Variable(varName) =>
          if (scopeBindings(curScope.head).contains(varName))
            scopeBindings(curScope.head)(varName.asInstanceOf[String])
          else 0

        case Assign(varName, element) =>
          scopeBindings(curScope.head) += (varName -> element.eval)
          scopeBindings(curScope.head)(varName)

        case CreateSet(elements*) =>
          val new_bindings = mutable.Set[Any](elements *)
          new_bindings

        case Insert(varName, element) =>
          // If binding is available for varName, add element to the set
          // Using curScope.head to get in whcich scope operation is being performed into.
          // See more in Scope.
          if (scopeBindings(curScope.head).contains(varName))
            val new_bindings = scopeBindings(curScope.head)(varName).asInstanceOf[mutable.Set[Any]] + element.eval.asInstanceOf[Any]
            scopeBindings(curScope.head) += (varName -> new_bindings)
            new_bindings
          // Else create a new set and add element to the set
          else
            val new_bindings = mutable.Set(element.eval)
            scopeBindings(curScope.head) += (varName -> new_bindings)
            new_bindings

        case Union(setName1, setName2) =>
          setName1.eval.asInstanceOf[mutable.Set[Any]].union(setName2.eval.asInstanceOf[mutable.Set[Any]])

        case Intersection(setName1, setName2) =>
          setName1.eval.asInstanceOf[mutable.Set[Any]].intersect(setName2.eval.asInstanceOf[mutable.Set[Any]])

        case Difference(setName1, setName2) =>
          setName1.eval.asInstanceOf[mutable.Set[Any]].diff(setName2.eval.asInstanceOf[mutable.Set[Any]])

        case Symmetric_difference(setName1, setName2) =>
          // = (A-B)U(B-A)
          (setName1.eval.asInstanceOf[mutable.Set[Any]].diff(setName2.eval.asInstanceOf[mutable.Set[Any]])).
            union(setName2.eval.asInstanceOf[mutable.Set[Any]].diff(setName1.eval.asInstanceOf[mutable.Set[Any]]))

        case Product(setName1, setName2) =>
          //Creates a set of tuples of all possible combinations of the sets
          val new_binding = mutable.Set[Any]()
          for (i <- setName1.eval.asInstanceOf[mutable.Set[Any]])
            for (j <- setName2.eval.asInstanceOf[mutable.Set[Any]])
              new_binding += ((i, j))
          new_binding

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
          macroMap(macroName).eval

        case getSetFromScope(scopeName, setName) =>
          //Returns set from given scope.
          scopeBindings(scopeName)(setName)

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
}