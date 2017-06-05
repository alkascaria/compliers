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
    //not sure if needed?
    public static HashMap<String, VarRef> varsStackRef = new HashMap<>();
    //if the value changes, remember to update the corresponding value in the hashmap.
    public static HashMap<String, Integer> varsStackInt = new HashMap<>();
    public static HashMap<String, Boolean> varsStackBool = new HashMap<>();

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
     * @param statement(@code MJStatement)
     */
    public void visit(MJStatement statement) {
        statement.match(new StatementChecker(this));
    }

    /**
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
    //TODO: replace instanceof with TypeMatcher
    @Override
    public void visit(MJVarDecl varDecl) {
        MJType typeVar = varDecl.getType();
        String typeName = varDecl.getName();

        TemporaryVar tempVar;

        if (typeVar instanceof MJTypeInt) {
            tempVar = TemporaryVar(typeName);
            //allocates space on the stack
            Alloca alloca = Alloca(tempVar, TypeInt());
            //add it to current block, and consequently to current procedure
            this.curBlock.add(alloca);
            //put the variable onto the stack
            varsStackTempVar.put(typeName, tempVar);
        } else if (typeVar instanceof MJTypeBool) {
            tempVar = TemporaryVar(typeName);
            //allocates space on the stack
            Alloca alloca = Alloca(tempVar, TypeBool());
            this.curBlock.add(alloca);
            //put the variable onto the stack
            varsStackTempVar.put(typeName, tempVar);
        } else if (typeVar instanceof MJTypeIntArray) {
            //TODO: part two. handle array declarations
            //int C, create a struct with pointer to element and size.
            //parameter = Parameter(TypeIntArray(), typeName);
        }
        //TODO: next exercise, handle class declarations
        else if (typeVar instanceof MJTypeClass) {

        }
    }


    /**
     * Variable --> Temporary variable.
     *
     * @param varUse
     */
    @Override
    public void visit(MJVarUse varUse) {


    }

    /**
     *
     * @param varName: name of the variable whose value needs to be modified
     * @param varValue: new value of the variable
     *                This function updates all references to variables with the value provided
     *                It checks if a reference to the variable specified is there before inserting
     *                a new value into the hashmap
     */
    public void updateHashMapsVariableInt(String varName, int varValue)
    {
        //store value into variable's stack address
        TemporaryVar tempVar = this.varsStackTempVar.get(varName);
        VarRef varRef = VarRef(tempVar);
        Store storeRef = Store(varRef, ConstInt(varValue));
        this.curBlock.add(storeRef);
        //now add it to the var refs
        this.varsStackRef.put(varName, varRef);

        //if not there yet, add it
        if (!(this.varsStackInt.containsKey(varName)))
        {
            varsStackInt.put(varName, varValue);
        }
        //update
        else if(this.varsStackInt.containsKey(varName))
        {
            varsStackInt.put(varName, varValue);
        }
    }

    public void updateHashMapsVariableBool(String varName, boolean varBoolVal)
    {
        //store value into variable's stack address
        TemporaryVar tempVar = this.varsStackTempVar.get(varName);
        VarRef varRef = VarRef(tempVar);
        Store storeRef = Store(varRef, ConstBool(varBoolVal));
        this.curBlock.add(storeRef);
        //now add it to the var refs
        this.varsStackRef.put(varName, varRef);

        //if not there yet, add it
        if (!(this.varsStackBool.containsKey(varName)))
        {
            varsStackBool.put(varName, varBoolVal);
        }
        //update
        else if(this.varsStackBool.containsKey(varName))
        {
            varsStackBool.put(varName, varBoolVal);
        }

        System.out.println(this.varsStackBool.toString());
    }

    @Override
    public void visit(MJStmtAssign stmtAssign) {
        MJExpr exprLeft = stmtAssign.getLeft();
        MJExpr exprRight = stmtAssign.getRight();

        //ex: z = 5
        if ((exprLeft instanceof MJVarUse) && (exprRight instanceof MJNumber))
        {
            String nameLeft = ((MJVarUse) exprLeft).getVarName();
            int valueRight = ((MJNumber) exprRight).getIntValue();

            updateHashMapsVariableInt(nameLeft, valueRight);
        }
        //ex: b = d
        else if ((exprLeft instanceof MJVarUse) && (exprRight instanceof MJVarUse))
        {
            //distinguish between integer and boolean
            MJType typeExprRight = ((MJVarUse) exprRight).getVariableDeclaration().getType();

            String nameLeft = ((MJVarUse) exprLeft).getVarName();
            String nameRight = ((MJVarUse) exprRight).getVarName();

            if(typeExprRight instanceof MJTypeInt)
            {
                //get value from right-hand side and assign to left-hand side variable
                int varRight = this.varsStackInt.get(nameRight);
                updateHashMapsVariableInt(nameLeft, varRight);
            }
            else if(typeExprRight instanceof MJTypeBool)
            {
                //get value from right-hand side and assignt to left-hand side
                boolean boolRight = this.varsStackBool.get(nameRight);
                updateHashMapsVariableBool(nameLeft, boolRight);
            }
        }
        //ex: x = false
        else if ((exprLeft instanceof MJVarUse) && (exprRight instanceof MJBoolConst)) {
            String nameLeft = ((MJVarUse) exprLeft).getVarName();
            boolean boolRight = ((MJBoolConst) exprRight).getBoolValue();

            updateHashMapsVariableBool(nameLeft, boolRight);
        }
        //example : d = -5 or d = -v;
        else if ((exprLeft instanceof MJVarUse) && (exprRight instanceof MJExprUnary))
        {
            //variable name
            String nameLeft = ((MJVarUse) exprLeft).getVarName();
            MJExprUnary unaryRight = (MJExprUnary) exprRight;

            //unary minus with int constants (ex: d = -5)
            if (unaryRight.getExpr() instanceof MJNumber)
            {
                MJNumber numberRight = (MJNumber)unaryRight.getExpr();
                int valueRight = -(numberRight.getIntValue());
                updateHashMapsVariableInt(nameLeft, valueRight);
            }
            //unary neg with bool constants
            else if(unaryRight.getExpr() instanceof MJBoolConst)
            {
                MJBoolConst booleanRight = (MJBoolConst)unaryRight.getExpr();
                boolean boolRight = !(booleanRight.getBoolValue());
                updateHashMapsVariableBool(nameLeft, boolRight);
            }
            else if (unaryRight.getExpr() instanceof MJVarUse)
            {
                //check two cases. either boolean or integer
                MJVarUse varUseRight = (MJVarUse)unaryRight.getExpr();
                MJType typeVarUse = varUseRight.getVariableDeclaration().getType();
                String nameRight = varUseRight.getVarName();


                if(typeVarUse instanceof MJTypeInt)
                {
                    int varIntRight = - (this.varsStackInt.get(nameRight));
                    updateHashMapsVariableInt(nameLeft, varIntRight);
                }
                else if(typeVarUse instanceof MJTypeBool)
                {
                    boolean varBoolRight = !(this.varsStackBool.get(nameRight));
                    updateHashMapsVariableBool(nameLeft, varBoolRight);
                }

            }

        }
    }
}
