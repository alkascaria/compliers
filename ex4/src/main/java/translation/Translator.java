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

//Statements, visitor like here is fine.
//expressions, separate class MJExpr.Matcher<Operand> to pass back an operand when evaluating something


public class Translator extends MJElement.DefaultVisitor
{

    private final MJProgram javaProg;

    //variables declarations go onto the stack (ex: int a). contains no value yet!
    public static HashMap<String, TemporaryVar> varsStackTempVar = new HashMap<>();
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
     * @param stmtIf
     * Parses content of an IF statement.
     */
    public void visit(MJStmtIf stmtIf)
    {
        //need to check if it's false or true to decide which body we should enter
        MJExpr exprCondition = stmtIf.getCondition();
        ExprMatcher exprMatcher = new ExprMatcher();
        //check which operand type it is. we know it must be a boolean here
        //Operand operandCondition = Ast.ConstBool((boolean)exprCondition.match(exprMatcher));
        Operand operandCondition = Ast.ConstBool((boolean)exprCondition.match(exprMatcher));
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
     * @param stmtWhile
     */
    @Override
    public void visit(MJStmtWhile stmtWhile)
    {
        /*
        ExprMatcher exprMatcher = new ExprMatcher();
        //very similar to "IF"
        MJExpr exprCondition = stmtWhile.getCondition();


        MJStatement stmtLoopBody = stmtWhile.getLoopBody();

        BasicBlock basicBlockCondition = BasicBlock();

        BasicBlock basicBlockBodyWhile = BasicBlock();

        BasicBlock basicBlockAfterWhile = BasicBlock();

        //first of all, evaluate the while's condition
        this.curBlock.add(Jump(basicBlockCondition));
        this.curBlock = basicBlockCondition;
        //update references
        //this.curBlock = basicBlockCondition;
        //this.blocks.add(basicBlockCondition);

        //now check if the body should be evaluated or we should leave the loop
        //do now instantiate Branch in a reference variable or we will have problems (as it needs to be
        //instantiated repeatedly
        Operand operandCondition = Ast.ConstBool((boolean)exprCondition.match(exprMatcher));

        basicBlockCondition.add(Branch(operandCondition, basicBlockBodyWhile, basicBlockAfterWhile));


        //start of while:

        this.blocks.add(basicBlockCondition);
        //Like in if, Branch decides which body should be evaluated.
        this.curBlock = basicBlockBodyWhile;

        //while body

        this.blocks.add(basicBlockBodyWhile);
          //evaluate statements in the while's body
        stmtLoopBody.accept(this);
        //then check the condition again
        this.curBlock.add(Jump(basicBlockCondition));


        //now check the statements outside the loop
        this.curBlock = basicBlockAfterWhile;
        this.blocks.add(basicBlockAfterWhile);

        */
    }


    @Override
    public void visit(MJStmtPrint stmtPrint)
    {

        //constant or variable
        MJExpr expr = stmtPrint.getPrinted();

        Operand operand;

        ExprMatcher exprMatcher=new ExprMatcher();
        operand=Ast.ConstInt((int)expr.match(exprMatcher));               //check which operand type it is

        Print print = Print(operand);

        //if no error, go ahead and add it
        if(curBlockErrors.isEmpty())
        {
            this.curBlock.add(print);
        }
        //constant or variable

        /*Daniele example for using Load for printing a variable. ex: System.out.println(x);
        MJExpr expr = stmtPrint.getPrinted();

        if(expr instanceof MJVarUse)
        {
            String varName = ((MJVarUse) expr).getVarName();

            TemporaryVar temp = TemporaryVar("tempvar");
            this.curBlock.add(Load(temp, VarRef(this.varsStackTempVar.get(varName))));
            this.curBlock.add(Print(VarRef(temp)));

            //this.curBlock.add(Print(VarRef(this.varsStackTempVar.get(varName))));
            //Print print = Print(Ast);
        }
        */



        //if no error, go ahead and add it
       // if(curBlockErrors.isEmpty())
        //{
         //   this.curBlock.add(print);
        //}
    }

    //declaration --> variable.
    //Varuse --> create a temporary variable
    //store variable declaration in llvm with hashmap
    //varusage --> get the variable in hashmap
    //mapping from minijava to minimmvl variable.


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
        varsStackTempVar.put(typeName, tempVar);
    }

    /**
     *
     * @param varName: name of the variable whose value needs to be modified
     * @param varValue: new value of the variable
     *                This function updates all references to variables with the value provided
     *                It checks if a reference to the variable specified is there before inserting
     *                a new value into the hashmap
     */
    public static void updateHashMapsVariableInt(String varName, int varValue)
    {
        //store value into variable's stack address
        TemporaryVar tempVar = varsStackTempVar.get(varName);
        VarRef varRef = VarRef(tempVar);
        Store storeRef = Store(varRef, ConstInt(varValue));
        curBlock.add(storeRef);
        //now add it to the var refs

        //if not there yet, add it
        if (!(varsStackInt.containsKey(varName)))
        {
            varsStackInt.put(varName, varValue);
        }
        //update
        else if(varsStackInt.containsKey(varName))
        {
            varsStackInt.put(varName, varValue);
        }
    }

    public static void updateHashMapsVariableBool(String varName, boolean varBoolVal)
    {
        //store value into variable's stack address
        TemporaryVar tempVar = varsStackTempVar.get(varName);
        VarRef varRef = VarRef(tempVar);
        Store storeRef = Store(varRef, ConstBool(varBoolVal));
        curBlock.add(storeRef);
        //now add it to the var refs

        //if not there yet, add it
        if (!(varsStackBool.containsKey(varName)))
        {
            varsStackBool.put(varName, varBoolVal);
        }
        //update
        else if(varsStackBool.containsKey(varName))
        {
            varsStackBool.put(varName, varBoolVal);
        }

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

            String nameLeft = varUseLeft.getVarName();

            ExprMatcher exprMatcher=new ExprMatcher();
            Object value=exprRight.match(exprMatcher);      //perform right operations in class

            //if no error
            if(this.curBlockErrors.isEmpty())
            {
                if (value instanceof Integer)
                    updateHashMapsVariableInt(nameLeft, (int) value);
                else
                    updateHashMapsVariableBool(nameLeft, (boolean) value);
            }

        }
        System.out.println("HasMap for Int: "+ varsStackInt);
        System.out.println("HaspMap for Boolean: "+ varsStackBool);
    }
}
