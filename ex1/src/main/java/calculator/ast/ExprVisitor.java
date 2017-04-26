package calculator.ast;

/**
 * Created by Daniele on 26/04/2017.
 */
public interface ExprVisitor
{
    void visitAdd(Expr left, Expr right);
    void visitSub(Expr left, Expr right);
    void visitMult(Expr left, Expr right);
    void visitDiv(Expr left, Expr right);

}
