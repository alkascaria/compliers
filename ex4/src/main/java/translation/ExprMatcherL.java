package translation;

import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;
import static minillvm.ast.Ast.TypePointer;

/**
 * Created by Daniele on 09/06/2017.
 * Matches the correct left-hand side
 */

//TODO: add throw IllegalStatementException on expressions not allowed

public class ExprMatcherL implements MJExpr.Matcher<Operand> {
    @Override
    public Operand case_FieldAccess(MJFieldAccess fieldAccess)
    {
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
        return null;
    }

    public Operand case_VarUse(MJVarUse varUse)
    {
        String varName = varUse.getVarName();
        MJVarDecl varDecl = varUse.getVariableDeclaration();
        MJType type = varDecl.getType();

        //just try for integer for now.

        //now match the type of the variable being used
        return type.match(new MJType.Matcher<Operand>() {
            /**
             *
             * @param typeClass(@code MJTypeClass)
             * @return
             */
            @Override
            public Operand case_TypeClass(MJTypeClass typeClass)
            {
                return null;
            }

            /**
             *
             * @param typeBool(@code MJTypeBool)
             * @return
             */
            @Override
            public Operand case_TypeBool(MJTypeBool typeBool) {
                return VarRef(Translator.varsTemp.get(varName));
            }

            /**
             *
             * @param typeIntArray(@code MJTypeIntArray)
             * @return
             */
            @Override
            public Operand case_TypeIntArray(MJTypeIntArray typeIntArray)
            {
                return VarRef(Translator.varsTemp.get(varName));
            }

            /**
             *
             * @param typeInt(@code MJTypeInt)
             * @return
             */
            @Override
            public Operand case_TypeInt(MJTypeInt typeInt)
            {
                //the corresponding variable in the hashmap
                return VarRef(Translator.varsTemp.get(varName));
            }
        });
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
    //assigning value of an array to a variable
    //ex: b = a[7];
    public Operand case_ArrayLookup(MJArrayLookup arrayLookup)
    {

        //make sure the index is within range first
        ExprMatcherR exprMatcherR = new ExprMatcherR();
        //firstly, check if the index is within range
        exprMatcherR.checkArrayIndexInRange(arrayLookup);
        //get the variable name


        return ConstInt(0);
    }
}
