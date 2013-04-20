package r.builtins;

import r.nodes.*;

// TODO: complex numbers
final class Ln extends MathBase {

    static final CallFactory _ = new Ln("ln");

    private Ln(String name) {
        super(name);
    }

    @Override double op(ASTNode ast, double value) {
        return Math.log(value);
    }
}
