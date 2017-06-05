package translation;

import minijava.ast.*;
import minijava.ast.MJExpr.MatcherVoid;
import minillvm.ast.Ast;
import minillvm.ast.HaltWithError;

import static translation.Translator.updateHashMapsVariableBool;
import static translation.Translator.updateHashMapsVariableInt;

/**
 * Created by Daniele on 05/06/2017.
 * Matches the right-hand side of an expression,
 */
public class AssignMatcherRightVarUse implements MatcherVoid {
    String nameVarLeft = "";

    @Override
    public void case_FieldAccess(MJFieldAccess fieldAccess) {


    }

    @Override
    public void case_ExprBinary(MJExprBinary exprBinary) {

        String nameLeft=this.nameVarLeft;

        //operands
        int operand1 = 0, operand2 = 0;
        boolean operandBool1 = false, operandBool2 = false;

        MJExpr binaryLeft = ((MJExprBinary) exprBinary).getLeft();     //Left expr of the ExprBinary
        MJExpr binaryRight = ((MJExprBinary) exprBinary).getRight();   //Right exxpr of ExprBinary
        MJOperator binaryOperator = ((MJExprBinary) exprBinary).getOperator();     //Operator of ExprBinary

        //For binaryLeft
        //binaryLeft is a number eg. 5
        if (binaryLeft instanceof MJNumber) {
            operand1 = (((MJNumber) binaryLeft).getIntValue());
        }
        //binaryLeft is a variable eg. a
        else if (binaryLeft instanceof MJVarUse) {

            String binaryLeftName = (((MJVarUse) binaryLeft).getVarName());       //name of the binaryLeft expr
            MJVarDecl varDecl = ((MJVarUse) binaryLeft).getVariableDeclaration();
            MJType type = (varDecl.getType());   //Type of the binaryLeft expr

            //a is of int
            if (type instanceof MJTypeInt) {
                operand1 = Translator.varsStackInt.get(binaryLeftName);      //value of the binaryLeft expr
            }
            // a is boolean
            else if (type instanceof MJTypeBool) {
                operandBool1 = Translator.varsStackBool.get(binaryLeftName);
            }
        }
        //binaryLeft is a unary eg. -a/!true
        else if (binaryLeft instanceof MJExprUnary) {

            MJUnaryOperator unaryOperator = ((MJExprUnary) binaryLeft).getUnaryOperator();      //unaryOperator
            MJExpr exprUnary = ((MJExprUnary) binaryLeft).getExpr();        //unaryExpression

            //unaryExpression is a variable eg.-a
            if (exprUnary instanceof MJVarUse) {

                String nameUnary = ((MJVarUse) exprUnary).getVarName();     //name of the variable

                //identifying the operator
                if (unaryOperator instanceof MJUnaryMinus) {
                    operand1 = -(Translator.varsStackInt.get(nameUnary));      //assg the value
                }
                else if (unaryOperator instanceof MJNegate) {
                    operandBool1 = !(Translator.varsStackBool.get(nameUnary));
                }
            }
            //eg. -5
            else if (exprUnary instanceof MJNumber) {
                operand1 = -(((MJNumber) exprUnary).getIntValue());
            }
            //eg. !true
            else if (exprUnary instanceof MJBoolConst) {
                operandBool1 = !(((MJBoolConst) exprUnary).getBoolValue());
            }
        }
        //boolean
        else if (binaryLeft instanceof MJBoolConst) {
            operandBool1 = ((MJBoolConst) binaryLeft).getBoolValue();
        }

        //For binaryRight
        //binaryRight is a number
        if (binaryRight instanceof MJNumber) {
            operand2 = ((MJNumber) binaryRight).getIntValue();
        }
        //is a variable eg. a
        else if (binaryRight instanceof MJVarUse) {

            String binaryRightName = (((MJVarUse) binaryRight).getVarName());       //name of the binaryLeft expr
            MJVarDecl varDecl = ((MJVarUse) binaryRight).getVariableDeclaration();
            MJType type = (varDecl.getType());   //Type of the binaryLeft expr

            //is int
            if (type instanceof MJTypeInt) {
                operand2 = Translator.varsStackInt.get(binaryRightName);      //value of the binaryLeft expr
            }
            //is boolean
            else if (type instanceof MJTypeBool) {
                operandBool2 = Translator.varsStackBool.get(binaryRightName);
            }
        }
        //is unary
        else if (binaryRight instanceof MJExprUnary) {
            MJUnaryOperator unaryOperator = ((MJExprUnary) binaryRight).getUnaryOperator();
            MJExpr exprUnary = ((MJExprUnary) binaryRight).getExpr();

            //is a variable
            if (exprUnary instanceof MJVarUse) {

                String nameUnary = ((MJVarUse) exprUnary).getVarName();

                if (unaryOperator instanceof MJUnaryMinus) {
                    operand2 = -(Translator.varsStackInt.get(nameUnary));
                } else if (unaryOperator instanceof MJNegate) {
                    operandBool2 = !(Translator.varsStackBool.get(nameUnary));
                }
            }
            //is a number
            else if (exprUnary instanceof MJNumber) {
                operand2 = -(((MJNumber) exprUnary).getIntValue());
            }
            //is a number
            else if (exprUnary instanceof MJBoolConst) {
                operandBool2 = !(((MJBoolConst) exprUnary).getBoolValue());
            }
        }
        //is boolean
        else if (binaryRight instanceof MJBoolConst) {
            operandBool2 = ((MJBoolConst) binaryRight).getBoolValue();
        }

        int finalOperand1 = operand1;
        int finalOperand2 = operand2;

        boolean finalOperandBool = operandBool1;
        boolean finalOperandBool1 = operandBool2;

        //check for the operators
        binaryOperator.match(new MJOperator.MatcherVoid() {
            int valueRight = 0;
            boolean valueRightBool;

            @Override
            public void case_Plus(MJPlus plus) {
                valueRight = finalOperand1 + finalOperand2;
                updateHashMapsVariableInt(nameLeft, valueRight);
            }

            @Override
            public void case_Minus(MJMinus minus) {
                valueRight = finalOperand1 - finalOperand2;
                updateHashMapsVariableInt(nameLeft, valueRight);
            }

            @Override
            public void case_Equals(MJEquals equals) {
                //in assg there is no case of equals
                //but in if it can occur
            }

            @Override
            public void case_And(MJAnd and) {
                valueRightBool = finalOperandBool && finalOperandBool1;
                updateHashMapsVariableBool(nameLeft, valueRightBool);
            }

            @Override
            public void case_Less(MJLess less) {
                valueRightBool = finalOperand1 < finalOperand2;
                updateHashMapsVariableBool(nameLeft, valueRightBool);
            }

            @Override
            public void case_Div(MJDiv div) {
                if (finalOperand2 == 0) {
                    HaltWithError haltWithError = Ast.HaltWithError("Arithmetic Exception");
                    Translator.curBlock.add(haltWithError);
                    Translator.curBlockErrors.add(haltWithError);
                    //Translator.curBlock.add(HaltWithError("Arithmetic error: dividing by 0 is not allowed."));
                } else {
                    valueRight = finalOperand1 / finalOperand2;
                    updateHashMapsVariableInt(nameLeft, valueRight);
                }
            }

            @Override
            public void case_Times(MJTimes times) {
                valueRight = finalOperand1 * finalOperand2;
                updateHashMapsVariableInt(nameLeft, valueRight);
            }

        });

    }

