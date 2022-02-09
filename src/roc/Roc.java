package roc;

import roc.interpreter.Interpreter;
import roc.interpreter.RuntimeError;
import roc.lexer.Scanner;
import roc.lexer.Token;
import roc.lexer.TokenType;
import roc.parser.Expr;
import roc.parser.Parser;
import roc.parser.Stmt;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Roc {

    private static final Interpreter interpreter = new Interpreter();
    private static boolean hadError = false;
    static boolean hadRuntimeError = false;

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

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);

        interpreter.interpret(statements);
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " la final", message);
        } else {
            report(token.line, " la '" + token.lexeme + "'", message);
        }
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println("[linia " + error.token.line + "] " + error.getMessage());
        hadRuntimeError = true;
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[linia " + line + "] Eroare" + where + ": " + message);
        hadError = true;
    }
}

