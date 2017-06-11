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

        TemporaryVar pointerElementArray = accessIndexArray(arrayLookup);

        //return pointer to the element desired --> store value into it
        return VarRef(pointerElementArray);
    }


    /**
     * Input: arrayLookup. an access to an array element (ex: a[5])
     * @return
     */
    public TemporaryVar accessIndexArray(MJArrayLookup arrayLookup)
    {
        //make sure the index is within range first
        ExprMatcherR exprMatcherR = new ExprMatcherR();
        //firstly, check if the index is within range
        exprMatcherR.checkArrayIndexInRange(arrayLookup);
        //if in range, continue...
        MJExpr exprIndex = arrayLookup.getArrayIndex();
        Operand operIndex = exprIndex.match(exprMatcherR);

        //increase tempvar by 1, as position 0 contains length
        TemporaryVar tempIndexIncr = TemporaryVar("temp index increased");
        BinaryOperation binOpIncr = BinaryOperation(tempIndexIncr, operIndex, Add(), ConstInt(1));
        Translator.curBlock.add(binOpIncr);

        //now get back the original array's address
        MJExpr exprArray = arrayLookup.getArrayExpr();
        ExprMatcherL exprMatcherL = new ExprMatcherL();
        Operand arrayTemp = exprArray.match(exprMatcherL);

        //array address stored in arrayRef
        TemporaryVar arrayRef = TemporaryVar("array ref");
        Translator.curBlock.add(Load(arrayRef, arrayTemp));

        TemporaryVar pointerElementArray = TemporaryVar("element pointer");
        //start from the base address and get the value stored at index 0 --> length.
        Operand lengthBase = ConstInt(0);
        //get value at index i in array --> i = tempIndexIncr.
        OperandList operandList = OperandList(lengthBase, VarRef(tempIndexIncr));
        GetElementPtr elementPtr = GetElementPtr(pointerElementArray, VarRef(arrayRef), operandList);
        Translator.curBlock.add(elementPtr);

        return pointerElementArray;

    }
}
