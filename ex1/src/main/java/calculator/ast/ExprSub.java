package calculator.ast;

/**
 * Created by Daniele on 25/04/2017.
 */
public class ExprSub extends ExprBinary
{
    public ExprSub(Expr left, Expr right, int negated) {
        super(left, right, negated);
    }
}
