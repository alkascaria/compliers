package translation;

import main.MiniJavaCompiler;
import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;

import minillvm.analysis.*;

import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

//Statements, visitor like here is fine.
//expressions, separate class MJExpr.Matcher<Operand> to pass back an operand when evaluating something


public class Translator extends MJElement.DefaultVisitor {

    private final MJProgram javaProg;

    //variables declarations go onto the stack (ex: int a). contains no value yet!
    public static HashMap<String, TemporaryVar> varsStackTempVar = new HashMap<>();
    public static HashMap<String, VarRef> varsStackRef = new HashMap<>();
    public static HashMap<String, Integer> varsStackval = new HashMap<>();

    //variable assignments go onto the head (ex: a = 5)
   // public static HashMap<String, Integer> varsHeapValue = new HashMap<>();


    //stores which Proc and Block we are currently in.
    public static Proc curProc;
    public static BasicBlock curBlock;


    public Translator(MJProgram javaProg) {
        this.javaProg = javaProg;
    }

    public Prog translate() {
        Prog prog = Prog(TypeStructList(), GlobalList(), ProcList());

        //fill this with other blocks in the future
        BasicBlockList blocks = BasicBlockList();

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

        curBlock.add(ReturnExpr(ConstInt(0)));

        return prog;
    }

    /**
     *
     * @param statement(@code MJStatement)
     */
    public void visit(MJStatement statement) {
        statement.match(new StatementChecker(this));
    }

    /**
     *
     * @param operator(@code MJOperator)
     */
    public void operator(MJOperator operator) {
         operator.match(new OperatorChecker(this));
    }



    @Override
    public void visit(MJStmtPrint stmtPrint) {
        //constant or variable
        MJExpr expr = stmtPrint.getPrinted();

        Operand operand;
        ExprTranslatorMatcher trans = new ExprTranslatorMatcher();

        //check which operand type it is
        operand = expr.match(trans);
        System.out.println(operand.toString());

        Print print = Print(operand);
        this.curBlock.add(print);
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
    //TODO: replace instanceof with matcher
    @Override
    public void visit(MJVarDecl varDecl) {
        MJType typeVar = varDecl.getType();
        String typeName = varDecl.getName();

        TemporaryVar tempVar;

        if (typeVar instanceof MJTypeInt)
        {
            tempVar = TemporaryVar(typeName);
			//allocates space on the stack
            Alloca alloca = Alloca(tempVar, TypeInt());
            //add it to current block, and consequently to current procedure
            this.curBlock.add(alloca);
			//put the variable onto the stack
            varsStackTempVar.put(typeName, tempVar);
        }
        else if (typeVar instanceof MJTypeBool)
        {
            tempVar = TemporaryVar(typeName);
            //allocates space on the stack
            Alloca alloca = Alloca(tempVar, TypeBool());
            this.curBlock.add(alloca);
			//put the variable onto the stack
            varsStackTempVar.put(typeName, tempVar);
        }
        else if (typeVar instanceof MJTypeIntArray)
        {
            //TODO: part two. handle array declarations
            //int C, create a struct with pointer to element and size.
            //parameter = Parameter(TypeIntArray(), typeName);
        }
        //TODO: next exercise, handle class declarations
        else if (typeVar instanceof MJTypeClass)
        {

        }
    }

    //method to convert Minijava MJType into minillvm Type






    /**
     * Variable --> Temporary variable.
     *
     * @param varUse
     */
    @Override
    public void visit(MJVarUse varUse) {
        String varName = varUse.getVarName();

        TemporaryVar tempVar = TemporaryVar(varName);

    }

    @Override
    public void visit(MJStmtAssign stmtAssign) {
        MJExpr exprLeft = stmtAssign.getLeft();
        MJExpr exprRight = stmtAssign.getRight();


        System.out.println(exprLeft);
        //put value into the assigned stack space
        //ex: z = 5
        if ((exprLeft instanceof MJVarUse) && (exprRight instanceof MJNumber))
        {
            String nameLeft = ((MJVarUse) exprLeft).getVarName();
            int valueRight = ((MJNumber) exprRight).getIntValue();
            //store value into variable's stack address
            TemporaryVar tempVar = this.varsStackTempVar.get(nameLeft);
            VarRef varRef = VarRef(tempVar);
            Store storeRef = Store(varRef, ConstInt(valueRight));
            this.curBlock.add(storeRef);
            //now add it to the var refs
            this.varsStackRef.put(nameLeft, varRef);
            varsStackval.put(nameLeft, valueRight);
        }

    }

    /**
    public static Type matcherTypeVarUse(MJType type, MJVarDecl varDecl){

        return type.match(new MJType.Matcher<Operand>() {
            @Override
            public Operand case_TypeIntArray(MJTypeIntArray typeIntArray) {
                return null;
            }

            @Override
            public Operand case_TypeBool(MJTypeBool typeBool)
            {
                return null;

            }

            @Override
            public Operand case_TypeInt(MJTypeInt typeInt)
            {
                return Ast.ConstInt(Translator.varsStackval.get(varUse.getVarName()));

            }

            @Override
            public Operand case_TypeClass(MJTypeClass typeClass) {
                return null;
            }
        });

    }

     */

}
