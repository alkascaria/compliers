package analysis;

import minijava.ast.*;

import java.util.HashMap;

/**
 * Created by Daniele on 27/05/2017.
 */
public class StaticMethods
{

    public static MJProgram prog;

    /**
     *
     * @param program(@code MJProgram)
     */
    public StaticMethods(MJProgram program)
    {
        this.prog = program;

    }

    /**
     *
     * @param a(@code MJType)
     * @param b(@code MJType)
     * @return
     */
    public static boolean isSubTypeOff(MJType a, MJType b)
    {
        //create hashmap with all classes
        HashMap<String, MJClassDecl> table = new HashMap<>();

        //populate hashmap
        for (MJClassDecl classDecl : prog.getClassDecls())
        {
            table.put(classDecl.getName(), classDecl);
        }

        if ((a instanceof MJTypeClass) & (b instanceof MJTypeClass))
        {
            String classA = ((MJTypeClass) a).getName();
            String classB = ((MJTypeClass) b).getName();
            if (classA.compareTo(classB) == 0)
                return true;

            MJClassDecl clsA = table.get(classA);

            if(clsA.getExtended() instanceof MJExtendsClass)
            {
                MJExtendsClass tempClass = (MJExtendsClass) clsA.getExtended();

                while (tempClass != null)
                {
                    if (tempClass.getName().compareTo(classB) == 0)
                    {
                        return true;
                    }

                    if(table.get(tempClass.getName()).getExtended() instanceof MJExtendsClass)
                    {
                        tempClass = (MJExtendsClass) table.get(tempClass.getName()).getExtended();
                    }
                }
            }



        }
        else
        {
            if (a.toString().compareTo(b.toString()) == 0)
                return true;
            else
                return false;
        }
        return false;
    }
}
