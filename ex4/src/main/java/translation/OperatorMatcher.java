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
        //operaRight == zero?
        TemporaryVar tempVarZero = TemporaryVar("temp");
        Operand operZero = ConstInt(0);
        BinaryOperation divZeroTrue = BinaryOperation(tempVarZero, operZero, Eq(), this.operRight);
        Translator.curBlock.add(divZeroTrue);
        //true or false.
        Operand operResultZeroEq = VarRef(tempVarZero);

        BasicBlock blockTrueZero = BasicBlock();
        BasicBlock blockRest = BasicBlock();

        //now branch depending on whether it's true or false
        Branch branchZero = Branch(operResultZeroEq, blockTrueZero, blockRest);
        Translator.curBlock.add(branchZero);

        //if we are indeed dividing by zero
        Translator.curBlock = blockTrueZero;
        Translator.blocks.add(blockTrueZero);
        //then add error
        Translator.curBlock.add(HaltWithError("Dividing by 0"));

        //not dividing by zero
        Translator.curBlock = blockRest;
        Translator.blocks.add(blockRest);


        TemporaryVar tempVar = TemporaryVar("temp");
        BinaryOperation binDiv = BinaryOperation(tempVar, this.operLeft, Sdiv(), this.operRight.copy());

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
