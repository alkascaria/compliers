package analysis;

import minijava.ast.*;

import java.util.*;

public class Analysis {

    private final MJProgram prog;
    private List<TypeError> typeErrors = new ArrayList<>();

    public void addError(MJElement element, String message) {
        typeErrors.add(new TypeError(element, message));
    }

    public Analysis(MJProgram prog)
    {
        this.prog = prog;
    }

    /*
    TODO check program
    Add errors like
    "Class already exist"
    "Class Name should be Unique"
    "MethodParameter unique"
    "Extended class should not be main class."
    "Extended class should be declared."
    "Field need to be unique"
    new errors("This program has type errors")
     */
    /*
     */

    public void check()
    {
        //TODO implement type checking here as well!
        ClassChecker();
        UniqueMethodParam();
        UniqueFieldName();
        UniqueClassName();
        ExtendedClass();
        this.checkClassNamesDefined();

    }

    public void ClassChecker()
    {
        //TODO CHECK CLASS EXIST or NOT
    }

    public void UniqueMethodParam()
    {
        //TODO MethodParameter unique
    }

    public void UniqueFieldName()
    {
        //TODO Unique Field Name
    }

    public void UniqueClassName()
    {
        //Todo Unique Class Name
    }

    public void ExtendedClass()
    {
        //TODO Extended class should not be main class.
        //TODO Extended class should be declared.
    }




    public void checkClassNamesDefined()
    {
        MJClassDeclList classDeclList = this.prog.getClassDecls();

        //loop through all classes and check if they extend a valid class
        for(int i = 0; i < classDeclList.size(); i++ )
        {
            MJClassDecl classDecl = classDeclList.get(i);
            //get the class it extends, eventually
            //classDecl.getExtended();

        }

    }




    public List<TypeError> getTypeErrors() {
        return new ArrayList<>(typeErrors);
    }
}
