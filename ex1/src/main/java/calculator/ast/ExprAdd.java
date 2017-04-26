package calculator.ast;

/**
 * Created by mweber on 04/04/2017.
 */
public class ExprAdd extends ExprBinary
{
    public ExprAdd(Expr left, Expr right, int negated)
    {
        super(left, right, negated);
    }

    public void accept(ExprVisitor ExprMethods)
    {
        ExprMethods.visitAdd(super.getLeft(), super.getRight());
    }

}
