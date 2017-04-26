package calculator.ast;

/**
 * Created by mweber on 04/04/2017.
 */
public class ExprMult extends ExprBinary {
    public ExprMult(Expr left, Expr right, int sign) {
        super(left, right, sign);
    }

    public String accept(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitMult(this);
    }

    public int getSign()
    {
        return super.getSign();
    }
}
