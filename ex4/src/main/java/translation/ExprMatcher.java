package translation;

import minijava.ast.*;

/**
 * Created by alka on 6/5/2017.
 */

//TODO returing Object as return can be either int or boolean....Try to find an alternative

public class ExprMatcher implements MJExpr.Matcher {

    @Override
    public Object case_FieldAccess(MJFieldAccess fieldAccess) {
        return null;
    }

    @Override
    public Object case_ExprBinary(MJExprBinary exprBinary) {

        MJExpr exprLeft = exprBinary.getLeft();             //Left of the binaryExpr
        MJExpr exprRight = exprBinary.getRight();           //Right of the binaryExpr
        MJOperator operator = exprBinary.getOperator();     //Operator of the binaryExpr

        Object operand1 , operand2;

        //Matching it with the expr and getting the operands
        ExprMatcher exprMatcher = new ExprMatcher();
        operand1 = exprLeft.match(exprMatcher);
        operand2 = exprRight.match(exprMatcher);

        //Doing the operation operand1 operator operand2
        OperatorMatcher operatorMatcher = new OperatorMatcher();
        operatorMatcher.getOperands(operand1, operand2);
        Object value = operator.match(operatorMatcher);

        return value;
    }

    @Override
    public Object case_ExprNull(MJExprNull exprNull) {
        return null;
    }

    @Override
    public Object case_MethodCall(MJMethodCall methodCall) {
        return null;
    }

    @Override
    public Object case_ExprUnary(MJExprUnary exprUnary) {
        System.out.println(exprUnary);

        MJUnaryOperator unaryOperator = exprUnary.getUnaryOperator();      //unaryOperator

        MJExpr unary = exprUnary.getExpr();                               //unaryExpression

        ExprMatcher exprMatcher=new ExprMatcher();

        return unaryOperator.match(new MJUnaryOperator.Matcher<Object>() {

            @Override
            public Object case_UnaryMinus(MJUnaryMinus unaryMinus) {
                int operand;

                if (unary instanceof MJVarUse) {
                    String name = ((MJVarUse) unary).getVarName();
                    operand = -(Translator.varsStackInt.get(name));      //assg the value
                } else if (unary instanceof MJNumber){
                    operand = -(((MJNumber) unary).getIntValue());
                }
                else
                    operand=-((int)unary.match(exprMatcher));
                return operand;
            }

            @Override
            public Object case_Negate(MJNegate negate) {
                System.out.println("hello");
                boolean operandBool;
                if (unary instanceof MJVarUse) {
                    System.out.println("hi");
                    String name = ((MJVarUse) unary).getVarName();
                    operandBool = !(Translator.varsStackBool.get(name));      //assg the value
                } else if (unary instanceof MJBoolConst)
                    operandBool = !(((MJBoolConst) unary).getBoolValue());
                else {
                    operandBool = !((boolean)unary.match(exprMatcher));
                }
                return operandBool;
            }
        });
    }

    /**
     *
     * @param boolConst(@code MJBoolConst)
     * @return
     */
    @Override
    public Object case_BoolConst(MJBoolConst boolConst)
    {
        //return boolean
        return boolConst.getBoolValue();
    }

    /**
     *
     * @param number(@code MJNumber)
     * @return
     */
    @Override
    public Object case_Number(MJNumber number) {
        //return int value
        return number.getIntValue();
    }

    @Override
    public Object case_VarUse(MJVarUse varUse) {
        String name = varUse.getVarName();
        MJVarDecl varDecl = varUse.getVariableDeclaration();
        MJType type = varDecl.getType();

        return type.match(new MJType.Matcher<Object>() {
            /**
             *
             * @param typeClass(@code MJTypeClass)
             * @return
             */
            @Override
            public Object case_TypeClass(MJTypeClass typeClass)
            {
                return null;
            }

            /**
             *
             * @param typeBool(@code MJTypeBool)
             * @return
             */
            @Override
            public Object case_TypeBool(MJTypeBool typeBool) {
                //return boolean value
                return Translator.varsStackBool.get(name);
            }

            /**
             *
             * @param typeIntArray(@code MJTypeIntArray)
             * @return
             */
            @Override
            public Object case_TypeIntArray(MJTypeIntArray typeIntArray) {
                return null;
            }

            /**
             *
             * @param typeInt(@code MJTypeInt)
             * @return
             */
            @Override
            public Object case_TypeInt(MJTypeInt typeInt) {
                //return int
                //value of the binaryLeft expr
                return Translator.varsStackInt.get(name);
            }
        });
    }

    /**
     *
     * @param newIntArray(@code MJNewIntArray)
     * @return
     */
    @Override
    public Object case_NewIntArray(MJNewIntArray newIntArray) 
    {
        throw new RuntimeException();
    }

    /**
     *
     * @param exprThis(@code MJExprThis)
     * @return
     */
    @Override
    public Object case_ExprThis(MJExprThis exprThis) 
    {
       throw new RuntimeException();
    }

    /**
     *
     * @param arrayLength(@code MJArrayLength)
     * @return
     */
    @Override
    public Object case_ArrayLength(MJArrayLength arrayLength)
    {
        int operand;
        operand = Translator.varsStackInt.get(arrayLength);
        return operand;
    }

    /**
     *
     * @param newObject(@code MJNewObject)
     * @return
     */
    @Override
    public Object case_NewObject(MJNewObject newObject) {
        throw new RuntimeException();
    }

    /**
     *
     * @param arrayLookup(@code MJArrayLookup)
     * @return
     */
    @Override
    public Object case_ArrayLookup(MJArrayLookup arrayLookup) {
       throw new RuntimeException();
    }
}
