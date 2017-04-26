package calculator.ast;

/**
 * Created by Daniele on 26/04/2017.
 */
public class ExprPrinter implements ExprVisitor {


    @Override
    public String visitAdd(ExprAdd exprAdd)
    {
        String stringToReturn ="";
        //positive = 0. negative = 1

        //positive
        if(exprAdd.getSign() == 0)
        {
            stringToReturn =  "(" + exprAdd.getLeft().accept(this) + "+" + exprAdd.getRight().accept(this) + ")";
        }
        //negative
        else if(exprAdd.getSign() == 1)
        {
            stringToReturn =  "-" + "(" + exprAdd.getLeft().accept(this) + "+" + exprAdd.getRight().accept(this) + ")";
        }

        return (stringToReturn);
    }

    @Override
    public String visitNumber(ExprNumber number)
    {
        String stringToReturn = Integer.toString(number.getValue());
        return (stringToReturn);
    }

    public String visitSub(ExprSub exprSub)
    {
        String stringToReturn = "";

        //positive
        if(exprSub.getSign() == 0)
        {
            stringToReturn =  "(" + exprSub.getLeft().accept(this) + "-" + exprSub.getRight().accept(this) + ")";
        }
        //negative
        else if(exprSub.getSign() == 1)
        {
            stringToReturn =  "-" + "(" + exprSub.getLeft().accept(this) + "-" + exprSub.getRight().accept(this) + ")";
        }

        return (stringToReturn);
    }

    public String visitDiv(ExprDiv exprDiv)
    {
        String stringToReturn = "";

        //positive
        if(exprDiv.getSign() == 0)
        {
            stringToReturn =  "(" + exprDiv.getLeft().accept(this) + "/" + exprDiv.getRight().accept(this) + ")";
        }
        //negative
        else if(exprDiv.getSign() == 1)
        {
            stringToReturn =  "-" + "(" + exprDiv.getLeft().accept(this) + "/" + exprDiv.getRight().accept(this) + ")";
        }

        return (stringToReturn);
    }

    public String visitMult(ExprMult exprMult)
    {
        String stringToReturn = "";

        //positive
        if(exprMult.getSign() == 0)
        {
            stringToReturn =  "(" + exprMult.getLeft().accept(this) + "*" + exprMult.getRight().accept(this) + ")";
        }
        //negative
        else if(exprMult.getSign() == 1)
        {
            stringToReturn =  "-" + "(" + exprMult.getLeft().accept(this) + "*" + exprMult.getRight().accept(this) + ")";
        }
        return (stringToReturn);
    }

}
