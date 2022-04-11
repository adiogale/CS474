import ClassOperations.ClassDefinition.{ClassDef, Constructor, Extend, ExtendAbstractClass, Fields, Implements, Method, NewObject, Object}

import scala.collection.mutable
import SetOperations.SetOperations.*
import SetOperations.SetOperations

import scala.collection.mutable.ArrayBuffer

object ExceptionOperations {
  // Exception mapping of className with reason of the exception.
  private val exceptionMap: mutable.Map[String, String] = mutable.Map[String, String]()
  // Flag to recognise if throw is called
  private val flag: mutable.Stack[Any] = mutable.Stack(0)
  // Reason of the Exception
  private val exception: mutable.Stack[Any] = mutable.Stack()

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
        // Gives reason of exception, pass its the className
        case getReasonOfException(className) =>
          exceptionMap(className)
        // Checks if left hand side and right hand side are equal and returns the boolean value of it
        case Condition(lhs, rhs) =>
          // evals the LHS
          val lhsVal = lhs.eval
          // evals the RHS
          val rhsVal = rhs.eval
          //Checks equality, if yes, return true, else, false
          if(lhsVal.equals(rhsVal)) {
            true
          }
          else{
            false
          }
        // If-else construct
        case IfElse(condition, thenExp, elseExp) =>
          // If part of the construct
          if(condition.eval.asInstanceOf[Boolean]) {
            // Loop over all the statements
            for(setOperation: Any <- thenExp) {
              // Check if Throw is called or not. If yes, do not evaluate the statements.
              if(flag.top == 0) {
                // Checks each type and evaluates accordingly.
                if (setOperation.isInstanceOf[SetOperations])
                  setOperation.asInstanceOf[SetOperations].eval
                else if (setOperation.isInstanceOf[ClassOperations.ClassDefinition])
                  setOperation.asInstanceOf[ClassOperations.ClassDefinition].eval
                else if (setOperation.isInstanceOf[ExceptionOperations.ExceptionClass])
                  setOperation.asInstanceOf[ExceptionOperations.ExceptionClass].eval
              }
            }
          }
          // else part of the construct
          else{
            evaluate(elseExp)
          }

        // Exception class
        case ExceptionClassDef(className, reason) =>
          exceptionMap += (className -> reason)

        //Try-catch construct
        case TryCatch(tryExp, catchExp) =>
          // Evaluate the try part
          val p = tryExp.eval
          // If Throw has been called, evaluate the catch part
          if(flag.top == 1) {
            // The className is obtained from exception.
            val className = exception.pop().asInstanceOf[String]
            // Prints the reason of the exception anyway.
            println("Exception occurred: "
              + exceptionMap(className).asInstanceOf[String])
            //Evaluates the catch expression
            catchExp.eval
            // Resets the flag
            flag.pop()
            // Returns the reason
            exceptionMap(className).asInstanceOf[String]
          }
          // If Throw is not called, return the try part
          else{
            p
          }

        // Try part of the structure
        case Try(tryExp*) =>
          // Loop over all the statements
          for(op: Any <- tryExp) {
            // Check if the Throw is called, do not evaluate the statements.
            if(flag.top == 0) {
              // Evaluate according to the types.
              if (op.isInstanceOf[SetOperations])
                op.asInstanceOf[SetOperations].eval
              else if (op.isInstanceOf[ClassOperations.ClassDefinition])
                op.asInstanceOf[ClassOperations.ClassDefinition].eval
              else if (op.isInstanceOf[ExceptionOperations.ExceptionClass])
                op.asInstanceOf[ExceptionOperations.ExceptionClass].eval
            }
          }
          return true

        // catch part of the catch
        case Catch(catchExp*) =>
          evaluate(catchExp.toList)

        // Throw expression
        case ThrowExpression(className, reason) =>
          // If the exception class is already defined, then replace the reason.
          if(exceptionMap.contains(className)) {
            exceptionMap -= (className)
            exceptionMap += (className -> reason)
            exception.push(className)
          }
          // If class is not present, Throw key not found exception.
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
  // This evaluates the statements by looping over the list
  private def evaluate(exp: List[Any]) : Unit =
    for(setOperation: Any <- exp) {
      // evaluate according to the type.
      if(setOperation.isInstanceOf[SetOperations])
        setOperation.asInstanceOf[SetOperations].eval
      else if(setOperation.isInstanceOf[ClassOperations.ClassDefinition])
        setOperation.asInstanceOf[ClassOperations.ClassDefinition].eval
      else if(setOperation.isInstanceOf[ExceptionOperations.ExceptionClass])
        setOperation.asInstanceOf[ExceptionOperations.ExceptionClass].eval
    }
  @main def runExceptions: Unit = {
    import ExceptionOperations.ExceptionClass.*
  }
}