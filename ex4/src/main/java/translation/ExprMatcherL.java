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
        System.out.println("Matching here");

        return ConstInt(number.getIntValue());
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
    /***
     * a[7] = b
     *Set into an array
     * Input: arrayLookup. an expression containing something like a[7]
     * Output: reference to the address where a value should be stored
     */
    public Operand case_ArrayLookup(MJArrayLookup arrayLookup)
    {
        TemporaryVar pointerElementArray = StaticMethods.accessIndexArray(arrayLookup);

        //return pointer to the element desired --> store value into it
        return VarRef(pointerElementArray);
    }



}
