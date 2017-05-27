package analysis;

import frontend.SourcePosition;
import minijava.ast.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static minijava.ast.MJ.TypeClass;

/**
 * Created by alka on 5/27/2017.
 */
public class typechecker {

    private LinkedHashMap<Object, Object> hashMap, varclass, varmethod;

    public List<TypeError> errors = new ArrayList<>();

    public List<TypeError> getErrors() {
        return errors;
    }

    public typechecker(LinkedHashMap varmethod, LinkedHashMap varclass, LinkedHashMap hashMap) {
        this.varmethod = varmethod;
        this.varclass = varclass;
        this.hashMap = hashMap;
    }

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

    //Finding out whether the variable is declared and its type
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

    void CheckStmtassg(MJStmtAssign stmtAssign) {

        if (stmtAssign.getLeft() instanceof MJVarUse) {
            MJVarUse left = (MJVarUse) (stmtAssign.getLeft());
            MJType type = CheckType(left);
            CheckExpr_ofassf(type, stmtAssign.getRight());
        }
    }

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
                    check_Expruniary((MJExprUnary) stmtAssign);
                } else if (!(stmtAssign instanceof MJBoolConst))
                    this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type boolean"));
            } else if (type instanceof MJExprBinary) {
                if (!(stmtAssign instanceof MJVarUse))
                    this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of Expr Binary"));
            } else if (type instanceof MJTypeIntArray) {
                if (!(stmtAssign instanceof MJNewIntArray))
                    this.errors.add(new TypeError(stmtAssign, "Variable assignment should be be Array Int"));
            }
        }
    }

    Boolean check_exprbinary(MJExprBinary exprBinary) {
        System.out.println(exprBinary.getLeft());
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
        } else
            return false;

    }

    Boolean check_Expruniary(MJExprUnary exprUnary) {
        Boolean value = null;
        if (exprUnary.getUnaryOperator() instanceof MJUnaryMinus) {
            if (exprUnary.getExpr() instanceof MJNumber) {
                value = true;
            } else
                value = false;
        } else if (exprUnary.getUnaryOperator() instanceof MJNegate) {
            if (exprUnary.getExpr() instanceof MJBoolConst)
                value = true;
            else
                value = false;
        }
        return value;
    }

    //check if subtyping

    void CheckReturn(MJStmtReturn stmtReturn, MJMethodDecl methodDecl) {
        //return type in signature.
        MJType returnType = methodDecl.getReturnType();
        System.out.println(returnType);

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
