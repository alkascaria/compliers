package calculator.ast;

public abstract class ExprBinary extends Expr {
    private Expr left;
    private Expr right;
    //0 = positive
    //1 = negative
    private int isNegative;

    public ExprBinary(Expr left, Expr right, int negated)
    {
        this.left = left;
        this.right = right;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }
    
}
