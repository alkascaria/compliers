
package analysis;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import minijava.ast.*;

import javax.lang.model.type.NullType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by alka on 5/23/2017.
 */
public class SymbolTable extends MJElement.DefaultVisitor {

    //hashmaps
    private LinkedHashMap<Object, Object> hashMap, varclass, varmethod;

    MJProgram program;

    public List<TypeError> errors = new ArrayList<>();

    public List<TypeError> getErrors() {
        return errors;
    }

    public SymbolTable(MJProgram program) {

        hashMap = new LinkedHashMap<Object, Object>();
        varclass = new LinkedHashMap<Object, Object>();
        varmethod = new LinkedHashMap<Object, Object>();

        this.program = program;
    }

    public void createST() {
        program.accept(this);

        STMain(program.getMainClass());
        STClass(program.getClassDecls());
        System.out.println("HasMap is " + hashMap);
    }


    //for mainclass
    public void STMain(MJMainClass mainClass) {

        if (hashMap.containsKey(mainClass.getName())) {

            errors.add(new TypeError(mainClass, "Main class is already defined"));
        } else {
            hashMap.put(mainClass.getName(), null);
        }
        Block(mainClass.getMainBody(), mainClass.getArgsName(), null);     //call for the mainbody considering it as a block
    }
    //for other classes

    public void STClass(MJClassDeclList classDeclList) {
        MJClassDecl classDecl;

        for (int i = 0; i < classDeclList.size(); i++) {
            classDecl = classDeclList.get(i);
            Class(classDecl);   //constructing st for a class

            varclass.clear();   //clearing the contents of scope of that class
        }
    }

    //block: block of code found between {}
    //mainArgs: arguments in the main method
    //typeReturn: type of the function where the block is in
    public void Block(MJBlock block, String mainArgs, MJMethodDecl methodDecl) {
        for (MJStatement statement : block) {
            if (statement instanceof MJStmtAssign)   //assginment
            {
                hashMap.put(((MJStmtAssign) statement).getLeft(), ((MJStmtAssign) statement).getRight());
            }
            if (statement instanceof MJVarDecl) {
                if ((hashMap.containsKey(((MJVarDecl) statement).getName()))) {
                    //variables
                    if (!varmethod.containsKey(((MJVarDecl) statement).getName())) {    //diff. btw local and global
                        System.out.println(hashMap.containsKey(((MJVarDecl) statement).getName()));
                        varmethod.put(((MJVarDecl) statement).getName(), ((MJVarDecl) statement).getType());
                    } else {
                        this.errors.add(new TypeError(statement, "Variable declaration should be unique"));
                    }
                } else {
                    //checking if already defined with String[] type as argument of main
                    if (mainArgs.length() > 0) {
                        String varName = (((MJVarDecl) statement).getName());
                        if (varName.equals(mainArgs)) {
                            this.errors.add(new TypeError(statement, "Variable already defined in main method's argumets"));
                        } else
                            hashMap.put(((MJVarDecl) statement).getName(), ((MJVarDecl) statement).getType());
                    } else {
                        varmethod.put(((MJVarDecl) statement).getName(), ((MJVarDecl) statement).getType());
                        hashMap.put(((MJVarDecl) statement).getName(), ((MJVarDecl) statement).getType());
                    }


                }
            }

            //checking if the main args doesn't get redefined somewhere
        }

        for (MJStatement statement : block) {
            typechecker tc = new typechecker(varmethod, varclass, hashMap);
            if (statement instanceof MJStmtAssign)   //Type check
                tc.CheckStmtassg((MJStmtAssign) statement);
            if (statement instanceof MJStmtPrint)
                tc.CheckSOP((MJStmtPrint) statement);
            if (statement instanceof MJStmtReturn)
            {
                tc.CheckReturn((MJStmtReturn)statement, methodDecl);
            }

            errors.addAll(tc.getErrors());
        }
        varmethod.clear();

    }

    //for each class
    public void Class(MJClassDecl classDecl) {
        MJExtended extClass = classDecl.getExtended();

        if (hashMap.containsKey(classDecl.getName()))
            this.errors.add(new TypeError(classDecl, "Class is already defined"));
        else {
            if (extClass instanceof MJExtendsClass)
                //ClassName, ExtendedClass
                hashMap.put(classDecl.getName(), ((MJExtendsClass) extClass).getName());
            else
                hashMap.put(classDecl.getName(), null);

            Field(classDecl.getFields());
            Method(classDecl.getMethods());
        }
    }

    //methods
    public void Method(MJMethodDeclList methodDeclList) {
        MJMethodDecl methodDecl;

        for (int i = 0; i < methodDeclList.size(); i++) {

            methodDecl = methodDeclList.get(i);

            if (hashMap.containsKey(methodDecl.getName())) {
                if (varclass.containsKey(methodDecl.getName()))
                    this.errors.add(new TypeError(methodDecl, "Method names should be unique"));
            } else {
                if (methodDecl instanceof MJMethodDecl) {
                    hashMap.put(methodDecl.getName(), methodDecl.getReturnType());
                    hashMap.put(methodDecl.getName(), methodDecl.getFormalParameters());
                }

                Block(methodDecl.getMethodBody(), "", methodDecl);  //body of method
            }
            varmethod.clear();  //clearing the scope of methods
        }
    }

    //for variable

    public void Field(MJVarDeclList varDeclList) {
        MJVarDecl varDecl;

        for (int i = 0; i < varDeclList.size(); i++) {
            varDecl = varDeclList.get(i);

            if (varDecl instanceof MJVarDecl) {
                if (hashMap.containsKey(varDecl.getName())) {
                    if (!varclass.containsKey(varDecl.getName())) {
                        varclass.put(varDecl.getName(), varDecl.getType());
                    } else
                        this.errors.add(new TypeError(varDecl, "Field names should be unique"));
                } else {
                    hashMap.put(varDecl.getName(), varDecl.getType());
                    varclass.put(varDecl.getName(), varDecl.getType());
                }
            }
        }
    }

}