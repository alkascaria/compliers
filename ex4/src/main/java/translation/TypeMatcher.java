package translation;

import minijava.ast.*;
import minillvm.ast.*;

/**
 * Created by Daniele on 02/06/2017.
 * Matches correct type of a vardecl
 */
public class TypeMatcher implements MJType.Matcher<Type> {
    @Override
    public Type case_TypeClass(MJTypeClass typeClass)
    {
        //Type type = TypeInt();
        return null;
    }

    @Override
    public Type case_TypeBool(MJTypeBool typeBool)
    {
            return null;
    }

    @Override
    public Type case_TypeIntArray(MJTypeIntArray typeIntArray)
    {
        return null;
    }

    @Override
    public Type case_TypeInt(MJTypeInt typeInt)
    {
        return null;
    }
}
