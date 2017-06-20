package translation;

import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;


import java.util.HashMap;


//Statements, visitor like here is fine.
//expressions, separate class MJExpr.Matcher<Operand> to pass back an operand when evaluating something


public class Translator extends MJElement.DefaultVisitor {


    private final MJProgram javaProg;

    //variables declarations go onto the stack (ex: int a). contains no value yet!
    public static HashMap<String, TemporaryVar> varsTemp = new HashMap<>();

    //stores which Block we are currently in.
    public static BasicBlock curBlock;
    //stores all blocks we are
    public static BasicBlockList blocks;

    //stores all classes in form of structs
    public static TypeStructList structsList;



    public Prog prog;


    public Translator(MJProgram javaProg) {
        this.javaProg = javaProg;
    }

    public Prog translate()
    {
        prog = Prog(TypeStructList(), GlobalList(), ProcList());

        //fill this with other blocks in the future
        blocks = BasicBlockList();

        //main method
        Proc mainProc = Proc("main", TypeInt(), ParameterList(), blocks);
        //this.curProc = mainProc; useless

        prog.getProcedures().add(mainProc);

        structsList = TypeStructList();
        prog.getStructTypes().addAll(structsList);

        //main block
        BasicBlock mainBlock = BasicBlock();

        //get the list of instructions
        mainBlock.setName("entry");
        blocks.add(mainBlock);
        this.curBlock = mainBlock;

        javaProg.accept(this);

        curBlock.add(ReturnExpr(ConstInt(0)));

        return prog;
    }


    public void visit(MJClassDeclList classDeclList)
    {
        //loop through all classes in the class decl list
        for (MJClassDecl classDecl : classDeclList)
        {
            //store all fields of a class in here
            StructFieldList fieldsStruct = StaticMethods.convertClassFieldsToStructFields(classDecl.getFields());
            //class with the fields
            TypeStruct classStruct = Ast.TypeStruct(classDecl.getName(), fieldsStruct);

            structsList.add(classStruct);
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

        BasicBlock basicBlockFalse = BasicBlock();

        BasicBlock basicBlockAfterIfElse = BasicBlock();

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
        basicBlockTrue.add(Jump(basicBlockAfterIfElse));


        //FALSE code block (else) in IF. same functioning as TRUE block.
        this.curBlock = basicBlockFalse;
        this.blocks.add(basicBlockFalse);
        statementsFalse.accept(this);
        basicBlockFalse.add(Jump(basicBlockAfterIfElse));


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
    public void visit(MJStmtReturn stmtReturn) {
        throw new RuntimeException();
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
    public void visit(MJVarDecl varDecl) {

        MJType typeName = varDecl.getType();
        String varName = varDecl.getName();
        TemporaryVar tempVar = TemporaryVar(varName);

        //a = null by default
        //int a;
        if (typeName instanceof MJTypeInt) {
            Type typeInt = TypeInt();
            //put variable declaration onto stack
            Alloca allocVar = Alloca(tempVar, typeInt);
            this.curBlock.add(allocVar);
            //add to hashmap
            this.varsTemp.put(varName, tempVar);
        }
        //boolean a;
        else if (typeName instanceof MJTypeBool) {
            Type typeBool = TypeBool();
            //put variable declaration onto stack
            Alloca allocVar = Alloca(tempVar, typeBool);
            this.curBlock.add(allocVar);
            //add to hashmap
            this.varsTemp.put(varName, tempVar);
        }
        //int[] a;
        else if (typeName instanceof MJTypeIntArray) {
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

        ExprMatcherR exprMatchR = new ExprMatcherR();
        Operand operRight = exprRight.match(exprMatchR);

        Store storeValue = Store(operLeft, operRight);


        this.curBlock.add(storeValue);

    }


}
