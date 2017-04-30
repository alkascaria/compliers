package calculator.ast;

/**
 *  Supports Expr type conversion
 *  @author praveengopal
 * @since   2017-04-29 
 * 
 *
 */
public abstract class Expr
{
    abstract public String accept(ExprVisitor e);

    abstract public int acceptEvaluate(ExprVisitor e);

}
