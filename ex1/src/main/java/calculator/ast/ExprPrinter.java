package calculator.ast;

/**
 * Created by Daniele on 26/04/2017.
 * This ExprPrinter Class Pretty Prints and Evaluates the input.
 * 
 * Addition, Subtraction, Multiplication, Division  
 * 
 * For every arithmetic expression, according to mathematics precedence rule,
 * the input pretty prints and evaluation applied to given input 
 */

public class ExprPrinter implements ExprVisitor {
	
    /**
     * @see calculator.ast.ExprVisitor#visitAdd(calculator.ast.ExprAdd)
     * Performs addition and returns expression with Pretty Prints
     * @param exprAdd(@code ExprAdd)
     * @return stringToReturn {@code String} returns expression with Pretty Prints  
     */
    @Override
    public String visitAdd(ExprAdd exprAdd)
    {
        String stringToReturn ="";
        

        // 0 = positive
        if(exprAdd.getSign() == 0)
        {
            stringToReturn =  "(" + exprAdd.getLeft().accept(this) + " + " + exprAdd.getRight().accept(this) + ")";
        }
        //1 = negative
        else if(exprAdd.getSign() == 1)
        {
            stringToReturn =  "-" + "(" + exprAdd.getLeft().accept(this) + " + " + exprAdd.getRight().accept(this) + ")";
        }

        return (stringToReturn);
    }
    /**
     * Converts number to String 
     * @param  number(@code ExprNumber)
     * @return stringToReturn {@code String} returns number as String   
     */
    @Override
    public String visitNumber(ExprNumber number)
    {
        String stringToReturn = Integer.toString(number.getValue());
        return (stringToReturn);
    }

    
    /**
     * Performs subtraction and returns expression with Pretty Prints 
     * @param  exprSub(@code ExprSub)
     * @return stringToReturn {@code String} returns expression with Pretty Prints   
     */
    public String visitSub(ExprSub exprSub)
    {
        String stringToReturn = "";

        // 0 = Positive              
        if(exprSub.getSign() == 0)
        {
            stringToReturn =  "(" + exprSub.getLeft().accept(this) + " - " + exprSub.getRight().accept(this) + ")";
        }
        // 1  = negative 
        else if(exprSub.getSign() == 1)
        {
            stringToReturn =  "-" + "(" + exprSub.getLeft().accept(this) + " - " + exprSub.getRight().accept(this) + ")";
        }

        return (stringToReturn);
    }
    /**
     * Performs Division and returns expression with Pretty Prints 
     * @param  exprDiv(@code ExprDiv)
     * @return stringToReturn {@code String} returns expression with Pretty Prints   
     */
    public String visitDiv(ExprDiv exprDiv)
    {
        String stringToReturn = "";
        
        // 0  = Positive 
        if(exprDiv.getSign() == 0)
        {        	
            stringToReturn =  "(" + exprDiv.getLeft().accept(this) + " / " + exprDiv.getRight().accept(this) + ")";
        }
        
       // 1  = negative 
        else if(exprDiv.getSign() == 1)
        {
            stringToReturn =  "-" + "(" + exprDiv.getLeft().accept(this) + " / " + exprDiv.getRight().accept(this) + ")";
        }

        return (stringToReturn);
    }
    /**
     * Performs multiplication and returns expression with Pretty Prints 
     * @param  exprMult(@code ExprMult)
     * @return stringToReturn {@code String} returns expression with Pretty Prints   
     */
    public String visitMult(ExprMult exprMult)
    {
        String stringToReturn = "";

        //0 = Positive 
        if(exprMult.getSign() == 0)
        {
            stringToReturn =  "(" + exprMult.getLeft().accept(this) + " * " + exprMult.getRight().accept(this) + ")";
        }
        //1 = negative 
        else if(exprMult.getSign() == 1)
        {
            stringToReturn =  "-" + "(" + exprMult.getLeft().accept(this) + " * " + exprMult.getRight().accept(this) + ")";
        }
        return (stringToReturn);
    }
    /**
     * Performs Addition operation and returns result as integer  
     * @param  exprAdd(@code ExprAdd)
     * @return result {@code int} returns result as integer   
     */
    public int visitEvaluateAdd(ExprAdd exprAdd)
    {
        int result = 0;
        
        //check sign.  Result is positive when sign = 0 else negative
        if(exprAdd.getSign() == 0)
        {
            result = exprAdd.getLeft().acceptEvaluate(this) + exprAdd.getRight().acceptEvaluate(this);
        }        
        else
        {
            result = -1 * (exprAdd.getLeft().acceptEvaluate(this) + exprAdd.getRight().acceptEvaluate(this));
        }

        return result;
    }

    /**
     * Performs Subtraction operation and returns result as integer  
     * @param  exprSub(@code ExprSub)
     * @return result {@code int} returns result as integer   
     */
    public int visitEvaluateSub(ExprSub exprSub)
    {
        int result = 0;
       //check sign.  Result is positive when sign = 0 else negative
        if(exprSub.getSign() == 0)
        {
            result = exprSub.getLeft().acceptEvaluate(this) - exprSub.getRight().acceptEvaluate(this);
        }
        else
        {
            result = -1 * (exprSub.getLeft().acceptEvaluate(this) - exprSub.getRight().acceptEvaluate(this));
        }

        return result;
    }

    /**
     * Performs Multiplication operation and returns result as integer  
     * @param  exprMult(@code ExprSub)
     * @return result {@code int} returns result as integer   
     */
    public int visitEvaluateMult(ExprMult exprMult)
    {
        int result = 0;
       //check sign.  Result is positive when sign = 0 else negative
        if(exprMult.getSign() == 0)
        {
            result = exprMult.getLeft().acceptEvaluate(this) * exprMult.getRight().acceptEvaluate(this);
        }
        else
        {
            result = -1 * (exprMult.getLeft().acceptEvaluate(this) * exprMult.getRight().acceptEvaluate(this));
        }

        return result;
    }

    /**
     * Performs Division operation and returns result as integer  
     * @param  exprDiv(@code ExprSub)
     * @return result {@code int} returns result as integer   
     */

    public int visitEvaluateDiv(ExprDiv exprDiv) {
        int result;
        //positive
        if (exprDiv.getRight().acceptEvaluate(this) != 0) {
            if (exprDiv.getSign() == 0) {
                result = exprDiv.getLeft().acceptEvaluate(this) / exprDiv.getRight().acceptEvaluate(this);
            }
            //negative
            else {
                result = -1 * (exprDiv.getLeft().acceptEvaluate(this) / exprDiv.getRight().acceptEvaluate(this));
            }

        } else {
            System.out.println("Divide by zero gives you infinity, so the given value");
            result = Integer.MAX_VALUE;
        }
        return result;
    }
    /**
     * Returns ExprNumber as integer  
     * @param  number(@code ExprNumber)
     * @return the value {@code number.getValue()} returns number as integer   
     */
    public int visitEvaluateNumber(ExprNumber number)
    {

        return number.getValue();
    }

}
