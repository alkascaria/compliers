package translation;

import minijava.ast.*;
import minillvm.ast.*;

import java.security.InvalidParameterException;

import static minillvm.ast.Ast.*;
import static minillvm.ast.Ast.Nullpointer;
import static minillvm.ast.Ast.VarRef;

/**
 * Created by Daniele on 11/06/2017.
 */
public class StaticMethods
{


    /**
     * Given a certain class, checks if it's null
     * @param varClassNull
     */

    public static void checkIfClassNull(TemporaryVar varClassNull)
    {

        //checks for null array
        BasicBlock blockNull = BasicBlock();
        BasicBlock blockRest = BasicBlock();
        TemporaryVar tempNull = TemporaryVar("null class");



        BinaryOperation binCheckNull = BinaryOperation(tempNull,VarRef(varClassNull), Eq(), Ast.Nullpointer());
        Translator.curBlock.add(binCheckNull);

        Branch branchIfNull = Branch(VarRef(tempNull), blockNull, blockRest);
        Translator.curBlock.add(branchIfNull);

        //if null...
        Translator.curBlock = blockNull;
        Translator.blocks.add(blockNull);
        Translator.curBlock.add(HaltWithError("Cannot perform a field access on a null class."));

        //no problem, all good!
        Translator.curBlock = blockRest;
        Translator.blocks.add(blockRest);
    }


    /**
     * Handles a method call when one is invoked
     * @param methodCall
     * @return
     */
    public static Operand handleMethodCall(MJMethodCall methodCall)
    {
        ExprMatcherR exprMatcherR = new ExprMatcherR();
        MJExpr exprReceiver = methodCall.getReceiver();

        Operand exprMatched = exprReceiver.match(exprMatcherR);


        if(methodCall.getReceiver() instanceof MJVarUse)
        {
            MJVarUse varUse = (MJVarUse) methodCall.getReceiver();


            if (varUse.getVariableDeclaration().getType() instanceof MJTypeClass)
            {

                MJTypeClass objClassReceived = (MJTypeClass) varUse.getVariableDeclaration().getType();

                TemporaryVar tempReturnValue = TemporaryVar("return value");
                int rightProcedure = Translator.indexGlobal.get(methodCall.getMethodDeclaration());
                Proc procedure = Translator.prog.getProcedures().get(rightProcedure);
                OperandList parametersToPass = OperandList();

                for (MJExpr exprParam : methodCall.getArguments())
                {
                    Operand operandParam = exprParam.match(exprMatcherR);
                    parametersToPass.add(operandParam);
                }

                //pass the obj reference too
                Variable paramRight = Translator.varsTemp.get(varUse.getVarName());

                TemporaryVar tempVar = TemporaryVar("temp");
                Translator.curBlock.add(Load(tempVar, VarRef(paramRight)));
                Operand operParam = VarRef(paramRight);
                parametersToPass.addFront(operParam);


                Translator.curBlock.add(Call(tempReturnValue, ProcedureRef(procedure), parametersToPass));
                return VarRef(tempReturnValue);
            }

        }

        throw new InvalidParameterException("Method " + methodCall.toString() + " not found");
    }

 /**
     * Returns all structs as a StructFieldList for a certain class
     * @param classDecl
     * @return StructFieldList: list of structFields in the current class
     */

