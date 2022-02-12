package roc.interpreter;

import roc.memory.Environment;
import roc.parser.Stmt;

import java.util.List;

public class RocFunction implements RocCallable {

    private final Stmt.Function declaration;
    private final Environment closure;

    private final boolean isInitializer;

    public RocFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }


    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {

        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if(isInitializer) return closure.getAt(0, "this");
            return returnValue.value;
        }
        return null;
    }

    @Override
    public String toString() {
        return "<functia " + declaration.name.lexeme + ">";
    }

    RocFunction bind(RocInstance instance) {

        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new RocFunction(declaration, environment, isInitializer);
    }
}
