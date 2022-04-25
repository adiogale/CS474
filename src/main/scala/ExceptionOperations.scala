import ClassOperations.ClassDefinition.*

import scala.collection.mutable
import SetOperations.SetOperations.*
import ExceptionOperations.ExceptionClass.*
import SetOperations.SetOperations

import scala.collection.mutable.ArrayBuffer
//0 = false
//1 = true
object ExceptionOperations {
  // Exception mapping of className with reason of the exception.
  private val exceptionMap: mutable.Map[String, String] = mutable.Map[String, String]()
  // Flag to recognise if throw is called
  private val flag: mutable.Stack[Boolean] = mutable.Stack(false)
  // Reason of the Exception
  private val exception: mutable.Stack[Any] = mutable.Stack()
  // Flag to eliminate stand-alone use of Try or Catch
  private val TryCatchFlag: mutable.Stack[Boolean] = mutable.Stack[Boolean](false)
  private val TryStack: mutable.Stack[Integer] = mutable.Stack[Integer]()
  private type A = Condition[B] | List[Any]
  private type B = SetOperations | mutable.Set[Any]
  private val exceptionStack: mutable.Stack[Any] = mutable.Stack[Any]()

  enum ExceptionClass:
    case ExceptionClassDef(classDef: String, reason: String)
    case TryCatch(tryExp: ExceptionOperations.ExceptionClass.Try, catchExp: ExceptionOperations.ExceptionClass.Catch)
    case Try(tryExp: Any*)
    case Catch(catchExp: Any*)
    case ThrowExpression(className: String, reason: String)
    case Condition[B](lhs: B, rhs: B)
    case IfElse(condition: Condition[B], thenExp: List[Any], elseExp: List[Any])
    case getReasonOfException(className: String)

    private def innerEval[A](statements: A): List[Any] = {
      val retList = mutable.ArrayBuffer[Any]()
        for(setOperation: Any <- statements.asInstanceOf[List[Any]]) {
          // Check if Throw is called or not. If yes, do not evaluate the statements.
          try{
            if (!flag.top) {
              // Checks each type and evaluates accordingly.
              setOperation match {
                case operations: SetOperations => retList += operations.eval
                case _ => setOperation match {
                  case definition: ClassOperations.ClassDefinition => retList += definition.eval
                  case _ => setOperation match {
                    case clazz: ExceptionClass => retList += clazz.eval
                    case _ =>
                  }
                }
              }
            }
          }
          catch {
            case e:Throwable =>
              println("Exception in InnerEval: " + e.getMessage)
              retList += setOperation
          }
        }
        retList.toList
    }

    private def innerEvalB[B](statement: B): Any = {
      try{
          if (!flag.top) {
            // Checks each type and evaluates accordingly.
            statement match {
              case operations: SetOperations => return operations.eval
              case _ => statement match {
                case definition: ClassOperations.ClassDefinition => return definition.eval
                case _ => statement match {
                  case clazz: ExceptionClass => return clazz.eval
                  case _ => statement match {
                    case value: mutable.Set[Any] => return value
                    case _ =>
                  }
                }
              }
            }
          }
        }
        catch {
          case e: Throwable =>
            println("Exception: " + e.getMessage)
            statement
        }
    }

