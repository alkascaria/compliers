package translation;

import minijava.ast.*;
import minillvm.ast.Ast;
import minillvm.ast.StructFieldList;
import minillvm.ast.Type;
import minillvm.ast.TypeStruct;

import static minillvm.ast.Ast.TypeArray;
import static minillvm.ast.Ast.TypeBool;
import static minillvm.ast.Ast.TypeInt;

/**
 * Created by Daniele on 20/06/2017.
 */
public class TypeMatcher implements MJType.Matcher<Type> {
    @Override
    public Type case_TypeClass(MJTypeClass typeClass)
    {
        //store all fields of a class in here
        StructFieldList fieldsStruct = StaticMethods.convertClassFieldsToStructFields(typeClass.getClassDeclaration().getFields());
        //class with the fields
        TypeStruct classStruct = Ast.TypeStruct(typeClass.getClassDeclaration().getName(), fieldsStruct);

        return classStruct;
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
