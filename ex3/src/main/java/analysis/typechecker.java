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

    void TCheck(MJStmtAssign stmtAssign) {

        if (stmtAssign.getLeft() instanceof MJVarUse) {
            MJType type = null;
            MJVarUse left = (MJVarUse) (stmtAssign.getLeft());
            if (varmethod.containsKey(left.getVarName()))
                type = (MJType) varmethod.get(left.getVarName());
            else if (varclass.containsKey(left.getVarName()))
                type = (MJType) varclass.get(left.getVarName());
            else if (hashMap.containsKey(left.getVarName()))
                type = (MJType) hashMap.get(left.getVarName());
            else
                this.errors.add(new TypeError(stmtAssign, "Variable is not declared"));
            TypeCheck(type, stmtAssign.getRight());

        }
    }

    void TypeCheck(MJType type, MJExpr stmtAssign) {
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
