package roc.parser;

import roc.lexer.Token;

import java.util.List;

abstract class Expr {

    static class Binary extends Expr {

        public Expr left;
        public Token operator;
        public Expr right;

        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    static class Grouping extends Expr {

        public Expr expression;

        public Grouping(Expr expression) {
            this.expression = expression;
        }
    }

    static class Literal extends Expr {

        public Object value;

        public Literal(Object value) {
            this.value = value;
        }
    }

    static class Unary extends Expr {

        public Token operator;
        public Expr expression;

        public Unary(Token operator, Expr expression) {
            this.operator = operator;
            this.expression = expression;
        }
    }

}
