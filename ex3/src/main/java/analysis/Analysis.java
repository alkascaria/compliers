package analysis;

import minijava.ast.*;

import java.util.*;

/**
 * Adds errors
 * Symbol table error
 * Check method, parameter uniqueness
 * Create Class table
 * Checks inheritance loop
 */
public class Analysis {

    private final MJProgram prog;
    private List<TypeError> typeErrors = new ArrayList<>();

    /**
     * Adds error to error list
     * @param element(@code MJElement)
     * @param message(@code String)
     */
    public void addError(MJElement element, String message)
    {
        typeErrors.add(new TypeError(element, message));
    }

    /**
     * Initializes MJProgram and Staticmethods
     * @param prog(@code MJProgram)
     *
     */
    public Analysis(MJProgram prog)
    {
        this.prog = prog;
    }

    LinkedList loop = new LinkedList(); //for extended loop
    Stack class_name = new Stack(); //for extented class declration

   /**
     * Checks and adds symbol table errors to error list
     */
    public void check() {

        ExtendedClass();

        MethodParameterNames();

        SymbolTable st = new SymbolTable(prog);
        st.createST();

        checkMethodOverriding();

        checkMethodUniqueness();

        typeErrors.addAll(st.getErrors());
    }


    /**
     * Check method Uniqueness
     *
     */
    public void checkMethodUniqueness()
    {
        HashMap<String, MJType> hashMethods = new HashMap<>();

       //loop through all class declarations
        for(MJClassDecl classDecl : prog.getClassDecls())
        {
            //loop through all methods in the class
            for(MJMethodDecl methodDecl : classDecl.getMethods())
            {
                //if not in the hash, then add it
                if(!(hashMethods.containsKey(methodDecl.getName())))
                {
                    hashMethods.put(methodDecl.getName(), methodDecl.getReturnType());
                }
                //it's there already.
                else
                {
                    this.addError(methodDecl, "Method declarations must be unique. Minijava does not support method overloading either.");
                }
            }

            hashMethods.clear();
        }
    }

    /**
     * check uniqueness of parameter names
     */
    public void MethodParameterNames()
    {
        //loop through all classes
        for(MJClassDecl classDecl : this.prog.getClassDecls())
        {
            //loop through all methods in the classDecl
            for(MJMethodDecl methodDecl : classDecl.getMethods())
            {
                //create hash map for all parameters' names.
                HashMap<String, MJType> parametersList = new HashMap<>();

                //firstly, populate the hashmap
                for(MJVarDecl varDecl : methodDecl.getFormalParameters())
                {
                    //if it's there already, then we got a problem.
                    if(parametersList.containsKey(varDecl.getName()))
                    {
                        //problem: parameter already there.
                        this.addError(varDecl, "Parameter names must be unique.");
                    }
                    //else add it
                    else
                    {
                        parametersList.put(varDecl.getName(), varDecl.getType());
                    }
                }

            }

        }
    }

    /**
     * The ExtendClass check for inheritance loop as well as whether the extended class is declared 
     */
    public void ExtendedClass() {

        MJClassDeclList classDeclList = this.prog.getClassDecls();
        MJClassDecl classDecl;
        MJExtended ext_class;

        String parent;

        //loop through all classes and create a stack
        for (int i = 0; i < classDeclList.size(); i++) {

            classDecl = classDeclList.get(i);

            //getting the extended classname
            ext_class = classDecl.getExtended();

            if (ext_class.toString().contentEquals("ExtendsNothing")) {

                class_name.push(classDecl.getName());
            }
            if (ext_class instanceof MJExtendsClass) {

                parent = ((MJExtendsClass) ext_class).getName();

                //pushing the class name into stack
                class_name.push(classDecl.getName());

                //check for loop in extended class
                //creating a linked list in the form class->extended class
                if (loop.isEmpty() || !loop.contains(classDecl.getName())) {
                    loop.push(parent);
                    loop.push(classDecl.getName());
                } else if (loop.contains(classDecl.getName()) && !loop.contains(parent)) {
                    int j = loop.indexOf(classDecl.getName());
                    loop.add(j + 1, parent);
                } else if (loop.contains(classDecl.getName()) && loop.contains(parent)) {
                    int index1 = loop.indexOf(classDecl.getName());

                    //check whether there is any loop present
                    for (int j = 0; j < index1; j++) {
                        if (parent.equals(loop.get(j))) {
                            this.addError(classDecl.getExtended().getParent(), "The class cannot be extended as it forms circular reference.");
                        }
                    }
                }
            }

        }
        //checking the validity of the class
        for (int i = 0; i < classDeclList.size(); i++) {
            classDecl = classDeclList.get(i);

            //get the class it extends
            ext_class = classDecl.getExtended();

            if (ext_class instanceof MJExtendsClass) {
                parent = ((MJExtendsClass) ext_class).getName();

                //check whether the extended class is declared
                if (class_name.search(parent) == -1) {
                    addError(classDecl.getExtended().getParent(), "Parent not defined");
                } else if (parent.equals(classDecl.getName())) {
                    this.addError(classDecl.getExtended().getParent(), "A class cannot extend itself");
                } else if (parent.equals(prog.getMainClass().getName())) {
                    this.addError(classDecl.getExtended().getParent(), "Parent is main class : main class cannot be extended");
                }
            }
        }
    }

