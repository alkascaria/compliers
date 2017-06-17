package translation;

import minijava.ast.*;
import minillvm.ast.*;

import java.io.InvalidObjectException;
import java.security.InvalidParameterException;

import static minillvm.ast.Ast.*;
import static minillvm.ast.Ast.TypePointer;

/**
 * Created by Daniele on 09/06/2017.
 * Matches the correct left-hand side
 */


public class ExprMatcherL implements MJExpr.Matcher<Operand> {
    @Override
    public Operand case_FieldAccess(MJFieldAccess fieldAccess)  {
        throw new InvalidParameterException("Field access " + fieldAccess.toString() + " not supported as left-hand side expression!");
    }

    @Override
    public Operand case_ExprBinary(MJExprBinary exprBinary)
    {
        throw new InvalidParameterException("Binary expression " + exprBinary.toString() + " not supported as left-hand side expression!");

    }

    @Override
    public Operand case_ExprNull(MJExprNull exprNull)
    {
        throw new InvalidParameterException("Null reference  " +  exprNull.toString() + " not supported as left-hand side expression!");

    }

    @Override
    public Operand case_MethodCall(MJMethodCall methodCall)
    {
        throw new InvalidParameterException("Method call " + methodCall.toString() + " not supported as left-hand side expression!");

    }

    @Override
    public Operand case_ExprUnary(MJExprUnary exprUnary)
    {
        throw new InvalidParameterException("Unary expression " + exprUnary.toString() + " not supported as left-hand side expression!");

    }

    @Override
    public Operand case_BoolConst(MJBoolConst boolConst)
    {
        throw new InvalidParameterException("Boolean constant " + boolConst.toString() + " not supported as left-hand side expression!");

    }

    @Override
    public Operand case_Number(MJNumber number)
    {
        throw new InvalidParameterException("Integer constant not supported as left-hand side expression!");
    }

    public Operand case_VarUse(MJVarUse varUse)
    {
        String varName = varUse.getVarName();
        MJVarDecl varDecl = varUse.getVariableDeclaration();
        MJType type = varDecl.getType();

        //now match the type of the variable being used
        return type.match(new MJType.Matcher<Operand>() {
            /**
             *
             * @param typeClass(@code MJTypeClass)
             * @return
             */
            @Override
            public Operand case_TypeClass(MJTypeClass typeClass)
            {
                throw new InvalidParameterException("Variable class usage  " + typeClass.toString() + " not supported as left-hand side expression!");

            }

            /**
             *
             * @param typeBool(@code MJTypeBool)
             * @return
             */
            @Override
            public Operand case_TypeBool(MJTypeBool typeBool) {
                return VarRef(Translator.varsTemp.get(varName));
            }

            /**
             *
             * @param typeIntArray(@code MJTypeIntArray)
             * @return
             */
            @Override
            public Operand case_TypeIntArray(MJTypeIntArray typeIntArray)
            {
                return VarRef(Translator.varsTemp.get(varName));
            }

            /**
             *
             * @param typeInt(@code MJTypeInt)
             * @return
             */
            @Override
            public Operand case_TypeInt(MJTypeInt typeInt)
            {
                //the corresponding variable in the hashmap
                return VarRef(Translator.varsTemp.get(varName));
            }
        });
    }

    @Override
    public Operand case_NewIntArray(MJNewIntArray newIntArray) {
        throw new InvalidParameterException("New integer array instantiation " + newIntArray.toString() + " not supported as left-hand side expression!");

    }

    @Override
    public Operand case_ExprThis(MJExprThis exprThis)
    {
        throw new InvalidParameterException("'this' reference " + exprThis.toString() + " not supported as left-hand side expression!");

    }

    @Override
    public Operand case_ArrayLength(MJArrayLength arrayLength)
    {
        throw new InvalidParameterException("Array length "  + arrayLength.toString() + " not supported as left-hand side expression!");
    }

    @Override
    public Operand case_NewObject(MJNewObject newObject)
    {
        throw new InvalidParameterException("New object instantiation "  + newObject.toString() + " not supported as left-hand side expression!");
    }

    @Override
    /***
     * a[7] = b
     *Set into an array
     * Input: arrayLookup. an expression containing something like a[7]
     * Output: reference to the address where a value should be stored
     */
    public Operand case_ArrayLookup(MJArrayLookup arrayLookup)
    {
        TemporaryVar pointerElementArray = StaticMethods.accessIndexArray(arrayLookup);

        //return pointer to the element desired --> store value into it
        return VarRef(pointerElementArray);
    }



}
