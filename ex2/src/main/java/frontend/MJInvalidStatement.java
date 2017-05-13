package frontend;

import minijava.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Daniele on 10/05/2017.
 */


//This is the visitor class
public class MJInvalidStatement extends MJElement.DefaultVisitor
{
    MJFrontend frontEndVar;

    public List<SyntaxError> syntaxErrorsFound = new ArrayList<>();

    public void acceptProgram(MJProgram program, MJFrontend frontend)
    {
        frontEndVar = frontend;
        program.accept(this);
    }

    /**
     *
     * @param block(@code MJBlock)
     *
     */
    @Override public void visit(MJBlock block)
    {
        for (MJStatement i : block )
        {
            i.accept(this);
        }
    }

    /**
     * checks invalid assignment.
     * @param stmtAssign(@code MJStmtAssign)
     *
     */
    @Override public void visit(MJStmtAssign stmtAssign)
    {

        detectInvalidAssignExpr(stmtAssign);

        stmtAssign.getLeft().accept(this);
        stmtAssign.getRight().accept(this);
    }
    /**
     *
     * @param stmtWhile(@code MJStmtWhile)
     *
     */ 
    @Override public void visit(MJStmtWhile stmtWhile)
    {
        stmtWhile.getCondition().accept(this);
        stmtWhile.getLoopBody().accept(this);
    }
    /**
     *
     * @param stmtIf(@code MJStmtIf)
     *
     */
    @Override public void visit(MJStmtIf stmtIf)
    {
        stmtIf.getIfFalse().accept(this);
        stmtIf.getIfTrue().accept(this);
    }
    /**
     *
     * @param stmtExpr(@code MJStmtExpr)
     *
     */
    @Override public void visit(MJStmtExpr stmtExpr)
    {
        detectInvalidStmtExpr(stmtExpr);

        stmtExpr.getExpr().accept(this);
    }
    /**
     *
     * @param methodCall(@code MJMethodCall)
     *
     */
    @Override public void visit(MJMethodCall methodCall)
    {
        MJExpr receivingExpr = methodCall.getReceiver();

        if(receivingExpr instanceof MJNumber)
        {
            String errorMsg = "Cannot call a function on a number.";
            this.syntaxErrorsFound.add(new SyntaxError(methodCall, errorMsg));
        }
    }

    @Override  public void visit(MJExprBinary exprBinary)
    {
        //binary expressions' types checking is done in the type and name analysis
    }
    
   /**
     *
     * @param arrayLookup(@code MJArrayLookup)
     *
     */
    @Override
    public void visit(MJArrayLookup arrayLookup)
    {
        //if it contains a NewIntArray and has an extra index, then it's a 2D array. Forbid it!
        if(arrayLookup.getArrayExpr() instanceof MJNewIntArray )
        {
            String errorMsg = "2D arrays are not supported in MiniJava.";
            this.syntaxErrorsFound.add(new SyntaxError(arrayLookup, errorMsg));
        }
    }

    /**
     * Detects invalid stray Expression
     * @param stmtExpr(@code MJStmtExpr)
     *
     */
    //This is not needed
    public void detectInvalidStmtExpr(MJStmtExpr stmtExpr)
    {
        MJExpr exprInStatement = stmtExpr.getExpr();

        if( (!(exprInStatement instanceof MJMethodCall)) && (!(exprInStatement instanceof MJNewObject)))
        {
            String errorMsg = "Invalid stray expression. The only stray expressions allowed are for Method Call and new Object Creation.";
            this.syntaxErrorsFound.add(new SyntaxError(stmtExpr, errorMsg));
        }
    }


 /**
     * Detects invalid assignments
     * @param stmtLeft
     * @param stmtRight
     * @param stmtAssign
     *
     */
    public void detectInvalidAssignExpr(MJStmtAssign stmtAssign)
    {
        MJExpr stmtLeft = stmtAssign.getLeft();
        MJExpr stmtRight = stmtAssign.getRight();
        String errorMsg = "";

       if (stmtLeft instanceof MJNumber)
        {
            errorMsg = "The left-hand side of an assignment expression cannot be a number.";
            this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
        }

        else if(stmtLeft instanceof MJExprBinary)
        {
            errorMsg = "The left-hand side of an assignment expression cannot be a binary expression.";
            this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
        }
        else if(stmtLeft instanceof MJExprThis)
       {
           errorMsg = "The left-hand side of an assignment expression cannot be a 'this' reference.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }
       else if(stmtLeft instanceof MJExprNull)
       {
           errorMsg = "The left-hand side of an assignment expression cannot be null.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }
        else if(stmtLeft instanceof  MJArrayLength)
       {
           errorMsg = "The left-hand side of an assignment expression cannot contain an array length.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }
       else if(stmtLeft instanceof MJExprUnary)
       {
           errorMsg = "The left-hand side of an assignment expression cannot be preceded by a binary unary expression: - or !.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }
       else if(stmtLeft instanceof MJNegate)
       {
           errorMsg = "The left-hand side of an assignment expression cannot contain a unary negation.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }
        //cannot assign a number to a number
        else if(stmtLeft instanceof MJBoolConst)
       {
           errorMsg = "The left-hand side of an assignment expression cannot contain a Boolean Constant.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }

    }




}

