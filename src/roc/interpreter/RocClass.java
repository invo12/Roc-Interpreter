package roc.interpreter;

import java.util.List;
import java.util.Map;

public class RocClass implements RocCallable {

    final String name;
    private final Map<String, RocFunction> methods;

    public RocClass(String name, Map<String, RocFunction> methods) {
        this.name = name;
        this.methods = methods;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        RocFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {

        RocInstance instance = new RocInstance(this);
        RocFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    RocFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        return null;
    }
}
