package translation;

import analysis.Analysis;
import analysis.ClassTable;
import analysis.*;
import analysis.ClassType;
import minijava.ast.*;
import minijava.ast.MJMethodDeclList;
import minillvm.ast.*;
import minillvm.ast.Type;
import org.apache.tools.ant.taskdefs.Get;

import static minillvm.ast.Ast.*;
import static minillvm.ast.Ast.StructFieldList;


import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


//Statements, visitor like here is fine.
//expressions, separate class MJExpr.Matcher<Operand> to pass back an operand when evaluating something


public class Translator extends MJElement.DefaultVisitor {

    private final MJProgram javaProg;

    //variables declarations go onto the stack (ex: int a). contains no value yet!
    public static HashMap<String, TemporaryVar> varsTemp = new HashMap<>();

    public static HashMap<MJMethodDecl, Proc> methodsProcs = new HashMap<>();

    //global corresponding to the V-Table of ClassDecl
    public static HashMap<MJClassDecl, Global> vTableClass = new HashMap<>();

    public static HashMap<MJClassDecl, TypeStruct> structsMap = new HashMap<>();

    //constants assigned to fields of the structMap
    public static HashMap<TypeStruct, ConstStruct> valuesStructs = new HashMap<>();



    //stores which Block we are currently in.
    public static BasicBlock curBlock;
    //stores all blocks considered
    public static BasicBlockList blocks;



    public Prog prog;


    public Translator(MJProgram javaProg)
    {
        this.javaProg = javaProg;
    }


    public Prog translate(MJProgram javaProg)
    {

        prog = Prog(TypeStructList(), GlobalList(), ProcList());
        //handle all classes declarations before going on with the main
        this.handleClassDeclList(javaProg.getClassDecls());

        this.handleMainClass(javaProg.getMainClass());

        return prog;
    }

    public void handleMainClass(MJMainClass mainClass) {

        //fill this with other blocks in the future
        blocks = BasicBlockList();

        //main method
        Proc mainProc = Proc("main", TypeInt(), ParameterList(), blocks);

        prog.getProcedures().add(mainProc);

        //main block
        BasicBlock mainBlock = BasicBlock();

        //get the list of instructions
        mainBlock.setName("entry");
        blocks.add(mainBlock);
        this.curBlock = mainBlock;


        mainClass.getMainBody().accept(this);

        curBlock.add(ReturnExpr(ConstInt(0)));
    }


    public void handleClassDeclList(MJClassDeclList classDeclList)
    {

        //generate a struct for every class and save this into a hashMap
        for(MJClassDecl classDecl : classDeclList)
        {
            StructFieldList structFieldList = StaticMethods.returnStructsFieldsInClassAndParents(classDecl,StructFieldList());
            TypeStruct structClass = TypeStruct(classDecl.getName(), structFieldList);
            structsMap.put(classDecl ,structClass);

            prog.getStructTypes().add(structClass);
        }



        //firstly, need to initialize all methods and put them into a hashMap (method --> Proc)
        for(MJClassDecl classDecl : classDeclList)
        {
            initMethodsDeclarations(classDecl);
        }
        //now create virutal Method Table

        for (MJClassDecl classDecl : classDeclList)
        {
            //for every class, get its fields and the ones of their parents, adding them to it ( replication)

            ClassData dataClass = createVirtualMethodTable(classDecl);
            //get all the elements obtained from the class instantiation
            TypeStruct virtualMethodTable = dataClass.getVirtualTable();
            prog.getStructTypes().add(virtualMethodTable);
            //ProcList proceduresList = dataClass.getProcList();


            TypeStruct structClass = structsMap.get(classDecl);

            //get fields in the structClass
            StructFieldList structFieldsClass = structClass.getFields();
            //add the V-Table as the first field
            TypePointer pointerVTable = TypePointer(virtualMethodTable);
            structFieldsClass.addFront(StructField(pointerVTable, virtualMethodTable.getName()));

            structClass.setFields(structFieldsClass);;

            structsMap.replace(classDecl, structClass);

            for(StructField structField : structClass.getFields())
            {
                System.out.println(structField.getName());
            }


            //add reference to all procedures to the V-Table
            // for(Proc proc : proceduresList)
            // {
            // System.out.println(proc.getName().toString());
            //   constListVirtual.add(ProcedureRef(proc));
            // }


            //now store t
            //ConstStruct constVirtualTable =  ConstStruct(virtualMethodTable, constListVirtual);
            //Global globalRefVirtualTable = Global(virtualMethodTable, "v_table" + classDecl.getName(), true, constVirtualTable);
            //this.prog.getGlobals().add(globalRefVirtualTable);

            //will need to be used later on in the constructor
            //Translator.vTableClass.put(classDecl, globalRefVirtualTable);
        }



    }


