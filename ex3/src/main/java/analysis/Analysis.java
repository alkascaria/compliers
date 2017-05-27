package analysis;

import minijava.ast.*;

import java.util.*;

public class Analysis {

    private final MJProgram prog;
    private List<TypeError> typeErrors = new ArrayList<>();

    public void addError(MJElement element, String message) {
        typeErrors.add(new TypeError(element, message));
    }

    public Analysis(MJProgram prog) {
        this.prog = prog;
    }

    LinkedList loop = new LinkedList(); //for extended loop
    Stack class_name = new Stack(); //for extented class declration

    public void check() {

        ExtendedClass();

        UniqueMethodParams();

        SymbolTable st = new SymbolTable(prog);
        st.createST();
        typeErrors.addAll(st.getErrors());
    }


    public void checkMethodProperlyOverriden() {
        //Build a Class Table
        HashMap<String, MJElement> table = new HashMap<>();
        table.put(prog.getMainClass().getName(), prog.getMainClass());
        for (MJClassDecl classDecl : prog.getClassDecls()) {
            table.put(classDecl.getName(), classDecl);
        }

        for (MJClassDecl classDecl : prog.getClassDecls()) {
            if (classDecl.getExtended() != null) {

                HashMap<String, MJMethodDecl> methodTable = new HashMap<>();
                MJElement parent = table.get(((MJExtendsClass) classDecl.getExtended()).getName());
                if (parent instanceof MJClassDecl) {
                    for (MJMethodDecl methodDecl : ((MJClassDecl) parent).getMethods()) {
                        methodTable.put(methodDecl.getName(), methodDecl);
                    }

                    //compare declaration of parent with child
                    for (MJMethodDecl method : classDecl.getMethods()) {
                        if (methodTable.containsKey(method.getName())) {
                            MJMethodDecl parentMethod = methodTable.get(method.getName());
                            if (!isSubTypeOff(method.getReturnType(), parentMethod.getReturnType())) {
                                this.addError(method, "Invalid Return Type for Method; " + method.getName());
                            }
                            if (method.getFormalParameters().size() != parentMethod.getFormalParameters().size()) {
                                this.addError(method, "Unmatched amount of params with parent for Method; " + method.getName());
                                ;
                            } else {
                                for (int i = 0; i < method.getFormalParameters().size(); i++) {
                                    if (!(isSubTypeOff(method.getFormalParameters().get(i).getType(),
                                            parentMethod.getFormalParameters().get(i).getType()))) {
                                        this.addError(method, "Invalid param Type for Method; " + method.getName());

                                    }
                                }
                            }
                        }
                    }
                }

            }

        }
    }

    public boolean isSubTypeOff(MJType a, MJType b) {
        HashMap<String, MJClassDecl> table = new HashMap<>();
        //table.put(prog.getMainClass().getName(), prog.getMainClass());
        for (MJClassDecl classDecl : prog.getClassDecls()) {
            table.put(classDecl.getName(), classDecl);
        }

        if ((a instanceof MJTypeClass) & (b instanceof MJTypeClass)) {
            String classA = ((MJTypeClass) a).getName();
            String classB = ((MJTypeClass) b).getName();
            if (classA.compareTo(classB) == 0)
                return true;

            MJClassDecl clsA = table.get(classA);

            MJExtendsClass tempClass = (MJExtendsClass) clsA.getExtended();
            while (tempClass != null) {
                if (tempClass.getName().compareTo(classB) == 0) {
                    return true;
                }
                tempClass = (MJExtendsClass) (table.get(tempClass.getName()).getExtended());
            }
        } else {
            if (a.toString().compareTo(b.toString()) == 0)
                return true;
            else
                return false;
        }
        return false;
    }

    public void UniqueMethodParams() {
        List<String> methodNameList = new ArrayList<>();
        int counter = 0;
        for (MJClassDecl classDecl : prog.getClassDecls()) {
            List<MJMethodDecl> methodDeclList = classDecl.getMethods();
            for (MJMethodDecl methodDecl : methodDeclList) {
                methodNameList.add(methodDecl.getName());
                counter++;
            }
            if (counter > 0) {
                this.addError(prog, "Method-parameter names should be unique.");
            }
        }
    }

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
                            this.addError(classDecl.getExtended().getParent(), "The class cannot be extented as it forms a loop");
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

    public List<TypeError> getTypeErrors() {
        return new ArrayList<>(typeErrors);
    }
}
