package roc.lexer;

public enum TokenType {

    // arithmetic
    PLUS, MINUS, STAR, SLASH, INT, STRING,
    MODULO, DOUBLE, EQUAL,

    // comparison
    GREATER, GREATER_EQUALS, LESS, LESS_EQUALS,
    EQUAL_EQUAL, EXMARK_EQUALS,

    // logical
    EXMARK, AND, OR, ADEVARAT, FALS,

    // paranthesis
    LEFT_BRACE, RIGHT_BRACE, LEFT_SQUARE, RIGHT_SQUARE,
    LEFT_ROUND, RIGHT_ROUND,

    // reserved words
    VAR, PENTRU, CATTIMP, DACA, ALTFEL, DIN, CONST,
    AFISEAZA, NUL, CLASA, MOSTENESTE, EOF,

    // other
    SEMICOLON, COMMA, DOT, IDENTIFIER, FUN, RETURNEAZA,
    SUPER, INSTANTA
}
