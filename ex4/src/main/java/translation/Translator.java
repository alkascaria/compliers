package translation;

import minijava.ast.*;
import minillvm.ast.*;

import static minillvm.ast.Ast.*;

import minillvm.analysis.*;

import java.util.HashMap;

//Statements, visitor like here is fine.
//expressions, separate class MJExpr.Matcher<Operand> to pass back an operand when evaluating something


public class Translator extends MJElement.DefaultVisitor {

    private final MJProgram javaProg;

    public static HashMap<String, Type> varDeclsType = new HashMap<>();
    public static HashMap<String, TemporaryVar> varDeclsTempVar = new HashMap<>();


    //stores which Proc and Block we are currently in.
    Proc curProc;
    BasicBlock curBlock;


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

        //main block?
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

        if (typeVar instanceof MJTypeInt) {
            tempVar = TemporaryVar(typeName);

            Alloca(tempVar, TypeInt());  //allocates space on the stack


            varDeclsType.put(typeName, TypeInt());   //Put the variable into a Hashmap
            varDeclsTempVar.put(typeName, tempVar);   //Put the variable into a Hashmap
        } else if (typeVar instanceof MJTypeBool) {
            tempVar = TemporaryVar(typeName);
            Alloca(tempVar, TypeBool());
            varDeclsType.put(typeName, TypeBool());   //Put the variable into a Hashmap
            varDeclsTempVar.put(typeName, tempVar);   //Put the variable into a Hashmap
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
        if ((exprLeft instanceof MJVarUse) && (exprRight instanceof MJNumber)) {
            String nameLeft = ((MJVarUse) exprLeft).getVarName();
            int valueRight=((MJNumber) exprRight).getIntValue();
            Type typeExprLeft = varDeclsType.get(nameLeft);
            TemporaryVar tempVar =  varDeclsTempVar.get(nameLeft);
            Operand operand = ConstInt(valueRight);
            Store(VarRef(tempVar), operand);
        }

        //    ExprTranslatorMatcher trans = new ExprTranslatorMatcher();

        //  Operand operLeft = exprLeft.match(trans);

        //Alloc(TemporaryVar var, Operand sizeInBytes)
        //allocate X bytes on the heap


    }


}
