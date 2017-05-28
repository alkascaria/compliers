package analysis;

import frontend.SourcePosition;
import minijava.ast.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static minijava.ast.MJ.TypeClass;

/**
 * Type Checker
 * Created by alka on 5/27/2017.
 */
public class typechecker {

    private LinkedHashMap<Object, Object> hashMap, varclass, varmethod;
    /**
     * Gets Error
     * @return errors(@code errors)
     */
    public List<TypeError> errors = new ArrayList<>();

    public List<TypeError> getErrors() {
        return errors;
    }
    /**
     * Initializes variables
     * @param varmethod(@code LinkedHashMap)
     * @param varclass(@code LinkedHashMap)
     * @param hashMap(@code LinkedHashMap)
     */
    public typechecker(LinkedHashMap varmethod, LinkedHashMap varclass, LinkedHashMap hashMap) {
        this.varmethod = varmethod;
        this.varclass = varclass;
        this.hashMap = hashMap;
    }
    /**
     * Checks if condition in while is of type boolean
     * @param stmtWhile(@code MJStmtWhile)
     */
    void checkwhile(MJStmtWhile stmtWhile) {
        //checking whether the condition of while is of type boolean
        if ((stmtWhile.getCondition() instanceof MJTypeBool)) {
        } else if ((stmtWhile.getCondition() instanceof MJBoolConst)) {
        } else
            this.errors.add(new TypeError(stmtWhile, "The condition of while should be of type boolean"));
    }
    /**
     * Checks if condition in if is of type boolean
     * @param stmtIf(@code MJStmtIf)
     */
    void checkif(MJStmtIf stmtIf) {
        //checking whether condition of if is of type boolean
        if ((stmtIf.getCondition() instanceof MJTypeBool)) {
        } else if ((stmtIf.getCondition() instanceof MJBoolConst)) {
        } else
            this.errors.add(new TypeError(stmtIf, "The condition of if should be of type boolean"));

    }
    /**
     * Checks if MJStmtPrint prints only integer
     * @param stmtPrint(@code MJStmtPrint)
     */
    void CheckSOP(MJStmtPrint stmtPrint) {
        MJType type = null;
        if (stmtPrint.getPrinted() instanceof MJVarUse) {   //checking whether its of varuse
            MJVarUse variable = (MJVarUse) stmtPrint.getPrinted();
            type = CheckType(variable);     //getting the type of variable
            if (!(type instanceof MJTypeInt)) {     //SOP only allows int... so check for that
                this.errors.add(new TypeError(stmtPrint, "System.out.println can only print an integer"));
            }
        } else if (stmtPrint.getPrinted() instanceof MJArrayLength) {

        } else if (stmtPrint.getPrinted() instanceof MJDiv) {

        } else if (stmtPrint.getPrinted() instanceof MJUnaryMinus) {

        } else if (stmtPrint.getPrinted() instanceof MJExprBinary) {

        } else if (!(stmtPrint.getPrinted() instanceof MJNumber)) {
            //SOP can have a constant number
            this.errors.add(new TypeError(stmtPrint, "System.out.println can only print an integer"));
        }
    }
    /**
     * Finding out whether the variable is declared and its type
     * @param variable(@code MJVarUse)
     * @return (@code type)
     */
    
