package roc.memory;

import roc.interpreter.RuntimeError;
import roc.lexer.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> values = new HashMap<>();

    public void define(String name, Object value) {

        values.put(name, value);
    }

    public Object get(Token name) {

        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        throw new RuntimeError(name, "Variabila nedefinita '" + name.lexeme + "'");
    }
}