    @Override
    public void case_ExprNull(MJExprNull exprNull) {

    }

    @Override
    public void case_MethodCall(MJMethodCall methodCall) {

    }

    @Override
    public void case_ExprUnary(MJExprUnary exprUnary) {
        MJExpr unaryRight = exprUnary.getExpr();

        String nameLeft = this.nameVarLeft;

        //match different cases again
        unaryRight.match(new MatcherVoid() {
            @Override
            public void case_FieldAccess(MJFieldAccess fieldAccess) {

            }

            @Override
            public void case_ExprBinary(MJExprBinary exprBinary) {

            }

            @Override
            public void case_ExprNull(MJExprNull exprNull) {

            }

            @Override
            public void case_MethodCall(MJMethodCall methodCall) {

            }

            @Override
            public void case_ExprUnary(MJExprUnary exprUnary) {

            }

            //unary neg with bool constants (ex: d = !false)
            @Override
            public void case_BoolConst(MJBoolConst boolConst) {
                boolean boolRight = !(boolConst.getBoolValue());
                updateHashMapsVariableBool(nameLeft, boolRight);
            }

            //unary minus with int constants (ex: d = -5)
            @Override
            public void case_Number(MJNumber number) {
                int valueRight = -(number.getIntValue());
                updateHashMapsVariableInt(nameLeft, valueRight);
            }

            //ex: a = !b and a =-b
            @Override
            public void case_VarUse(MJVarUse varUse) {
                //need to check for two subcases
                MJType typeVarUse = varUse.getVariableDeclaration().getType();
                String nameRight = varUse.getVarName();

                if (typeVarUse instanceof MJTypeInt) {
                    int varIntRight = -(Translator.varsStackInt.get(nameRight));
                    updateHashMapsVariableInt(nameLeft, varIntRight);
                } else if (typeVarUse instanceof MJTypeBool) {
                    boolean varBoolRight = !(Translator.varsStackBool.get(nameRight));
                    updateHashMapsVariableBool(nameLeft, varBoolRight);
                }

            }

            @Override
            public void case_NewIntArray(MJNewIntArray newIntArray) {

            }

            @Override
            public void case_ExprThis(MJExprThis exprThis) {

            }

            @Override
            public void case_ArrayLength(MJArrayLength arrayLength) {

            }

            @Override
            public void case_NewObject(MJNewObject newObject) {

            }

            @Override
            public void case_ArrayLookup(MJArrayLookup arrayLookup) {

            }
        });


    }

