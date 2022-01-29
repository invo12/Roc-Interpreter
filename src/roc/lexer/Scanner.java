package roc.lexer;

import roc.Roc;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    private final String source;
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {

        char c = advance();
        List<Token> tokens = new ArrayList<>();
        while (!isAtEnd()) {

            switch (c) {
                case ' ', '\r', '\t' -> c = advance();
                case '\n' -> {
                    c = advance();
                    line++;
                }
                default -> {
                    if (isNumber(source.charAt(current))) {
                        start = current;
                        boolean isInt = true;
                        try {
                            while (!isAtEnd() && (isDouble(source.charAt(current)))) {
                                if (source.charAt(current) == '.') {
                                    isInt = false;
                                }
                                current++;
                            }
                            if (source.charAt(current - 1) == '.') {
                                //error
                                throw new NumberFormatException("Numarul trebuie sa aiba cifre dupa .");
                            }

                            if (isInt) {
                                int number = Integer.parseInt(source.substring(start, current));
                                tokens.add(new Token(TokenType.INT, null, number, line));
                            } else {
                                double number = Double.parseDouble(source.substring(start, current));
                                tokens.add(new Token(TokenType.DOUBLE, null, number, line));
                            }
                        } catch (NumberFormatException exception) {
                            Roc.error(line, exception.getMessage());
                        }
                    }
                    c = peek();
                }
            }
        }
        if (peek() == '\0') {
            tokens.add(new Token(TokenType.EOF, null, null, line));
        }
        return tokens;
    }

    private boolean isAtEnd() {

        return current >= source.length();
    }

    private boolean isDouble(char c) {

        return isNumber(c) || c == '.';
    }

    private boolean isNumber(char c) {

        return c >= '0' && c <= '9';
    }

    private char advance() {

        return source.charAt(current++);
    }

    private char peek() {

        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
}
