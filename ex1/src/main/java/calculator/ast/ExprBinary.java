package calculator.ast;

/**
 * 
 * @author praveengopal
 * 
 * Holds Binary expression 
 */
public abstract class ExprBinary extends Expr {
    private Expr left;
    private Expr right;
    //positive = 0.
    //negative = 1
    private int sign;

    public ExprBinary(Expr left, Expr right, int sign)
    {
        this.left = left;
        this.right = right;
        this.sign = sign;
    }

    public ExprBinary(Expr left)
    {
        this.left = left;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }

    public int getSign()
    {
        return sign;
    }
}
