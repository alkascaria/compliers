package frontend;

import minijava.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Daniele on 10/05/2017.
 */


//TODO: matchers are not needed. just use relevant visit methods
//TODO: Replace startsWith with instanceOf
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


    @Override public void visit(MJBlock block)
    {
        for (MJStatement i : block )
        {
            i.accept(this);
        }
    }

    //check its right and left-hand sides for arrays.
    @Override public void visit(MJStmtAssign stmtAssign)
    {
        String stmtLeft = stmtAssign.getLeft().toString();
        String stmtRight = stmtAssign.getRight().toString();

        detectInvalidAssignExpr(stmtLeft, stmtRight, stmtAssign);

        stmtAssign.getLeft().accept(this);
        stmtAssign.getRight().accept(this);
    }

    @Override public void visit(MJStmtWhile stmtWhile)
    {
        stmtWhile.getCondition().accept(this);
        stmtWhile.getLoopBody().accept(this);
    }

    @Override public void visit(MJStmtIf stmtIf)
    {
        stmtIf.getIfFalse().accept(this);
        stmtIf.getIfTrue().accept(this);
    }

    @Override public void visit(MJStmtExpr stmtExpr)
    {
        String stmtExprContent = stmtExpr.toString();
        detectInvalidStmtExpr(stmtExpr);

        stmtExpr.getExpr().accept(this);
    }

    @Override public void visit(MJMethodCall methodCall)
    {
        MJExpr receivingExpr = methodCall.getReceiver();

        if(receivingExpr.getClass().isInstance("MJNumber"))
        {
            String errorMsg = "Cannot call a function on a number.";
            this.syntaxErrorsFound.add(new SyntaxError(methodCall, errorMsg));
        }
    }

    @Override  public void visit(MJExprBinary exprBinary)
    {
        //binary expressions' types checking is done in the type and name analysis
    }

    @Override
    public void visit(MJArrayLookup arrayLookup)
    {
        String arrayExpContent = arrayLookup.getArrayExpr().toString();
        String arrayIndexContent = arrayLookup.getArrayIndex().toString();

        //if it contains a NewIntArray and has an extra index, then it's a 2D array. Forbid it!
        if(arrayExpContent.contains("NewIntArray") && arrayIndexContent.length() > 0)
        {
            String errorMsg = "2D arrays are not supported in MiniJava.";
            this.syntaxErrorsFound.add(new SyntaxError(arrayLookup, errorMsg));
        }
    }

    //this is not needed

    public void detectInvalidStmtExpr(MJStmtExpr stmtExpr)
    {
        String exprContent = stmtExpr.getExpr().toString();

        if(  (!exprContent.startsWith("MethodCall")) && (!exprContent.startsWith("NewObject"))  )
        {
            String errorMsg = "Invalid stray expression. The only stray expressions allowed are for Method Call and new Object Creation.";
            this.syntaxErrorsFound.add(new SyntaxError(stmtExpr, errorMsg));
        }
    }




    public void detectInvalidAssignExpr(String stmtLeft, String stmtRight, MJStmtAssign stmtAssign)
    {
        String errorMsg = "";


       if ( (stmtLeft.startsWith("Number")) )
        {
            errorMsg = "The left-hand side of an assignment expression cannot be a number.";
            this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
        }

        else if(stmtLeft.startsWith("ExprBinary"))
        {
            errorMsg = "The left-hand side of an assignment expression cannot be a binary expression.";
            this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
        }
        else if(stmtLeft.startsWith("ExprThis"))
       {
           errorMsg = "The left-hand side of an assignment expression cannot be a 'this' reference.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }
       else if(stmtLeft.startsWith("ExprNull"))
       {
           errorMsg = "The left-hand side of an assignment expression cannot be null.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }
        else if(stmtLeft.startsWith("ArrayLength"))
       {
           errorMsg = "The left-hand side of an assignment expression cannot contain an array length.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }
       else if(stmtLeft.startsWith("ExprUnary"))
       {
           errorMsg = "The left-hand side of an assignment expression cannot contain a unary expression.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }
        //cannot assign a number to a number
        else if(stmtLeft.startsWith("BoolConst"))
       {
           errorMsg = "The left-hand side of an assignment expression cannot contain a Boolean Constant.";
           this.syntaxErrorsFound.add(new SyntaxError(stmtAssign, errorMsg));
       }

    }




}

