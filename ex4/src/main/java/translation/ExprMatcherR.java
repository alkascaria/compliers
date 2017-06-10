package translation;

import minijava.ast.*;
import minillvm.ast.*;
import minillvm.ast.BinaryOperation;
import minillvm.ast.Load;
import minillvm.ast.TemporaryVar;

import static minillvm.ast.Ast.*;


/**
 * Created by alka on 6/5/2017.
 * Modified by Daniele
 */

public class ExprMatcherR implements MJExpr.Matcher<Operand>
{

    ExprMatcherR()
    {

    }

    /**
     *
     * @param fieldAccess(@code MJFieldAccess)
     * @return
     */
    @Override
    public Operand case_FieldAccess(MJFieldAccess fieldAccess)
    {
        return null;
    }

    /**
     *
     * @param exprBinary(@code MJExprBinary)
     * @return
     */
    @Override
    public Operand case_ExprBinary(MJExprBinary exprBinary) {

        //Left of the binaryExpr
        MJExpr exprLeft = exprBinary.getLeft();
        //Right of the binaryExpr
        MJExpr exprRight = exprBinary.getRight();
        //Operator of the binaryExpr
        MJOperator operator = exprBinary.getOperator();

        Operand operand1 , operand2;

        //Matching it with the expr and getting the operands
        ExprMatcherR exprMatchR = new ExprMatcherR();
        operand1 = exprLeft.match(exprMatchR);
        operand2 = exprRight.match(exprMatchR);

        //Doing the operation operand1 operator operand2
        OperatorMatcher operatorMatcher = new OperatorMatcher(operand1, operand2);

        //this will already kinda contain a VarRef. return it directly
        Operand value = operator.match(operatorMatcher);


        return (value);
    }

    /**
     *
     * @param exprNull(@code MJExprNull)
     * @return
     */
    @Override
    public Operand case_ExprNull(MJExprNull exprNull)
    {
        Operand operandNull = Nullpointer();
        return operandNull;
    }

    /**
     *
     * @param methodCall(@code MJMethodCall)
     * @return
     */
    @Override
    public Operand case_MethodCall(MJMethodCall methodCall)
    {
        return null;
    }

    /**
     *
     * @param exprUnary(@code MJExprUnary)
     * @return
     */
    @Override
    public Operand case_ExprUnary(MJExprUnary exprUnary)
    {
        System.out.println(exprUnary);

        //unaryOperator
        MJUnaryOperator unaryOperator = exprUnary.getUnaryOperator();

        //unaryExpression
        MJExpr unary = exprUnary.getExpr();

        ExprMatcherR exprMatcher=new ExprMatcherR();

        return unaryOperator.match(new MJUnaryOperator.Matcher<Operand>()
        {
            /**
             *
             * @param unaryMinus(@code MJUnaryMinus)
             * @return
             */
            @Override
            public Operand case_UnaryMinus(MJUnaryMinus unaryMinus)
            {
                Operand minus = ConstInt(-1);

                //ex: -b
               if (unary instanceof MJVarUse)
                {
                    System.out.println("Checking unary varUse");
                    String varName = ((MJVarUse) unary).getVarName();
                    //get corresponding value and put it into a temp var.
                    TemporaryVar varStored = TemporaryVar("temp");
                    Load loadRef = Load(varStored, VarRef(Translator.varsTemp.get(varName)));
                    Translator.curBlock.add(loadRef);
                    TemporaryVar tempVarReturn = TemporaryVar("return");
                    //same as MJNumber. make use of a binary operation to apply a -1
                    BinaryOperation binaryMinusVar = BinaryOperation(tempVarReturn, VarRef(varStored), Mul(), minus);
                    Translator.curBlock.add(binaryMinusVar);

                    return VarRef(tempVarReturn);
                }
                //ex: -4
                if (unary instanceof MJNumber)
                {
                    TemporaryVar tempVar = TemporaryVar("temp");
                    int numberMinValue = (((MJNumber) unary).getIntValue());
                    //put negative value into temporary variable. negative minus is also kinda binary
                    //ex: 5 * -1
                    BinaryOperation binaryMinus = BinaryOperation(tempVar, Ast.ConstInt(numberMinValue), Mul(), minus);
                    Translator.curBlock.add(binaryMinus);

                    return VarRef(tempVar);
                }

                //ex: -(...)
                else
                {
                    //content of expression
                    Operand operExpr = unary.match(exprMatcher);
                    //take content of expression and
                    TemporaryVar tempVar = TemporaryVar("temp");
                    BinaryOperation binaryMinusExpr = BinaryOperation(tempVar, operExpr, Mul(), minus);
                    Translator.curBlock.add(binaryMinusExpr);

                   return VarRef(tempVar);
                }
            }

            /**
             *
             * @param negate(@code MJNegate)
             * @return
             */
            @Override
            public Operand case_Negate(MJNegate negate)
            {
                //ex: !b
                if (unary instanceof MJVarUse)
                {
                    String varName = ((MJVarUse) unary).getVarName();
                    //contains the stored variable
                    TemporaryVar varStored = TemporaryVar("temp");
                    Load loadRef = Load(varStored, VarRef(Translator.varsTemp.get(varName)));
                    Translator.curBlock.add(loadRef);
                    TemporaryVar tempVarReturn = TemporaryVar("return");
                    //XOR with true to invert boolean value
                    BinaryOperation binaryOp = BinaryOperation(tempVarReturn, VarRef(varStored) , Xor(), ConstBool(true));
                    Translator.curBlock.add(binaryOp);
                    //put the value of the corresponding variable into a temp var

                    return VarRef(tempVarReturn);
                }
                //ex: !false
                else if (unary instanceof MJBoolConst)
                {
                    boolean boolValue = ((MJBoolConst) unary).getBoolValue();
                    TemporaryVar tempVarReturn = TemporaryVar("temp");
                    //again, XOR with true to invert boolean logic.
                    BinaryOperation binOpReturn = BinaryOperation(tempVarReturn, ConstBool(boolValue), Xor(), ConstBool(true));
                    Translator.curBlock.add(binOpReturn);

                    return VarRef(tempVarReturn);
                }
                //other expressions. ex: !(...)
                else
                {
                    //content of expression
                    Operand operExpr = unary.match(exprMatcher);
                    //take content of expression and
                    TemporaryVar tempVar = TemporaryVar("temp");
                    BinaryOperation binaryMinusExpr = BinaryOperation(tempVar, operExpr, Xor(), ConstBool(true));
                    Translator.curBlock.add(binaryMinusExpr);

                    return VarRef(tempVar);
                }
            }
        });
    }

