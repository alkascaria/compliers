package translation;

import static minillvm.ast.Ast.TypePointer;
import minijava.ast.*;
import minillvm.ast.TypePointer;
import minillvm.ast.Ast;
import minillvm.ast.Type;


/**
 * Created by Daniele on 02/06/2017.
 * Matches correct type of a vardecl
 */
public class TypeMatcher implements MJType.Matcher<Type> {

    /**
     *
     * @param typeClass
     * @return
     */
    @Override
    public Type case_TypeClass(MJTypeClass typeClass)
    {
        return null;
    }

    /**
     *
     * @param typeBool(@CODE MJTypeBool)
     * @return the type(@code Ast.TypeBool())
     */
    @Override
    public Type case_TypeBool(MJTypeBool typeBool)
    {
        return Ast.TypeBool();
    }

    /**
     *
     * @param typeIntArray
     * @return
     */
    @Override
    public Type case_TypeIntArray(MJTypeIntArray typeIntArray)
    {
        TypePointer typePointer = TypePointer(Ast.TypeArray(Ast.TypeInt(),0));
        return (typePointer);

    }

    /**
     *
     * @param typeInt
     * @return
     */
    @Override
    public Type case_TypeInt(MJTypeInt typeInt)
    {
        return Ast.TypeInt();
    }
}
