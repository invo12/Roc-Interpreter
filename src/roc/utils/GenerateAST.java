package roc.utils;

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
                "Unary: Token operator, Expr expression"));
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
        writer.println("abstract class " + baseName + " {");
        writer.println();

        for (String type : types) {
            generateClass(baseName, writer, type);
        }

        writer.println("}");
        writer.close();
    }

    private static void generateClass(String baseName, PrintWriter writer, String type) {

        String[] values = type.split(": ");
        String className = values[0];
        String argumentList = values[1];
        writer.println("\tstatic class " + className + " extends " + baseName + " {");
        writer.println();

        generateMembers(writer, className, argumentList);
        writer.println();
        generateConstructor(writer, className, argumentList);
        writer.println("\t}\n");
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
