package analysis;

import minijava.ast.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
        } else if (!(stmtPrint.getPrinted() instanceof MJNumber)) {     //SOP can have a constant number
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
            CheckExpr(type, stmtAssign.getRight());
        }
    }

    void CheckExpr(MJType type, MJExpr stmtAssign) {
        if (type instanceof MJType) {
            if (type instanceof MJTypeInt) {
                if (!(stmtAssign instanceof MJNumber))
                    this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type int"));
            } else if (type instanceof MJTypeBool) {
                if (!(stmtAssign instanceof MJBoolConst))
                    this.errors.add(new TypeError(stmtAssign, "Variable assignment should be of type boolean"));
            }
        }
    }
}
