package translation;

import minijava.ast.*;
import minillvm.ast.*;
import static minillvm.ast.Ast.*;

//Statements, visitor like here is fine.
//expressions, separate class MJExpr.Matcher<Operand> to pass back an operand when evaluating something


public class Translator extends MJElement.DefaultVisitor  {

	private final MJProgram javaProg;

	Operand operand;

	//stores which Proc and Block we are currently in.
	Proc curProc;
	BasicBlock curBlock;


	public Translator(MJProgram javaProg) {
		this.javaProg = javaProg;
	}

	public Prog translate()
    {
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

        curBlock.add(ReturnExpr(ConstInt(0)))	;

        return prog;
	}



	@Override public void visit(MJStmtPrint stmtPrint)
	{
		//constant or variable
		MJExpr expr = stmtPrint.getPrinted();
		Operand operand;
		ExprTranslatorMatcher trans = new ExprTranslatorMatcher();

		//check which operand type it is
		operand = expr.match(trans);
		Print print = Print(operand);
		this.curBlock.add(print);
	}


}
