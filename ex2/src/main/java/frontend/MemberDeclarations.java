package frontend;

import minijava.ast.*;

/**
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

    public void addVarToVarList(MJVarDecl varDecl)
    {
        this.varDeclList.add(0, varDecl);
    }

    public void addMethodToMethodList(MJMethodDecl methodDecl)
    {
        this.methodDeclList.add(0, methodDecl);
    }

    public MJVarDeclList getVarDeclList()
    {
        return this.varDeclList;
    }

    public MJMethodDeclList getMethodDeclList()
    {
        return this.methodDeclList;
    }

    public void addMemberToCorrectList(MJMemberDecl memberDecl)
    {
        memberDecl.match(this);
    }

    @Override
    public void case_MethodDecl(MJMethodDecl methodDecl)
    {
        this.addMethodToMethodList(methodDecl);
    }

    @Override
    public void case_VarDecl(MJVarDecl varDecl)
    {
        this.addVarToVarList(varDecl);

    }
}
