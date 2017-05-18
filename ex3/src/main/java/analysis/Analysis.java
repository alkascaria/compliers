package analysis;

import minijava.ast.*;

import java.util.*;

public class Analysis {

    private final MJProgram prog;
    private List<TypeError> typeErrors = new ArrayList<>();

    public void addError(MJElement element, String message) {
        typeErrors.add(new TypeError(element, message));
    }

    public Analysis(MJProgram prog)
    {
        this.prog = prog;
    }



    public void check()
    {
        this.checkClassNamesDefined();
        //TODO implement type checking here as well!
    }


    public void checkClassNamesDefined()
    {
        MJClassDeclList classDeclList = this.prog.getClassDecls();

        //loop through all classes and check if they extend a valid class
        for(int i = 0; i < classDeclList.size(); i++ )
        {
            MJClassDecl classDecl = classDeclList.get(i);
            //get the class it extends, eventually
            //classDecl.getExtended();

        }

    }




    public List<TypeError> getTypeErrors() {
        return new ArrayList<>(typeErrors);
    }
}