    /**
     * Creates a virtual method table with all corresponding functions for a class passed
     * @param classDecl
     * @return
     */

    public ClassData createVirtualMethodTable(MJClassDecl classDecl)
    {
        // System.out.println("Creating V-Table for " + classDecl.getName());

        //initialize functions pointers as fields of the current class as TypeStruct
        ClassData classDataBaseClass = getFunctionsInClass(classDecl, StructFieldList(), StructFieldList(), new ClassData());

        ProcList procsBaseClass = classDataBaseClass.getProcList();

        //contains all struct fields in the correct order, i.e: parent classes and current class
        ClassData classDataReturn = new ClassData();
        MJClassDecl parentClass = classDecl.getDirectSuperClass();

        //now check all parents in a recursive fashion. do not add functions overridden at base class level!
        while(parentClass != null)
        {
            //and now get all the functions in parent classes
            classDataReturn = getFunctionsInClass(parentClass, classDataBaseClass.getStructFieldList(), classDataReturn.getStructFieldList(), classDataReturn);
            parentClass = parentClass.getDirectSuperClass();
        }

        ProcList procListAll = classDataReturn.getProcList();

        //now all procedures in base class after the ones inherited
        for(Proc procBase : procsBaseClass)
        {
            procListAll.add(procBase.copy());
        }


        //firstly, all parents-inherited functions, then the ones of the base class
        StructFieldList structFieldListReturn = StructFieldList();

        //take functions in parents and put them before the ones of current base class
        for(StructField structFieldParent : classDataReturn.getStructFieldList())
        {
           // System.out.println("Adding " + structFieldParent.getName().toString() + " from parents class");
            structFieldListReturn.add(structFieldParent.copy());


        }

       // Operand lengthBase = ConstInt(0);
       // TemporaryVar tempIndex = TemporaryVar("index struct field list");
       // Translator.curBlock.add(Load(tempIndex, ConstInt(0)));

        //functions of the base class
        for(StructField structFieldBase : classDataBaseClass.getStructFieldList())
        {
            structFieldListReturn.add(structFieldBase.copy());
        }

        TypeStruct structVirtualTable = TypeStruct("virtual_method_table" + classDecl.getName() , structFieldListReturn);
        TemporaryVar tempStructVTable = TemporaryVar("v-table");

        StructFieldList structFieldsWithPointers = StructFieldList();

        //System.out.println("V-Table contains:");
        for(Proc  proc: procListAll)
        {
            //Daniele: store pointers to the different procedures here

           // Bitcast binCastPointer = Bitcast(arrayPointer, TypePointer(structVirtualTable), VarRef(arrayHeapVar));


           // TemporaryVar tempVTable = TemporaryVar("v-table");
         //  Load loadStructFIeldList = Load(tempVTable, structVirtualTable);
          // TemporaryVar tempPointer = TemporaryVar("temp pointer");
           //GetElementPtr elementPtr = GetElementPtr(tempPointer, , OperandList());

        }




        classDataReturn.setStructFieldList(structFieldListReturn);
        classDataReturn.setVirtualTable(structVirtualTable);
        classDataReturn.setProcList(procListAll);


        return classDataReturn;
    }

