
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
 * Creating Symbol Table
 * Created by alka on 5/23/2017.
 */
public class SymbolTable extends MJElement.DefaultVisitor {

    //hashmaps
    private LinkedHashMap<Object, Object> hash_main, hash_class, hash_method, map;

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

        hash_main = new LinkedHashMap<Object, Object>();
        hash_class = new LinkedHashMap<Object, Object>();
        hash_method = new LinkedHashMap<Object, Object>();
        map = new LinkedHashMap<Object, Object>();

        this.program = program;
    }
    /**
     * Create Symbol Table
     */
    public void createST() {
        program.accept(this);

        STMain(program.getMainClass());
        STClass(program.getClassDecls());
    }

   /**
     * Symbol Table for Main Class
     * @param mainClass(@code MJMainClass)
     */
    public void STMain(MJMainClass mainClass) {
        if (hash_main.containsKey(mainClass.getName())) {
            errors.add(new TypeError(mainClass, "Main class is already defined"));
        } else {
            hash_main.put(mainClass.getName(), null);
        }
        Block_main(mainClass.getMainBody(), mainClass.getArgsName());     //call for the mainbody considering it as a block
    }
     /**
     * Symbol Table for other classes
     * @param classDeclList(@code MJClassDeclList)
     */
    public void STClass(MJClassDeclList classDeclList) {
        MJClassDecl classDecl;

        for (int i = 0; i < classDeclList.size(); i++) {
            classDecl = classDeclList.get(i);
            Class(classDecl);   //constructing st for a class

            hash_class.clear();   //clearing the contents of scope of that class
        }
    }


     /**
     * Type of the function where the block is in
     * @param block(@code MJBlock) block of code found between {}
     * @param mainArgs(@ String) arguments in the main method
     * @param methodDecl(@ MJMethodDecl)
     */

    public void Block_main(MJBlock block, String mainArgs) {
        for (MJStatement statement : block) {
            if (statement != null && statement instanceof MJStmtAssign)   //assginment
            {

                hash_main.put(((MJStmtAssign) statement).getLeft(), ((MJStmtAssign) statement).getRight());
            }

            //checking for existence of class to be instantiated
            else if (statement instanceof MJStmtExpr) {

                CheckExistenceClassInstantiation((MJStmtExpr) statement, hash_main);
                CheckCallMethodExistence((MJStmtExpr) statement);
            } else if (statement instanceof MJVarDecl) {
                if ((hash_main.containsKey(((MJVarDecl) statement).getName()))) {
                    //variables
                    this.errors.add(new TypeError(statement, "Variable declaration should be unique"));
                } else if (mainArgs.length() > 0) {
                    //checking if already defined with String[] type as argument of main
                    String varName = (((MJVarDecl) statement).getName());
                    if (varName.equals(mainArgs)) {
                        this.errors.add(new TypeError(statement, "Variable already defined in main method's argumets"));
                    }
                    //we're in the main. make sure
                } else
                    hash_main.put(((MJVarDecl) statement).getName(), ((MJVarDecl) statement).getType());
            }
            //checking if the main args doesn't get redefined somewhere
        }
        for (MJStatement statement : block) {
            typechecker tc = new typechecker(hash_method, hash_class, hash_main);
            if (statement instanceof MJStmtAssign)   //Type check
                tc.CheckStmtassg((MJStmtAssign) statement);
            else if (statement instanceof MJStmtPrint)
                tc.CheckSOP((MJStmtPrint) statement);
            else if (statement instanceof MJStmtReturn) {
                tc.CheckReturn((MJStmtReturn) statement, null, mainArgs);
            }
            //Checking whether while condition is a boolean
            else if (statement instanceof MJStmtWhile)
                tc.checkwhile((MJStmtWhile) statement);
            else if (statement instanceof MJStmtIf)
                tc.checkif((MJStmtIf) statement);
            errors.addAll(tc.getErrors());
        }
    }
    /**
     * Check Method Existence
     * @param statement(@code MJStmtExpr)
     */
    void Block(MJBlock block) {
        for (MJStatement statement : block) {
            if (statement != null && statement instanceof MJStmtAssign)   //assginment
            {
                hash_method.put(((MJStmtAssign) statement).getLeft(), ((MJStmtAssign) statement).getRight());
            }

            //checking for existence of class to be instantiated
            if (statement instanceof MJStmtExpr) {
                CheckExistenceClassInstantiation((MJStmtExpr) statement, hash_method);
                CheckCallMethodExistence((MJStmtExpr) statement);
            }

            if (statement instanceof MJVarDecl) {
                //variables
                if (!hash_method.containsKey(((MJVarDecl) statement).getName())) {    //diff. btw local and global
                    hash_method.put(((MJVarDecl) statement).getName(), ((MJVarDecl) statement).getType());
                } else {
                    this.errors.add(new TypeError(statement, "Variable declaration should be unique"));
                }
            }
        }
        //checking if the main args doesn't get redefined somewhere

        for (MJStatement statement : block)

        {
            typechecker tc = new typechecker(hash_method, hash_class, hash_main);
            if (statement instanceof MJStmtAssign)   //Type check
                tc.CheckStmtassg((MJStmtAssign) statement);
            else if (statement instanceof MJStmtPrint)
                tc.CheckSOP((MJStmtPrint) statement);
                //Checking whether while condition is a boolean
            else if (statement instanceof MJStmtWhile)
                tc.checkwhile((MJStmtWhile) statement);
            else if (statement instanceof MJStmtIf)
                tc.checkif((MJStmtIf) statement);
            errors.addAll(tc.getErrors());
        }
        hash_method.clear();
    }

    /**
     * Check Class Instance
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

            if(!(this.map.containsKey(methodCall.getMethodName())))
            {
                this.errors.add(new TypeError(exprStmt, "Calling an undefined method name"));
            }
            //method found somewhere. now check if the parameters correspond
            else
            {
                //check if all parameters passed are subtypes of the ones in the declaration
                if(this.map.get(methodCall.getMethodName()) != null)
                {
                    MJVarDeclList varDeclList =  (MJVarDeclList) this.map.get(methodCall.getMethodName());

                    if(varDeclList != null)
                    {
                        //loop through all of them and check if subtype
                        for(MJVarDecl varDeclMethod : varDeclList)
                        {
                            if(varDeclMethod != null) {


                                for (MJExpr exprArg : methodCall.getArguments()) {

                                    System.out.println(exprArg.toString());

                                    if (exprArg != null && exprArg instanceof MJVarUse ) {

                                        typechecker tc = new typechecker(hash_method, hash_class, hash_main);
                                        //type of the method name's parameter
                                        MJType typeMethod = varDeclMethod.getType();
                                        //type of the corresponding method call's passed parameter
                                        MJVarUse varUse = (MJVarUse)exprArg;
                                        MJType typeParam = tc.CheckType(varUse);

                                        if(varUse != null && typeParam != null && typeMethod != null)
                                        {
                                            boolean isSubType = StaticMethods.isSubTypeOff(typeParam, typeMethod);
                                            //if a single one is not subtype, then raise an error already
                                            if (isSubType == false ) {
                                                this.errors.add(new TypeError(exprArg, "Method's parameters must be subtypes of method's declaration's arguments."));
                                            }
                                        }

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
     * Check Class Instance
     * @param statement(@code MJStmtExpr)
     */
    public void CheckExistenceClassInstantiation(MJStmtExpr statement, LinkedHashMap map) {
        MJStmtExpr stmtExpr = statement;
        MJExpr exprStmt = stmtExpr.getExpr();
        if (exprStmt instanceof MJNewObject) {
            //now check if a class exists if with the name declared
            MJNewObject newObj = (MJNewObject) exprStmt;

            if (!(this.map.containsKey(newObj.getClassName()))) {
                this.errors.add(new TypeError(statement, "Class declaration not found"));
            }
        }
    }

    /**
     * For each class
     * @param classDecl(@code MJClassDecl)
     */

    public void Class(MJClassDecl classDecl) {
        MJExtended extClass = classDecl.getExtended();

        if (hash_main.containsKey(classDecl.getName()))
            this.errors.add(new TypeError(classDecl, "Class is already defined"));
        else {
            if (extClass instanceof MJExtendsClass)
                //ClassName, ExtendedClass
                hash_main.put(classDecl.getName(), ((MJExtendsClass) extClass).getName());
            else
                hash_main.put(classDecl.getName(), null);

            Field(classDecl.getFields());
            Method(classDecl.getMethods());
        }
    }

   /**
     * For Methods
     * @param methodDeclList(@code MJMethodDeclList)
     */
    public void Method(MJMethodDeclList methodDeclList) {
        MJMethodDecl methodDecl;

        for (int i = 0; i < methodDeclList.size(); i++) {

            methodDecl = methodDeclList.get(i);

            if (hash_class.containsKey(methodDecl.getName()))
                this.errors.add(new TypeError(methodDecl, "Method names should be unique"));
            else if (methodDecl instanceof MJMethodDecl) {
                hash_class.put(methodDecl.getName(), methodDecl.getReturnType());
            }

            Block(methodDecl.getMethodBody());  //body of method
        }
    }

    /**
     * For variable
     * @param varDeclList(@code MJVarDeclList)
     */
    public void Field(MJVarDeclList varDeclList) {
        MJVarDecl varDecl;

        for (int i = 0; i < varDeclList.size(); i++) {
            varDecl = varDeclList.get(i);

            if (varDecl instanceof MJVarDecl) {
                if (!hash_class.containsKey(varDecl.getName())) {
                    hash_class.put(varDecl.getName(), varDecl.getType());
                } else
                    this.errors.add(new TypeError(varDecl, "Field names should be unique"));
            }
        }
    }




}