    def eval: Any =
      this match {
        // Gives reason of exception, pass its the className
        case getReasonOfException(className) =>
          exceptionMap(className)

        // Checks if left hand side and right hand side are equal and returns the boolean value of it
        case Condition(lhs, rhs) =>
          // evals the LHS
          val lhsVal = innerEvalB(lhs)
          // evals the RHS
          val rhsVal = innerEvalB(rhs)
          if (lhsVal.isInstanceOf[SetOperations] || rhsVal.isInstanceOf[SetOperations]){
            Condition(lhsVal, rhsVal)
//            if(lhsVal.isInstanceOf[SetOperations] && rhsVal.isInstanceOf[SetOperations])
//              Condition(lhsVal.asInstanceOf[SetOperations], rhsVal.asInstanceOf[SetOperations])
//            else if(lhsVal.isInstanceOf[SetOperations])
//              Condition(lhsVal.asInstanceOf[SetOperations], rhsVal)
          }
          else {
            //Checks equality, if yes, return true, else, false
            if (lhsVal.equals(rhsVal)) {
              true
            }
            else {
              false
            }
          }

        // If-else construct
        case IfElse(condition, thenExp, elseExp) =>
          val expCond = condition.eval
          expCond match {
            case bool: Boolean =>
              // If part of the construct
              if (bool) {
                // Loop over all the statements
                for (setOperation: Any <- thenExp) {
                  // Check if Throw is called or not. If yes, do not evaluate the statements.
                  if (!flag.top) {
                    // Checks each type and evaluates accordingly.
                    setOperation match {
                      case operations: SetOperations => operations.eval
                      case _ => setOperation match {
                        case definition: ClassOperations.ClassDefinition => definition.eval
                        case _ => setOperation match {
                          case clazz: ExceptionClass => clazz.eval
                          case _ =>
                        }
                      }
                    }
                  }
                }
              }
              // else part of the construct
              else {
                evaluate(elseExp)
              }
            case _ => expCond match {
              case condition1: ExceptionClass.Condition[B] =>
                val thenExpression: List[Any] = innerEval(thenExp)
                val elseExpression: List[Any] = innerEval(elseExp)
                IfElse(condition1, thenExpression, elseExpression)
              case _ =>
            }
          }

        // Exception class
        case ExceptionClassDef(className, reason) =>
          exceptionMap += (className -> reason)

        //Try-catch construct
        case TryCatch(tryExp, catchExp) =>
          TryStack.push(1)
          TryCatchFlag.push(true)
          // Evaluate the try part
          val p = tryExp.eval
          // If Throw has been called, evaluate the catch part
          if (flag.top) {
            // The className is obtained from exception.
            val className = exception.head.asInstanceOf[String]
            // Prints the reason of the exception anyway.
            println("Exception occurred: "
              + exceptionMap(className))
            //Evaluates the catch expression
            catchExp.eval
            // Resets the flag
            TryStack.pop()
            if(TryStack.isEmpty) {
              flag.pop()
            }
            TryCatchFlag.pop()
            // Returns the reason
            exceptionMap(className)
          }
          // If Throw is not called, return the try part
          else {
            TryStack.pop()
            TryCatchFlag.pop()
            p
          }

        // Try part of the structure
        case Try(tryExp*) =>
          if (TryCatchFlag.top) {
            // Loop over all the statements
            for (op: Any <- tryExp) {
              // Check if the Throw is called, do not evaluate the statements.
              if (!flag.top) {
                // Evaluate according to the types.
                op match {
                  case operations: SetOperations => operations.eval
                  case _ => op match {
                    case definition: ClassOperations.ClassDefinition => definition.eval
                    case _ => op match {
                      case clazz: ExceptionClass => clazz.eval
                      case _ =>
                    }
                  }
                }
              }
            }
          }
          true

        // catch part of the catch
        case Catch(catchExp*) =>
          if (TryCatchFlag.top) {
            evaluate(catchExp.toList)
          }

        // Throw expression
        case ThrowExpression(className, reason) =>
          // If the exception class is already defined, then replace the reason.
          if(exceptionMap.contains(className)) {
            exceptionMap -= className
            exceptionMap += (className -> reason)
            exception.push(className)
          }
          // If class is not present, Throw key not found exception.
          else{
            throw Exception("Key not found: " + className)
          }
          flag.push(true)


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
      setOperation match {
        case operations: SetOperations => operations.eval
        case _ => setOperation match {
          case definition: ClassOperations.ClassDefinition => definition.eval
          case _ => setOperation match {
            case clazz: ExceptionClass => clazz.eval
            case _ =>
          }
        }
      }
    }
  @main def runExceptions(): Unit = {
    import ExceptionOperations.ExceptionClass.*
    Assign("x", CreateSet(1,2,3)).eval
    Assign("y", CreateSet(1,2,3)).eval
    println(Condition(Variable("x"), Variable("y")).eval)
    Assign("i", CreateSet(3,6,9)).eval
    val exp = IfElse(Condition(Variable("i"), Variable("j")),
      List[Any](Insert("j", ValueOf(10002)), Insert("i", ValueOf(20002))),
      List[Any](Insert("j", ValueOf(4)))).eval
    println(exp)
  }
}