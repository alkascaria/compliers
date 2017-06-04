package translation;

import minijava.ast.MJNegate;
import minijava.ast.MJUnaryMinus;
import minijava.ast.MJUnaryOperator;
import minillvm.ast.Ast;
import minillvm.ast.Operator;

/**
 * UnaryOperator Checker
 * Created by praveengopal on 04/06/2017.
 */
public class UnaryOperatorChecker implements MJUnaryOperator.Matcher<Operator> {

    private final Translator translator;
    public UnaryOperatorChecker(Translator translator) {
        this.translator = translator;
    }

    /**
     *
     * @param negate(@code MJNegate)
     * @return
     */
    @Override
    public Operator case_Negate(MJNegate negate) {
        return Ast.Xor();
    }

    /**
     *
     * @param unaryMinus(@code MJUnaryMinus)
     * @return
     */
    @Override
    public Operator case_UnaryMinus(MJUnaryMinus unaryMinus) {
        return Ast.Sub();
    }

}
