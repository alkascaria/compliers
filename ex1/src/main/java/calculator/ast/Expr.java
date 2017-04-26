package calculator.ast;

public abstract class Expr
{
    abstract public String accept(ExprVisitor e);

    abstract public int acceptEvaluate(ExprVisitor e);

}
