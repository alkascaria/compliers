package frontend;

import minijava.ast.*;

/**
 *Adds memberDeclaration to Corresponding DeclLists
 * Created by Daniele on 10/05/2017.
 */
public class MemberDeclarations implements MJMemberDecl.MatcherVoid
{
    private MJVarDeclList varDeclList;
    private MJMethodDeclList methodDeclList;

    public MemberDeclarations()
    {
        varDeclList = MJ.VarDeclList();
        methodDeclList = MJ.MethodDeclList();
    }
    /** adds variable to varDecl List
     * @param  varDecl(@code MJVarDecl)
     */
    public void addVarToVarList(MJVarDecl varDecl)
    {
        this.varDeclList.add(0, varDecl);
    }
    /** adds method to method List
     * @param  methodDecl(@code MJMethodDecl)
     */
    public void addMethodToMethodList(MJMethodDecl methodDecl)
    {
        this.methodDeclList.add(0, methodDecl);
    }
    /**
     * Returns variable Declaration List
     * @return the value {@code varDeclList} returns variable Declaration List
     */
    public MJVarDeclList getVarDeclList()
    {
        return this.varDeclList;
    }
    /**
     * Returns method Declaration List
     * @return the value {@code methodDeclList} returns method Declaration List
     */
    public MJMethodDeclList getMethodDeclList()
    {
        return this.methodDeclList;
    }

    /**
     * Adds member to correctList
     * @param memberDecl(@code MJMemberDecl)
     */
    public void addMemberToCorrectList(MJMemberDecl memberDecl)
    {
        memberDecl.match(this);
    }

    /**
     *Adds methods to methodList
     * @param methodDecl(@code MJMemberDecl)
     *
     */
    @Override
    public void case_MethodDecl(MJMethodDecl methodDecl)
    {
        this.addMethodToMethodList(methodDecl);
    }
    /**
     * Add variable to variableList
     * @param varDecl(@code MJVarDecl)
     *
     */
    @Override
    public void case_VarDecl(MJVarDecl varDecl)
    {
        this.addVarToVarList(varDecl);

    }
}
