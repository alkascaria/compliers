package translation;

import minijava.ast.*;

/**
 * Created by alka on 6/5/2017.
 */
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

        Object operand1 = 0, operand2 = 0;

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

        MJUnaryOperator unaryOperator = exprUnary.getUnaryOperator();      //unaryOperator

        MJExpr expr = exprUnary.getExpr();                               //unaryExpression

        return unaryOperator.match(new MJUnaryOperator.Matcher<Object>() {

            MJExpr unary = exprUnary.getExpr();

            @Override
            public Object case_UnaryMinus(MJUnaryMinus unaryMinus) {
                int operand;

                if (exprUnary instanceof MJVarUse) {
                    String name = ((MJVarUse) exprUnary).getVarName();
                    operand = -(Translator.varsStackInt.get(name));      //assg the value
                }
                else {
                    operand = -(((MJNumber) unary).getIntValue());
                }
                return operand;
            }

            @Override
            public Object case_Negate(MJNegate negate) {
                boolean operandBool;
                if (exprUnary instanceof MJVarUse) {
                    String name = ((MJVarUse) exprUnary).getVarName();
                    operandBool = !(Translator.varsStackBool.get(name));      //assg the value
                } else
                    operandBool = !(((MJBoolConst) unary).getBoolValue());
                return operandBool;
            }
        });
    }

    @Override
    public Object case_BoolConst(MJBoolConst boolConst) {
        boolean operandBool = boolConst.getBoolValue();
        return operandBool;
    }

    @Override
    public Object case_Number(MJNumber number) {
        int operand = number.getIntValue();
        return operand;
    }

    @Override
    public Object case_VarUse(MJVarUse varUse) {
        String name = varUse.getVarName();
        MJVarDecl varDecl = varUse.getVariableDeclaration();
        MJType type = varDecl.getType();

        return type.match(new MJType.Matcher<Object>() {
            @Override
            public Object case_TypeClass(MJTypeClass typeClass) {
                return null;
            }

            @Override
            public Object case_TypeBool(MJTypeBool typeBool) {
                boolean operand = Translator.varsStackBool.get(name);
                return operand;
            }

            @Override
            public Object case_TypeIntArray(MJTypeIntArray typeIntArray) {
                return null;
            }

            @Override
            public Object case_TypeInt(MJTypeInt typeInt) {
                int operand = Translator.varsStackInt.get(name);      //value of the binaryLeft expr
                return operand;
            }
        });
    }

    @Override
    public Object case_NewIntArray(MJNewIntArray newIntArray) {
        return null;
    }

    @Override
    public Object case_ExprThis(MJExprThis exprThis) {
        return null;
    }

    @Override
    public Object case_ArrayLength(MJArrayLength arrayLength) {
        return null;
    }

    @Override
    public Object case_NewObject(MJNewObject newObject) {
        return null;
    }

    @Override
    public Object case_ArrayLookup(MJArrayLookup arrayLookup) {
        return null;
    }
}
