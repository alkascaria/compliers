package analysis;

import minijava.ast.*;

import java.util.Stack;

/**
 * Created by alka on 5/28/2017.
 */
public class Handlenew {

    MJProgram program;

    Stack name = new Stack();   //stack for storing the class declrations and method declrations

    public Handlenew(MJProgram program) {
        this.program = program;
        parse_program(program);
    }

    void stack() {
        MJClassDeclList classDeclList = program.getClassDecls();    //all the class declration lists
        MJMethodDeclList MethodDeclList;
        MJVarDeclList varDeclList;
        MJClassDecl classDecl;
        MJMethodDecl methodDecl;
        MJVarDecl varDecl;
        for (int i = 0; i < classDeclList.size(); i++) {    //as one classdec at a time
            classDecl = classDeclList.get(i);
            name.push(classDecl.getName()); //class name
            varDeclList = classDecl.getFields();    //get all the variable decl.
            for (int k = 0; k < varDeclList.size(); k++) {
                varDecl = varDeclList.get(k);
                name.push(varDecl.getName());
            }
            MethodDeclList = classDecl.getMethods();
            for (int j = 0; j < MethodDeclList.size(); j++) {
                methodDecl = MethodDeclList.get(i);
                name.push(methodDecl.getName());
            }
        }
    }

    void parse_program(MJProgram program) {
        parse_Block(program.getMainClass().getMainBody());
    }

    void parse_Block(MJBlock method_body) {
        for (MJStatement statement : method_body) {
        }
    }
}
