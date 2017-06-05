package translation;

import minijava.ast.*;
import minijava.ast.MJExpr.MatcherVoid;

/**
 * Created by Daniele on 05/06/2017.
 * Matches the right-hand side of an expression,
 */
public class AssignMatcherRightVarUse implements MatcherVoid
{
    String nameVarLeft = "";


    @Override
    public void case_FieldAccess(MJFieldAccess fieldAccess)
    {


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
    public void case_ExprUnary(MJExprUnary exprUnary)
    {
        MJExpr unaryRight = exprUnary.getExpr();

        String nameLeft = this.nameVarLeft;

        //match different cases again
        unaryRight.match(new MatcherVoid()
        {
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
            public void case_BoolConst(MJBoolConst boolConst)
            {
                boolean boolRight = !(boolConst.getBoolValue());
                Translator.updateHashMapsVariableBool(nameLeft, boolRight);
            }

            //unary minus with int constants (ex: d = -5)
            @Override
            public void case_Number(MJNumber number)
            {
                int valueRight = -(number.getIntValue());
                Translator.updateHashMapsVariableInt(nameLeft, valueRight);
            }

            //ex: a = !b and a =-b
            @Override
            public void case_VarUse(MJVarUse varUse)
            {
                //need to check for two subcases
                MJType typeVarUse = varUse.getVariableDeclaration().getType();
                String nameRight = varUse.getVarName();

                if(typeVarUse instanceof MJTypeInt)
                {
                    int varIntRight = - (Translator.varsStackInt.get(nameRight));
                    Translator.updateHashMapsVariableInt(nameLeft, varIntRight);
                }
                else if(typeVarUse instanceof MJTypeBool)
                {
                    boolean varBoolRight = !(Translator.varsStackBool.get(nameRight));
                    Translator.updateHashMapsVariableBool(nameLeft, varBoolRight);
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
    public void case_BoolConst(MJBoolConst boolConst)
    {
        boolean boolRight = boolConst.getBoolValue();
        Translator.updateHashMapsVariableBool(this.nameVarLeft, boolRight);
    }

    //ex: z = 5
    @Override
    public void case_Number(MJNumber number)
    {
        int valueRight = number.getIntValue();
        Translator.updateHashMapsVariableInt(this.nameVarLeft, valueRight);

    }

    //ex: b = d (check case where d is int and boolean)
    @Override
    public void case_VarUse(MJVarUse varUse)
    {
        MJType typeExprRight = varUse.getVariableDeclaration().getType();
        String nameRight = varUse.getVarName();

        //assigning integer variable to integer variable
        if(typeExprRight instanceof MJTypeInt)
        {
            //get value from right-hand side and assign to left-hand side variable
            int varRight = Translator.varsStackInt.get(nameRight);
            Translator.updateHashMapsVariableInt(this.nameVarLeft, varRight);
        }
        //assigning boolean variable to boolean variable
        else if(typeExprRight instanceof MJTypeBool)
        {
            //get value from right-hand side and assign to left-hand side
            boolean boolRight = Translator.varsStackBool.get(nameRight);
           Translator.updateHashMapsVariableBool(this.nameVarLeft, boolRight);
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
