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
        //TODO: other classes.
        //Goto --> visit the main class' body.
        visit(program.getMainClass().getMainBody());

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
        System.out.println("Visiting stmt assign and checking its left and right-hand sides");
        System.out.println(stmtAssign.getLeft().toString());
        System.out.println(stmtAssign.getRight().toString());
        stmtAssign.getLeft().match(this);
        stmtAssign.getRight().match(this);
    }

    //important case for 2D arrays
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
    public void case_ExprBinary(MJExprBinary exprBinary) {

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
    public void case_MethodCall(MJMethodCall methodCall) {

    }

    @Override
    public void case_StmtIf(MJStmtIf stmtIf)
    {

    }

    @Override
    public void case_Negate(MJNegate negate) {

    }

    @Override
    public void case_ArrayLookup(MJArrayLookup arrayLookup)
    {
       String arrayExpContent = arrayLookup.getArrayExpr().toString();
       String arrayIndexContent = arrayLookup.getArrayIndex().toString();

       //if it contains a NewIntArray and has an extra index, then it's a 2D array.
       if(arrayExpContent.contains("NewIntArray") && arrayIndexContent.length() > 0)
       {
           SyntaxError syntaxError = new SyntaxError(arrayLookup, "2D arrays not supported in MiniJava.");
           this.syntaxErrorsFound.add(syntaxError);
       }
    }

    @Override
    public void case_ExtendsNothing(MJExtendsNothing extendsNothing)
    {

    }


}