    /**
     *
     * @param boolConst(@code MJBoolConst)
     * @return
     */
    @Override
    public Operand case_BoolConst(MJBoolConst boolConst)
    {
        //return boolean
        return ConstBool(boolConst.getBoolValue());
    }

    /**
     *
     * @param number(@code MJNumber)
     * @return
     */
    @Override
    public Operand case_Number(MJNumber number)
    {
        //return int value
        return ConstInt(number.getIntValue());
    }

    /**
     *
     * @param varUse(@code MJVarUse)
     * @return
     */
    @Override
    public Operand case_VarUse(MJVarUse varUse)
    {
        String varName = varUse.getVarName();
        MJVarDecl varDecl = varUse.getVariableDeclaration();
        MJType type = varDecl.getType();


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
            public Operand case_TypeBool(MJTypeBool typeBool)
            {
                //return boolean value
                //return int
                TemporaryVar tempBool = TemporaryVar("tempvar");
                //put the value of the varUse found into the tempVar
                Load loadBool = Load(tempBool, VarRef(Translator.varsTemp.get(varName)));
                Translator.curBlock.add(loadBool);

                return VarRef(tempBool);
            }

            /**
             *
             * @param typeIntArray(@code MJTypeIntArray)
             * @return
             */
            @Override
            public Operand case_TypeIntArray(MJTypeIntArray typeIntArray)
            {
                TemporaryVar tempIntArray = TemporaryVar("tempVarArr");
                Load loadArray = Load(tempIntArray, VarRef(Translator.varsTemp.get(varName)));
                Translator.curBlock.add(loadArray);

                return VarRef(tempIntArray);
            }

            /**
             *
             * @param typeInt(@code MJTypeInt)
             * @return
             */
            @Override
            public Operand case_TypeInt(MJTypeInt typeInt)
            {
                //return int
                TemporaryVar tempInt = TemporaryVar("tempvar");
                //put the value of the varUse found into the tempVar
                Load loadInt = Load(tempInt, VarRef(Translator.varsTemp.get(varName)));
                Translator.curBlock.add(loadInt);

                return VarRef(tempInt);
            }
        });
    }

    /**
     * a = new int[5];
     * @param newIntArray(@code MJNewIntArray)
     * @return
     */
    @Override
    public Operand case_NewIntArray(MJNewIntArray newIntArray)
    {
        MJExpr exprSize = newIntArray.getArraySize();
        //store size of the array into this
        Operand operArraySize = exprSize.match(this);
        Operand operZero = ConstInt(0);
        //boolean. true or false
        TemporaryVar varIsNegativeSize = TemporaryVar("isSizeNegative");
        //first of all, check if size is negative
        Translator.curBlock.add(BinaryOperation(varIsNegativeSize, operArraySize, Slt(), operZero));

        BasicBlock blockOkay = BasicBlock();
        BasicBlock blockWrong = BasicBlock();

        Operand checkNegSize = VarRef(varIsNegativeSize);

        //choose the right block to jump to
        Branch branchIsNeg = Branch(checkNegSize, blockWrong, blockOkay);
        Translator.curBlock.add(branchIsNeg);

        //not good, we got something negative here.
        Translator.curBlock = blockWrong;
        Translator.blocks.add(blockWrong);
        //then add error to it and stop execution.
        blockWrong.add(HaltWithError("An array cannot be initialized with negative size."));

        //OKAY, we got a valid value (also 0 is valid here). then continue...
        Translator.curBlock = blockOkay;
        Translator.blocks.add(blockOkay);

        //size needs to be given + 1
        TemporaryVar arraySizeIncr = TemporaryVar("array size adjusted");
        Operand operOne = ConstInt(1);
        BinaryOperation binAddSize = BinaryOperation(arraySizeIncr, operArraySize.copy(), Add(), operOne);
        Translator.curBlock.add(binAddSize);

        //allocate space for each element of the array
        Operand operArrSizeInc = VarRef(arraySizeIncr);
        TemporaryVar sizeArrayMembers = TemporaryVar("array members size");
        //just to make just it's enough
        Operand sizePerEment = ConstInt(8);
        BinaryOperation binSizeMember = BinaryOperation(sizeArrayMembers, operArrSizeInc, Mul(), sizePerEment);
        Translator.curBlock.add(binSizeMember);


        Operand sizeTotalArray = VarRef(sizeArrayMembers);
        //good, now allocate the space for the array.
        TemporaryVar arrayHeapVar = TemporaryVar("array");
        Alloc allocHeap = Alloc(arrayHeapVar, sizeTotalArray);
        Translator.curBlock.add(allocHeap);

        //now cast pointer into array
        TemporaryVar arrayPointer = TemporaryVar("cast array pointer");
        TypeArray typeArray = TypeArray(TypeInt(),0 );
        //thanks to Joseff for bitcasting, i.e: converting into an actual array
        //arrayPointer will then contain the converted array
        Bitcast binCastPointer = Bitcast(arrayPointer, TypePointer(typeArray), VarRef(arrayHeapVar));
        Translator.curBlock.add(binCastPointer);
        //now set the size of the array in the first position
        TemporaryVar arrayLengthNew = TemporaryVar("array length");
        Operand addressZero = ConstInt(0);
        //                 0   |1| 2 |3 | 4|5
        //store like this: size|e1|e2|e3|e4|e5...
        //save the array size in the first position of the array
        OperandList operandList = OperandList(addressZero, addressZero.copy());
        GetElementPtr elementPtr = GetElementPtr(arrayLengthNew, VarRef(arrayPointer),operandList);
        Translator.curBlock.add(elementPtr);
        //again, put the length in the 0 position.
        Store storeArr = Store(VarRef(arrayLengthNew),operArraySize.copy());
        Translator.curBlock.add(storeArr);

        return VarRef(arrayPointer);
    }

    /**
     *
     * @param exprThis(@code MJExprThis)
     * @return
     */
    @Override
    public Operand case_ExprThis(MJExprThis exprThis)
    {
        return null;
    }

    /**
     *
     * @param arrayLength(@code MJArrayLength)
     * ex: a.length
     * @return
     */
    @Override
    public Operand case_ArrayLength(MJArrayLength arrayLength)
    {
        MJExpr exprArray = arrayLength.getArrayExpr();
        //returns reference to the array's declaration (i.e: base address)
        //in form of a temporary variable
        return this.returnArrayLength(exprArray);
    }

    /**
     *
     * @param exprArray contains the expression with the array.
     * ex: e.length --> get e or e[5] --> get e
     * @return Operand --> VarRef(lengthVarReturn) = VarRef(tempVar)
     */

    public Operand returnArrayLength(MJExpr exprArray)
    {
        ExprMatcherL matcherL = new ExprMatcherL();
        TemporaryVar arrayTemp = exprArray.match(matcherL);


        TemporaryVar arrayRef = TemporaryVar("array ref");
        //store reference of reference /credits to Joseff for double referencing arrays
        Translator.curBlock.add(Load(arrayRef, VarRef(arrayTemp)));
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

    /**
     *
     * @param newObject
     * @return
     */
    @Override
    public Operand case_NewObject(MJNewObject newObject)
    {
        return null;
    }

    /**
     *
     * @param arrayLookup(@code MJArrayLookup)
     * @return
     */
    @Override
    public Operand case_ArrayLookup(MJArrayLookup arrayLookup)
    {
        MJExpr exprArray = arrayLookup.getArrayExpr();
        //firstly, get the array size
        Operand arraySize = returnArrayLength(exprArray);

        //then get the array index we're trying to access
        MJExpr exprArrayIndex = arrayLookup.getArrayIndex();
        ExprMatcherR exprMatcherR = new ExprMatcherR();
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

        //all good, go ahead and get the value in it








        return ConstInt(0);
    }
}
