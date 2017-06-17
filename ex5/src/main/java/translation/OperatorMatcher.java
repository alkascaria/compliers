package translation;

import minijava.ast.*;
import minillvm.ast.*;

import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;



import static minillvm.ast.Ast.Add;
import static minillvm.ast.Ast.Sdiv;

/**
 * Created by alka on 6/6/2017.
 * Modified by Daniele on 8/6/2017
 */

public class OperatorMatcher implements MJOperator.Matcher<Operator>
{
    Operand operLeft, operRight;

    public OperatorMatcher(Operand operInputL, Operand operInputR)
    {
        this.operLeft = operInputL;
        this.operRight = operInputR;
    }

    /**
     *
     * @param plus(@code MJPlus)
     * @return
     */
    @Override
    public Operator case_Plus(MJPlus plus)
    {
        return Add();

    }

    /**
     *
     * @param minus(@code MJMinus)
     * @return
     */
    @Override
    public Operator case_Minus(MJMinus minus)
    {

        return Sub();
    }

    /**
     *
     * @param equals(@code MJEquals)
     * @return
     */
    @Override
    public Operator case_Equals(MJEquals equals)
    {
      return Eq();
    }

    /**
     *
     * @param and(@code MJAnd)
     * @return
     */
    @Override
    public Operator case_And(MJAnd and)
    {

        return And();
    }

    /**
     *
     * @param less(@code MJLess)
     * @return
     */
    @Override
    public Operator case_Less(MJLess less)
    {

        return Slt();
    }

    /**
     *
     * @param div(@code MJDiv)
     * @return
     */
    @Override
    public Operator case_Div(MJDiv div)
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


       //if it's good, just evaluate it in ExprMatcherR
        return Sdiv();
    }

    /**
     *
     * @param times(@code MJTimes)
     * @return
     */
    @Override
    public Operator case_Times(MJTimes times)
    {
        return Mul();
    }
}
