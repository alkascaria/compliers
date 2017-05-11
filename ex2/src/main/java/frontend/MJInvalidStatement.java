package frontend;

import minijava.ast.*;

import java.util.ArrayList;
import java.util.List;

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
        //body of main
        //TODO: other classes.

        //Goto --> visit method for block
        visit(program.getMainClass().getMainBody());

    }

    @Override public void visit(MJBlock block)
    {
        for (MJStatement i : block )
        {
            i.match(this);
        }
    }


    //Can contain 2d Array! prevent that
    @Override public void visit(MJStmtExpr stmtExpr)
    {


    }

    //check its right and left-hand sides for arrays.
    @Override public void visit(MJStmtAssign stmtAssign)
    {
        System.out.println("Visiting stmt assign");

        stmtAssign.getRight().match(this);
        stmtAssign.getLeft().match(this);
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
    public void case_Number(MJNumber number) {

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
    public void case_NewIntArray(MJNewIntArray newIntArray) {

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
        System.out.println("In array lookup from assignment expr ");
        String errorMsgArr = "2D Arrays are not supported in minijava.";
        SyntaxError syntaxError = new SyntaxError(arrayLookup, errorMsgArr);
        this.syntaxErrorsFound.add(syntaxError);

    }

    @Override
    public void case_ExtendsNothing(MJExtendsNothing extendsNothing) {

    }

    //ExtendsNothing
    @Override public void visit(MJExtendsNothing mjExtendsNothing) {

        if (mjExtendsNothing.getParent().getClass().toString().equals("minijava.ast.MJExtendsClassImpl"))
        {
            System.out.println("Extends found");
            SyntaxError syntaxError = new SyntaxError(mjExtendsNothing.getParent(), "Error: Extends not supports in Minijava");

            this.syntaxErrorsFound.add(syntaxError);

        } else
        {
            mjExtendsNothing.getParent().accept(this);
        }
    }

}

