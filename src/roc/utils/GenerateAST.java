package roc.utils;

import roc.lexer.Token;
import roc.lexer.TokenType;
import roc.parser.AstPrinter;
import roc.parser.Expr;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {

    public static void main(String[] args) throws IOException {
        defineAst(".", "Expr", Arrays.asList(
                "Binary: Expr left, Token operator, Expr right",
                "Grouping: Expr expression",
                "Literal: Object value",
                "Unary: Token operator, Expr right"));
    }

    private static void defineAst(
            String outputDir, String baseName, List<String> types)
            throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package roc.parser;");
        writer.println();
        writer.println("import roc.lexer.Token;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("public abstract class " + baseName + " {");
        writer.println();

        defineVisitor(writer, baseName, types);

        for (String type : types) {
            generateClass(baseName, writer, type);
        }

        writer.println("\tpublic abstract <R> R accept(Visitor<R> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(
            PrintWriter writer, String baseName, List<String> types) {
        writer.println("\tinterface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("\t\tR visit" + typeName + baseName + "(" +
                    typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("\t}");
    }

    private static void generateClass(String baseName, PrintWriter writer, String type) {

        String[] values = type.split(": ");
        String className = values[0];
        String argumentList = values[1];
        writer.println("\tpublic static class " + className + " extends " + baseName + " {");
        writer.println();

        generateMembers(writer, className, argumentList);
        writer.println();
        generateConstructor(writer, className, argumentList);
        writer.println();
        generateVisitorMethod(baseName, writer, className);
        writer.println("\t}\n");
    }

    private static void generateVisitorMethod(String baseName, PrintWriter writer, String className) {

        writer.println("\t\t@Override");
        writer.println("\t\tpublic <R> R accept(Visitor<R> visitor) {");
        writer.println("\t\t\treturn visitor.visit" +
                className + baseName + "(this);");
        writer.println("\t\t}");
    }

    private static void generateMembers(PrintWriter writer, String className, String argumentList) {

        String[] arguments = argumentList.split(", ");
        for (String argument : arguments) {
            String argumentType = argument.split(" ")[0];
            String argumentName = argument.split(" ")[1];
            writer.println("\t\tpublic " + argumentType + " " + argumentName + ";");
        }
    }

    private static void generateConstructor(PrintWriter writer, String className, String argumentList) {

        writer.println("\t\tpublic " + className + "(" + argumentList + ") {");

        String[] arguments = argumentList.split(", ");
        for (String argument : arguments) {
            String argumentName = argument.split(" ")[1];
            writer.println("\t\t\tthis." + argumentName + " = " + argumentName + ";");
        }
        writer.println("\t\t}");
    }
}
