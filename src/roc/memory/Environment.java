package roc.memory;

import roc.interpreter.RuntimeError;
import roc.lexer.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> values = new HashMap<>();
    private final Environment enclosing;

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if(enclosing != null){
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Variabila nedefinita '" + name.lexeme + "'.");
    }

    public void define(String name, Object value) {

        values.put(name, value);
    }

    public Object get(Token name) {

        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        throw new RuntimeError(name, "Variabila nedefinita '" + name.lexeme + "'");
    }
}
