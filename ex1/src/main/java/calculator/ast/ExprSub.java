package calculator.ast;

/**
 * Created by Daniele on 25/04/2017.
 * 
 * This class performs Subtraction
 */
public class ExprSub extends ExprBinary
{
    public ExprSub(Expr left, Expr right, int negated) {
        super(left, right, negated);
    }
    /**
     * Returns Subtraction symbol as String  
     * @param  exprVisitor(@code ExprVisitor)
     * @return the value {@code exprVisitor.visitSub()} returns Subtraction symbol as String   
     */
    public String accept(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitSub(this);
    }
    /**
     * Returns expression evaluation as number  
     * @param  exprVisitor(@code ExprVisitor)
     * @return the value {@code exprVisitor.visitEvaluateDiv()} returns expression's  evaluation as integer    
     */
    public int acceptEvaluate(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitEvaluateSub(this);
    }


}