    public static StructFieldList returnStructsFieldsInClass(MJClassDecl classDecl, boolean isBaseClass) {

        StructFieldList structFieldListReturn = StructFieldList();


        //return all fields found in the class passed
        for (MJVarDecl varDecl : classDecl.getFields()) {
            TypeMatcher typeMatcher = new TypeMatcher();
            //convert all fields
            String fieldName = classDecl.getName() + "_" + varDecl.getName();
            Type fieldType = varDecl.getType().match(typeMatcher);

            //now create a field with these
            StructField fieldStruct = StructField(fieldType, fieldName);

            if (isBaseClass == true) {
                structFieldListReturn.add(fieldStruct);
            } else {
                structFieldListReturn.addFront(fieldStruct);

            }
        }

        return structFieldListReturn;


    }
    //stores default value into the different fields of a class (on heap memory)
    public static void initializeDefaultValueFields(TypeStruct typeNewObjClass, TemporaryVar bitCastClass)
    {
        for(int i = 1; i < typeNewObjClass.getFields().size(); i++)
        {
            TemporaryVar tempPointer = TemporaryVar("struct field");
            GetElementPtr elementPointer = GetElementPtr(tempPointer, VarRef(bitCastClass), OperandList(ConstInt(0), ConstInt(i)));
            Translator.curBlock.add(elementPointer);
            //dereference, i.e: get rid of the pointer type

            TemporaryVar tempVar = TemporaryVar("deref temp");
            Translator.curBlock.add(Load(tempVar, VarRef(tempPointer)));

            Type typeField = tempVar.calculateType();

            //integer --> default value = 0
            if(typeField.equalsType(TypeInt()))
            {
                Operand defaultValue = ConstInt(0);
                Translator.curBlock.add(Store(VarRef(tempPointer), defaultValue));
            }
            //boolean --> default value = false
            else if(typeField.equalsType(TypeBool()))
            {
                Operand defaultValue = ConstBool(false);
                Translator.curBlock.add(Store(VarRef(tempPointer), defaultValue));
            }
            //array default value = null
            else if(typeField.equalsType(TypeArray(TypeInt(), 0)))
            {
                TypeArray typeArray = TypeArray(TypeInt(), 0);
                TemporaryVar tempArrayCasted = TemporaryVar("arrayCasted");
                Bitcast arrayConverted = Bitcast(tempArrayCasted, TypePointer(TypePointer(typeArray)), VarRef(bitCastClass));
                Translator.curBlock.add(arrayConverted);
                Operand operNull = Nullpointer();
                Translator.curBlock.add(Store(VarRef(tempArrayCasted), operNull));
            }
            //class = null
            else
            {
                TemporaryVar classNullCasted = TemporaryVar("arrayCasted");
                Bitcast classConverted = Bitcast(classNullCasted, TypePointer(TypePointer(typeNewObjClass)), VarRef(bitCastClass));
                Translator.curBlock.add(classConverted);
                Operand operNull = Nullpointer();

                Translator.curBlock.add(Store(VarRef(classNullCasted), operNull));
            }
        }
    }

    /** Dereference: true if getting the value (right-hand side)
     *               false if getting the element (left-hand side)
     * @param needToDereference
     * @param fieldAccess
     * @return
     */

    public static Operand handleFieldClass(boolean needToDereference, MJFieldAccess fieldAccess, Operand addressObjHeap)
    {
        System.out.println("Handling field");
        MJExpr exprReceiver = fieldAccess.getReceiver();

        ExprMatcherR exprMatcherR = new ExprMatcherR();

        //a.x
        if(exprReceiver instanceof MJVarUse)
        {
            MJVarUse varUse = (MJVarUse)exprReceiver;

            //if trying to access a class. well, there aren't any other options, right?
            if(varUse.getVariableDeclaration().getType() instanceof MJTypeClass)
            {
                MJTypeClass objClassReceived = (MJTypeClass)varUse.getVariableDeclaration().getType();
                MJClassDecl classDecl = objClassReceived.getClassDeclaration();

                TemporaryVar tempVar =  TemporaryVar("temp Deref");
                Translator.curBlock.add(Load(tempVar, VarRef(Translator.varsTemp.get(varUse.getVarName()))));
                return StaticMethods.accessFieldInClass(classDecl, fieldAccess, needToDereference, addressObjHeap);

            }
        }
        //new A().x
        else if(exprReceiver instanceof MJNewObject)
        {
            //firstly, instantiate the class
            exprReceiver.match(exprMatcherR);

            //then access the value
            MJClassDecl classDecl = ((MJNewObject) exprReceiver).getClassDeclaration();

            return StaticMethods.accessFieldInClass(classDecl, fieldAccess, needToDereference, addressObjHeap);
        }

        throw new InvalidParameterException("Nothing matched on right-hand side field access?");

    }

    /**
     *
     * @param classDecl
     * @param fieldAccess: true =  need to get the value (right-hand side)
     *                     false = need to get the reference (left-hand side)
     * @return
     */

