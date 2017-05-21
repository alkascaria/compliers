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

    LinkedList loop = new LinkedList(); //for extended loop
    Stack class_name = new Stack(); //for extented class declration

    /*
    TODO check program
    Add errors like
    "Class already exist"
    "Class Name should be Unique"
    "MethodParameter unique"
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
        List<String> methodNameList = new ArrayList<>();
        int counter = 0;
        for (MJClassDecl classDecl : prog.getClassDecls()) {
            List<MJMethodDecl> methodDeclList = classDecl.getMethods();
            for (MJMethodDecl methodDecl: methodDeclList) {
                methodNameList.add(methodDecl.getName());
                counter ++;
            }
            if(counter > 0){
                typeErrors.add(new TypeError(prog, "Method-parameter names should be unique."));
            }
        }
    }

    public void UniqueFieldName()
    {
        List<String> fieldNameList = new ArrayList<>();
        int counter = 0;
        for (MJClassDecl classDecl : prog.getClassDecls()) {
            List<MJVarDecl> varDeclList = classDecl.getFields();
            for (MJVarDecl varDecl: varDeclList) {
                fieldNameList.add(varDecl.getName());
                counter ++;
            }
            if(counter > 0){
                typeErrors.add(new TypeError(prog, "Field names should be unique."));
            }
        }
    }

    public void UniqueClassName()
    {
        HashMap<String, MJElement> table = new HashMap<>();
        table.put(prog.getMainClass().getName(), prog.getMainClass());
        for(MJClassDecl classDecl : prog.getClassDecls())
        {
            if(table.containsKey(classDecl.getName()))
            {
                //Collision Detected
                typeErrors.add(new TypeError(classDecl, classDecl.getName() + "Already declared"));
            }
            else
            {
                table.put(classDecl.getName(), classDecl);
            }
        }
    }

    public void ExtendedClass()
    {
        MJClassDeclList classDeclList = this.prog.getClassDecls();
        MJClassDecl classDecl;

        String name, exte_class;

        //loop through all classes and create a stack
        for (int i = 0; i < classDeclList.size(); i++) {
            classDecl = classDeclList.get(i);

            //getting the extended classname
            name = classDecl.getExtended().toString();
            exte_class = name.substring(13, name.length() - 1);

            //pushing the class name into stack
            class_name.push(classDecl.getName());

            //check for loop in extended class
            //creating a linked list in the form class->extended class
            if (loop.isEmpty() || !loop.contains(classDecl.getName())) {
                loop.add(classDecl.getName());
                loop.add(exte_class);
            } else if (loop.contains(classDecl.getName()) && !loop.contains(exte_class)) {
                int j = loop.indexOf(classDecl.getName());
                loop.add(j + 1, exte_class);
            } else if (loop.contains(classDecl.getName()) && loop.contains(exte_class)) {
                int index = loop.indexOf(classDecl.getName());
                //check whether there is any loop present
                for (int j = 0; j < index; j++) {
                    if (exte_class.contentEquals(loop.get(j).toString())) {
                        this.addError(classDecl.getExtended().getParent(), "The class cannot be extented as it is forms a loop");
                    }
                }
            }
        }

        //checking the validity of the class
        for (int i = 0; i < classDeclList.size(); i++) {
            classDecl = classDeclList.get(i);

            //get the class it extends
            name = classDecl.getExtended().toString();
            exte_class = name.substring(13, name.length() - 1);

            //checking only if a class extends
            if (!exte_class.isEmpty()) {

                //check whether the extended class is declared
                if (class_name.search(exte_class) == -1) {
                    this.addError(classDecl.getExtended().getParent(), "The class cannot be extented as it is a'Main' or donot exit");
                }else if (exte_class.contentEquals(classDecl.getName())) {
                    this.addError(classDecl.getExtended().getParent(), "A class cannot extend itself");
                }
            }
        }
    }

    public List<TypeError> getTypeErrors() {
        return new ArrayList<>(typeErrors);
    }
}
