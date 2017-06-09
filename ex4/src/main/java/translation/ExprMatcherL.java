package translation;

import minijava.ast.*;
import minillvm.ast.Ast;
import minillvm.ast.Load;
import minillvm.ast.TemporaryVar;

import static minillvm.ast.Ast.VarRef;

/**
 * Created by Daniele on 09/06/2017.
 */
public class ExprMatcherL implements MJExpr.Matcher<TemporaryVar> {
    @Override
    public TemporaryVar case_FieldAccess(MJFieldAccess fieldAccess)
    {
        //throw illegalstatementexception
        return null;
    }

    @Override
    public TemporaryVar case_ExprBinary(MJExprBinary exprBinary) {
        return null;
    }

    @Override
    public TemporaryVar case_ExprNull(MJExprNull exprNull) {
        return null;
    }

    @Override
    public TemporaryVar case_MethodCall(MJMethodCall methodCall) {
        return null;
    }

    @Override
    public TemporaryVar case_ExprUnary(MJExprUnary exprUnary) {
        return null;
    }

    @Override
    public TemporaryVar case_BoolConst(MJBoolConst boolConst) {
        return null;
    }

    @Override
    public TemporaryVar case_Number(MJNumber number) {
        return null;
    }

    //TODO: put types for var use into its own class both in ExprMatcherL and ExprMatcherR
    public TemporaryVar case_VarUse(MJVarUse varUse)
    {
        String varName = varUse.getVarName();
        MJVarDecl varDecl = varUse.getVariableDeclaration();
        MJType type = varDecl.getType();

        //just try for integer for now.

        //now match the type of the variable being used
        return type.match(new MJType.Matcher<TemporaryVar>() {
            /**
             *
             * @param typeClass(@code MJTypeClass)
             * @return
             */
            @Override
            public TemporaryVar case_TypeClass(MJTypeClass typeClass)
            {
                return null;
            }

            /**
             *
             * @param typeBool(@code MJTypeBool)
             * @return
             */
            @Override
            public TemporaryVar case_TypeBool(MJTypeBool typeBool) {
                return Translator.varsTemp.get(varName);
            }

            /**
             *
             * @param typeIntArray(@code MJTypeIntArray)
             * @return
             */
            @Override
            public TemporaryVar case_TypeIntArray(MJTypeIntArray typeIntArray)
            {
                return null;
            }

            /**
             *
             * @param typeInt(@code MJTypeInt)
             * @return
             */
            @Override
            public TemporaryVar case_TypeInt(MJTypeInt typeInt)
            {
                //the corresponding variable in the hashmap
                return Translator.varsTemp.get(varName);
            }
        });
    }

    @Override
    public TemporaryVar case_NewIntArray(MJNewIntArray newIntArray) {
        return null;
    }

    @Override
    public TemporaryVar case_ExprThis(MJExprThis exprThis) {
        return null;
    }

    @Override
    public TemporaryVar case_ArrayLength(MJArrayLength arrayLength) {
        return null;
    }

    @Override
    public TemporaryVar case_NewObject(MJNewObject newObject) {
        return null;
    }

    @Override
    public TemporaryVar case_ArrayLookup(MJArrayLookup arrayLookup) {
        return null;
    }
}
