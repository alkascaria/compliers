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
     * @param fieldAccess
     * @return
     */
    @Override
    public Operand case_FieldAccess(MJFieldAccess fieldAccess) {
        return null;
    }

    /**
     * @param exprBinary
     * @return
     */
    @Override
    public Operand case_ExprBinary(MJExprBinary exprBinary) {

        //passing it to the ExprMatcher class
        ExprMatcher exprMatcher=new ExprMatcher();
        Object value = exprBinary.match(exprMatcher);

        return Ast.ConstInt((int)value);
    }

    /**
     * @param exprNull
     * @return
     */
    @Override
    public Operand case_ExprNull(MJExprNull exprNull) {
        return Ast.Nullpointer();
    }

    /**
     * @param methodCall
     * @return
     */
    @Override
    public Operand case_MethodCall(MJMethodCall methodCall) {
        return null;
    }

    /**
     * @param exprUnary
     * @return
     */
    @Override
    public Operand case_ExprUnary(MJExprUnary exprUnary) {
        MJExpr unary = exprUnary.getExpr();       //getting the unary operand
        int unaryValue = 0;

        //check if there is a varuse get its value instead.
        if (unary instanceof MJVarUse) {
            //if var use is there,
            unaryValue = -(Translator.varsStackInt.get(((MJVarUse) unary).getVarName()));
        }
        else        //it is a Number
            unaryValue=-(((MJNumber) unary).getIntValue());

        return Ast.ConstInt(unaryValue);
    }

    /**
     * @param boolConst
     * @return
     */
    @Override
    public Operand case_BoolConst(MJBoolConst boolConst) {
        return Ast.ConstBool(boolConst.getBoolValue());
    }

    /**
     * @param number
     * @return
     */
    @Override
    public Operand case_Number(MJNumber number) {
        //Optimized
        return Ast.ConstInt(number.getIntValue());
    }

    /**
     * @param varUse(@code MJVarUse)
     * @return
     */
    @Override
    public Operand case_VarUse(MJVarUse varUse) {
        MJVarDecl varDecl = varUse.getVariableDeclaration();
        MJType typeVar = varDecl.getType();

        return typeVar.match(new MJType.Matcher<Operand>() {
            @Override
            public Operand case_TypeIntArray(MJTypeIntArray typeIntArray) {
                return null;
            }

            @Override
            public Operand case_TypeBool(MJTypeBool typeBool) {
                return Ast.ConstBool(Translator.varsStackBool.get(varUse.getVarName()));
            }

            @Override
            public Operand case_TypeInt(MJTypeInt typeInt) {
                return Ast.ConstInt(Translator.varsStackInt.get(varUse.getVarName()));
            }

            @Override
            public Operand case_TypeClass(MJTypeClass typeClass) {
                return null;
            }
        });
    }

    /**
     * @param newIntArray
     * @return
     */
    @Override
    public Operand case_NewIntArray(MJNewIntArray newIntArray) {
        return null;
    }

    /**
     * @param exprThis
     * @return
     */
    @Override
    public Operand case_ExprThis(MJExprThis exprThis) {
        return null;
    }

    /**
     * @param arrayLength
     * @return
     */
    @Override
    public Operand case_ArrayLength(MJArrayLength arrayLength) {
        return null;
    }

    /**
     * @param newObject
     * @return
     */
    @Override
    public Operand case_NewObject(MJNewObject newObject) {
        return null;
    }

    /**
     * @param arrayLookup
     * @return
     */
    @Override
    public Operand case_ArrayLookup(MJArrayLookup arrayLookup) {
        return null;
    }
}