package roc.parser;

import roc.Roc;
import roc.lexer.Token;
import roc.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
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
            if (match(FUN)) return function("functie");
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt function(String kind) {

        Token name = consume(IDENTIFIER, "Trebuie nume pentru " + kind);
        consume(LEFT_ROUND, "Trebuie '(' dupa nume " + kind);

        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_ROUND)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Nu poti avea mai mult de 255 de parametri");
                }
                parameters.add(consume(IDENTIFIER, "Trebuie ca parametrul sa aiba nume"));
            } while (match(COMMA));
        }
        consume(RIGHT_ROUND, "Trebuie ')' dupa parametri");

        consume(LEFT_BRACE, "Trebuie '{' inainte de corpul " + kind);
        List<Stmt> body = block();

        return new Stmt.Function(name, parameters, body);
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
        else if (match(DACA)) return ifStatement();
        else if (match(CATTIMP)) return whileStatement();
        else if (match(PENTRU)) return forStatement();
        else if (match(RETURNEAZA)) return returnStatement();
        return expressionStatement();
    }

    private Stmt returnStatement() {

        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }

        consume(SEMICOLON, "Trebuie ';' dupa valoarea returnata");
        return new Stmt.Return(keyword, value);
    }

    private Stmt forStatement() {

        consume(LEFT_ROUND, "Trebuie '(' dupa pentru");
        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }
        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "trebuie ';' dupa conditia din pentru");

        Expr increment = null;
        if (!check(RIGHT_ROUND)) {
            increment = expression();
        }
        consume(RIGHT_ROUND, "Trebuie ')' dupa expresiile din pentru");

        Stmt body = statement();

        if (increment != null) {
            body = new Stmt.Block(asList(body, new Stmt.Expression(increment)));
        }

        if (condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if (initializer != null) {
            body = new Stmt.Block(asList(initializer, body));
        }

        return body;
    }

    private Stmt whileStatement() {

        consume(LEFT_ROUND, "Trebuie '(' dupa cattimp");
        Expr condition = expression();
        consume(RIGHT_ROUND, "Trebuie ')' dupa conditia din cattimp");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt ifStatement() {

        consume(LEFT_ROUND, "Trebuie '(' dupa if");
        Expr condition = expression();
        consume(RIGHT_ROUND, "Trebuie ')' dupa expresia din daca");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ALTFEL)) {
            elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private List<Stmt> block() {

        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Trebuie '}' la finalul blocului");
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

        Expr expr = or();

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

    private Expr or() {

        Expr expr = and();

        while (match(OR)) {

            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {

        Expr expr = equality();

        while (match(AND)) {

            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
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

        return call();
    }

    private Expr call() {

        Expr expr = primary();

        while (true) {

            if (match(LEFT_ROUND)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr finishCall(Expr callee) {

        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_ROUND)) {
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "Functia nu poate primi mai mult de 255 de argumente.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }

        Token paren = consume(RIGHT_ROUND, "Trebuie ')' dupa argumentele functiei");

        return new Expr.Call(callee, paren, arguments);
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
