package calculator.ast;

/**
 * Created by mweber on 04/04/2017.
 * 
 * This Class performs Multiplication
 */
public class ExprMult extends ExprBinary {
    public ExprMult(Expr left, Expr right, int sign) {
        super(left, right, sign);
    }
    /**
     * Returns Multiplication symbol as String  
     * @param  exprVisitor(@code ExprVisitor)
     * @return the value {@code exprVisitor.visitMult()} returns Multiplication symbol as String   
     */
    public String accept(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitMult(this);
    }
    /**
     * Returns expression evaluation as number  
     * @param  exprVisitor(@code ExprVisitor)
     * @return the value {@code exprVisitor.visitEvaluateMult()} returns expression's  evaluation as integer    
     */
    public int acceptEvaluate(ExprVisitor exprVisitor)
    {
        return exprVisitor.visitEvaluateMult(this);
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
