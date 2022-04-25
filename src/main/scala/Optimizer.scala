import SetOperations.SetOperations.*
import ClassOperations.ClassDefinition.*
import ExceptionOperations.ExceptionClass.*
import scala.collection.mutable

object Optimizer {
  private def functionIfElse(y: ExceptionOperations.ExceptionClass): Any = y match {
    case IfElse(condition, thenExp, elseExp) =>
      val condition1 = condition.asInstanceOf[SetOperations.SetOperations].eval
      if (condition1 == true) thenExp
      else elseExp
    case p => p
  }

  def functionSetOp(x: SetOperations.SetOperations): Any = x match {
    case Union(x, y) =>
      if (x.asInstanceOf[SetOperations.SetOperations].eval == y.asInstanceOf[SetOperations.SetOperations].eval) x
      else if(x.asInstanceOf[SetOperations.SetOperations].eval == mutable.Set[Any]()) y
      else if(y.asInstanceOf[SetOperations.SetOperations].eval == mutable.Set[Any]()) x
      else Union(x, y).eval
    case Intersection(x, y) =>
      if (x.asInstanceOf[SetOperations.SetOperations].eval == y.asInstanceOf[SetOperations.SetOperations].eval) x
      else if(x.asInstanceOf[SetOperations.SetOperations].eval == mutable.Set[Any]()
        || y.asInstanceOf[SetOperations.SetOperations].eval == mutable.Set[Any]()) mutable.Set[Any]()
      else Intersection(x, y)
    case Difference(x, y) =>
      if (x.asInstanceOf[SetOperations.SetOperations].eval == y.asInstanceOf[SetOperations.SetOperations].eval) CreateSet().eval
      else if(x.asInstanceOf[SetOperations.SetOperations].eval == mutable.Set[Any]()) CreateSet().eval
      else if(y.asInstanceOf[SetOperations.SetOperations].eval == mutable.Set[Any]()) x
      else Difference(x, y)
    case Symmetric_difference(x, y) =>
      if (x.asInstanceOf[SetOperations.SetOperations].eval == y.asInstanceOf[SetOperations.SetOperations].eval) CreateSet().eval
      else if(x.asInstanceOf[SetOperations.SetOperations].eval == mutable.Set[Any]()) y
      else if(y.asInstanceOf[SetOperations.SetOperations].eval == mutable.Set[Any]()) x
      else Symmetric_difference(x, y)
    case p => p
  }

  @main def run(): Unit =

    val collection = Seq(Union(Variable("x"), Variable("y")), Union(Variable("x"), Variable("x")), Union(Variable("x"), CreateSet()),
      Intersection(Variable("x"), Variable("y")), Intersection(Variable("x"), Variable("x")), Intersection(Variable("x"), CreateSet()),
      Difference(Variable("x"), Variable("y")), Difference(Variable("x"), Variable("x")),Difference(Variable("x"), CreateSet()),
      Symmetric_difference(Variable("x"), Variable("y")), Symmetric_difference(Variable("x"), Variable("x")),
      Symmetric_difference(Variable("x"), CreateSet()))
//    val expression1 = Union(Variable("x"), Variable("x"))
//    println(Seq(expression1.eval.asInstanceOf[SetOperations.SetOperations]).map(function))
//    val expression = Assign("x", CreateSet(1, 2)).eval
    //  val collection = Seq(IF(Check(SetName("x"),Value(1)),Then(Insert(SetName("x"),Valset(2,3))),Else(Assign(SetName("x"),Valset(1)))))
    println(collection.map(functionSetOp))

}