    public static Operand accessFieldInClass(MJClassDecl classDecl, MJFieldAccess fieldAccess, boolean needToDereference, Operand addressObjHeap)
    {
        TypeStruct typeStructClass = Translator.structsMap.get(classDecl);

        boolean found = false;

        //loop through all fields starting from the one in position 1, looking for the correct one.
        for(int i = 1; i < typeStructClass.getFields().size(); i++)
        {
            MJClassDecl classCurrent = classDecl;
            while(found == false)
            {
                String fieldName = classCurrent.getName() + "_" + fieldAccess.getFieldName();

                StructField structFieldAtPos = typeStructClass.getFields().get(i);

                if(structFieldAtPos.getName().equals(fieldName))
                {
                    found = true;
                    TemporaryVar tempVarElement = TemporaryVar("element found");

                    GetElementPtr elementPtr = GetElementPtr(tempVarElement, addressObjHeap, OperandList(ConstInt(0), ConstInt(i)));
                    Translator.curBlock.add(elementPtr);

                    if (needToDereference == true)
                    {
                        TemporaryVar tempVar = TemporaryVar("deref temp");
                        Translator.curBlock.add(Load(tempVar, VarRef(tempVarElement)));

                        return VarRef(tempVar);
                    }
                    else
                    {
                        return VarRef(tempVarElement);
                    }
                }
                else
                {
                    if (classCurrent.getDirectSuperClass() != null)
                    {
                        classCurrent = classCurrent.getDirectSuperClass();
                    }
                    else
                    {
                        break;
                    }

                }
                //if not found, change parent
            }
        }

        throw new InvalidParameterException("Field " +  fieldAccess.getFieldName() + " for class" + classDecl.getName() + "  being accessed was not found!");

    }


