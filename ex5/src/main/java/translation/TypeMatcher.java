package translation;

import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;

import static minillvm.ast.Ast.TypeArray;
import static minillvm.ast.Ast.TypeBool;
import static minillvm.ast.Ast.TypeInt;

/**
 * Created by Daniele on 20/06/2017.
 */
public class TypeMatcher implements MJType.Matcher<Type>
{

    //TODO: usage of classes goes here
    @Override
    public Type case_TypeClass(MJTypeClass typeClass)
    {
        return Translator.structsMap.get(typeClass.getClassDeclaration());
    }

    @Override
    public Type case_TypeBool(MJTypeBool typeBool) {
        return TypeBool();
    }

    @Override
    public Type case_TypeIntArray(MJTypeIntArray typeIntArray) {
        return TypeArray(TypeInt(), 0);
    }

    @Override
    public Type case_TypeInt(MJTypeInt typeInt)
    {
        return TypeInt();
    }
}
