package roc.lexer;

import roc.Roc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("si", TokenType.AND);
        keywords.put("sau", TokenType.OR);
        keywords.put("adevarat", TokenType.ADEVARAT);
        keywords.put("fals", TokenType.FALS);
        keywords.put("var", TokenType.VAR);
        keywords.put("pentru", TokenType.PENTRU);
        keywords.put("cattimp", TokenType.CATTIMP);
        keywords.put("daca", TokenType.DACA);
        keywords.put("altfel", TokenType.ALTFEL);
        keywords.put("din", TokenType.DIN);
        keywords.put("const", TokenType.CONST);
        keywords.put("afiseaza", TokenType.AFISEAZA);
        keywords.put("nul", TokenType.NUL);
        keywords.put("clasa", TokenType.CLASA);
        keywords.put("mosteneste", TokenType.MOSTENESTE);
        keywords.put("fun", TokenType.FUN);
        keywords.put("returneaza", TokenType.RETURNEAZA);
        keywords.put("super", TokenType.SUPER);
        keywords.put("instanta", TokenType.INSTANTA);
        keywords.put("evadeaza", TokenType.EVADEAZA);
        keywords.put("continua", TokenType.CONTINUA);
    }

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
            case '/':
                // if it is a comment discard all until new line
                if (match('/')) {
                    while (!isAtEnd() && peek() != '\n')
                        advance();
                } else {
                    addToken(TokenType.SLASH);
                    break;
                }
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUALS_EQUALS : TokenType.EQUAL);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUALS : TokenType.GREATER);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUALS : TokenType.LESS);
                break;
            case '!':
                addToken(match('=') ? TokenType.EXMARK_EQUALS : TokenType.EXMARK);
                break;
            case '%':
                addToken(TokenType.MODULO);
                break;
            case '(':
                addToken(TokenType.LEFT_ROUND);
                break;
            case ')':
                addToken(TokenType.RIGHT_ROUND);
                break;
            case '[':
                addToken(TokenType.LEFT_SQUARE);
                break;
            case ']':
                addToken(TokenType.RIGHT_SQUARE);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '"':
                string();
                break;
            case ' ', '\r', '\t':
                break;
            case '\n':
                line++;
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isIdentifierChar(c)) {
                    identifier();
                } else {
                    Roc.error(line, "Caracter invalid " + c);
                }
                break;
        }
    }

    private void number() {

        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.') {
            if (isDigit(peekNext())) {
                advance();
            } else {
                Roc.error(line, "Numarul nu se poate termina in '.'");
                return;
            }
            while (isDigit(peek())) {
                advance();
            }
        }

        double number = Double.parseDouble(source.substring(start, current));
        addToken(TokenType.DOUBLE, number);
    }

    private void string() {

        int startLine = line;
        while (peek() != '"' && !isAtEnd()) {

            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (!match('"')) {
            Roc.error(line, "Sir de caractere neterminat, inceput la linia " + startLine);
            return;
        }

        addToken(TokenType.STRING, source.substring(start + 1, current - 1));
    }

    private void identifier() {

        while (!isAtEnd() && isIdentifierChar(peek())) {
            advance();
        }

        String name = source.substring(start, current);
        if (keywords.containsKey(name)) {
            addToken(keywords.get(name));
            return;
        }

        addToken(TokenType.IDENTIFIER);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {

        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd() {

        return current >= source.length();
    }

    private boolean isIdentifierChar(char c) {

        return isDigit(c) ||
                (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c == '_');
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

    private boolean match(char expected) {

        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }
}
