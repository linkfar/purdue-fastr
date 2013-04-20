package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

final class Logical extends ArrayConstructorBase {
    static final CallFactory _ = new Logical("logical", new String[]{"length"}, new String[]{});

    private Logical(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                if (args.length == 0) { return RLogical.EMPTY; }
                int len = arrayLength(args[0], ast);
                return RLogical.RLogicalFactory.getUninitializedArray(len);
            }
        };
    }
}
