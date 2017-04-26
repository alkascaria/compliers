package calculator.ast;

/**
 * Created by Daniele on 25/04/2017.
 */
public class ExprDiv  extends ExprBinary {
    public ExprDiv(Expr left, Expr right, int sign) {
        super(left, right, sign);
    }

    public String accept(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitDiv(this);
    }

    public int getSign()
    {
        return super.getSign();
    }
}