    /**
     * Adds pointers to functions of the current class to the StructFieldList to be returned.
     * Makes sure overridden functions are not added twice (hence, parent is ignored)
     * @param classDecl: current class declaration where we are adding functions to.
     * @param structFieldsBaseClass: structFieldList with functions of the base class
     * @param structFieldsParents: structFieldLsit with functions of the parent classes
     * @return a structFieldList for functions contained in class
     */
    public ClassData getFunctionsInClass(MJClassDecl classDecl, StructFieldList structFieldsBaseClass, StructFieldList structFieldsParents, ClassData classData)
    {
        //loop through all functions and create a pointer to the procedures previously stored
        for(MJMethodDecl methodDecl: classDecl.getMethods())
        {
            //get type of all parameters and store it into this list
            TypeRefList typeRefList = TypeRefList();
            TypeMatcher typeMatcher = new TypeMatcher();

            for(MJVarDecl paramDecl : methodDecl.getFormalParameters())
            {
                // System.out.println("Adding " + structFieldBase.getName().toString() + " from base class");
                Type typeParam = paramDecl.getType().match(typeMatcher);
                typeRefList.add(typeParam);
            }

            Type resultType = methodDecl.getReturnType().match(typeMatcher);
            TypeProc typeProcedures = TypeProc(typeRefList, resultType);
            //finally, pointer to the procedure created
            TypePointer typePointerProc = TypePointer(typeProcedures);

            //check for not adding overridden procedures
            if(!(procedureSameNameExists(methodDecl.getName(), structFieldsBaseClass)))
            {
                //if we're in the base class, then add to the front
                if(structFieldsBaseClass.isEmpty())
                {
                   // System.out.println("Adding func " + methodDecl.getName() + " in V-Table of class " + classDecl.getName() + " to cur Class from base " );
                    structFieldsParents.add(StructField(typePointerProc, methodDecl.getName()));
                    Proc procOfMethod = methodsProcs.get(methodDecl);
                    classData.procList.add(procOfMethod.copy());
                   // System.out.println("adding proc " + procOfMethod.getName() + " from base class");

                }
                //not in the base class
                else
                {
                   // System.out.println("Adding func " + methodDecl.getName() + " in V-Table of class " + classDecl.getName() + " to cur Class from parent." );
                    structFieldsParents.addFront(StructField(typePointerProc, methodDecl.getName()));
                    Proc procOfMethod = methodsProcs.get(methodDecl);
                    classData.procList.addFront(procOfMethod.copy());
                  //  System.out.println("adding proc " + procOfMethod.getName() + " from parent");

                }

                //and put it as field with the corresponding proc name into the list
            }

            classData.setStructFieldList(structFieldsParents);
        }

        return classData;
    }




