package analysis;

import frontend.SourcePosition;
import minijava.ast.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static minijava.ast.MJ.TypeClass;
import static minijava.ast.MJ.TypeInt;

/**
 * Type Checker
 * Created by alka on 5/27/2017.
 */
public class typechecker {

    private LinkedHashMap<Object, Object> hash_main, hash_method, hash_class;

    /**
     * Gets Error
     *
     * @return errors(@code errors)
     */
    public List<TypeError> errors = new ArrayList<>();

    public List<TypeError> getErrors() {
        return errors;
    }

    /**
     * Initializes variables
     *
     * @param hash_method(@code LinkedHashMap)
     * @param hash_class(@code LinkedHashMap)
     * @param hash_main(@code LinkedHashMap)
     */
    public typechecker(LinkedHashMap hash_method, LinkedHashMap hash_class, LinkedHashMap hash_main) {
        this.hash_method = hash_method;
        this.hash_class = hash_class;
        this.hash_main = hash_main;
    }

    /**
     * Checks if condition in while is of type boolean
     *
     * @param stmtWhile(@code MJStmtWhile)
     */
    void checkwhile(MJStmtWhile stmtWhile) {
        //checking whether the condition of while is of type boolean
        if ((stmtWhile.getCondition() instanceof MJTypeBool)) {
        } else if ((stmtWhile.getCondition() instanceof MJBoolConst)) {
        } else if (stmtWhile.getCondition() instanceof MJExprBinary) {
            if (((MJExprBinary) stmtWhile.getCondition()).getOperator() instanceof MJLess) {
            }
            if (((MJExprBinary) stmtWhile.getCondition()).getOperator() instanceof MJAnd) {
            }
            if (((MJExprBinary) stmtWhile.getCondition()).getOperator() instanceof MJEquals) {
            }
        } else
            this.errors.add(new TypeError(stmtWhile, "The condition of while should be of type boolean"));
    }

    /**
     * Checks if condition in if is of type boolean
     *
     * @param stmtIf(@code MJStmtIf)
     */
    void checkif(MJStmtIf stmtIf) {
        //checking whether condition of if is of type boolean
        if ((stmtIf.getCondition() instanceof MJTypeBool)) {
        } else if ((stmtIf.getCondition() instanceof MJBoolConst)) {
        } else if (stmtIf.getCondition() instanceof MJExprBinary) {
            if (((MJExprBinary) stmtIf.getCondition()).getOperator() instanceof MJLess) {
            }
            if (((MJExprBinary) stmtIf.getCondition()).getOperator() instanceof MJAnd) {
            }
            if (((MJExprBinary) stmtIf.getCondition()).getOperator() instanceof MJEquals) {
            }
        } else if (stmtIf.getCondition() instanceof MJFieldAccess) {
        } else
            this.errors.add(new TypeError(stmtIf, "The condition of if should be of type boolean"));

    }

    /**
     * Checks if MJStmtPrint prints only integer
     *
     * @param stmtPrint(@code MJStmtPrint)
     */
    void CheckSOP(MJStmtPrint stmtPrint) {
        MJType type = null;
        if (stmtPrint.getPrinted() instanceof MJVarUse) {//checking whether its of varuse

            MJVarUse variable = (MJVarUse) stmtPrint.getPrinted();
            type = CheckType(variable);     //getting the type of variable
            if (!(type instanceof MJTypeInt)) {     //SOP only allows int... so check for that
                this.errors.add(new TypeError(stmtPrint, "System.out.println can only print an integer"));
            }
        } else if (stmtPrint.getPrinted() instanceof MJExprBinary) {
            Boolean value = check_exprbinary((MJExprBinary) stmtPrint.getPrinted());
            System.out.println(value);
            System.out.println("Hi" + ((MJExprBinary) stmtPrint.getPrinted()).getLeft());
        } else if (stmtPrint.getPrinted() instanceof MJArrayLength) {

        } else if ((stmtPrint.getPrinted() instanceof MJNumber)) {
        } else if (stmtPrint.getPrinted() instanceof MJMethodCall) {
        }
        //SOP can have a constant number
        else
            this.errors.add(new TypeError(stmtPrint, "System.out.println can only print an integer"));
    }


