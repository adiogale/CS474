import ClassOperations.ClassDefinition.{ClassDef, Constructor, Extend, ExtendAbstractClass, Fields, Implements, Method, NewObject, Object}

import scala.collection.mutable
import SetOperations.SetOperations.*
import SetOperations.SetOperations

import scala.collection.mutable.ArrayBuffer

object ExceptionOperations {
  private val exceptionMap: mutable.Map[String, String] = mutable.Map[String, String]()
  private val flag: mutable.Stack[Any] = mutable.Stack(0)
  private val exception: mutable.Stack[Any] = mutable.Stack()
  private val ifElseFlag: mutable.Stack[Boolean] = mutable.Stack[Boolean]()

  enum ExceptionClass:
    case ExceptionClassDef(classDef: String, reason: String)
    case TryCatch(tryExp: ExceptionOperations.ExceptionClass.Try, catchExp: ExceptionOperations.ExceptionClass.Catch)
    case Try(tryExp: Any*)
    case Catch(catchExp: Any*)
    case Throw(e: Any)
    case ThrowExpression(className: String, reason: String)
    case Condition(lhs: SetOperations, rhs: SetOperations)
    case IfElse(condition: Condition, thenExp: List[Any], elseExp: List[Any])
    case getReasonOfException(className: String)

    def eval: Any =
      this match {
        case getReasonOfException(className) =>
          exceptionMap(className)

        case Condition(lhs, rhs) =>
          val lhsVal = lhs.eval
          val rhsVal = rhs.eval
          if(lhsVal.equals(rhsVal)) {
            true
          }
          else{
            false
          }

        case IfElse(condition, thenExp, elseExp) =>
          if(condition.eval.asInstanceOf[Boolean]) {
            for(setOperation: Any <- thenExp) {
              if(flag.top == 0) {
                if (setOperation.isInstanceOf[SetOperations])
                  setOperation.asInstanceOf[SetOperations].eval
                else if (setOperation.isInstanceOf[ClassOperations.ClassDefinition])
                  setOperation.asInstanceOf[ClassOperations.ClassDefinition].eval
                else if (setOperation.isInstanceOf[ExceptionOperations.ExceptionClass])
                  setOperation.asInstanceOf[ExceptionOperations.ExceptionClass].eval
              }
            }
          }
          else{
            evaluate(elseExp)
          }

        case ExceptionClassDef(className, reason) =>
          exceptionMap += (className -> reason)

        case TryCatch(tryExp, catchExp) =>
          val p = tryExp.eval
          if(flag.top == 1) {
            val className = exception.pop().asInstanceOf[String]
            println("Exception occurred: "
              + exceptionMap(className).asInstanceOf[String])
            catchExp.eval
            flag.pop()
            exceptionMap(className).asInstanceOf[String]
          }
          else{
            p
          }

        case Try(tryExp*) =>
          for(op: Any <- tryExp) {
            if(flag.top == 0) {
              if (op.isInstanceOf[SetOperations])
                op.asInstanceOf[SetOperations].eval
              else if (op.isInstanceOf[ClassOperations.ClassDefinition])
                op.asInstanceOf[ClassOperations.ClassDefinition].eval
              else if (op.isInstanceOf[ExceptionOperations.ExceptionClass])
                op.asInstanceOf[ExceptionOperations.ExceptionClass].eval
            }
          }
          return true

        case Catch(catchExp*) =>
          evaluate(catchExp.toList)

        case ThrowExpression(className, reason) =>
          if(exceptionMap.contains(className)) {
            exceptionMap -= (className)
            exceptionMap += (className -> reason)
            exception.push(className)
          }
          else{
            throw Exception("Key not found: " + className)
          }
          flag.push(1)


//        case Throw(e) =>
//          if(e.isInstanceOf[String])
//            throw exceptionMap(e.asInstanceOf[String]).asInstanceOf[mutable.Map[String, Any]]("exception").asInstanceOf[Exception]
//          else if(e.isInstanceOf[Exception])
//            throw e.asInstanceOf[Exception]
//          else
//            throw Exception("Exception occurred due to unknown reason.")
      }
  private def evaluate(exp: List[Any]) : Unit =
    for(setOperation: Any <- exp) {
      if(setOperation.isInstanceOf[SetOperations])
        setOperation.asInstanceOf[SetOperations].eval
      else if(setOperation.isInstanceOf[ClassOperations.ClassDefinition])
        setOperation.asInstanceOf[ClassOperations.ClassDefinition].eval
      else if(setOperation.isInstanceOf[ExceptionOperations.ExceptionClass])
        setOperation.asInstanceOf[ExceptionOperations.ExceptionClass].eval
    }
  @main def runExceptions: Unit = {
    import ExceptionOperations.ExceptionClass.*
//    println(flag)
    ExceptionClassDef("c1", "Termination due to exhaustion").eval
    Assign("x", CreateSet(1, 2, 3)).eval
    Assign("y", CreateSet(1, 2, 3)).eval
    TryCatch(Try(Insert("x", ValueOf(98)), Insert("x", ValueOf(1002))),
      Catch(Assign("x", CreateSet(-1)),Assign("y", CreateSet(9,6,3)), Assign("z", CreateSet(8,9,0)))).eval
    
//     Object("o2")
  }
}