package calculator.ast;

/**
 * Created by Daniele on 26/04/2017.
 */
public interface ExprVisitor
{
    public String visitAdd(ExprAdd exprAdd);
    public String visitDiv(ExprDiv exprDiv);
    public String visitSub(ExprSub exprSub);
    public String visitMult(ExprMult exprMult);

    public String visitNumber(ExprNumber number);
}
