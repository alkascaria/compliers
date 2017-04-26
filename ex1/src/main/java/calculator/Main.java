package calculator;

import calculator.ast.Expr;
import calculator.ast.ExprPrinter;
import calculator.ast.ExprVisitor;
import exprs.ExprParser;
import exprs.ExprParser.ParserError;
import exprs.Lexer;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;

import java.io.Reader;
import java.io.StringReader;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Write one expression per line:");
        Scanner s = new Scanner(System.in);
        while (s.hasNext()) {
            try
            {
                String input = s.nextLine();
                Expr expr = parseString(input);
                if (expr != null)
                {
                    System.out.println(prettyPrint(expr) + " = " + evaluate(expr));
                }
            }
            catch (ParserError e)
            {
                System.out.println(e.toString());
            }
        }

    }

    public static Expr parseString(String input) throws Exception {
        Reader in = new StringReader(input);


        return parse(in);
    }


    public static Expr parse(Reader in) throws Exception {
        ComplexSymbolFactory sf = new ComplexSymbolFactory();
        Lexer lexer = new Lexer(sf, in);
        ExprParser parser = new ExprParser(lexer, sf);

        parser.onError((ParserError e) ->
        {
            throw e;
        });


        Symbol result = parser.parse();

        return (Expr) result.value;
    }

    public static String prettyPrint(Expr e)
    {
        ExprPrinter exprPrinter = new ExprPrinter();

        String returnPrinter = e.accept(exprPrinter);


        //if it's a simple integer, that means it has no brackets around it --> add brackets around it.
        try
        {
            int parsedNumber = Integer.parseInt(returnPrinter);
            returnPrinter = "(" + parsedNumber + ")";
        }
        catch (NumberFormatException exception)
        {
            //do nothing, it's a normal expression then.
        }

      return returnPrinter;
    }

    public static int run(String s) {
        try {
            System.out.println(s);
            return evaluate(parseString(s));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int evaluate(Expr e)
    {
        ExprPrinter exprPrinter = new ExprPrinter();

        return e.acceptEvaluate(exprPrinter);
    }


}
