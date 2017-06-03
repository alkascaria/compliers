package translation;


import minijava.ast.*;

/**
 * Translation of statements and expressions
 * Created by praveengopal on 01/06/2017.
 */
public class StatementChecker implements MJStatement.MatcherVoid{

    private final Translator translator;

    public StatementChecker(Translator translator) {
        this.translator = translator;
    }

    //Statement Expression
    public void case_StmtExpr(MJStmtExpr stmtExpr) {
    //Todo StmtExpr

    }

    //Statement While
    public void case_StmtWhile(MJStmtWhile stmtWhile){
        //Todo StmtWhile
    }

    //Statement Return
    public void case_StmtReturn(MJStmtReturn stmtReturn) {
      //Todo StmtReturn
    }

    //Statment If
    public void case_StmtIf(MJStmtIf stmtIf){
        //ToDo StmtIf
    }

    public void case_StmtPrint(MJStmtPrint stmtPrint) {
        //Todo StmtPrint
    }

    public void case_StmtAssign(MJStmtAssign stmtAssign) {
       //Todo Statement Assignement
    }

    public void case_VarDecl(MJVarDecl varDecl) {
        //Todo Variable
    }

    //block
    public void case_Block(MJBlock block) {
    //Todo Block
    }

    public void case_Number(MJNumber number){
    //Todo Number
    }

    public void case_ExprBinary(MJExprBinary exprExpr){
    //ToDo Expression Binary
    }

}