    MJType CheckType(MJVarUse variable) {
        MJType type = null;
        if (varmethod.containsKey(variable.getVarName())) // looking in local method
            type = (MJType) varmethod.get(variable.getVarName()); // getting its type
        else if (varclass.containsKey(variable.getVarName())) // looking in local class
            type = (MJType) varclass.get(variable.getVarName()); // getting its type
        else if (hashMap.containsKey(variable.getVarName())) // looking in global class
            type = (MJType) hashMap.get(variable.getVarName()); // getting its type
        else
            this.errors.add(new TypeError(variable, "Variable is not declared"));
        return type;
    }
    /**
     * Checks Statement assignment
     * @param stmtAssign(@code MJStmtAssign)
     */
    void CheckStmtassg(MJStmtAssign stmtAssign) {
        MJType type = null;
        if (stmtAssign.getLeft() instanceof MJVarUse) {
            MJVarUse left = (MJVarUse) (stmtAssign.getLeft());
            type = CheckType(left);
            CheckExpr_ofassf(type, stmtAssign.getRight());
        } else if (stmtAssign.getLeft() instanceof MJArrayLookup) {
            if (stmtAssign.getRight() instanceof MJVarUse) {
                MJVarUse right = (MJVarUse) (stmtAssign.getRight());
                type = CheckType(right);
            }
            System.out.println(type);
            CheckExpr_ofassf(type, stmtAssign.getLeft());
        }
    }
    /**
     * Check Expression
     * @param type(@code MJType)
     * @param stmtAssign(@code MJExpr)
     */
    void CheckExpr_ofassf(MJType type, MJExpr stmtAssign) {
        if (type instanceof MJType) {
            System.out.println(type);
            System.out.println(stmtAssign);
            if (type instanceof MJTypeInt) {
                if (stmtAssign instanceof MJExprBinary) {
                    if (!(check_exprbinary((MJExprBinary) stmtAssign)))
                        this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type int"));
                } else if (stmtAssign instanceof MJExprUnary) {
                    check_Expruniary((MJExprUnary) stmtAssign);
                } else if (!(stmtAssign instanceof MJNumber))
                    this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type int"));
            } else if (type instanceof MJTypeBool) {
                if (stmtAssign instanceof MJExprBinary) {
                    if (!(check_exprbinary((MJExprBinary) stmtAssign)))
                        this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type int"));
                } else if (stmtAssign instanceof MJExprUnary) {

                    if (!(check_Expruniary((MJExprUnary) stmtAssign)))
                        this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type boolean"));
                } else if (!(stmtAssign instanceof MJBoolConst))
                    this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type boolean"));
            } else if (type instanceof MJExprBinary) {
                if (!(stmtAssign instanceof MJVarUse))
                    this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of Expr Binary"));
            } else if (type instanceof MJTypeIntArray) {
                if ((stmtAssign instanceof MJNewIntArray)) {
                    if (((MJNewIntArray) stmtAssign).getArraySize() instanceof MJTypeInt) {
                    } else if (((MJNewIntArray) stmtAssign).getArraySize() instanceof MJNumber) {
                    } else
                        this.errors.add(new TypeError(stmtAssign, "Array size should be of int"));
                } else
                    this.errors.add(new TypeError(stmtAssign, "Variable assignment should be be Array Int"));
            }
        }

    }
    /**
     * Checks Expression Binary
     * @param exprBinary(@code MJExprBinary)
     * @return (@code (exprBinary.getLeft() instanceof MJNumber) && ((exprBinary.getRight()) instanceof MJNumber))
     * || @code  (exprBinary.getLeft() instanceof MJTypeInt) && ((exprBinary.getRight()) instanceof MJTypeInt)
     * || @code (exprBinary.getLeft() instanceof MJTypeBool) && ((exprBinary.getRight()) instanceof MJTypeBool)
     * || @code (exprBinary.getLeft() instanceof MJVarUse) && ((exprBinary.getRight()) instanceof MJVarUse)? @true:@false
     *
     *
     */
    Boolean check_exprbinary(MJExprBinary exprBinary) {
        if ((exprBinary.getLeft() instanceof MJNumber) && ((exprBinary.getRight()) instanceof MJNumber))
            return true;
        else if ((exprBinary.getLeft() instanceof MJTypeInt) && ((exprBinary.getRight()) instanceof MJTypeInt))
            return true;
        else if ((exprBinary.getLeft() instanceof MJTypeBool) && ((exprBinary.getRight()) instanceof MJTypeBool))
            return true;
        else if ((exprBinary.getLeft() instanceof MJVarUse) && ((exprBinary.getRight()) instanceof MJVarUse)) {
            if (CheckType((MJVarUse) exprBinary.getLeft()).equals(CheckType((MJVarUse) exprBinary.getRight())))
                return true;
            else
                return false;
        }
        else {
            return false;
        }

    }
    /**
     * Checking unarray operators
     * @param exprUnary(@code MJExprUnary)
     * @return value(@code Value)
     */
    Boolean check_Expruniary(MJExprUnary exprUnary) {
        Boolean value = null;

        if (exprUnary.getUnaryOperator() instanceof MJUnaryMinus) {

            if (exprUnary.getExpr() instanceof MJNumber) {
                System.out.println("hi");
                value = true;
            } else if ((exprUnary.getExpr() instanceof MJTypeInt))
                value = true;
            else if (exprUnary.getExpr() instanceof MJVarUse) {
                value = true;
                MJType type = CheckType((MJVarUse) exprUnary.getExpr());
                if (type instanceof MJTypeBool)
                    value = true;
                else if (type instanceof MJBoolConst)
                    value = true;
                else
                    value = false;
            } else
                value = false;
        } else if (exprUnary.getUnaryOperator() instanceof MJNegate) {
            if (exprUnary.getExpr() instanceof MJBoolConst) {
                value = true;
            } else if (exprUnary.getExpr() instanceof MJTypeInt) {
                value = true;
            } else if (exprUnary.getExpr() instanceof MJVarUse) {
                value = true;
                MJType type = CheckType((MJVarUse) exprUnary.getExpr());
                if (type instanceof MJTypeBool)
                    value = true;
                else if (type instanceof MJBoolConst)
                    value = true;
                else
                    value = false;
            } else
                value = false;
        }
        return value;
    }

    /**
     * check if subtyping
     * @param stmtReturn(@code MJStmtReturn)
     * @param methodDecl(@code MJMethodDecl)
     * @param mainArgs(@code String)
     */

    void CheckReturn(MJStmtReturn stmtReturn, MJMethodDecl methodDecl, String mainArgs)
    {

        //if in main method and found a return statement...
        //found a return statement in the main
         if(stmtReturn instanceof MJStmtReturn && mainArgs.length() > 0)
         {
            this.errors.add(new TypeError(stmtReturn, "Return statements are not allowed in the main method."));
         }

        //make sure it's not a an invalid signature method (as in main)
        if(methodDecl != null )
        {
            //return type in signature.
            MJType returnType = methodDecl.getReturnType();

            //check if the return variable is declared
            if(stmtReturn.getResult() instanceof MJVarUse)
            {
                MJVarUse varUse = (MJVarUse)stmtReturn.getResult();
                //check if declared.
                if(!(this.hashMap.containsKey(varUse)))
                {
                    this.errors.add(new TypeError(varUse, "Returning an undeclared variable is not allowed."));
                }
            }

            if (stmtReturn.getResult() instanceof MJNewObject) {
                MJNewObject newObj = (MJNewObject) stmtReturn.getResult();
                MJType typeObj = TypeClass(newObj.getClassName());
                boolean validSubType = StaticMethods.isSubTypeOff(returnType, typeObj);

                if (validSubType == false) {
                    this.errors.add(new TypeError(newObj, "Return statement is not a subtype of method's return type "));
                }
                //check if returnType doesn't extend newObj
            }
        }

    }
}