    /**
     * Finding out whether the variable is declared and its type
     *
     * @param variable(@code MJVarUse)
     * @return (@code type)
     */
    MJType CheckType(MJVarUse variable) {
        MJType type = null;
        if (hash_method.containsKey(variable.getVarName())) // looking in local method
            type = (MJType) hash_method.get(variable.getVarName()); // getting its type
        else if (hash_class.containsKey(variable.getVarName())) // looking in local class
            type = (MJType) hash_class.get(variable.getVarName()); // getting its type
        else if (hash_main.containsKey(variable.getVarName())) // looking in global class
            type = (MJType) hash_main.get(variable.getVarName()); // getting its type
        else
            this.errors.add(new TypeError(variable, "Variable is not declared"));
        return type;
    }

    /**
     * Checks Statement assignment
     *
     * @param stmtAssign(@code MJStmtAssign)
     */
    void CheckStmtassg(MJStmtAssign stmtAssign) {
        MJType type = null;
        if (stmtAssign.getLeft() instanceof MJVarUse) {
            MJVarUse left = (MJVarUse) (stmtAssign.getLeft());
            type = CheckType(left);
            if (stmtAssign.getRight() instanceof MJMethodCall) {
                if (((MJMethodCall) stmtAssign.getRight()).getReceiver() instanceof MJExprThis) {
                    if (hash_class.containsKey(((MJMethodCall) stmtAssign.getRight()).getMethodName())) {
                    }
                }
            }
            CheckExpr_ofassf(type, stmtAssign.getRight());
        } else if (stmtAssign.getLeft() instanceof MJArrayLookup) {
            if (stmtAssign.getRight() instanceof MJVarUse) {
                MJVarUse right = (MJVarUse) (stmtAssign.getRight());
                type = CheckType(right);
            }
            CheckExpr_ofassf(type, stmtAssign.getLeft());
        }
    }