    /**
     * Check for overriding procedures  (if they were already stored in the v-table
     * @param name
     * @param structFieldList
     * @return
     */
    public boolean procedureSameNameExists(String name, StructFieldList structFieldList)
    {
        for(StructField structField: structFieldList)
        {
            if(structField.getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }


    /**
     * Stores procedures into the list of prog procedures and hashMap
     * Transforms methods into procedures
     */
    public void initMethodsDeclarations(MJClassDecl classDecl)
    {
        //firstly,

        for(MJMethodDecl methodDecl : classDecl.getMethods())
        {
            System.out.println("Adding method " + methodDecl.getName());

            TypeMatcher typeMatcher = new TypeMatcher();
            String methodName = methodDecl.getName();
            Type returnType = methodDecl.getReturnType().match(typeMatcher);


            ParameterList parameterList = ParameterList();
            //for all parameters, convert their type
            for (MJVarDecl paramDecl : methodDecl.getFormalParameters())
            {
                String paramName = paramDecl.getName();
                Type paramType = paramDecl.getType().match(typeMatcher);

                Parameter parameter = Parameter(paramType, paramName);
                parameterList.add(parameter);
            }

            //create new blocks for the procedure
            blocks = BasicBlockList();
            BasicBlock methodBlock = BasicBlock();
            methodBlock.setName(methodDecl.getName());
            blocks.add(methodBlock);
            curBlock = methodBlock;
            //add all stuff to the method block
            methodDecl.getMethodBody().accept(this);

            Proc methodProc = Proc(methodName, returnType, parameterList, blocks);

            prog.getProcedures().add(methodProc);

            methodsProcs.put(methodDecl, methodProc);
        }
    }



    /**
     * @param stmtIf(@code MJStmtIf)
     *                     Parses content of an IF statement.
     *                     BeforeBlock
     *                     if(exprCondtion)
     *                     {ifBlock}
     *                     else
     *                     {elseBlock}
     */
    public void visit(MJStmtIf stmtIf) {

        //need to check if it's false or true to decide which body we should enter
        MJExpr exprCondition = stmtIf.getCondition();
        ExprMatcherR exprMatchR = new ExprMatcherR();
        //will be boolean
        Operand operandCondition = exprCondition.match(exprMatchR);

        //true or false?
        //true statements if(){statements}
        MJStatement statementsTrue = stmtIf.getIfTrue();
        //else statements else{statements}
        MJStatement statementsFalse = stmtIf.getIfFalse();

        BasicBlock basicBlockTrue = BasicBlock();
        basicBlockTrue.setName("true IF");

        BasicBlock basicBlockFalse = BasicBlock();
        basicBlockFalse.setName("false IF");

        BasicBlock basicBlockAfterIfElse = BasicBlock();
        basicBlockAfterIfElse.setName("END if-else");

        //add the new terminating instruction to the current block
        Branch branchIfElse = Branch(operandCondition, basicBlockTrue, basicBlockFalse);
        this.curBlock.add(branchIfElse);

        //we can now define the two code blocks. Branch decides which one should be evaluated anyway

        //TRUE code block in IF-ELSE.
        //update references
        this.curBlock = basicBlockTrue;
        this.blocks.add(basicBlockTrue);
        //evaluate all expressions inside the true block
        statementsTrue.accept(this);
        //once we're done with this block, go on with the remaining code
        this.curBlock.add(Jump(basicBlockAfterIfElse));


        //FALSE code block (else) in IF. same functioning as TRUE block.
        this.curBlock = basicBlockFalse;
        this.blocks.add(basicBlockFalse);
        statementsFalse.accept(this);
        this.curBlock.add(Jump(basicBlockAfterIfElse));


        //done, go ahead with remaining code
        this.curBlock = basicBlockAfterIfElse;
        this.blocks.add(basicBlockAfterIfElse);

    }

    /**
     * @param (@code MJBlock)
     */
    public void visit(MJBlock block) {
        for (MJStatement statement : block) {
            statement.accept(this);
        }
    }


    /**
     * @param stmtWhile(@code MJStmtWhile)
     *                        BeforeBlock
     *                        while(exprCondition)
     *                        {ConditionBlock}
     *                        AfterBlock
     */
    @Override
    public void visit(MJStmtWhile stmtWhile) {

        BasicBlock conditionBlock = BasicBlock();
        BasicBlock bodyBlock = BasicBlock();
        BasicBlock restBlock = BasicBlock();

        //first of all, go directly to the condition (top part)
        this.curBlock.add(Jump(conditionBlock));
        //update block
        this.curBlock = conditionBlock;

        //Now check the condition:
        ExprMatcherR exprMatchR = new ExprMatcherR();
        MJExpr exprCondition = stmtWhile.getCondition();
        Operand operCondition = exprCondition.match(exprMatchR);

        //if condition met, go to the while body, if not met, go ahead with rest
        Branch branchWhile = Branch(operCondition, bodyBlock, restBlock);
        this.blocks.add(conditionBlock);
        conditionBlock.add(branchWhile);

        //good, now evaluate while body
        blocks.add(bodyBlock);
        this.curBlock = bodyBlock;
        MJStatement stmtsBody = stmtWhile.getLoopBody();
        stmtsBody.accept(this);
        //check the condition again
        this.curBlock.add(Jump(conditionBlock));

        //now check what happens outside the while
        this.curBlock = restBlock;
        blocks.add(restBlock);
    }

    /**
     * @param stmtReturn(@code MJStmtReturn)
     *                         return expr
     */
    @Override
    public void visit(MJStmtReturn stmtReturn)
    {
        ExprMatcherR exprMatcherR = new ExprMatcherR();

        Operand operReturn = stmtReturn.getResult().match(exprMatcherR);

        Translator.curBlock.add(ReturnExpr(operReturn));
    }


    /**
     * @param stmtPrint(@code MJStmtPrint)
     *                        eg: System.out.println(expr)
     *                        System.out.println(5*7+9)
     */
    @Override
    public void visit(MJStmtPrint stmtPrint) {
        //constant or variable
        MJExpr expr = stmtPrint.getPrinted();
        Operand operand;
        ExprMatcherR exprMatchR = new ExprMatcherR();
        //check which operand type it is
        //operand=Ast.ConstInt((int)expr.match(exprMatcher));
        operand = expr.match(exprMatchR);
        Print print = Print(operand);
        this.curBlock.add(print);

    }

    /**
     * Variable --> parameter declaration
     *
     * @param varDecl(@code MJVarDecl)
     *                      Type id
     *                      eg: int a;
     *                      boolean b;
     */
    @Override
    public void visit(MJVarDecl varDecl)
    {

        MJType typeName = varDecl.getType();
        String varName = varDecl.getName();
        TemporaryVar tempVar = TemporaryVar(varName);

        //a = null by default
        //int a;
        if (typeName instanceof MJTypeInt)
        {
            Type typeInt = TypeInt();
            //put variable declaration onto stack
            Alloca allocVar = Alloca(tempVar, typeInt);
            this.curBlock.add(allocVar);
            //add to hashmap
            this.varsTemp.put(varName, tempVar);
        }
        //boolean a;
        else if (typeName instanceof MJTypeBool)
        {
            Type typeBool = TypeBool();
            //put variable declaration onto stack
            Alloca allocVar = Alloca(tempVar, typeBool);
            this.curBlock.add(allocVar);
            //add to hashmap
            this.varsTemp.put(varName, tempVar);
        }
        //int[] a;
        else if (typeName instanceof MJTypeIntArray)
        {
            //create a pointer to an array.
            TemporaryVar tempArray = TemporaryVar("arrayPointer");
            //allocate onto stack an array with size 0 to allow for pointers to refer to it
            TypeArray typeArray = TypeArray(TypeInt(), 0);
            Alloca allocVar = Alloca(tempArray, TypePointer(TypePointer(typeArray)));
            this.curBlock.add(allocVar);
            TemporaryVar tempVarReturn = TemporaryVar("arrayCasted");
            //converting to array. credits to Joseff for this tip!
            Bitcast arrayConverted = Bitcast(tempVarReturn, TypePointer(TypePointer(typeArray)), VarRef(tempArray));
            this.curBlock.add(arrayConverted);
            //finally, add to hashmap
            this.varsTemp.put(varName, tempVarReturn);
        }
        else if(typeName instanceof MJTypeClass)
        {
            //store it into the hash map too
            MJClassDecl classDecl = ((MJTypeClass)typeName).getClassDeclaration();

            String className = ((MJTypeClass) typeName).getClassDeclaration().getName();
            TemporaryVar classTemp = TemporaryVar("class_var_"+ className);
            this.curBlock.add(Alloc(classTemp,ConstInt(0)));
            TemporaryVar tempClassReturn = TemporaryVar("return class" + className);


            this.curBlock.add(Bitcast(tempClassReturn, TypePointer(TypePointer(structsMap.get(classDecl))), VarRef(classTemp)));
            this.varsTemp.put(varName, tempClassReturn);
        }

    }

    /**
     * @param stmtAssign(@code MJStmtAssign)FieldAccess)
     *                         parses the assignment instuctions
     *                         expr=expr
     *                         eg: a=5;
     *                         a[]=new int[10]
     */
    @Override
    public void visit(MJStmtAssign stmtAssign) {
        MJExpr exprLeft = stmtAssign.getLeft();
        MJExpr exprRight = stmtAssign.getRight();

        //match left --> put var use into exprmatcher L
        ExprMatcherL exprMatchL = new ExprMatcherL();
        Operand operLeft = exprLeft.match(exprMatchL);
        System.out.println("Left is type" + operLeft.calculateType().toString());

        ExprMatcherR exprMatchR = new ExprMatcherR();
        Operand operRight = exprRight.match(exprMatchR);
        System.out.println("Right is type " + operRight.calculateType().toString());

        //if same type or assigning null, go ahead!
        if(operLeft.calculateType().equals(operRight.calculateType()) ||
                operLeft.calculateType().equalsType(TypeNullpointer()) ||
                        operRight.calculateType().equalsType(TypeNullpointer()))
        {
            Store storeValue = Store(operLeft, operRight);
            this.curBlock.add(storeValue);
        }
        else
        {
                //convert left to value of right-hand side
                 TemporaryVar tempCastLeft = TemporaryVar("temp cast left");
                 Translator.curBlock.add(Bitcast(tempCastLeft,TypePointer(operRight.calculateType()),operLeft));

                 Store storeValue = Store(VarRef(tempCastLeft), operRight);
                 Translator.curBlock.add(storeValue);
        }




    }


}
