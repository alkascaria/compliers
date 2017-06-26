package translation;

import minijava.ast.MJMethodDecl;
import minillvm.ast.*;

import java.util.HashMap;

/**
 * Created by Daniele on 26/06/2017.
 */
public class ClassData
{

    //list of all procedures stored in the current class, also inherited from parents
    ProcList procList;

    //list of all structField, also inherites from parents
    StructFieldList structFieldList ;

    //virtual table
    TypeStruct typeStructVirtualTable;




    public ClassData()
    {
        structFieldList = Ast.StructFieldList();
        this.procList = Ast.ProcList();

    }


    public void setVirtualTable(TypeStruct typeStruct)
    {
        this.typeStructVirtualTable = typeStruct;
    }

    public TypeStruct getVirtualTable()
    {
        return  this.typeStructVirtualTable;
    }

    public void setStructFieldList(StructFieldList structFieldsInput)
    {
        this.structFieldList = structFieldsInput;
    }

    public StructFieldList getStructFieldList()
    {
        return this.structFieldList;
    }

    public void setProcList(ProcList procListInput)
    {
        this.procList = procListInput;
    }

    public ProcList getProcList()
    {
        return this.procList;
    }
}
