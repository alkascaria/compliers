package frontend;


import minijava.ast.*;

import analysis.*;

import java.io.FileReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        String fileName;
        if (args.length > 0) {
            fileName = args[0];
        } else {
            System.out.println("Enter a filename: ");
            fileName = new Scanner(System.in).nextLine();
        }

        try (FileReader r = new FileReader(fileName)) {
            MJFrontend frontend = new MJFrontend();
            //parse phase
            MJProgram prog = frontend.parse(r);
            System.out.println(prog);
            //analysis phase: name + type checking.
            //pass the parsed program to the analysis phase
            Analysis analysis = new Analysis(prog);
            analysis.check();

            frontend.getSyntaxErrors().forEach(System.out::println);
            analysis.getTypeErrors().forEach(System.out::println);
        }
    }
}
