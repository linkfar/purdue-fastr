package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class OpAnd extends OperationsBase {
    static final CallFactory _ = new OpAnd("&&");

    private OpAnd(String name) {
        super(name);
    }

    @Override public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        // exprs.length == 2
        return new LogicalOperation.And(ast, exprs[0], exprs[1]);
    }
}
