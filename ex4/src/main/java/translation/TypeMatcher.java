package translation;

import minijava.ast.*;
import minillvm.ast.Ast;
import minillvm.ast.Type;



/**
 * Created by Daniele on 02/06/2017.
 * Matches correct type of a vardecl
 */
public class TypeMatcher implements MJType.Matcher<Type>
{
    /**
     *
     * @param typeClass(@code MJTypeClass)
     * @return
     */
    @Override
    public Type case_TypeClass(MJTypeClass typeClass)
    {
        throw new RuntimeException();
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
     * @param typeIntArray(@code MJTypeIntArray)
     * @return
     */
    @Override
    public Type case_TypeIntArray(MJTypeIntArray typeIntArray)
    {
        //inizialize with 0 initially
        return Ast.TypeArray(Ast.TypeInt(), typeIntArray.size());
    }

    /**
     *
     * @param typeInt(@code MJTypeInt)
     * @return
     */
    @Override
    public Type case_TypeInt(MJTypeInt typeInt)
    {
       return Ast.TypeInt();

    }
}
