package calculator.ast;

/**
 *  Supports Expr type conversion.
 *  Used for the Representation of generic nodes in our abstract syntax tree
 *  @author praveengopal, Steve
 * @since   2017-04-29 
 * 
 *
 */
public abstract class Expr
{
    abstract public String accept(ExprVisitor e);

    abstract public int acceptEvaluate(ExprVisitor e);

}
