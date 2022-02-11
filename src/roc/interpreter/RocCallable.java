package roc.interpreter;

import java.util.List;

public interface RocCallable {

    int arity();

    Object call(Interpreter interpreter, List<Object> arguments);
}
