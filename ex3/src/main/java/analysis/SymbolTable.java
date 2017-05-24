
/* In reference with
http://alumni.cs.ucr.edu/~vladimir/cs152/assignments.html#A5
 */

package analysis;

import minijava.ast.*;

import javax.lang.model.type.NullType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alka on 5/23/2017.
 */
public class SymbolTable extends MJElement.DefaultVisitor {

    Object presentClass = null;
    Object presentMethod = null;

    private HashMap<Object, Object> hashMap;
    MJProgram program;

    private List<TypeError> errors = new ArrayList<>();

    public List<TypeError> getErrors() {
        return errors;
    }

    public SymbolTable(MJProgram program) {
        hashMap = new HashMap<Object, Object>();
        this.program = program;
    }

    public void createST() {
        program.accept(this);
        System.out.println("HasMap is " + hashMap);
    }

    @Override
    //main class
    public void visit(MJMainClass main) {
        if (!addClass(main.getName(), "Null")) {
            errors.add(new TypeError(main, "Main class is already defined"));
        }
        presentClass = main.getName();
    }

    @Override
    //for classl
    public void visit(MJClassDecl classDecl) {
        MJExtended ext_class = classDecl.getExtended();

        //classes which doonot have extends
        if (ext_class.toString().contentEquals("ExtendsNothing")) {
            if (!addClass(classDecl.getName(), "Null")) {
                errors.add(new TypeError(classDecl, "Class is already defined"));
            }
        }
        if (ext_class instanceof MJExtendsClass) {
            if (!addClass(classDecl.getName(), (((MJExtendsClass) ext_class).getName())))
                errors.add(new TypeError(classDecl, "Class is already defined"));
        }
        presentClass = classDecl.getName();
    }

    //adding the classes which have extends
    public boolean addClass(String name, String parent) {
        if (contains(name)) {
            return false;
        } else {
            hashMap.put(name, parent);
            return true;
        }
    }

    public boolean contains(String name) {
        return hashMap.containsKey(name);
    }

    @Override
    public void visit(MJBlock block) {
    }

    //method declration
    @Override
    public void visit(MJMethodDecl methodDecl) {
        System.out.println("in here");
        if (addMethod(methodDecl.getName(), methodDecl.getReturnType())) {
            errors.add(new TypeError(methodDecl, "method is already defined in "));
        }
        presentMethod = methodDecl.getName();
        //parameter of method
        for (MJVarDecl varDecl : methodDecl.getFormalParameters()) {
            if (addMethod(varDecl.getName(), varDecl.getType())) {
                errors.add(new TypeError(methodDecl, "parameter is already defined"));
            }
        }
    }

    //variable declration
    @Override
    public void visit(MJVarDecl varDecl) {
        if (presentClass != null) {
            if (presentMethod == null) {
                if (addMethod(varDecl.getName(), varDecl.getType())) {
                    errors.add(new TypeError(varDecl, varDecl.getName() + " is already defined"));
                }
            } else if (addMethod(varDecl.getName(), varDecl.getType())) {
                errors.add(new TypeError(varDecl, varDecl.getName() + " is already defined"));
            }
        }
    }


    public Object getClass(String name) {
        if (name == null) {
            return null;
        }
        if (contains(name)) {
            return hashMap.get(name);
        } else
            return null;
    }

    //for adding method,parameters and variables
    public boolean addMethod(String id, MJType param) {
        if (contains(id)) {
            return false;
        } else {
            hashMap.put(id, param);
            return true;
        }
    }
}