package translation;

import minijava.ast.*;
import minillvm.ast.Ast;
import minillvm.ast.HaltWithError;

/**
 * Created by alka on 6/6/2017.
 */

//TODO returing Object as return can be either int or boolean....Try to find an alternative

public class OperatorMatcher implements MJOperator.Matcher {

    //There can be boolean as well as integer operations
    int operandInt1 = 0, operandInt2 = 0;
    boolean operandBool1 = false, operandBool2 = false;

    //getting the operands and identifying whether its boolean or integer
    void getOperands(Object operand1, Object operand2) {
        if ((operand1 instanceof Integer) && (operand2 instanceof Integer)) {
            operandInt1 = (int) operand1;
            operandInt2 = (int) operand2;
        }
        else {
            operandBool1 = (boolean) operand1;
            operandBool2 = (boolean) operand2;
        }
    }

    /**
     *
     * @param plus(@code MJPlus)
     * @return
     */
    @Override
    public Object case_Plus(MJPlus plus)
    {
        return (operandInt1 + operandInt2);
    }

    /**
     *
     * @param minus(@code MJMinus)
     * @return
     */
    @Override
    public Object case_Minus(MJMinus minus)
    {

        return (operandInt1 - operandInt2);
    }

    /**
     *
     * @param equals(@code MJEquals)
     * @return
     */
    @Override
    public Object case_Equals(MJEquals equals)
    {
        return (operandBool1 = operandBool2);
    }

    /**
     *
     * @param and(@code MJAnd)
     * @return
     */
    @Override
    public Object case_And(MJAnd and)
    {
        return (operandBool1 && operandBool2);
    }

    /**
     *
     * @param less(@code MJLess)
     * @return
     */
    @Override
    public Object case_Less(MJLess less)
    {
        return (operandInt1 < operandInt2);
    }

    /**
     *
     * @param div(@code MJDiv)
     * @return
     */
    @Override
    public Object case_Div(MJDiv div) {
        if (operandInt2 == 0) {
            HaltWithError haltWithError = Ast.HaltWithError("Arithmetic Exception. Dividing by 0 is not allowed");
            Translator.curBlock.add(haltWithError);
            Translator.curBlockErrors.add(haltWithError);
            return (-1);
        } else
            return (operandInt1 / operandInt2);
    }

    /**
     *
     * @param times(@code MJTimes)
     * @return
     */
    @Override
    public Object case_Times(MJTimes times)
    {
        return (operandInt1 * operandInt2);
    }
}
