package translation;
import minijava.ast.*;
import minillvm.ast.Ast;
import minijava.ast.MJOperator;
import minillvm.ast.Operator;
import minijava.ast.MJDiv;
import minijava.ast.MJEquals;
import minijava.ast.MJAnd;
import minijava.ast.MJPlus;
import minijava.ast.MJTimes;
import minijava.ast.MJLess;
import minijava.ast.MJMinus;


/**
 * OperatorChecker
 * Created by praveengopal on 03/06/2017.
 */
public class OperatorChecker implements MJOperator.Matcher<Operator> {

    //Todo operator checker

    //Operator = Add() | Sub() | Mul() | Sdiv() | Srem()
     //   | And() | Or() | Xor()
    //| Eq() | Sgt() | Sge() | Slt() | Sle()
    /**
     *
     * @param times(@code MJTimes)
     * @return
     */
    @Override
    public Operator case_Times(MJTimes times) {
        return Ast.Mul();
    }


    /**
     *
     * @param equals(@code MJEquals)
     * @return
     */
    @Override
    public Operator case_Equals(MJEquals equals) {
        return Ast.Eq();
    }

    /**
     *
     * @param minus(@code MJMinus)
     * @return
     */
    @Override
    public Operator case_Minus(MJMinus minus) {
        return Ast.Sub();
    }

    /**
     *
     * @param div(@code MJDiv)
     * @return
     */
    @Override
    public Operator case_Div(MJDiv div) {
        return Ast.Sdiv();
    }

    /**
     *
     * @param less(@code MJLess)
     * @return
     */
    @Override
    public Operator case_Less(MJLess less) {
        return Ast.Slt();
    }

    /**
     *
     * @param plus(@code MJPlus)
     * @return
     */
    @Override
    public Operator case_Plus(MJPlus plus) {
        return Ast.Add();
    }

    /**
     *
     * @param and(@code MJAnd)
     * @return
     */
    @Override
    public Operator case_And(MJAnd and) {
        return Ast.And();
    }


}
