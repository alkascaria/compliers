package calculator.ast;

/**
 * Created by Daniele on 25/04/2017.
 */
public class ExprDiv  extends ExprBinary {
    public ExprDiv(Expr left, Expr right, int negated) {
        super(left, right, negated);
    }
}
