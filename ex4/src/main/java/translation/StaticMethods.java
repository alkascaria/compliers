package translation;

import minijava.ast.MJArrayLookup;
import minijava.ast.MJExpr;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;
import static minillvm.ast.Ast.VarRef;

/**
 * Created by Daniele on 11/06/2017.
 */
public class StaticMethods
{

    /**
     * Input: arrayLookup. an access to an array element (ex: a[5])
     * @return
     */
    public static TemporaryVar accessIndexArray(MJArrayLookup arrayLookup)
    {
        //make sure the index is within range first
        ExprMatcherR exprMatcherR = new ExprMatcherR();
        //firstly, check if the index is within range
        checkArrayIndexInRange(arrayLookup);
        //if in range, continue...
        MJExpr exprIndex = arrayLookup.getArrayIndex();
        Operand operIndex = exprIndex.match(exprMatcherR);

        //increase tempvar by 1, as position 0 contains length
        TemporaryVar tempIndexIncr = Ast.TemporaryVar("temp index increased");
        BinaryOperation binOpIncr = Ast.BinaryOperation(tempIndexIncr, operIndex, Add(), ConstInt(1));
        Translator.curBlock.add(binOpIncr);

        //now get back the original array's address
        MJExpr exprArray = arrayLookup.getArrayExpr();
        ExprMatcherL exprMatcherL = new ExprMatcherL();
        Operand arrayTemp = exprArray.match(exprMatcherL);

        //array address stored in arrayRef
        TemporaryVar arrayRef = Ast.TemporaryVar("array ref");
        Translator.curBlock.add(Load(arrayRef, arrayTemp));

        TemporaryVar pointerElementArray = Ast.TemporaryVar("element pointer");
        //start from the base address and get the value stored at index 0 --> length.
        Operand lengthBase = ConstInt(0);
        //get value at index i in array --> i = tempIndexIncr.
        OperandList operandList = OperandList(lengthBase, VarRef(tempIndexIncr));
        GetElementPtr elementPtr = GetElementPtr(pointerElementArray, VarRef(arrayRef), operandList);
        Translator.curBlock.add(elementPtr);

        return pointerElementArray;
    }

    /**
     *
     * @param arrayLookup
     * Checks that index is not negative ( out of lower bound)
     * checks that index is not beyond the array's length (out of upper bound)
     */
    public static void checkArrayIndexInRange(MJArrayLookup arrayLookup)
    {
        MJExpr exprArray = arrayLookup.getArrayExpr();
        //firstly, get the array size
        Operand arraySize = StaticMethods.returnArrayLength(exprArray);

        ExprMatcherR exprMatcherR = new ExprMatcherR();

        //then get the array index we're trying to access
        MJExpr exprArrayIndex = arrayLookup.getArrayIndex();
        //ExprMatcherR exprMatcherR = new ExprMatcherR();
        Operand arrayIndex = exprArrayIndex.match(exprMatcherR);

        //make sure the arrayIndex is not < 0, i.e: negative
        Operand operZero = ConstInt(0);
        TemporaryVar tempNegative = TemporaryVar("negative");
        BinaryOperation binNotNegative = BinaryOperation(tempNegative, arrayIndex, Slt(), operZero);
        Translator.curBlock.add(binNotNegative);
        Operand operIsNegative = VarRef(tempNegative);

        //case where index is < 0
        BasicBlock blockLessZero = BasicBlock();
        //case where index > 0
        BasicBlock blockOkayNotNeg = BasicBlock();

        Translator.curBlock.add(Branch(operIsNegative, blockLessZero, blockOkayNotNeg));

        //if we are indeed trying to use a negative index
        Translator.curBlock = blockLessZero;
        Translator.blocks.add(blockLessZero);
        //then add error
        Translator.curBlock.add(HaltWithError("Index Out of Lower Bound error: Array access indexes cannot be negative!"));

        //it's alright, accessing a valid element (index >= 0)
        Translator.curBlock = blockOkayNotNeg;
        Translator.blocks.add(blockOkayNotNeg);


        //now check if we're accessing an index too high, i.e: size = 5 --> max index = 4

        //now check if accessing a value beyond the size --> a[size] --> make sure indexAccess < size
        TemporaryVar tempIndexOutHigh = TemporaryVar("out of upper bound");
        BinaryOperation binOpTooHigh = BinaryOperation(tempIndexOutHigh,arrayIndex.copy(), Slt(), arraySize);
        Translator.curBlock.add(binOpTooHigh);
        //true if index smaller than size. false if index greater than size
        Operand operTooHigh = VarRef(tempIndexOutHigh);

        //case where index is > 0
        BasicBlock blockOutUpperBound = BasicBlock();
        //case where index > 0
        BasicBlock blockOkayUpper = BasicBlock();

        Translator.curBlock.add(Branch(operTooHigh, blockOkayUpper, blockOutUpperBound ));

        //if we are indeed trying to use a negative index
        Translator.curBlock = blockOutUpperBound;
        Translator.blocks.add(blockOutUpperBound);
        //then add error
        Translator.curBlock.add(HaltWithError("Index Out of Upper Bound error: you cannot access a value that is beyond the array's size!"));

        //it's alright, accessing a valid element (index >= 0)
        Translator.curBlock = blockOkayUpper;
        Translator.blocks.add(blockOkayUpper);
    }

    /**
     *
     * @param exprArray contains the expression with the array.
     * ex: e.length --> get e or e[5] --> get e
     * @return Operand --> VarRef(lengthVarReturn) = VarRef(tempVar)
     */

    public static Operand returnArrayLength(MJExpr exprArray)
    {
        ExprMatcherL matcherL = new ExprMatcherL();
        Operand arrayTemp = exprArray.match(matcherL);


        TemporaryVar arrayRef = TemporaryVar("array ref");
        //store reference of reference /credits to Joseff for double referencing arrays
        Translator.curBlock.add(Load(arrayRef, arrayTemp));
        TemporaryVar lengthArray = TemporaryVar("arrayLength");
        //start from the base address and get the value stored at index 0 --> length.
        Operand lengthBase = ConstInt(0);
        //get value at index 0 in the array
        OperandList operandList = OperandList(lengthBase, lengthBase.copy());
        GetElementPtr elementPtr = GetElementPtr(lengthArray, VarRef(arrayRef), operandList);
        Translator.curBlock.add(elementPtr);
        //assign it to a variable and return
        TemporaryVar lengthVarReturn =  TemporaryVar("length return");
        Load loadVar =  Load(lengthVarReturn, VarRef(lengthArray));
        Translator.curBlock.add(loadVar);

        return VarRef(lengthVarReturn);
    }
}