    //ex: x = false
    @Override
    public void case_BoolConst(MJBoolConst boolConst) {
        boolean boolRight = boolConst.getBoolValue();
        updateHashMapsVariableBool(this.nameVarLeft, boolRight);
    }

    //ex: z = 5
    @Override
    public void case_Number(MJNumber number) {
        int valueRight = number.getIntValue();
        updateHashMapsVariableInt(this.nameVarLeft, valueRight);

    }

    //ex: b = d (check case where d is int and boolean)
    @Override
    public void case_VarUse(MJVarUse varUse) {
        MJType typeExprRight = varUse.getVariableDeclaration().getType();
        String nameRight = varUse.getVarName();

        //assigning integer variable to integer variable
        if (typeExprRight instanceof MJTypeInt) {
            //get value from right-hand side and assign to left-hand side variable
            int varRight = Translator.varsStackInt.get(nameRight);
            updateHashMapsVariableInt(this.nameVarLeft, varRight);
        }
        //assigning boolean variable to boolean variable
        else if (typeExprRight instanceof MJTypeBool) {
            //get value from right-hand side and assign to left-hand side
            boolean boolRight = Translator.varsStackBool.get(nameRight);
            updateHashMapsVariableBool(this.nameVarLeft, boolRight);
        }

    }

    @Override
    public void case_NewIntArray(MJNewIntArray newIntArray) {

    }

    @Override
    public void case_ExprThis(MJExprThis exprThis) {

    }

    @Override
    public void case_ArrayLength(MJArrayLength arrayLength) {

    }

    @Override
    public void case_NewObject(MJNewObject newObject) {

    }

    @Override
    public void case_ArrayLookup(MJArrayLookup arrayLookup) {

    }
}
