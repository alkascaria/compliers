package translation;

import com.sun.org.apache.xpath.internal.operations.UnaryOperation;
import minijava.ast.*;
import minillvm.ast.TemporaryVar;

import minijava.ast.*;
import minillvm.ast.*;

import java.util.function.UnaryOperator;

import static minillvm.ast.Ast.*;


/**
 * Created by alka on 6/5/2017.
 */

//TODO returing Operand as return can be either int or boolean....Try to find an alternative

public class ExprMatcher implements MJExpr.Matcher<Operand> {

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
        ExprMatcher exprMatcher = new ExprMatcher();
        operand1 = exprLeft.match(exprMatcher);
        operand2 = exprRight.match(exprMatcher);

        //Doing the operation operand1 operator operand2
        OperatorMatcher operatorMatcher = new OperatorMatcher();
        operatorMatcher.getOperands(operand1, operand2);
        Operand value = operator.match(operatorMatcher);


        return value;
    }

    /**
     *
     * @param exprNull(@code MJExprNull)
     * @return
     */
    @Override
    public Operand case_ExprNull(MJExprNull exprNull)
    {
        return null;
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

        ExprMatcher exprMatcher=new ExprMatcher();

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
                    System.out.println("Checking unary neg expression");
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
                //return boolean value
                //return int
                TemporaryVar tempBool = TemporaryVar("tempvar");
                //put the value of the varUse found into the tempVar
                Load loadBool = Load(tempBool, VarRef(Translator.varsTemp.get(varName)));
                //not sure if needed
                Translator.curBlock.add(loadBool);

                return VarRef(tempBool);
                //return Translator.varsStackBool.get(varName);
            }

            /**
             *
             * @param typeIntArray(@code MJTypeIntArray)
             * @return
             */
            @Override
            public Operand case_TypeIntArray(MJTypeIntArray typeIntArray)
            {
                return null;
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
                //not sure if needed
                Translator.curBlock.add(loadInt);

                return VarRef(tempInt);
            }
        });
    }

    /**
     *
     * @param newIntArray(@code MJNewIntArray)
     * @return
     */
    @Override
    public Operand case_NewIntArray(MJNewIntArray newIntArray)
    {
        return null;
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
     * @return
     */
    @Override
    public Operand case_ArrayLength(MJArrayLength arrayLength)
    {
        //return int
        //return Translator.varsStackInt.get(arrayLength);
        return null;
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
    public Operand case_ArrayLookup(MJArrayLookup arrayLookup) {
        return null;
    }
}