    /**
     * Check Expression
     *
     * @param type(@code MJType)
     * @param stmtAssign(@code MJExpr)
     */
    void CheckExpr_ofassf(MJType type, MJExpr stmtAssign) {
        if (type instanceof MJType) {
            if (type instanceof MJTypeInt) {
                if (stmtAssign instanceof MJExprBinary) {
                    if (!(check_exprbinary((MJExprBinary) stmtAssign)))
                        this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type int"));
                } else if (stmtAssign instanceof MJExprUnary) {
                    check_Expruniary((MJExprUnary) stmtAssign, type);
                } else if (!(stmtAssign instanceof MJNumber))
                    this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type int"));
            } else if (type instanceof MJTypeBool) {
                if (stmtAssign instanceof MJExprBinary) {
                    if (!(check_exprbinary((MJExprBinary) stmtAssign)))
                        this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type int"));
                } else if (stmtAssign instanceof MJExprUnary) {
                    if (!(check_Expruniary((MJExprUnary) stmtAssign, type)))
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

    Boolean Typecheck(MJType type1, MJType type2) {
        if ((type1 instanceof MJNumber) && (type2 instanceof MJNumber))
            return true;
        else if ((type1 instanceof MJTypeInt) && (type2 instanceof MJTypeInt))
            return true;
        else if ((type1 instanceof MJTypeBool) && (type2 instanceof MJTypeBool))
            return true;
        else if ((type1 instanceof MJTypeInt) && (type2 instanceof MJNumber))
            return true;
        else if ((type1 instanceof MJNumber) && (type2 instanceof MJTypeInt))
            return true;
        else if ((type1 instanceof MJTypeBool) && (type2 instanceof MJBoolConst))
            return true;
        else if ((type1 instanceof MJBoolConst) && (type2 instanceof MJTypeBool))
            return true;
        else
            return false;
    }

    /**
     * Checks Expression Binary
     *
     * @param exprBinary(@code MJExprBinary)
     * @return (@code (exprBinary.getLeft() instanceof MJNumber) && ((exprBinary.getRight()) instanceof MJNumber))
     * || @code  (exprBinary.getLeft() instanceof MJTypeInt) && ((exprBinary.getRight()) instanceof MJTypeInt)
     * || @code (exprBinary.getLeft() instanceof MJTypeBool) && ((exprBinary.getRight()) instanceof MJTypeBool)
     * || @code (exprBinary.getLeft() instanceof MJVarUse) && ((exprBinary.getRight()) instanceof MJVarUse)? @true:@false
     */
    Boolean check_exprbinary(MJExprBinary exprBinary) {
        MJType type1 = null, type2 = null;
        System.out.println(exprBinary.getLeft());
        System.out.println(exprBinary.getRight());
        if (exprBinary.getLeft() instanceof MJVarUse) {

            type1 = CheckType((MJVarUse) exprBinary.getLeft());
            if ((exprBinary.getRight()) instanceof MJVarUse) {
                type2 = CheckType((MJVarUse) exprBinary.getRight());
            } else if (exprBinary.getRight() instanceof MJType) {
                System.out.println("Hi");
                System.out.println((MJType) exprBinary.getRight());
                type2 = (MJType) exprBinary.getRight();
            }
            System.out.println(type2);
            return (Typecheck(type1, type2));
        } else if ((exprBinary.getRight() instanceof MJType) && (exprBinary.getLeft() instanceof MJType)) {
            return (Typecheck((MJType) exprBinary.getLeft(), (MJType) exprBinary.getRight()));
        } else if ((exprBinary.getLeft() instanceof MJNumber) && (exprBinary.getRight() instanceof MJNumber))
            return true;
        else if ((type1 instanceof MJTypeInt) && (type2 instanceof MJNumber))
            return true;
        else if ((type1 instanceof MJNumber) && (type2 instanceof MJTypeInt))
            return true;
        else if ((type1 instanceof MJTypeBool) && (type2 instanceof MJBoolConst))
            return true;
        else if ((type1 instanceof MJBoolConst) && (type2 instanceof MJTypeBool))
            return true;
        else
            return false;
    }

    /**
     * Checking unarray operators
     *
     * @param exprUnary(@code MJExprUnary)
     * @return value(@code Value)
     */
    Boolean check_Expruniary(MJExprUnary exprUnary, MJType type1) {
        Boolean value = null;
        if (type1 instanceof MJTypeInt || type1 instanceof MJNumber) {
            if (exprUnary.getUnaryOperator() instanceof MJUnaryMinus) {

                if (exprUnary.getExpr() instanceof MJNumber) {
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
            } else
                value = false;
        } else if (type1 instanceof MJTypeBool || type1 instanceof MJBoolConst) {
            if (exprUnary.getUnaryOperator() instanceof MJNegate) {
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
            } else
                value = false;
        } else
            value = false;
        return value;
    }

    /**
     * check if subtyping
     *
     * @param stmtReturn(@code MJStmtReturn)
     * @param methodDecl(@code MJMethodDecl)
     * @param mainArgs(@code String)
     */

    void CheckReturn(MJStmtReturn stmtReturn, MJMethodDecl methodDecl, String mainArgs) {

        //if in main method and found a return statement...
        //found a return statement in the main
        if ((stmtReturn instanceof MJStmtReturn)) {

            if (mainArgs.length() > 0)
                this.errors.add(new TypeError(stmtReturn, "Return statements are not allowed in the main method."));
        }
    }

    void checkreturn_methods(MJStmtReturn stmtReturn, MJMethodDecl methodDecl) {
        if ((stmtReturn instanceof MJStmtReturn)) {

            //return type in signature.
            MJType returnType = methodDecl.getReturnType();
            if (stmtReturn.getResult() instanceof MJNewObject) {
                MJNewObject newObj = (MJNewObject) stmtReturn.getResult();
                MJType typeObj = TypeClass(newObj.getClassName());
                boolean validSubType = StaticMethods.isSubTypeOff(returnType, typeObj);
                if (validSubType == false) {
                    this.errors.add(new TypeError(newObj, "Return statement is not a subtype of method's return type "));
                }
                //check if returnType doesn't extend newObj
            } else if (stmtReturn.getResult() instanceof MJVarUse) {
                MJType type = CheckType((MJVarUse) stmtReturn.getResult());
                if (!(returnType.toString().contentEquals(type.toString()))) {
                    this.errors.add(new TypeError(stmtReturn, "Return statement is not a subtype of method's return type "));
                }
                System.out.println("Hi" + type);
            }
        }
    }
}

