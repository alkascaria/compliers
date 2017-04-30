package calculator.ast;

/**
 * Supports ExprVisitor type conversion  
 * 
 */
public class ExprNumber extends Expr {
	private int value;

	public ExprNumber(int value) {
		super();
		this.value = value;

	}
	public ExprNumber(String value)
	{
	    this.value = Integer.parseInt(value);	    
	}
	/**
     * Returns exprVisitor as String  
     * @param  exprVisitor(@code ExprVisitor)
     * @return the value {@code exprVisitor.visitNumber()} returns exprVisitor as String   
     */
	public String accept(ExprVisitor exprVisitor)
	{
		return exprVisitor.visitNumber(this); 
	}
	/**
     * Returns exprVisitor as Integer  
     * @param  exprVisitor(@code ExprVisitor)
     * @return the value {@code exprVisitor.visitEvaluateNumber()} returns exprVisitor as Integer   
     */
	public int acceptEvaluate(ExprVisitor exprVisitor)
	{
		return exprVisitor.visitEvaluateNumber(this);
	}
	/**
     * Returns value as Integer     
     */
	public int getValue() {
		return value;
	}
}