   /**
     * create class table
     * for classes extended by another class, create method table
     * add parent methods to method table
     * compare current class' methods with parent's methods and check if methods are overridden correctly
     *
     */
    public void checkMethodOverriding()
    {
        //create class table
        HashMap<String, MJElement> table = new HashMap<>();

        String mainClassName = prog.getMainClass().getName();

        //add main class to class table
        //table.put(prog.getMainClass().getName(), prog.getMainClass());
        //add all other classes to class table
        for (MJClassDecl classDecl : prog.getClassDecls())
        {
            table.put(classDecl.getName(), classDecl);
        }

        //check all classes that DO EXTEND another class
        for (MJClassDecl classDecl : prog.getClassDecls())
        {
            //if it does extend another class. Make sure this class is NOT the main class!

            if ( classDecl != null && classDecl.getExtended() instanceof MJExtendsClass)
            {
                //create a method table
                HashMap<String, MJMethodDecl> methodTable = new HashMap<>();
                MJElement parent = table.get(((MJExtendsClass) classDecl.getExtended()).getName());

                //add all methods of the parent to the method table
                if (parent instanceof MJClassDecl && ((MJClassDecl) parent).getName() != mainClassName)
                {
                    for (MJMethodDecl methodDecl : ((MJClassDecl) parent).getMethods())
                    {
                        methodTable.put(methodDecl.getName(), methodDecl);
                    }
                }

                //check if parent has current class' methods
                for (MJMethodDecl method : classDecl.getMethods())
                {
                    //parent does have current class' method.
                    if (methodTable.containsKey(method.getName()))
                    {
                        MJMethodDecl parentMethod = methodTable.get(method.getName());

                       String returnTypePar = parentMethod.getReturnType().toString();
                       String returnTypeCur = method.getReturnType().toString();

                        //firstly, check if return types are subtypes of each other
                        if (!StaticMethods.isSubTypeOff(method.getReturnType(), parentMethod.getReturnType()))
                        {
                            this.addError(method, "A method overriding a parent's method must have the same type " +
                                    "or be a subtype of the parent's type.");
                        }
                        //check if the amount of parameters is not the same
                        if (method.getFormalParameters().size() != parentMethod.getFormalParameters().size())
                        {
                            this.addError(method, "Methods overriding must have the same amount of parameters of their parents");
                        }
                        //check if all parameters in the current method's signature are subtypes of the ones in the parent's method signature
                        for (int i = 0; i < method.getFormalParameters().size(); i++)
                        {
                            if (!(StaticMethods.isSubTypeOff(method.getFormalParameters().get(i).getType(), parentMethod.getFormalParameters().get(i).getType())))
                            {
                                this.addError(method, "All parameters in methods overriding parent's methods must have the same type or be subtypes");
                            }
                        }

                    }
                }

            }
        }

    }

    /**
     * Gets type Error
     * @return typeErrors {@code new ArrayList<>(typeErrors)}
     */

    public List<TypeError> getTypeErrors() {
        return new ArrayList<>(typeErrors);
    }
}
