package roc.parser;

import roc.Roc;
import roc.lexer.Token;
import roc.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static roc.lexer.TokenType.*;

public class Parser {

    private static class ParseError extends RuntimeException {
    }

    private List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {

        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration() {

        try {
            if (match(VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {

        Token name = consume(IDENTIFIER, "Trebuie nume de variabila");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Trebuie ';' la finalul declaratiei de variabile");
        return new Stmt.Var(name, initializer);
    }

    private Stmt statement() {

        if (match(AFISEAZA)) return printStatement();
        else if (match(LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
    }

    private List<Stmt> block() {

        List<Stmt> statements = new ArrayList<>();

        while(!check(RIGHT_BRACE) && !isAtEnd()){
            statements.add(declaration());
        }

        consume(RIGHT_BRACE,"Trebuie '}' la finalul blocului");
        return statements;
    }

    private Stmt printStatement() {

        Expr expr = expression();
        consume(SEMICOLON, "Trebuie ';' dupa expresie");
        return new Stmt.Print(expr);
    }

    private Stmt expressionStatement() {

        Expr expr = expression();
        consume(SEMICOLON, "Trebuie ';' dupa expresie");
        return new Stmt.Expression(expr);
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {

        Expr expr = equality();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Nu pot atribui valoare acestei entitati");
        }

        return expr;
    }

    private Expr equality() {

        Expr expr = comparison();

        while (match(EXMARK_EQUALS, EQUALS_EQUALS)) {
            Token operator = previous();
            Expr right = comparison();

            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {

        Expr expr = term();

        while (match(GREATER, GREATER_EQUALS, LESS, LESS_EQUALS)) {
            Token operator = previous();
            Expr right = term();

            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {

        Expr expr = factor();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = factor();

            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {

        Expr expr = unary();

        while (match(SLASH, STAR, MODULO)) {
            Token operator = previous();
            Expr right = unary();

            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(EXMARK, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {

        if (match(ADEVARAT)) return new Expr.Literal(true);
        if (match(FALS)) return new Expr.Literal(false);
        if (match(NUL)) return new Expr.Literal(null);

        if (match(DOUBLE) || match(STRING)) return new Expr.Literal(previous().literal);

        if (match(IDENTIFIER)) return new Expr.Variable(previous());

        if (match(LEFT_ROUND)) {
            Expr expression = expression();
            consume(RIGHT_ROUND, "Trebuie ')' dupa expresie");
            return new Expr.Grouping(expression);
        }

        throw error(peek(), "Trebuie expresie.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {

        if (isAtEnd()) return false;
        return tokens.get(current).type == type;
    }

    private boolean isAtEnd() {

        return peek().type == EOF;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token consume(TokenType type, String message) {

        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Roc.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASA:
                case FUN:
                case VAR:
                case PENTRU:
                case DACA:
                case CATTIMP:
                case ALTFEL:
                case RETURNEAZA:
                    return;
            }

            advance();
        }
    }
}
