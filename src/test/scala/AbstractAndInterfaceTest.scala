import ClassOperations.ClassDefinition.*
import SetOperations.SetOperations.{Assign, CreateSet, Insert, ValueOf, Variable}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

class AbstractAndInterfaceTest extends AnyFlatSpec with Matchers {
  behavior of "Abstract class"
  it should "not be able to instantiate abstract class" in {
    AbstractClass("abs1", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2)), ("a", CreateSet(3,4))), Implements(), AbstractMethods("m3"),
      Method("m1", Insert("a", ValueOf(1))), Method("m2", Assign("b", CreateSet(8,9)))).eval
    NewObject("o1", "abs1").eval shouldEqual "Can not initialize abstract class"
  }

  behavior of "Interface"
  it should "not able to be instantiated" in {
    Interface("i2", AbstractMethods("m2", "m3")).eval
    NewObject("o1", "i2").eval shouldEqual "Can not initialize interface"
  }

  behavior of "Extending abstract class"
  it should "concrete class should implement all the abstract methods from abstract class" in {
    AbstractClass("abs1", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2)), ("a", CreateSet(3,4))), Implements(), AbstractMethods("m3"),
      Method("m1", Insert("a", ValueOf(1))), Method("m2", Assign("b", CreateSet(8,9)))).eval
    ClassDef("c3", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2,3)), ("a", CreateSet(5,6))), Implements(), ExtendAbstractClass("abs1"),
      Method("m1", Insert("a", ValueOf(1))), Method("m2", Insert("c", ValueOf(4)))).eval shouldEqual
      "Implement all methods from all the interfaces and abstract methods from extended abstract class. Methods not implemented: HashSet(m3)"
  }

  behavior of "Implementing an interface"
  it should "concrete class should implement all the methods from interface" in {
    Interface("i1", AbstractMethods("m1", "m2", "m3")).eval
    ClassDef("c7", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2,3)), ("a", CreateSet(5,6))), Implements("i1"), ExtendAbstractClass(),
      Method("m1", Insert("a", ValueOf(1))), Method("m2", Insert("c", ValueOf(4)))).eval shouldEqual
      "Implement all methods from all the interfaces and abstract methods from extended abstract class. Methods not implemented: HashSet(m3)"
  }

  behavior of "Implementing an interface and extending an abstract class"
  it should "concrete class should implement all the methods from interface and all the methods from abstract class" in {
    Interface("i1", AbstractMethods("m1", "m2")).eval
    AbstractClass("abs1", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2)), ("a", CreateSet(3,4))), Implements(), AbstractMethods("m3"),
      Method("m1", Insert("a", ValueOf(1))), Method("m2", Assign("b", CreateSet(8,9)))).eval
    ClassDef("c7", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2,3)), ("a", CreateSet(5,6))), Implements("i1"), ExtendAbstractClass("abs1"),
      Method("m1", Insert("a", ValueOf(1)))).eval shouldEqual
      "Implement all methods from all the interfaces and abstract methods from extended abstract class. Methods not implemented: HashSet(m2, m3)"
    ClassDef("c7", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2,3)), ("a", CreateSet(5,6))), Implements("i1"), ExtendAbstractClass("abs1"),
      Method("m1", Insert("a", ValueOf(1))), Method("m2", Insert("c", ValueOf(4)))).eval shouldEqual
      "Implement all methods from all the interfaces and abstract methods from extended abstract class. Methods not implemented: HashSet(m3)"
  }

  behavior of "Extending an interface by an interface"
  it should "have all the methods from previous interface and current interface and one interface must be able to extend multiple interfaces" in {
    Interface("i1", AbstractMethods("m1", "m2")).eval
    Interface("i2", AbstractMethods("m2", "m3")).eval
    Interface("i3", AbstractMethods("m4", "m5")).eval
    ExtendInterface("i3", "i2").eval
    ExtendInterface("i3", "i1").eval
    GetInterface("i3").eval.asInstanceOf[mutable.ArrayBuffer[String]] should contain allOf ("m1", "m2", "m3", "m4", "m5")
  }

  behavior of "Extending multiple interfaces by a class"
  it should "class should have all the methods from all the interfaces" in {
    Interface("i1", AbstractMethods("m1", "m2")).eval
    Interface("i2", AbstractMethods("m2", "m3")).eval
    ClassDef("c4", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2,3)), ("a", CreateSet(5,6))), Implements("i1", "i2"), ExtendAbstractClass(),
      Method("m1", Insert("a", ValueOf(1))), Method("m3", Assign("b", CreateSet(5,6,7,8))), Method("m2", Insert("c", ValueOf(4)))).eval
    NewObject("o1", "c4").eval
    CallMethod("m3", "o1").eval
    Object("o1").eval.asInstanceOf[mutable.Map[String, Any]]("fields").asInstanceOf[mutable.Map[String,Any]]("b").asInstanceOf[mutable.Set[Any]] should contain allOf (5,6,7,8)
  }

  behavior of "Extending a class by an interface"
  it should "show error message" in {
    Interface("i1", AbstractMethods("m1", "m2")).eval
    ClassDef("c7", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2,3)), ("a", CreateSet(5,6))), Implements(), ExtendAbstractClass(),
      Method("m1", Insert("a", ValueOf(1)))).eval
    ExtendInterface("i1", "c7").eval shouldEqual "Can not extend a class"
  }
  behavior of "Extending abstract class by an abstract class"
  it should "have all abstract methods in extended abstract class" in {
    AbstractClass("abs1", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2)), ("a", CreateSet(3,4))), Implements(), AbstractMethods("m3"),
      Method("m1", Insert("a", ValueOf(1))), Method("m2", Assign("b", CreateSet(8,9)))).eval
    AbstractClass("abs2", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2)), ("a", CreateSet(3,4))), Implements(), AbstractMethods("m4"),
      Method("m1", Insert("a", ValueOf(1)))).eval
    Extend("abs2", "abs1").eval
    ClassDef("c7", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2,3)), ("a", CreateSet(5,6))), Implements(), ExtendAbstractClass("abs2"),
      Method("m1", Insert("a", ValueOf(1))), Method("m4", Insert("c", ValueOf(5)))).eval shouldEqual
      "Implement all methods from all the interfaces and abstract methods from extended abstract class. Methods not implemented: HashSet(m3)"
  }
  behavior of "Class extending itself"
  it should "Give error message" in {
    ClassDef("c7", Fields("a", "b", "c"), Constructor(("c", CreateSet(1,2,3)), ("a", CreateSet(5,6))), Implements(), ExtendAbstractClass("abs2"),
      Method("m1", Insert("a", ValueOf(1))), Method("m4", Insert("c", ValueOf(5)))).eval
    Extend("c7", "c7").eval shouldEqual "Class can not extend itself."
  }
}
