import ClassOperations.ClassDefinition
import ClassOperations.ClassDefinition.{CallMethod, ClassDef, Constructor, ExtendAbstractClass, Fields, Implements, Method, NewObject, Object}
import ExceptionOperations.ExceptionClass.{Condition, IfElse}
import Optimizer.functionSetOp
import SetOperations.SetOperations.*
import SetOperations.SetOperations
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

class PartialEvalTest extends AnyFlatSpec with Matchers {
  behavior of "Union"
  it should "Partially evaluate union if either or both of the params are not yet bound" in {
    val exp = Union(Variable("x"), Variable("y")).eval
    exp should be (Union(Variable("x"), Variable("y")))
    Assign("x", CreateSet(1,2,3)).eval
    val expX = Union(Variable("x"), Variable("y")).eval
    expX should be (Union(mutable.HashSet(1,2,3), Variable("y")))
    Assign("y", CreateSet(3,4,5)).eval
    val expY = expX.asInstanceOf[SetOperations].eval
    expY should be (mutable.HashSet(1,2,3,4,5))
  }

  behavior of "Intersection"
  it should "Partially evaluate intersection if either or both of the params are not yet bound" in {
    val exp = Intersection(Variable("p"), Variable("q")).eval
    exp should be (Intersection(Variable("p"), Variable("q")))
    Assign("p", CreateSet(1,2,3)).eval
    val expX = Intersection(Variable("p"), Variable("q")).eval
    expX should be (Intersection(mutable.HashSet(1,2,3), Variable("q")))
    Assign("q", CreateSet(3,4,5)).eval
    val expY = expX.asInstanceOf[SetOperations].eval
    expY should be (mutable.HashSet(3))
  }

  behavior of "Difference"
  it should "Partially evaluate difference if either or both of the params are not yet bound" in {
    val exp = Difference(Variable("a"), Variable("b")).eval
    exp should be (Difference(Variable("a"), Variable("b")))
    Assign("a", CreateSet(1,2,3)).eval
    val expX = Difference(Variable("a"), Variable("b")).eval
    expX should be (Difference(mutable.HashSet(1,2,3), Variable("b")))
    Assign("b", CreateSet(3,4,5)).eval
    val expY = expX.asInstanceOf[SetOperations].eval
    expY should be (mutable.HashSet(1, 2))
  }

  behavior of "Symmetric difference"
  it should "Partially evaluate symmetric difference if either or both of the params are not yet bound" in {
    val exp = Symmetric_difference(Variable("e"), Variable("f")).eval
    exp should be (Symmetric_difference(Variable("e"), Variable("f")))
    Assign("e", CreateSet(1,2,3)).eval
    val expX = Symmetric_difference(Variable("e"), Variable("f")).eval
    expX should be (Symmetric_difference(mutable.HashSet(1,2,3), Variable("f")))
    Assign("f", CreateSet(3,4,5)).eval
    val expY = expX.asInstanceOf[SetOperations].eval
    expY should be (mutable.HashSet(1, 2, 4, 5))
  }

  behavior of "Insert"
  it should "Partially evaluate insert if the variable is not yet bound" in {
    val expIn = Insert("w", ValueOf(6)).eval
    Assign("w", CreateSet(2, 3)).eval
    expIn should be (Insert("w", 6))
    val expInW = expIn.asInstanceOf[SetOperations].eval
    expInW should be (mutable.HashSet(2,3,6))
  }

  behavior of "If-else"
  it should "Partially evaluate if-else" in {
    Assign("i", CreateSet(3,6,9)).eval
    val exp = IfElse(Condition(Variable("i"), Variable("j")),
      List[Any](Insert("j", ValueOf(10002)), Insert("i", ValueOf(20002))),
      List[Any](Insert("j", ValueOf(4)))).eval
    exp should be (IfElse(Condition(mutable.HashSet(9, 3, 6),Variable("j")),List(Insert("j",10002),
      mutable.HashSet(9, 20002, 3, 6)),List(Insert("j",4))))
  }

  behavior of "NewObject"
  it should "partially evaluate the new object if class name is not found" in {
    val exp = NewObject("o1", "c1").eval
    exp should be (NewObject("o1", "c1"))
    ClassDef("c1", Fields("x", "y"), Constructor(("c", CreateSet(1, 2)), ("y", ValueOf(2)), ("x", ValueOf(1, 3, 4, 2))), Implements(), ExtendAbstractClass(),
      Method("m1", Assign("x", CreateSet(1, 2, 3))), Method("m2", Insert("x", ValueOf(9)))).eval
    val expP = exp.asInstanceOf[ClassDefinition].eval
    expP should be (mutable.HashMap("fields" -> mutable.HashMap("x" -> (1,3,4,2), "y" -> 2), "class" -> "c1"))
  }

  behavior of "Object"
  it should "partially evaluate the retrieval of object if object name is not found" in {
    val expOb1 = Object("o2").eval
    expOb1 should be (Object("o2"))
  }
}