    /**
     * Input: arrayLookup. an access to an array element (ex: a[5])
     *
     * @return value(@code TemporaryVar) returns the reference to the starting index of an array
     */
    public static TemporaryVar accessIndexArray(MJArrayLookup arrayLookup) {
        //now get back the original array's address
        MJExpr exprArray = arrayLookup.getArrayExpr();
        //check if it's null

        ExprMatcherL exprMatcherL = new ExprMatcherL();
        Operand arrayTemp = exprArray.match(exprMatcherL);

        //array address stored in arrayRef
        TemporaryVar arrayRef = Ast.TemporaryVar("array ref");
        Translator.curBlock.add(Load(arrayRef, arrayTemp));

        //checks for null array
        BasicBlock blockNull = BasicBlock();
        BasicBlock blockRest = BasicBlock();
        TemporaryVar tempNull = TemporaryVar("null array");
        BinaryOperation binCheckNull = BinaryOperation(tempNull, VarRef(arrayRef), Eq(), Ast.Nullpointer());
        Translator.curBlock.add(binCheckNull);

        Branch branchIfNull = Branch(VarRef(tempNull), blockNull, blockRest);
        Translator.curBlock.add(branchIfNull);

        //if null...
        Translator.curBlock = blockNull;
        Translator.blocks.add(blockNull);
        Translator.curBlock.add(HaltWithError("Cannot perform an array look on an array that is null"));

        //no problem, all good!
        Translator.curBlock = blockRest;
        Translator.blocks.add(blockRest);

        //make sure the index is within range first
        ExprMatcherR exprMatcherR = new ExprMatcherR();
        //firstly, check if the index is within range
        checkArrayIndexInRange(arrayLookup);
        //if in range, continue...
        MJExpr exprIndex = arrayLookup.getArrayIndex();
        Operand operIndex = exprIndex.match(exprMatcherR);

        //increase tempvar by 1, as position 0 contains length
        TemporaryVar tempIndexIncr = TemporaryVar("temp index increased");
        BinaryOperation binOpIncr = BinaryOperation(tempIndexIncr, operIndex, Add(), ConstInt(1));
        Translator.curBlock.add(binOpIncr);

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
     * @param exprSize(@code MJExpr)
     *                       Returns an Operand
     *                       Input: an expression containing the size of the array
     *                       Output: the size of the array being initialized
     * @return the value {@code Operand) returns an error in case the size passed is negative else the totalsize of an array+1
     */
    public static Operand checkValidArraySize(MJExpr exprSize) {
        ExprMatcherR exprMatcherR = new ExprMatcherR();

        //store size of the array into this
        Operand operArraySize = exprSize.match(exprMatcherR);
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

        return operArraySize;
    }

    /**
     * @param arrayLookup Checks that index is not negative ( out of lower bound)
     *                    checks that index is not beyond the array's length (out of upper bound)
     */
    public static void checkArrayIndexInRange(MJArrayLookup arrayLookup) {
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
        BinaryOperation binOpTooHigh = BinaryOperation(tempIndexOutHigh, arrayIndex.copy(), Slt(), arraySize);
        Translator.curBlock.add(binOpTooHigh);
        //true if index smaller than size. false if index greater than size
        Operand operTooHigh = VarRef(tempIndexOutHigh);

        //case where index is > 0
        BasicBlock blockOutUpperBound = BasicBlock();
        //case where index > 0
        BasicBlock blockOkayUpper = BasicBlock();

        Translator.curBlock.add(Branch(operTooHigh, blockOkayUpper, blockOutUpperBound));

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
     * @param exprArray contains the expression with the array.
     *                  ex: e.length --> get e or e[5] --> get e
     * @return Operand --> VarRef(lengthVarReturn) = VarRef(tempVar)
     */

    public static Operand returnArrayLength(MJExpr exprArray) {
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
        TemporaryVar lengthVarReturn = TemporaryVar("length return");
        Load loadVar = Load(lengthVarReturn, VarRef(lengthArray));
        Translator.curBlock.add(loadVar);

        return VarRef(lengthVarReturn);
    }

    /**
     * @param arrayPointer:   pointer to the current array
     * @param operArrSizeInc: size of the array + 1 (where the while loop will loop to)
     *                        Initializes all values of an array to 0 with a while loop.
     *                        we can assume never acces index [0] in an array with size 0.
     *                        loop through all "cells" in the array and assign 0 to them
     */

    public static void initializeArrayValuesToZero(TemporaryVar arrayPointer, Operand operArrSizeInc) {

        TemporaryVar varIndex = TemporaryVar("index");
        Alloca alloc = Alloca(varIndex, TypeInt());
        Translator.curBlock.add(alloc);
        //initialize temp var to 1
        Store storeOne = Store(VarRef(varIndex), ConstInt(1));
        Translator.curBlock.add(storeOne);

        BasicBlock conditionBlock = BasicBlock();
        BasicBlock bodyBlock = BasicBlock();
        BasicBlock restBlock = BasicBlock();

        //firstly, jump to the condition block
        Translator.curBlock.add(Jump(conditionBlock));
        Translator.curBlock = conditionBlock;
        Translator.blocks.add(conditionBlock);

        //now check if the condition is matched
        //boolean. true or false. check if operIndex < operArraySizeIncreased

        TemporaryVar tempIndex = TemporaryVar("index obtained");
        Translator.curBlock.add(Load(tempIndex, VarRef(varIndex)));
        TemporaryVar atEndOfArray = TemporaryVar("at end of array");
        BinaryOperation binOpCompare = BinaryOperation(atEndOfArray, VarRef(tempIndex), Slt(), operArrSizeInc.copy());
        Translator.curBlock.add(binOpCompare);

        Branch branchAtEnd = Branch(VarRef(atEndOfArray), bodyBlock, restBlock);
        Translator.curBlock.add(branchAtEnd);

        //now handle true case: putting element into index of array
        Translator.blocks.add(bodyBlock);
        Translator.curBlock = bodyBlock;

        //store 0 into current position(i) of array
        TemporaryVar tempIndexStored = TemporaryVar("temp index stored");
        Translator.curBlock.add(GetElementPtr(tempIndexStored, VarRef(arrayPointer), OperandList(ConstInt(0), VarRef(tempIndex))));
        Translator.curBlock.add(Store(VarRef(tempIndexStored), ConstInt(0)));

        //now increase index by 1: i++
        TemporaryVar tempIndexIncreased = TemporaryVar("index increased temp");
        Translator.curBlock.add(BinaryOperation(tempIndexIncreased, VarRef(tempIndex), Add(), ConstInt(1)));
        //put the updated value into the actual index
        Translator.curBlock.add(Store(VarRef(varIndex), VarRef(tempIndexIncreased)));
        //go back up and check condition
        Translator.curBlock.add(Jump(conditionBlock));

        //handle rest of code, i.e: leave while loop's body
        Translator.blocks.add(restBlock);
        Translator.curBlock = restBlock;

    }



}
