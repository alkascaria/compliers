
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

import static minijava.ast.MJ.TypeClass;

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

    /**
     *
     * @param program(@code MJProgram)
     */
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
        System.out.println("Classmap is " + varclass);
        System.out.println("Methodmap is " + varmethod);
    }

    /**
     *
     * @param mainClass(@code MJMainClass)
     */
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

    /**
     *
     * @param classDeclList(@code MJClassDeclList)
     */
    public void STClass(MJClassDeclList classDeclList) {
        MJClassDecl classDecl;

        for (int i = 0; i < classDeclList.size(); i++) {
            classDecl = classDeclList.get(i);
            Class(classDecl);   //constructing st for a class

            varclass.clear();   //clearing the contents of scope of that class
        }
    }


    //typeReturn: type of the function where the block is in

    /**
     *
     * @param block(@code MJBlock) block of code found between {}
     * @param mainArgs(@ String) arguments in the main method
     * @param methodDecl(@ MJMethodDecl)
     */
    public void Block(MJBlock block, String mainArgs, MJMethodDecl methodDecl)
    {
        for (MJStatement statement : block)
        {
            System.out.println(statement.getClass());

            if (statement != null && statement instanceof MJStmtAssign)   //assginment
            {
                hashMap.put(((MJStmtAssign) statement).getLeft(), ((MJStmtAssign) statement).getRight());
            }

            //checking for existence of class to be instantiated
            if(statement instanceof  MJStmtExpr)
            {
                CheckExistenceClassInstantiation((MJStmtExpr)statement);
                CheckCallMethodExistence((MJStmtExpr)statement);
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
                    if (mainArgs.length() > 0)
                    {
                        String varName = (((MJVarDecl) statement).getName());
                        if (varName.equals(mainArgs)) {
                            this.errors.add(new TypeError(statement, "Variable already defined in main method's argumets"));
                        } else
                            hashMap.put(((MJVarDecl) statement).getName(), ((MJVarDecl) statement).getType());

                        //we're in the main. make sure
                    }
                    else {
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
            else if (statement instanceof MJStmtPrint)
                tc.CheckSOP((MJStmtPrint) statement);
            else if (statement instanceof MJStmtReturn) {
                tc.CheckReturn((MJStmtReturn) statement, methodDecl, mainArgs);
            }
            //Checking whether while condition is a boolean
            else if (statement instanceof MJStmtWhile)
                tc.checkwhile((MJStmtWhile) statement);
            else if (statement instanceof MJStmtIf)
                tc.checkif((MJStmtIf) statement);
            errors.addAll(tc.getErrors());
        }
        varmethod.clear();

    }

    /**
     *
     * @param statement(@code MJStmtExpr)
     */
    public void CheckCallMethodExistence(MJStmtExpr statement)
    {
        MJStmtExpr stmtExpr = statement;
        MJExpr exprStmt = stmtExpr.getExpr();
        //
        if (exprStmt instanceof MJMethodCall)
        {
            //check for a method with the same name
            MJMethodCall methodCall = (MJMethodCall)exprStmt;
            //TODO:, check if the receiver was defined and the method belongs to that class.

            if(!(this.hashMap.containsKey(methodCall.getMethodName())))
            {
                this.errors.add(new TypeError(exprStmt, "Calling an undefined method name"));
            }
            //method found somewhere. now check if the parameters correspond
            else
            {
                //check if all parameters passed are subtypes of the ones in the declaration
                if(this.hashMap.get(methodCall.getMethodName()) != null)
                {
                    MJVarDeclList varDeclList =  (MJVarDeclList) this.hashMap.get(methodCall.getMethodName());

                    if(varDeclList != null)
                    {
                        //loop through all of them and check if subtype
                        for(MJVarDecl varDeclMethod : varDeclList)
                        {
                            for(MJExpr exprArg : methodCall.getArguments())
                            {
                                if(exprArg instanceof MJVarUse)
                                {
                                    typechecker tc = new typechecker(varmethod, varclass, hashMap);
                                    //type of the method name's parameter
                                    MJType typeMethod = varDeclMethod.getType();
                                    //type of the corresponding method call's passed parameter
                                    MJType typeParam = tc.CheckType((MJVarUse)exprArg);

                                    boolean isSubType = StaticMethods.isSubTypeOff(typeParam, typeMethod);
                                    //if a single one is not subtype, then raise an error already
                                    if(isSubType == false)
                                    {
                                        this.errors.add(new TypeError(typeParam, "Method's parameters must be subtypes of method's declaration's arguments."));
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }


    /**
     *
     * @param statement(@code MJStmtExpr)
     */
    public void CheckExistenceClassInstantiation(MJStmtExpr statement)
    {
        MJStmtExpr stmtExpr = statement;
        MJExpr exprStmt = stmtExpr.getExpr();
        if(exprStmt instanceof MJNewObject)
        {
            //now check if a class exists if with the name declared
            MJNewObject newObj = (MJNewObject) exprStmt;


            if(!(this.hashMap.containsKey(newObj.getClassName())))
            {
                this.errors.add(new TypeError(statement, "Class declaration not found"));
            }
        }
    }

    /**
     *
     * @param classDecl(@code MJClassDecl)
     */
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

    /**
     *
     * @param methodDeclList(@code MJMethodDeclList)
     */
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
           // varmethod.clear();  //clearing the scope of methods
        }
    }

    /**
     *
     * @param varDeclList(@code MJVarDeclList)
     */
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