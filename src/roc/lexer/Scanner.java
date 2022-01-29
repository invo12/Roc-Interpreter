package roc.lexer;

import roc.Roc;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {

        while (!isAtEnd()) {

            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {

        char c = advance();
        switch (c) {
            case ' ', '\r', '\t':
                break;
            case '\n':
                line++;
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else {
                    Roc.error(line, "Caracter invalid " + c);
                }
                break;
        }
    }

    private void number() {

        boolean isInt = true;
        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.') {
            if (isDigit(peekNext())) {
                isInt = false;
                advance();
            } else {
                Roc.error(line, "Numarul nu se poate termina in '.'");
                return;
            }
            while (isDigit(peek())) {
                advance();
            }
        }

        if (isInt) {
            int number = Integer.parseInt(source.substring(start, current));
            tokens.add(new Token(TokenType.INT, null, number, line));
        } else {
            double number = Double.parseDouble(source.substring(start, current));
            tokens.add(new Token(TokenType.DOUBLE, null, number, line));
        }
    }

    private boolean isAtEnd() {

        return current >= source.length();
    }

    private boolean isDigit(char c) {

        return c >= '0' && c <= '9';
    }

    private char advance() {

        return source.charAt(current++);
    }

    private char peek() {

        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {

        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
}
