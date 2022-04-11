import ClassOperations.ClassDefinition.*
import ExceptionOperations.ExceptionClass.{Catch, Condition, ExceptionClassDef, IfElse, ThrowExpression, Try, TryCatch, getReasonOfException}
import SetOperations.SetOperations.*
import SetOperations.SetOperations
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

class ExceptionOperationTest extends AnyFlatSpec with Matchers {
  behavior of "Condition"
  it should "check if 2 set operations are equal after the evaluation. Should return true if equal else false." in {
    Assign("x", CreateSet(1, 2, 3)).eval
    Insert("x", ValueOf(4)).eval
    Assign("y", CreateSet(1, 2, 3)).eval
    Insert("y", ValueOf(6)).eval
    Condition(Variable("x"), Variable("y")).eval shouldBe false
    Insert("x", ValueOf(6)).eval
    Insert("y", ValueOf(4)).eval
    Condition(Variable("x"), Variable("y")).eval shouldBe true
  }

  behavior of "If-then"
  it should "check if condition is true, if yes then evaluate thenExp" in {
    Assign("x", CreateSet(1, 2, 3)).eval
    Assign("y", CreateSet(1, 2, 3)).eval
    IfElse(Condition(Variable("x"), Variable("y")),
      List[Any](Insert("y", ValueOf(10002)), Insert("x", ValueOf(20002))),
      List[Any](Insert("y", ValueOf(4)), Insert("x", ValueOf(6)))).eval
    Condition(Variable("x"), Variable("y")).eval shouldBe false
    Variable("x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf(1, 2, 3, 20002)
    Variable("y").eval.asInstanceOf[mutable.Set[Any]] should contain allOf(1, 2, 3, 10002)
  }

  behavior of "If-else"
  it should "check if condition is false, then evaluate elseExp" in {
    Assign("x", CreateSet(1, 2, 3)).eval
    Insert("x", ValueOf(4)).eval
    Assign("y", CreateSet(1, 2, 3)).eval
    Insert("y", ValueOf(6)).eval
    IfElse(Condition(Variable("x"), Variable("y")),
      List[Any](Insert("y", ValueOf(1002)), Insert("x", ValueOf(20002))),
      List[Any](Insert("y", ValueOf(4)), Insert("x", ValueOf(6)))).eval
    Condition(Variable("x"), Variable("y")).eval shouldBe true
    Variable("x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf(1, 2, 3, 4, 6)
    Variable("y").eval.asInstanceOf[mutable.Set[Any]] should contain allOf(1, 2, 3, 4, 6)
  }

  behavior of "Nested If-else"
  it should "check if condition is true, if yes then evaluate thenExp else evaluate elseExp" in {
    Assign("x", CreateSet(1, 2, 3)).eval
    Assign("y", CreateSet(1, 2, 3)).eval
    IfElse(Condition(Variable("x"), Variable("y")),
      List[Any](IfElse(Condition(CreateSet(1,2,3), CreateSet(1,2,3)),
        List[Any](Assign("p", CreateSet(1,2,3,1002))), List[Any]())),
      List[Any](Insert("y", ValueOf(4)), Insert("x", ValueOf(6)))).eval
    Variable("p").eval.asInstanceOf[mutable.Set[Any]] should contain allOf(1,2,3,1002)
  }

  behavior of "ExceptionClassDef"
  it should "create an exception class" in {
    ExceptionClassDef("c1", "Termination due to exhaustion").eval
    getReasonOfException("c1").eval.asInstanceOf[String] shouldBe "Termination due to exhaustion"
  }

  behavior of "TryCatch - simple try without catch"
  it should "If no exception occurs in try block, catch should not be evaluated and all statements of try should be evaluated" in {
    ExceptionClassDef("c1", "Termination due to exhaustion").eval
    Assign("x", CreateSet(1, 2, 3)).eval
    Assign("y", CreateSet(1, 2, 3)).eval
    TryCatch(Try(Insert("x", ValueOf(98)), Insert("x", ValueOf(1002))),
      Catch(Assign("x", CreateSet(-1)),Assign("y", CreateSet(9,6,3)), Assign("z", CreateSet(8,9,0)))).eval
    Variable("x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf(1,2,3,98,1002)
  }

  behavior of "TryCatch - try with catch"
  it should "If exception occurs in try block, catch should be evaluated" in {
    ExceptionClassDef("c1", "Termination due to exhaustion").eval
    Assign("x", CreateSet(1, 2, 3)).eval
    Assign("y", CreateSet(1, 2, 3)).eval
    TryCatch(Try(ThrowExpression("c1", "avada kedavra!")),
      Catch(Assign("x", CreateSet(-1)),Assign("y", CreateSet(9,6,3)), Assign("z", CreateSet(8,9,0)))).eval.asInstanceOf[String] shouldBe "avada kedavra!"
    Variable("x").eval.asInstanceOf[mutable.Set[Any]] should contain (-1)
  }

  behavior of "TryCatch - try with throw in between 2 statements"
  it should "If throw is called in between 2 statements, latter statement should not be evaluated and catch should be evaluated" in {
    Assign("x", CreateSet(1, 2, 3)).eval
    Assign("y", CreateSet(1, 2, 3)).eval
    TryCatch(Try(Insert("x", ValueOf(100)), ThrowExpression("c1", "avada kedavra!"), Insert("x", ValueOf(-100))),
      Catch(Insert("x", ValueOf(-1)), Insert("y", ValueOf(-100)))).eval.asInstanceOf[String] shouldBe "avada kedavra!"
    Variable("x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf(1,2,3,100,-1)
    Variable("y").eval.asInstanceOf[mutable.Set[Any]] should contain allOf(1,2,3,-100)
  }

  behavior of "Nested try-catch"
  it should "call catch of all outer try-catch blocks if exception occurs in inner try-catch block" in {
    ExceptionClassDef("c1", "Termination due to exhaustion").eval
    Assign("x", CreateSet(1,2,3)).eval
    var Exp = TryCatch(
      Try(
        Insert("x", ValueOf(4)),
        TryCatch(
          Try(
            Insert("x", ValueOf(5)),
            ThrowExpression("c1", "Inner exception"),
            Insert("x", ValueOf(6))
          ),
          Catch(Insert("x", ValueOf(98)))
        ),
        Insert("x", ValueOf(97))
      ),
      Catch(Insert("x", ValueOf(99))))
    println(Exp)
    Exp.eval
    Variable("x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf(1,2,3,98,99)
  }
}
