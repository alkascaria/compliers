package translation;

import minijava.ast.*;
import minillvm.ast.Operand;

import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;
import static minillvm.ast.Ast.VarRef;


/**
 * Created by Daniele on 02/06/2017.
 */
public class ExprTranslatorMatcher implements MJExpr.Matcher<Operand> {


    /**
     * @param fieldAccess
     * @return
     */
    @Override
    public Operand case_FieldAccess(MJFieldAccess fieldAccess) {
        return null;
    }

    /**
     * @param exprBinary
     * @return
     */
    @Override
    public Operand case_ExprBinary(MJExprBinary exprBinary) {
        //TODO Refactor to avoid 'instanceof' if possible

        int count = 0;      //setting a count to identify if there is a varUse()

        MJExpr exprLeft = exprBinary.getLeft();
        MJExpr exprRight = exprBinary.getRight();
        MJOperator operator = exprBinary.getOperator();

        MJVarDecl varDecl;
        MJType typeVar = null;
        String name = null;
        int operand1 = 0, operand2 = 0;

        //check if there is a varUse
        // if exprRight is a varuse
        if (exprRight instanceof MJVarUse) {
            count = 1;          //count is changed
            name = ((MJVarUse) exprRight).getVarName();     //the name of the variable
            varDecl = ((MJVarUse) exprRight).getVariableDeclaration();
            typeVar = varDecl.getType();        //type of the vardec
            operand1 = ((MJNumber) exprLeft).getIntValue();         //operand1=exprLeft
        }//if exprLeft is a varuse
        else if (exprLeft instanceof MJVarUse) {
            count = 2;
            name = ((MJVarUse) exprLeft).getVarName();      //the name of the variable
            varDecl = ((MJVarUse) exprLeft).getVariableDeclaration();
            typeVar = varDecl.getType();         //type of the vardec
            operand2 = ((MJNumber) exprRight).getIntValue();        //operand2=exprRight
        } else {
            //There is a unaryMinus
            if (exprLeft instanceof MJExprUnary) {
                MJExpr unLeft = (((MJExprUnary) exprLeft).getExpr());       //getting the unary operand
                operand1 = -((((MJNumber) unLeft).getIntValue()));      //operand1=unary
                operand2 = (((MJNumber) exprRight).getIntValue());      //operand2=exprRigh
            } else if (exprRight instanceof MJExprUnary) {
                MJExpr unRight = (((MJExprUnary) exprRight).getExpr());       //getting the unary operand
                operand2 = -((((MJNumber) unRight).getIntValue()));     //operand2=unary
                operand1 = (((MJNumber) exprLeft).getIntValue());       //operand1=exprLeft
            }//Both are numbers
            else {
                operand1 = (((MJNumber) exprLeft).getIntValue());       //operand1=exprLeft
                operand2 = (((MJNumber) exprRight).getIntValue());      //operand2=exprRigh
            }
        }

        if (typeVar instanceof MJTypeInt) {
            if (count == 1) {
                operand2 = (Translator.varsStackInt.get(name));
            } else if (count == 2) {
                operand1 = (Translator.varsStackInt.get(name));
            }
        }

        //Order of the operand is impoertant. eg. -2-1 is different from 1- -2
        int finalOperand1 = operand1;
        int finalOperand2 = operand2;
        return operator.match(new MJOperator.Matcher<Operand>() {
            @Override
            public Operand case_Plus(MJPlus plus) {
                return Ast.ConstInt(finalOperand1 + finalOperand2);
            }

            @Override
            public Operand case_Minus(MJMinus minus) {
                return Ast.ConstInt(finalOperand1 - finalOperand2);
            }

            @Override
            public Operand case_Equals(MJEquals equals) {
                return null;
            }

            @Override
            public Operand case_And(MJAnd and) {
                return null;
            }

            @Override
            public Operand case_Less(MJLess less) {
                return null;
            }

            @Override
            public Operand case_Div(MJDiv div)
            {
                if(finalOperand2 == 0)
                {
                    HaltWithError haltWithError = HaltWithError("Test");
                    Translator.curBlock.add(haltWithError);
                    Translator.curBlockErrors.add(haltWithError);
                    //Translator.curBlock.add(HaltWithError("Arithmetic error: dividing by 0 is not allowed."));
                    return Ast.ConstInt(-1);
                }

                return Ast.ConstInt(finalOperand1 / finalOperand2);



            }

            @Override
            public Operand case_Times(MJTimes times) {
                return Ast.ConstInt(finalOperand1 * finalOperand2);
            }
        });
    }

    /**
     * @param exprNull
     * @return
     */
    @Override
    public Operand case_ExprNull(MJExprNull exprNull) {
        return Ast.Nullpointer();
    }

    /**
     * @param methodCall
     * @return
     */
    @Override
    public Operand case_MethodCall(MJMethodCall methodCall) {
        return null;
    }

    /**
     * @param exprUnary
     * @return
     */
    @Override
    public Operand case_ExprUnary(MJExprUnary exprUnary) {
        MJExpr unary = exprUnary.getExpr();       //getting the unary operand
        int unaryValue = 0;

        //check if there is a varuse get its value instead.
        if (unary instanceof MJVarUse)
        {
            //if var use is there,
            unaryValue = -(Translator.varsStackInt.get(((MJVarUse) unary).getVarName()));
        }

        return Ast.ConstInt(unaryValue);
    }

    /**
     * @param boolConst
     * @return
     */
    @Override
    public Operand case_BoolConst(MJBoolConst boolConst) {
        return Ast.ConstBool(boolConst.getBoolValue());
    }

    /**
     * @param number
     * @return
     */
    @Override
    public Operand case_Number(MJNumber number) {
        //Optimized
        return Ast.ConstInt(number.getIntValue());
    }

    /**
     * @param varUse(@code MJVarUse)
     * @return
     */
    @Override
    public Operand case_VarUse(MJVarUse varUse) {
        MJVarDecl varDecl = varUse.getVariableDeclaration();
        MJType typeVar = varDecl.getType();

        return typeVar.match(new MJType.Matcher<Operand>() {
            @Override
            public Operand case_TypeIntArray(MJTypeIntArray typeIntArray) {
                return null;
            }

            @Override
            public Operand case_TypeBool(MJTypeBool typeBool) {
                return Ast.ConstBool(Translator.varsStackBool.get(varUse.getVarName()));
            }

            @Override
            public Operand case_TypeInt(MJTypeInt typeInt) {
                return Ast.ConstInt(Translator.varsStackInt.get(varUse.getVarName()));
            }

            @Override
            public Operand case_TypeClass(MJTypeClass typeClass) {
                return null;
            }
        });
    }

    /**
     * @param newIntArray
     * @return
     */
    @Override
    public Operand case_NewIntArray(MJNewIntArray newIntArray) {
        return null;
    }

    /**
     * @param exprThis
     * @return
     */
    @Override
    public Operand case_ExprThis(MJExprThis exprThis) {
        return null;
    }

    /**
     * @param arrayLength
     * @return
     */
    @Override
    public Operand case_ArrayLength(MJArrayLength arrayLength) {
        return null;
    }

    /**
     * @param newObject
     * @return
     */
    @Override
    public Operand case_NewObject(MJNewObject newObject) {
        return null;
    }

    /**
     * @param arrayLookup
     * @return
     */
    @Override
    public Operand case_ArrayLookup(MJArrayLookup arrayLookup) {
        return null;
    }
}