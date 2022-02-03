package roc.parser;

import roc.Roc;
import roc.lexer.Token;
import roc.lexer.TokenType;

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

    public Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr expression() {
        return equality();
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

        while (match(SLASH, STAR)) {
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

        if (match(INT) || match(DOUBLE) || match(STRING)) return new Expr.Literal(previous().literal);

        if (match(LEFT_ROUND)) {
            Expr expression = expression();
            consume(RIGHT_ROUND, "Expect ')' after expression");
            return new Expr.Grouping(expression);
        }

        throw error(peek(), "Expect expression.");
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
}
