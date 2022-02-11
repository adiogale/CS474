import scala.collection.mutable
import scala.collection.mutable.Set
import scala.collection.mutable.Map


object SetOperations {
  private val bindings: mutable.Map[String, Any] = mutable.Map[String, Any]("x" -> mutable.Set(3, 4))
  private val macroMap: mutable.Map[String, SetOperations] = mutable.Map[String, SetOperations]()

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

    def eval: Any =
      this match
        case ValueOf(varName) => varName

        case Variable(varName) =>
          if (bindings.contains(varName))
            bindings(varName.asInstanceOf[String])
          else 0

        case Assign(varName, element) =>
          bindings += (varName -> element.eval)
          bindings(varName)

        case CreateSet(elements*) =>
          val new_bindings = mutable.Set[Any](elements *)
          new_bindings

        case Insert(varName, element) =>
          if (bindings.contains(varName))
            val new_bindings = bindings(varName).asInstanceOf[mutable.Set[Any]] + element.eval.asInstanceOf[Any]
            bindings += (varName -> new_bindings)
            new_bindings
          else
            val new_bindings = mutable.Set(element.eval)
            bindings += (varName -> new_bindings)
            new_bindings

        case Union(setName1, setName2) =>
          setName1.eval.asInstanceOf[mutable.Set[Any]].union(setName2.eval.asInstanceOf[mutable.Set[Any]])

        case Intersection(setName1, setName2) =>
          setName1.eval.asInstanceOf[mutable.Set[Any]].intersect(setName2.eval.asInstanceOf[mutable.Set[Any]])

        case Difference(setName1, setName2) =>
          setName1.eval.asInstanceOf[mutable.Set[Any]].diff(setName2.eval.asInstanceOf[mutable.Set[Any]])

        case Symmetric_difference(setName1, setName2) =>
          (setName1.eval.asInstanceOf[mutable.Set[Any]].diff(setName2.eval.asInstanceOf[mutable.Set[Any]])).
            union(setName2.eval.asInstanceOf[mutable.Set[Any]].diff(setName1.eval.asInstanceOf[mutable.Set[Any]]))

        case Product(setName1, setName2) =>
          val new_binding = mutable.Set[Any]()
          for (i <- setName1.eval.asInstanceOf[mutable.Set[Any]])
            for (j <- setName2.eval.asInstanceOf[mutable.Set[Any]])
              new_binding += ((i, j))
          new_binding

        case Delete(varName, element) =>
          bindings(varName).asInstanceOf[mutable.Set[Any]] -= element.eval.asInstanceOf[Any]

        case CheckElement(varName, element) =>
          val tempVal = varName.eval
          if (tempVal.isInstanceOf[mutable.Set[Any]])
            if (tempVal.asInstanceOf[mutable.Set[Any]].contains(element.eval))
              true
            else false
          else
            "Variable Name is not a set"

        case SetMacro(macroName, macroValue) =>
          macroMap += (macroName -> macroValue)

        case GetMacro(macroName) =>
          macroMap(macroName).eval

  @main def main(): Unit =
    import SetOperations.*
    Assign("x", CreateSet(1,2)).eval
    var varName = 9
    SetMacro("insert", Insert("x", ValueOf(varName))).eval
    varName = 10
    Assign("r", GetMacro("insert")).eval
    print(Variable("r").eval)
//    Variable("r").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (1,2,10)
}