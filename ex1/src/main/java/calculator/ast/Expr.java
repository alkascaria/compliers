package calculator.ast;

public abstract class Expr 
{
    abstract public String accept(ExprVisitor e);

}
