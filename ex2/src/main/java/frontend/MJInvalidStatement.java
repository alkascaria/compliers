package frontend;

import minijava.ast.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniele on 10/05/2017.
 */

//This is the visitor class
public class MJInvalidStatement extends MJElement.DefaultVisitor
{
    MJFrontend frontEndVar;

    public List<SyntaxError> syntaxErrorsFound = new ArrayList<>();

    public void acceptProgram(MJProgram program, MJFrontend frontend)
    {
        frontEndVar = frontend;
        //body of main
        //TODO: other classes.
        visit(program.getMainClass().getMainBody());

    }

    @Override public void visit(MJBlock block)
    {
        for (MJStatement i : block )
        {
           //check if the current i is an expression
            System.out.println(i.getClass().toString());
            if(i.getClass().toString().equals("class minijava.ast.MJStmtExprImpl"))
            {
                visit((MJStmtExpr)i);
            }
            else if(i.getClass().toString().equals("class minijava.ast.class minijava.ast.MJStmtAssignImpl"))
            {
                visit((MJStmtAssign)i);

            }
            else
            {
                i.accept(this);
            }
        }
    }

    @Override public void visit(MJStmtExpr stmtExpr)
    {

        if(stmtExpr.getExpr().getClass().toString().equals("class minijava.ast.MJArrayLookupImpl"))
        {
            System.out.println("Bad array found");
            SyntaxError syntaxError = new SyntaxError(stmtExpr.getExpr(), "Error: Two-Dimensional arrays are not supported in MiniJava.");
            this.syntaxErrorsFound.add(syntaxError);
        }
        else
        {
            stmtExpr.getExpr().accept(this);
        }
    }
//ExtendsNothing
    @Override public void visit(MJExtendsNothing mjExtendsNothing) {

        if (mjExtendsNothing.getParent().getClass().toString().equals("minijava.ast.MJExtendsClassImpl"))
        {
            System.out.println("Extends found");
            SyntaxError syntaxError = new SyntaxError(mjExtendsNothing.getParent(), "Error: Extends not supports in Minijava");

            this.syntaxErrorsFound.add(syntaxError);

        } else
        {
            mjExtendsNothing.getParent().accept(this);
        }
    }


    }

