package calculator;

import org.junit.Assert;
import org.junit.Test;

/**
 * This tests the basic version of the
 * It tests some basic expressions without lambdas or lets.
 *
 * To run this test you have to create a class named explang.interpret.Interpreter
 * This class should have a static method named "run" which takes a String,
 * parses and evaluates it and returns the result of the evaluation as an int.
 *
 * */
public class InterpreterTest {

    @Test
    public void testArith1() {
        String expr = "5*3+4";
        int v = Main.run(expr);
        //Expected 19
        Assert.assertEquals(19, v);
    }

    @Test
    public void testArith2() {
        String expr = "(5*3) + (60 / (15 - 5))";
        int v = Main.run(expr);
        //Expected 21
        Assert.assertEquals(21, v);
    }

    @Test
    public void testArith3() {
        String expr = "2*3+4*5";
        int v = Main.run(expr);
        //Expected 26
        Assert.assertEquals(26, v);
    }

    @Test
    public void testArith4() {
        String expr = "3+4*1-2";
        int v = Main.run(expr);
        //Expected 5
        Assert.assertEquals(5, v);
        
    }
    
    @Test
    public void testArith5() {
        String expr = "-9/6*4+16/9+3*(18-5+(4-2*8))";
        int v = Main.run(expr);
        Assert.assertEquals(0, v);

    }
    
    @Test
    public void testArith6() {
        String expr = "-6-5-7/5";
        int v = Main.run(expr);
        //Expected -12
        Assert.assertEquals(-12, v);
        
    }
    @Test
    public void testArith7() {
        String expr = "-7+9/4+3-1";
        int v = Main.run(expr);
        //Expected -3
        Assert.assertEquals(-3, v);
        
    }

    @Test
    public void testArith8() {
        String expr = "2+3*4/5";
        int v = Main.run(expr);
        Assert.assertEquals(4, v);
    }

    @Test
    public void testArith9() {
        String expr = "20/5*30/6";
        int v = Main.run(expr);
        Assert.assertEquals(20, v);
    }

    @Test
    public void testArith10() {
        String expr = "-4+-2";
        int v = Main.run(expr);
        Assert.assertEquals(-6, v);
    }

    @Test
    public void testArith11() {
        String expr = "-4--2";
        int v = Main.run(expr);
        Assert.assertEquals(-2, v);
    }

    @Test
    public void testArith12() {
        String expr = "-2--4";
        int v = Main.run(expr);
        Assert.assertEquals(2, v);
    }

    @Test
    public void testArith13() {
        String expr = "-3/-6";
        int v = Main.run(expr);
        Assert.assertEquals(0, v);
    }

    @Test
    public void testArith14() {
        String expr = "-3";
        int v = Main.run(expr);
        Assert.assertEquals(-3, v);
    }

    @Test
    public void testArith15() {
        String expr = "7/8*5+10-40*6+300";
        int v = Main.run(expr);
        Assert.assertEquals(70, v);
    }

    @Test
    public void testArith16() {
        String expr = "10/-5*-4-8--10";
        int v = Main.run(expr);
        Assert.assertEquals(10, v);
    }

    @Test
    public void testArith17() {
        String expr = "3+4*1-2";
        int v = Main.run(expr);
        Assert.assertEquals(5, v);

    }

    @Test
    public void testArith18() {
        String expr = "3+4*1-2";
        int v = Main.run(expr);
        Assert.assertEquals(5, v);

    }

    @Test
    public void testArith19() {
        String expr = "-6-5-7/5";
        int v = Main.run(expr);
        Assert.assertEquals(-12, v);

    }

    @Test
    public void testArith20() {
        String expr = "-7+9/4+3-1";
        int v = Main.run(expr);
        Assert.assertEquals(-3, v);
    }

    @Test
    public void paren_expr() {
        String expr = "(((((5)))))";
        int v = Main.run(expr);
        Assert.assertEquals(5, v);

    }

    @Test
    public void paren_expr1() {
        String expr = "(((((-10)))))";
        int v = Main.run(expr);
        Assert.assertEquals(-10, v);
    }


    @Test
    public void paren_expr2() {
        String expr = "(((((5)))))";
        int v = Main.run(expr);
        //Expected 5
        Assert.assertEquals(5, v);

    }
}
