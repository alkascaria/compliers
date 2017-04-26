package calculator.ast;

/**
 * Created by mweber on 04/04/2017.
 */
public class ExprAdd extends ExprBinary
{
    private int sign;

    public ExprAdd(Expr left, Expr right, int sign)
    {
        super(left, right, sign);
    }

    public String accept(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitAdd(this);
    }

    public int getSign()
    {
        return super.getSign();
    }
}
