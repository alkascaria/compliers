package calculator;

import calculator.ast.Expr;
import exprs.ExprParser.ParserError;
import org.junit.Test;

import static calculator.Main.parseString;
import static calculator.Main.prettyPrint;
import static org.junit.Assert.assertEquals;

/**
 * This class tests the parser with some input strings.
 * Before you run this test you have to make the method Main.parseToAST public.
 **/
public class ParserTest {

    @Test
    public void testOk1() throws Exception {
        String input = "((5*3) + 4)";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("((5 * 3) + 4)", output);
    }

    @Test
    public void testOk2() throws Exception {
        String input = "2 + 3";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("(2 + 3)", output);
    }

    @Test
    public void testOk3() throws Exception {
        String input = "2 + 3 * 4";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("(2 + (3 * 4))", output);
    }

    @Test
    public void testOk4() throws Exception {
        String input = "2 * 3 + 4 * 5";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("((2 * 3) + (4 * 5))", output);
    }

    @Test
    public void testOk5() throws Exception {
        String input = "-5";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("(-5)", output);
    }

    @Test
    public void testOk6() throws Exception {
        String input = "2+3*4/5";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("(2 + ((3 * 4) / 5))", output);
    }

    @Test
    public void testOk7() throws Exception {
        String input = "20/5*30/6";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("(((20 / 5) * 30) / 6)", output);
    }

    @Test
    public void testOk8() throws Exception {
        String input = "-4+-2";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("(-4 + -2)", output);
    }

    @Test
    public void testOk9() throws Exception {
        String input = "-4--2";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("(-4 - -2)", output);
    }

    @Test
    public void testOk10() throws Exception {
        String input = "-2--4";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("(-2 - -4)", output);
    }

    @Test
    public void testOk11() throws Exception {
        String input = "10/-5*-4-8--10";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("((((10 / -5) * -4) - 8) - -10)", output);
    }

    @Test
    public void testOk12() throws Exception {
        String input = "7/8*5+10-40*6+300";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("(((((7 / 8) * 5) + 10) - (40 * 6)) + 300)", output);
    }

    @Test
    public void testOk13() throws Exception {
        String input = "-9/6*4+16/9+3*(18-5+(4-2*8))";
        Expr e = parseString(input);
        String output = prettyPrint(e);
        System.out.println(output);
        assertEquals("((((-9 / 6) * 4) + (16 / 9)) + (3 * ((18 - 5) + (4 - (2 * 8)))))", output);
    }

    @Test(expected = ParserError.class)
    public void testFail1() throws Exception {
        String input = "((5*3) + 4";
        Main.parseString(input);
    }

    @Test(expected = ParserError.class)
    public void testFail2() throws Exception {
        String input = "3+";
        Main.parseString(input);
    }

    @Test(expected = ParserError.class)
    public void testFail3() throws Exception {
        String input = ")(5*3)-9)";
        Main.parseString(input);
    }

    @Test(expected = ParserError.class)
    public void testFail4() throws Exception {
        String input = "((7)))";
        Main.parseString(input);
    }
}
