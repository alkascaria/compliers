package translation;

import minijava.ast.*;
import minillvm.ast.Operand;

import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;
import static minillvm.ast.Ast.VarRef;

/**
 * Created by Daniele on 02/06/2017.
 */
public class ExprTranslatorMatcher implements MJExpr.Matcher<Operand> {


    /**
     *
     * @param fieldAccess
     * @return
     */
    @Override
    public Operand case_FieldAccess(MJFieldAccess fieldAccess) {
        return null;
    }

    /**
     *
     * @param exprBinary
     * @return
     */
    @Override
    public Operand case_ExprBinary(MJExprBinary exprBinary) {

        return null;
    }

    /**
     *
     * @param exprNull
     * @return
     */
    @Override
    public Operand case_ExprNull(MJExprNull exprNull)
    {
        return Ast.Nullpointer();
    }

    /**
     *
     * @param methodCall
     * @return
     */
    @Override
    public Operand case_MethodCall(MJMethodCall methodCall) {
        return null;
    }

    /**
     *
     * @param exprUnary
     * @return
     */
    @Override
    public Operand case_ExprUnary(MJExprUnary exprUnary) {

        return null;

    }

    /**
     *
     * @param boolConst
     * @return
     */
    @Override
    public Operand case_BoolConst(MJBoolConst boolConst)
    {
        return Ast.ConstBool(boolConst.getBoolValue());
    }

    /**
     *
     * @param number
     * @return
     */
    @Override
    public Operand case_Number(MJNumber number) {
        //Operand oper = ConstInt(number.getIntValue());

        //return oper;

        //Optimized
        return Ast.ConstInt(number.getIntValue());
    }

    /**
     *
     * @param varUse(@code MJVarUse)
     * @return
     */
    @Override
    public Operand case_VarUse(MJVarUse varUse) {
        //firstly, get the value / reference thatßs in the varuse.
        String varName = varUse.getVarName();
        TemporaryVar tempVar = Translator.varDeclsTempVar.get(varName);

        //okay, we got the temp var. Now how to get the value inside it?
        //TemporaryVar tempVar1 = TemporaryVar(varName);
        //Load(tempVar1,VarRef(tempVar));

       //  Operand oper = ConstInt();
        //return Ast.VarRef(tempVar);
        return VarRef(tempVar);
    }

    /**
     *
     * @param newIntArray
     * @return
     */
    @Override
    public Operand case_NewIntArray(MJNewIntArray newIntArray) {
        return null;
    }

    /**
     *
     * @param exprThis
     * @return
     */
    @Override
    public Operand case_ExprThis(MJExprThis exprThis) {
        return null;
    }

    /**
     *
     * @param arrayLength
     * @return
     */
    @Override
    public Operand case_ArrayLength(MJArrayLength arrayLength) {
        return null;
    }

    /**
     *
     * @param newObject
     * @return
     */
    @Override
    public Operand case_NewObject(MJNewObject newObject) {
        return null;
    }

    /**
     *
     * @param arrayLookup
     * @return
     */
    @Override
    public Operand case_ArrayLookup(MJArrayLookup arrayLookup) {
        return null;
    }



}
