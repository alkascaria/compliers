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

    //declaring a stack
    Stack symboltable = new Stack();

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
        this.ExtendedClass();


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
        MJClassDeclList classDeclList = this.prog.getClassDecls();

        //loop through all classes and create a stack
        for (int i = 0; i < classDeclList.size(); i++) {
            MJClassDecl classDecl = classDeclList.get(i);

            //pushing the class name into stack
            symboltable.push(classDecl.getName());
        }

        //checking the validity of the class
        for (int i = 0; i < classDeclList.size(); i++) {
            MJClassDecl classDecl = classDeclList.get(i);

            //get the class it extends
            String name = classDecl.getExtended().toString();
            String exte_class = name.substring(13, name.length() - 1);

            //checking only if a class extends
            if (!exte_class.isEmpty()) {

                //check whether the extended class is declared
                if (symboltable.search(exte_class) == -1) {
                    this.addError(classDecl.getExtended().getParent(), "The class cannot be extented as it is a'Main' or donot exit");
                }
            }
        }
    }

    public List<TypeError> getTypeErrors() {
        return new ArrayList<>(typeErrors);
    }
}
