package calculator.ast;

/**
 * Created by Daniele on 26/04/2017.
 */
public class ExprNeg extends Expr
{
    private Expr expNegStored;
    public ExprNeg(Expr expNeg)
    {
        this.expNegStored = expNeg;
    }
}
