package calculator.ast;

/**
 * This Class Performs Division
 * @author Created by Daniele on 25/04/2017.
 */
public class ExprDiv  extends ExprBinary {
    public ExprDiv(Expr left, Expr right, int sign) {
        super(left, right, sign);
    }
    /**
     * Returns Division symbol as String  
     * @param  exprVisitor(@code ExprVisitor)
     * @return the value {@code exprVisitor.visitDiv()} returns Division symbol as String   
     */
    public String accept(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitDiv(this);
    }
    /**
     * Returns expression evaluation as number  
     * @param  exprVisitor(@code ExprVisitor)
     * @return the value {@code exprVisitor.visitEvaluateDiv()} returns expression's  evaluation as integer    
     */
    public int acceptEvaluate(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitEvaluateDiv(this);
    }
    /**
     * Returns Sign as integer  
     * @return the value {@code getSign()} returns sign as integer   
     */
    public int getSign()
    {
        return super.getSign();
    }
}
