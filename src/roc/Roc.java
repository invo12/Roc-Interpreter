package roc;

import roc.lexer.Scanner;
import roc.lexer.Token;
import roc.parser.Parser;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Roc {

    private static boolean hadError = false;

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println("Utilizare: 'roc <nume_script>'");
            System.exit(64);
        } else {
            readFile(args[0]);
        }
    }

    private static void readFile(String fileName) throws Exception {

        byte[] bytes = Files.readAllBytes(Paths.get(fileName));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void run(String source) {

        Scanner scanner = new Scanner(source);

        List<Token> tokens = scanner.scanTokens();

        if (hadError) System.exit(65);

        for (Token token : tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        parser.parse();
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[linia " + line + "] Eroare" + where + ": " + message);
        hadError = true;
    }
}

