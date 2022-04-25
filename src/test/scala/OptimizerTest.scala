import ClassOperations.ClassDefinition.*
import Optimizer.*
import SetOperations.SetOperations.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

class OptimizerTest extends AnyFlatSpec with Matchers {
  behavior of "Intersection"
  it should "optimize for following cases of intersection" in {
    val collectionFalse = Seq(Intersection(Variable("x"), Variable("y")))
    val collectionTrue = Seq(Intersection(Variable("x"), Variable("x")))
    val collectionEmpty = Seq(Intersection(Variable("x"), CreateSet()))
    collectionFalse.map(functionSetOp) should contain (Intersection(Variable("x"),Variable("y"))) //
    collectionTrue.map(functionSetOp) should contain (Variable("x")) //, Variable("x"), mutable.HashSet()
    collectionEmpty.map(functionSetOp) should contain (mutable.HashSet()) //, Variable("x"), mutable.HashSet()
  }

  behavior of "Union"
  it should "optimize for following cases of union" in {
    val collectionFalse = Seq(Union(Variable("x"), Variable("y")))
    val collectionTrue = Seq(Union(Variable("x"), Variable("x")))
    val collectionEmpty = Seq(Union(Variable("x"), CreateSet()))
    collectionFalse.map(functionSetOp) should contain (Union(Variable("x"),Variable("y")))
    collectionTrue.map(functionSetOp) should contain (Variable("x"))
    collectionEmpty.map(functionSetOp) should contain (Variable("x"))
  }

  behavior of "Difference"
  it should "optimize for following cases of difference" in {
    val collectionFalse = Seq(Difference(Variable("x"), Variable("y")))
    val collectionTrue = Seq(Difference(Variable("x"), Variable("x")))
    val collectionEmpty = Seq(Difference(Variable("x"), CreateSet()))
    collectionFalse.map(functionSetOp) should contain (Difference(Variable("x"),Variable("y")))
    collectionTrue.map(functionSetOp) should contain (mutable.HashSet())
    collectionEmpty.map(functionSetOp) should contain (Variable("x"))
  }

  behavior of "Symmetric difference"
  it should "optimize for following cases of Symmetric difference" in {
    val collectionFalse = Seq(Symmetric_difference(Variable("x"), Variable("y")))
    val collectionTrue = Seq(Symmetric_difference(Variable("x"), Variable("x")))
    val collectionEmpty = Seq(Symmetric_difference(Variable("x"), CreateSet()))
    collectionFalse.map(functionSetOp) should contain (Symmetric_difference(Variable("x"),Variable("y")))
    collectionTrue.map(functionSetOp) should contain (mutable.HashSet())
    collectionEmpty.map(functionSetOp) should contain (Variable("x"))
  }

}
