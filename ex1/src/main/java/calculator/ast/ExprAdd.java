package calculator.ast;

/**
 *  This Class Performs addition,
 * @author Created by mweber on 04/04/2017.
 * 
 */

public class ExprAdd extends ExprBinary
{
	 
    public ExprAdd(Expr left, Expr right, int sign)
    {
        super(left, right, sign);
    }
    /**
     * Returns Addition symbol as String  
     * @param  exprVisitor(@code ExprVisitor)
     * @return the value {@code exprVisitor.visitAdd()} returns Addition symbol as String   
     */
    public String accept(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitAdd(this);
    }
    /**
     * Returns expression evaluation as number  
     * @param  exprVisitor(@code ExprVisitor)
     * @return the value {@code exprVisitor.visitEvaluateAdd()} returns expression's  evaluation as integer    
     */
    public int acceptEvaluate(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitEvaluateAdd(this);
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
