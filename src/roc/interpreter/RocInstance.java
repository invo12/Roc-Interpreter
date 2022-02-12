package roc.interpreter;

import roc.lexer.Token;

import java.util.HashMap;
import java.util.Map;

public class RocInstance {

    private final RocClass clasa;
    private final Map<String, Object> fields = new HashMap<>();

    RocInstance(RocClass clasa) {
        this.clasa = clasa;
    }

    public Object get(Token name) {

        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        RocFunction method = clasa.findMethod(name.lexeme);
        if (method != null) return method.bind(this);
        throw new RuntimeError(name, "Proprietate nedefinita '" + name.lexeme + "'");
    }

    public void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return "Instanta a clasei " + clasa.name;
    }
}
