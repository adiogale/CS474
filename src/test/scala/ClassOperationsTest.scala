import ClassOperations.ClassDefinition.*
import SetOperations.SetOperations.{Assign, CreateSet, Insert, ValueOf, Variable}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

class ClassOperationsTest extends AnyFlatSpec with Matchers {
  behavior of "create a class"
  it should "create class and its object with given names" in {
    ClassDef("c1", Fields("x", "y"), Constructor(("c", CreateSet(1, 2)), ("y", ValueOf(2)), ("x", ValueOf(1, 3, 4, 2))), Implements(), ExtendAbstractClass(),
      Method("m1", Assign("x", CreateSet(1, 2, 3))), Method("m2", Insert("x", ValueOf(9)))).eval
    NewObject("o1", "c1").eval
    Object("o1").eval.asInstanceOf[mutable.Map[String, Any]]("class").asInstanceOf[String] shouldEqual "c1"
  }

  behavior of "call a method"
  it should "be able to call a method" in {
    ClassDef("c1", Fields("x", "y"), Constructor(("c", CreateSet(1, 2)), ("y", ValueOf(2)), ("x", ValueOf(1, 3, 4, 2))), Implements(), ExtendAbstractClass(),
      Method("m1", Assign("x", CreateSet(1, 2, 3))), Method("m2", Insert("x", ValueOf(9)))).eval
    NewObject("o1", "c1").eval
    CallMethod("m1", "o1").eval
    Object("o1").eval.asInstanceOf[mutable.Map[String, Any]]("fields").asInstanceOf[mutable.Map[String,Any]]("x").asInstanceOf[mutable.Set[Any]] should contain allOf (1,2,3)
  }

  behavior of "Multiple Inheritance Not Allowed"
  it should "not allow for multiple inheritance to e implemented" in {
    ClassDef("c1", Fields("x", "y"), Constructor(("c", CreateSet(1, 2)), ("y", ValueOf(2)), ("x", ValueOf(1, 3, 4, 2))), Implements(), ExtendAbstractClass(),
      Method("m1", Assign("x", CreateSet(1, 2, 3))), Method("m2", Insert("x", ValueOf(9)))).eval
    ClassDef("c2", Fields("z", "y"), Constructor(("z", CreateSet(1, 2)), ("y", ValueOf(2))), Implements(), ExtendAbstractClass(),
      Method("m3", Assign("x", CreateSet(1, 2, 6))), Method("m2", Insert("x", ValueOf(8)))).eval
    ClassDef("c3", Fields("z", "w"), Constructor(("w", CreateSet(1, 4)), ("z", ValueOf(5))), Implements(), ExtendAbstractClass(),
      Method("m3", Assign("x", CreateSet(1, 2, 6))), Method("m2", Insert("x", ValueOf(8)))).eval
    Extend("c2", "c1").eval
    Extend("c2", "c3").eval shouldEqual "Can not extend multiple classes"
  }

  behavior of "Inheritance part 1"
  it should "be able to call methods from parent class" in {
    ClassDef("c1", Fields("x", "y"), Constructor(("c", CreateSet(1, 2)), ("y", ValueOf(2)), ("x", ValueOf(1, 3, 4, 2))), Implements(), ExtendAbstractClass(),
      Method("m1", Assign("x", CreateSet(1, 2, 3))), Method("m2", Insert("x", ValueOf(9)))).eval
    ClassDef("c2", Fields("z", "y"), Constructor(("z", CreateSet(1, 2)), ("y", ValueOf(2))), Implements(), ExtendAbstractClass(),
      Method("m3", Assign("x", CreateSet(1, 2, 6))), Method("m2", Insert("x", ValueOf(8)))).eval
    Extend("c2", "c1").eval
    NewObject("o1", "c1").eval
    CallMethod("m1", "o1").eval
    Object("o1").eval.asInstanceOf[mutable.Map[String, Any]]("fields").asInstanceOf[mutable.Map[String,Any]]("x").asInstanceOf[mutable.Set[Any]] should contain allOf (1,2,3)
  }

  behavior of "Inheritance part 2"
  it should "be able to call same methods from parent class but should call from its own class (override)" in {
    ClassDef("c1", Fields("x", "y"), Constructor(("c", CreateSet(1, 2)), ("y", ValueOf(2)), ("x", ValueOf(1, 3, 4, 2))), Implements(), ExtendAbstractClass(),
      Method("m1", Assign("x", CreateSet(1, 2, 3))), Method("m2", Insert("x", ValueOf(9)))).eval
    ClassDef("c2", Fields("z", "y"), Constructor(("z", CreateSet(1, 2)), ("y", ValueOf(2))), Implements(), ExtendAbstractClass(),
      Method("m1", Assign("x", CreateSet(1, 2, 6))), Method("m3", Insert("x", ValueOf(8)))).eval
    Extend("c2", "c1").eval
    NewObject("o1", "c1").eval
    CallMethod("m1", "o1").eval
    Object("o1").eval.asInstanceOf[mutable.Map[String, Any]]("fields").asInstanceOf[mutable.Map[String,Any]]("x").asInstanceOf[mutable.Set[Any]] should contain allOf (1,2,6)
  }
}
