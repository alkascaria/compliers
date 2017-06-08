package translation;

import minijava.ast.*;
import minillvm.ast.*;

import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;

import minillvm.analysis.*;

import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;

import static javafx.scene.input.KeyCode.T;

/**
 * Created by alka on 6/6/2017.
 * Modified by Daniele on 8/6/2017
 */

public class OperatorMatcher implements MJOperator.Matcher<Operand>
{

    Operand operLeft, operRight;

    OperatorMatcher(Operand operInput1, Operand operInput2)
    {
        this.operLeft = operInput1;
        this.operRight = operInput2;
    }
    /**
     *
     * @param plus(@code MJPlus)
     * @return
     */
    @Override
    public Operand case_Plus(MJPlus plus)
    {
        TemporaryVar tempVar = TemporaryVar("temp");
        BinaryOperation binSum = BinaryOperation(tempVar, this.operLeft, Add(), this.operRight);
        Translator.curBlock.add(binSum);

        return VarRef(tempVar);
    }

    /**
     *
     * @param minus(@code MJMinus)
     * @return
     */
    @Override
    public Operand case_Minus(MJMinus minus)
    {
        TemporaryVar tempVar = TemporaryVar("temp");
        BinaryOperation binSub = BinaryOperation(tempVar, this.operLeft, Sub(), this.operRight);
        Translator.curBlock.add(binSub);

        return VarRef(tempVar);
    }

    /**
     *
     * @param equals(@code MJEquals)
     * @return
     */
    @Override
    public Operand case_Equals(MJEquals equals)
    {
        TemporaryVar tempVar = TemporaryVar("temp");
        BinaryOperation binEq = BinaryOperation(tempVar, this.operLeft, Eq(), this.operRight);
        Translator.curBlock.add(binEq);

        return VarRef(tempVar);

    }

    /**
     *
     * @param and(@code MJAnd)
     * @return
     */
    @Override
    public Operand case_And(MJAnd and)
    {
        TemporaryVar tempVar = TemporaryVar("temp");
        BinaryOperation binAnd = BinaryOperation(tempVar, this.operLeft, And(), this.operRight);
        Translator.curBlock.add(binAnd);

        return VarRef(tempVar);
    }

    /**
     *
     * @param less(@code MJLess)
     * @return
     */
    @Override
    public Operand case_Less(MJLess less)
    {
        TemporaryVar tempVar = TemporaryVar("temp");
        BinaryOperation binLess = BinaryOperation(tempVar, this.operLeft, Slt(), this.operRight);
        Translator.curBlock.add(binLess);

        return VarRef(tempVar);
    }

    /**
     *
     * @param div(@code MJDiv)
     * @return
     */
    @Override
    public Operand case_Div(MJDiv div)
    {

        TemporaryVar tempVar = TemporaryVar("temp");
        BinaryOperation binDiv = BinaryOperation(tempVar, this.operLeft, Sdiv(), this.operRight);
        Translator.curBlock.add(binDiv);

        return VarRef(tempVar);

    }

    /**
     *
     * @param times(@code MJTimes)
     * @return
     */
    @Override
    public Operand case_Times(MJTimes times)
    {
        TemporaryVar tempVar = TemporaryVar("temp");
        BinaryOperation binTimes = BinaryOperation(tempVar, this.operLeft, Mul(), this.operRight);
        Translator.curBlock.add(binTimes);

        return VarRef(tempVar);
    }
}
