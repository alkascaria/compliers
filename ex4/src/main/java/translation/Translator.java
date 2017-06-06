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
    public static HashMap<String, VarRef> varsStackRef = new HashMap<>();
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
     * @param operator(@code MJOperator)
     */
    public void operator(MJOperator operator) {
        operator.match(new OperatorChecker(this));
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
        Operand operandCondition = Ast.ConstBool((boolean)exprCondition.match(exprMatcher));
        //true or false?
        boolean returnValue = ((boolean)exprCondition.match(exprMatcher));

        //actuall
        MJStatement statementsTrue = stmtIf.getIfTrue();
        //this is like the else?
        MJStatement statementsFalse = stmtIf.getIfFalse();


        BasicBlock basicBlockTrue = BasicBlock(

        );
        BasicBlock basicBlockFalse = BasicBlock(

        );

        if(returnValue == true)
        {
            statementsTrue.accept(this);
        }
        else if (returnValue == false)
        {
            statementsFalse.accept(this);
        }


        //firstly, parse the content

        //add terminating instructions at the end of blocks.
        //basicBlockTrue.add(Jump());
        //basicBlockFalse.add(Jump());

        this.blocks.add(basicBlockTrue);
        this.blocks.add(basicBlockFalse);

        Branch branchIFElse = Branch(operandCondition, basicBlockTrue, basicBlockFalse);

    }


    /**
     *
     * @param stmtWhile
     */
    @Override
    public void visit(MJStmtWhile stmtWhile)
    {


    }


    @Override
    public void visit(MJStmtPrint stmtPrint) {
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
    }

    //declaration --> variable.
    //Varuse --> Ttemporary variable

    //nstore variable declaration in llvm with hashmap
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
        varsStackRef.put(varName, varRef);

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
        varsStackRef.put(varName, varRef);

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
        System.out.println("HasMap for Int"+ varsStackInt);
        System.out.println("HaspMap for Boolean"+ varsStackBool);
    }
}
