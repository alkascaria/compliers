package translation;

import main.MiniJavaCompiler;
import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;

import minillvm.analysis.*;

import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Arrays;

//Statements, visitor like here is fine.
//expressions, separate class MJExpr.Matcher<Operand> to pass back an operand when evaluating something


public class Translator extends MJElement.DefaultVisitor
{

    private final MJProgram javaProg;

    //variables declarations go onto the stack (ex: int a). contains no value yet!
    public static HashMap<String, TemporaryVar> varsTemp = new HashMap<>();
    //not sure if needed?

    //TODO: DELETE
    //if the value changes, remember to update the corresponding value in the hashmap.
    public static HashMap<String, Integer> varsStackInt = new HashMap<>();
    public static HashMap<String, Boolean> varsStackBool = new HashMap<>();

    public static ArrayList<TerminatingInstruction> curBlockErrors = new ArrayList<TerminatingInstruction>();


    //variable assignments go onto the head (ex: a = 5)
    // public static HashMap<String, Integer> varsHeapValue = new HashMap<>();


    //stores which Proc and Block we are currently in.
    public static Proc curProc;
    public static BasicBlock curBlock;

    public static BasicBlockList blocks;

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
        this.curProc = mainProc;

        prog.getProcedures().add(mainProc);

        //main block
        BasicBlock mainBlock = BasicBlock
        (

        );

        //get the list of instruction
        mainBlock.setName("entry");
        blocks.add(mainBlock);
        this.curBlock = mainBlock;


        javaProg.accept(this);

        //check if curBlock contains a terminating instruction. if it doesn't, add Return


        if((curBlockErrors.isEmpty()))
        {
            //System.out.println("Empty");
            curBlock.add(ReturnExpr(ConstInt(0)));
        }

        curBlockErrors.clear();

        return prog;
    }


    /**
     * @param stmtIf(@code MJStmtIf)
     * Parses content of an IF statement.
     */
    public void visit(MJStmtIf stmtIf)
    {

        //need to check if it's false or true to decide which body we should enter
        MJExpr exprCondition = stmtIf.getCondition();
        ExprMatcher exprMatcher = new ExprMatcher();
        //check which operand type it is. we know it must be a boolean here
        //Operand operandCondition = Ast.ConstBool((boolean)exprCondition.match(exprMatcher));
        Operand operandCondition = exprCondition.match(exprMatcher);
        //true or false?
        //true statements if(){statements}
        MJStatement statementsTrue = stmtIf.getIfTrue();
        //else statements else{statements}
        MJStatement statementsFalse = stmtIf.getIfFalse();


        BasicBlock basicBlockTrue = BasicBlock();
        basicBlockTrue.setName("TRUE if");

        BasicBlock basicBlockFalse = BasicBlock();
        basicBlockFalse.setName("FALSE if");

        BasicBlock basicBlockAfterIfElse = BasicBlock();
        basicBlockAfterIfElse.setName("After if-else ");

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
     *
     * @param (@code MJBlock)
     */
    public void visit(MJBlock block)
    {
        for(MJStatement statement: block)
        {
            statement.accept(this);
        }
    }


    /**
     *
     * @param stmtWhile(@code MJStmtWhile)
     */
    @Override
    public void visit(MJStmtWhile stmtWhile)
    {

        ExprMatcher exprMatcher = new ExprMatcher();
        //very similar to "IF"
        MJExpr exprCondition = stmtWhile.getCondition();

        MJStatement stmtLoopBody = stmtWhile.getLoopBody();

        BasicBlock basicBlockBodyWhile = BasicBlock();
        BasicBlock basicBlockAfterWhile = BasicBlock();

        this.curBlock.add(Jump(basicBlockBodyWhile));

        this.curBlock = basicBlockBodyWhile;
        this.blocks.add(basicBlockBodyWhile);


        //TODO: recode with branches
        /*
        boolean condition = exprCondition.match(exprMatcher);

        while(condition)
        {
            //EVALUATE BODY
            stmtLoopBody.accept(this);
            //update condition
            condition = (boolean)exprCondition.match(exprMatcher);
        }

        */

        //leaving while, add the return to the other block
        this.curBlock.add(Jump(basicBlockAfterWhile));

        this.curBlock = basicBlockAfterWhile;
        this.blocks.add(basicBlockAfterWhile);

    }

    /**
     *
     * @param stmtReturn(@code MJStmtReturn)
     */
    @Override
    public void visit(MJStmtReturn stmtReturn)
    {
         throw new RuntimeException();
    }


    @Override
    public void visit(MJStmtPrint stmtPrint)
    {
        //constant or variable
        MJExpr expr = stmtPrint.getPrinted();

        Operand operand;

        ExprMatcher exprMatcher=new ExprMatcher();
        //check which operand type it is
        //operand=Ast.ConstInt((int)expr.match(exprMatcher));
        operand=expr.match(exprMatcher);

        Print print = Print(operand);

        //if no error, go ahead and add it
        if(curBlockErrors.isEmpty())
        {
            this.curBlock.add(print);
        }

    }

    /**
     * Variable --> parameter declaration
     *
     * @param varDecl(@code MJVarDecl)
     */


    //TODO: replace instanceof with TypeMatcher
    @Override
    public void visit(MJVarDecl varDecl)
    {
        TypeMatcher typeMatcher = new TypeMatcher();

        MJType typeVar = varDecl.getType();
        String typeName = varDecl.getName();

        Type typeReturn = typeVar.match(typeMatcher);
        TemporaryVar tempVar = TemporaryVar(typeName);
        Alloca alloca = Alloca(tempVar, typeReturn);
        this.curBlock.add(alloca);
        varsTemp.put(typeName, tempVar);
    }



    @Override
    public void visit(MJStmtAssign stmtAssign)
    {
        MJExpr exprLeft = stmtAssign.getLeft();
        MJExpr exprRight = stmtAssign.getRight();

        //left side in here since it's just about a few cases: MJVarUse and FieldAccess

        //a = something.
        if(exprLeft instanceof MJVarUse)
        {
            MJVarUse varUseLeft = (MJVarUse) exprLeft;
            String varNameLeft = varUseLeft.getVarName();

            ExprMatcher exprMatcher=new ExprMatcher();
            //returns right operand for the variable
            Operand operRight = exprRight.match(exprMatcher);

            //store the value of the right-hand side into the left-hand side.
            Store store = Store(VarRef(varsTemp.get(varNameLeft)), operRight);
            this.curBlock.add(store);
        }
    }
}
