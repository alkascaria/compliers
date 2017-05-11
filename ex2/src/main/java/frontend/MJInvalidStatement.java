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
public class MJInvalidStatement extends MJElement.DefaultVisitor implements MJElement.MatcherVoid
{
    MJFrontend frontEndVar;

    public List<SyntaxError> syntaxErrorsFound = new ArrayList<>();

    public void acceptProgram(MJProgram program, MJFrontend frontend)
    {
        frontEndVar = frontend;
        //visit the main class' body.
        visit(program.getMainClass().getMainBody());
        //visit other classes too
        visit(program.getClassDecls());
    }


    @Override public void visit(MJBlock block)
    {
        for (MJStatement i : block )
        {
            i.match(this);
        }
    }

    //check its right and left-hand sides for arrays.
    @Override public void visit(MJStmtAssign stmtAssign)
    {
        String stmtLeft = stmtAssign.getLeft().toString();
        String stmtRight = stmtAssign.getRight().toString();

        detectInvalidAssignExpr(stmtLeft, stmtRight, stmtAssign);

        stmtAssign.getLeft().match(this);
        stmtAssign.getRight().match(this);
    }

    @Override public void visit(MJStmtWhile stmtWhile)
    {
        stmtWhile.getCondition().match(this);
        stmtWhile.getLoopBody().match(this);
    }

    public void visit(MJStmtIf stmtIf)
    {
        stmtIf.getIfFalse().match(this);
        stmtIf.getIfTrue().match(this);
    }

    public void visit(MJStmtExpr stmtExpr)
    {
        String stmtExprContent = stmtExpr.toString();
        detectInvalidStmtExpr(stmtExpr);

        stmtExpr.getExpr().match(this);
    }

    public void visit(MJMethodCall methodCall)
    {
        String methodName = methodCall.getReceiver().toString();

        if(methodName.startsWith("Number"))
        {

            String errorMsg = "Cannot call a function on a number.";
            this.syntaxErrorsFound.add(new SyntaxError(methodCall, errorMsg));
        }
    }

    public void visit(MJExprBinary exprBinary)
    {
        detectInvalidExprBinary(exprBinary);
    }

    public void detectInvalidExprBinary(MJExprBinary exprBinary)
    {
        String leftHandSide = exprBinary.getLeft().toString();
        String rightHandSide = exprBinary.getRight().toString();

        if(leftHandSide.startsWith("ExprNull") || rightHandSide.startsWith("ExprNull"))
        {
            String errorMsg = "Cannot perform binary operations on 'null' references";
            this.syntaxErrorsFound.add(new SyntaxError(exprBinary, errorMsg));
        }
        else if(leftHandSide.startsWith("NewIntArray") || rightHandSide.startsWith("NewIntArray"))
        {
            String errorMsg = "Cannot perform binary operations on integer arrays";
            this.syntaxErrorsFound.add(new SyntaxError(exprBinary, errorMsg));
        }
        else if(leftHandSide.startsWith("BoolConst") || rightHandSide.startsWith("BoolConst"))
        {
            String errorMsg = "Cannot perform binary operations on boolean constants";
            this.syntaxErrorsFound.add(new SyntaxError(exprBinary, errorMsg));
        }
        else if(leftHandSide.startsWith("NewObject") && rightHandSide.startsWith(("NewObject")))
        {
            String errorMsg = "Cannot perform binary operations on an object's instantation.";
            this.syntaxErrorsFound.add(new SyntaxError(exprBinary, errorMsg));
        }
    }

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

    @Override
    public void case_StmtExpr(MJStmtExpr stmtExpr)
    {
        visit(stmtExpr);
    }

    @Override
    public void case_VarUse(MJVarUse varUse) {

    }

    @Override
    public void case_And(MJAnd and) {

    }

    @Override
    public void case_MethodDecl(MJMethodDecl methodDecl) {

    }

    @Override
    public void case_ExprList(MJExprList exprList) {

    }

    @Override
    public void case_TypeBool(MJTypeBool typeBool) {

    }

    @Override
    public void case_VarDeclList(MJVarDeclList varDeclList) {

    }

    @Override
    public void case_Number(MJNumber number)
    {

    }

    @Override
    public void case_FieldAccess(MJFieldAccess fieldAccess) {

    }


    //other important case for 2D arrays
    @Override
    public void case_StmtAssign(MJStmtAssign stmtAssign)
    {
        visit(stmtAssign);

    }

    @Override
    public void case_TypeIntArray(MJTypeIntArray typeIntArray) {

    }

    @Override
    public void case_NewObject(MJNewObject newObject) {

    }

    @Override
    public void case_Plus(MJPlus plus) {

    }

    @Override
    public void case_BoolConst(MJBoolConst boolConst) {

    }

    @Override
    public void case_Block(MJBlock block)
    {
        visit(block);

    }

    @Override
    public void case_MainClass(MJMainClass mainClass) {

    }

    @Override
    public void case_Minus(MJMinus minus) {

    }

    @Override
    public void case_TypeClass(MJTypeClass typeClass) {

    }

    @Override
    public void case_Times(MJTimes times) {

    }

    @Override
    public void case_StmtReturn(MJStmtReturn stmtReturn)
    {

    }

    @Override
    public void case_StmtPrint(MJStmtPrint stmtPrint)
    {
    }

    @Override
    public void case_ExprNull(MJExprNull exprNull) {

    }

    @Override
    public void case_Less(MJLess less) {

    }

    @Override
    public void case_StmtWhile(MJStmtWhile stmtWhile)
    {
        visit(stmtWhile);
    }

    @Override
    public void case_MethodDeclList(MJMethodDeclList methodDeclList) {

    }

    @Override
    public void case_ExprThis(MJExprThis exprThis) {

    }

    @Override
    public void case_ArrayLength(MJArrayLength arrayLength) {

    }

    @Override
    public void case_ClassDeclList(MJClassDeclList classDeclList) {

    }


    @Override
    public void case_VarDecl(MJVarDecl varDecl)
    {

    }

    @Override
    public void case_NewIntArray(MJNewIntArray newIntArray)
    {


    }

    @Override
    public void case_Equals(MJEquals equals) {

    }

    @Override
    public void case_ExprUnary(MJExprUnary exprUnary) {

    }

    @Override
    public void case_UnaryMinus(MJUnaryMinus unaryMinus) {

    }

    @Override
    public void case_TypeInt(MJTypeInt typeInt) {

    }

    @Override
    public void case_Program(MJProgram program) {

    }

    @Override
    public void case_ExprBinary(MJExprBinary exprBinary)
    {
        visit(exprBinary);
    }

    @Override
    public void case_ExtendsClass(MJExtendsClass extendsClass) {

    }

    @Override
    public void case_Div(MJDiv div) {

    }

    @Override
    public void case_ClassDecl(MJClassDecl classDecl) {

    }

    @Override
    public void case_MethodCall(MJMethodCall methodCall)
    {
        visit(methodCall);

    }

    @Override
    public void case_StmtIf(MJStmtIf stmtIf)
    {
        visit(stmtIf);
    }

    @Override
    public void case_Negate(MJNegate negate) {

    }

    @Override
    public void case_ArrayLookup(MJArrayLookup arrayLookup)
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

    @Override
    public void case_ExtendsNothing(MJExtendsNothing extendsNothing)
    {

    }


}

