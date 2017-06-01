package frontend;

import minijava.ast.MJProgram;
import minillvm.ast.Prog;
import translation.TestOutputLLVM;
import translation.Translator;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
            MJProgram prog = frontend.parse(r);
            System.out.println(prog);
            Translator translator = new Translator(prog);
            Prog progTranslated = translator.translate();

            File inputFile = new File(fileName);
            String input = new String(Files.readAllBytes(inputFile.toPath()), StandardCharsets.UTF_8);
            TestOutputLLVM.testTranslation(inputFile.getName(), input);


            frontend.getSyntaxErrors().forEach(System.out::println);
        }
    }
}
