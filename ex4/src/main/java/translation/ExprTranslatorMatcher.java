package translation;

import minijava.ast.*;
import minillvm.ast.Operand;

import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;

/**
 * Created by Daniele on 02/06/2017.
 */
public class ExprTranslatorMatcher implements MJExpr.Matcher<Operand> {

    @Override
    public Operand case_FieldAccess(MJFieldAccess fieldAccess) {
        return null;
    }

    @Override
    public Operand case_ExprBinary(MJExprBinary exprBinary) {

        return null;
    }

    @Override
    public Operand case_ExprNull(MJExprNull exprNull) {
        return null;
    }

    @Override
    public Operand case_MethodCall(MJMethodCall methodCall) {
        return null;
    }

    @Override
    public Operand case_ExprUnary(MJExprUnary exprUnary) {
        return null;
    }

    @Override
    public Operand case_BoolConst(MJBoolConst boolConst) {
        return null;
    }

    @Override
    public Operand case_Number(MJNumber number) {
        Operand oper = ConstInt(number.getIntValue());

        return oper;
    }

    @Override
    public Operand case_VarUse(MJVarUse varUse) {
        //firstly, get the value / reference thatßs in the varuse.
        String varName = varUse.getVarName();
        TemporaryVar tempVar = Translator.varDeclsTempVar.get(varName);
        TemporaryVar y = TemporaryVar(varName);
        Load(y,VarRef(tempVar));
        System.out.println(y);


       //  Operand oper = ConstInt();
        return null;
    }

    @Override
    public Operand case_NewIntArray(MJNewIntArray newIntArray) {
        return null;
    }

    @Override
    public Operand case_ExprThis(MJExprThis exprThis) {
        return null;
    }

    @Override
    public Operand case_ArrayLength(MJArrayLength arrayLength) {
        return null;
    }

    @Override
    public Operand case_NewObject(MJNewObject newObject) {
        return null;
    }

    @Override
    public Operand case_ArrayLookup(MJArrayLookup arrayLookup) {
        return null;
    }
}
