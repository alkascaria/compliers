package calculator.ast;

/**
 * Created by Daniele on 26/04/2017.
 */
public class ExprBracket extends ExprBinary
{
    public ExprBracket(Expr expr)
    {
        super(expr);
    }

    @Override
    public String accept(ExprVisitor e) {
        return null;
    }

    @Override
    public int acceptEvaluate(ExprVisitor e) {
        return 0;
    }
}
