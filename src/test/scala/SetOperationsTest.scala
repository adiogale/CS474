import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.must.Matchers.*
import SetOperations.SetOperations.*
import org.scalatest.enablers.Containing

import scala.collection.mutable

class SetOperationsTest extends AnyFlatSpec with Matchers {

  behavior of "create a set"
  it should "add element" in {
    Assign("x", CreateSet(1,2,3)).eval
    Variable("x").eval.asInstanceOf[mutable.Set[Any]]  should contain allOf (1,2,3)
  }

  behavior of "union with one element"
  it should "union of 2 sets" in {
    Assign("x", CreateSet(1)).eval
    Assign("y", CreateSet(3)).eval
    Assign("z", Union(Variable("x"), Variable("y"))).eval
    Variable("z").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (1,3)
  }

  behavior of "intersection"
  it should "show only common elements to both sets" in {
    Assign("x", CreateSet(1,2,3)).eval
    Insert("x", ValueOf(4)).eval
    Assign("y", CreateSet(3,4)).eval
    Insert("y", Variable("x")).eval
    Assign("z", Intersection(Variable("x"), Variable("y"))).eval
    Variable("z").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (4,3)
  }

  behavior of "union"
  it should "show all elements in both sets" in {
    Assign("x", CreateSet(1,2,3)).eval
    Insert("x", ValueOf(4)).eval
    Assign("y", CreateSet(3,4)).eval
    Insert("y", Variable("x")).eval
    Assign("z", Union(Variable("x"), Variable("y"))).eval
    Variable("z").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (1,2, 4,3)
  }

  behavior of "difference"
  it should "show element in first set that are not present in second set" in {
    Assign("x", CreateSet(1,2,3)).eval
    Insert("x", ValueOf(4)).eval
    Assign("y", CreateSet(3,4)).eval
    Insert("y", ValueOf(5)).eval
    Assign("p", Difference(Variable("x"), Variable("y"))).eval
    Variable("p").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (1,2)
  }

  behavior of "Cartesian product"
  it should "show set of tuples of all possible combinations of set1 and set2" in {
    Assign("x", CreateSet(1,2,3)).eval
    Assign("y", CreateSet(4,5)).eval
    Assign("p", Product(Variable("x"), Variable("y"))).eval
    Variable("p").eval.asInstanceOf[mutable.Set[Any]] should contain allOf ((1,4),(1,5),(2,4),(2,5),(3,4),(3,5))
  }

  behavior of "symmetric difference"
  it should "show element not common to both of the sets" in {
    Assign("x", CreateSet(1,2,3)).eval
    Insert("x", ValueOf(4)).eval
    Assign("y", CreateSet(3,4)).eval
    Insert("y", ValueOf(5)).eval
    Assign("p", Symmetric_difference(Variable("x"), Variable("y"))).eval
    Variable("p").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (1,2,5)
  }

  behavior of "delete"
  it should "Element is deleted from given set" in {
    Assign("x", CreateSet(1,2,3)).eval
    Insert("x", ValueOf(4)).eval
    Delete("x", ValueOf(1)).eval
    Variable("x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (3,2,4)
  }

  behavior of "macro"
  it should "Set a macro and retreive it to use in an operation" in {
    Assign("x", CreateSet(1,2)).eval
    var varName = 9
    SetMacro("insert", Insert("x", ValueOf(varName))).eval
    varName = 10
    Assign("r", GetMacro("insert")).eval
    Variable("r").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (1,2,9)
  }

  behavior of "scope"
  it should "match given sets" in {
    Assign("x", CreateSet(1,2)).eval
    getSetFromScope("global", "x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (1,2)
    Assign("y", CreateSet(3,4)).eval
    getSetFromScope("global", "y").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (3,4)
    Scope("scope1", "x", CreateSet(5,6)).eval
    getSetFromScope("scope1", "x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (5,6)
    Scope("scope2", "x", Insert("x",ValueOf(6))).eval
    getSetFromScope("scope2", "x").eval.asInstanceOf[mutable.Set[Any]] should contain (6)
    Scope("scope2", "x", Insert("x",ValueOf(5))).eval
    getSetFromScope("scope2", "x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (5,6)
    Scope("scope2", "x", Insert("x",ValueOf(7))).eval
    getSetFromScope("scope2", "x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (5,6,7)
    Scope("scope1", "x", Delete("x", ValueOf(5))).eval
    getSetFromScope("scope1", "x").eval.asInstanceOf[mutable.Set[Any]] should contain (6)
    getSetFromScope("scope2", "x").eval.asInstanceOf[mutable.Set[Any]] should contain allOf (6,5,7)
  }

}
