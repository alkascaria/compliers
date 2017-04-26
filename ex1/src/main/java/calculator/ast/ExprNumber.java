package calculator.ast;

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

	public String accept(ExprVisitor exprVisitor)
	{
		return exprVisitor.visitNumber(this);
	}

	public int acceptEvaluate(ExprVisitor exprVisitor)
	{
		return exprVisitor.visitEvaluateNumber(this);
	}

	public int getValue() {
		return value;
	}
}
