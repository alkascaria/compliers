package calculator.ast;

/**
 * Created by Daniele 
 * 
 * @since 26/04/2017.
 * 
 * This Visitor class Print and Evaluate Expressions 
 * 
 * Addition, Division , Subtraction , Multiplication
 * 
 * Once input entered, ExprPrinter refers Visitor Class for 
 * printing and evaluation
 * 
 */
public interface ExprVisitor
{
    //print
    public String visitAdd(ExprAdd exprAdd);
    public String visitDiv(ExprDiv exprDiv);
    public String visitSub(ExprSub exprSub);
    public String visitMult(ExprMult exprMult);
    public String visitNumber(ExprNumber number);


    //evaluate
    public int visitEvaluateAdd(ExprAdd exprAdd);
    public int visitEvaluateDiv(ExprDiv exprDiv);
    public int visitEvaluateSub(ExprSub exprSub);
    public int visitEvaluateMult(ExprMult exprMult);
    public int visitEvaluateNumber(ExprNumber number);



}
