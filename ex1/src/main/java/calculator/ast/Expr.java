package calculator.ast;

/**
 * @author praveengopal
 * @since   2017-04-29 
 * 
 * Supports Expr type conversion    
 * 
 */
public abstract class Expr
{
    abstract public String accept(ExprVisitor e);

    abstract public int acceptEvaluate(ExprVisitor e);

}
