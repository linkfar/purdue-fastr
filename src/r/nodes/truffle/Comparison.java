package r.nodes.truffle;

import com.oracle.truffle.nodes.*;

import r.*;
import r.data.*;
import r.data.RLogical.RLogicalFactory;
import r.nodes.*;
import r.nodes.BinaryOperation.BinaryOperator;


public class Comparison extends BaseR {

    final ValueComparison cmp;
    final BinaryOperator op;
    RNode left;
    RNode right;

    private static final boolean DEBUG_CMP = false;

    public Comparison(ASTNode ast, RNode left, RNode right, BinaryOperator op) {
        super(ast);
        this.left = updateParent(left);
        this.op = op;
        this.right = updateParent(right);

        switch(op) {
            case EQ: this.cmp = EQ; break;
            case LE: this.cmp = LE; break;
            default:
                throw new RuntimeException("not implemented operation");
        }
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        RAny lexpr = (RAny) left.execute(context, frame);
        RAny rexpr = (RAny) right.execute(context, frame);
        // this version assumes comparison of two scalars (int, double, int vs. double)
        try {
            if (DEBUG_CMP) Utils.debug("comparison - assuming scalar numbers");
            RArray larr = RValueConversion.expectArrayOne(lexpr);
            RArray rarr = RValueConversion.expectArrayOne(rexpr);
              // FIXME: If we can assume that when numeric scalars are compared, their types (int, double) are stable,
              //        we might try to improve performance by splitting the code below into different truffle nodes
            if (larr instanceof RDouble) { // note: could make this shorter if we didn't care about Java-level boxing
                double ldbl = ((RDouble) larr).getDouble(0);
                if (ldbl == RDouble.NA) {
                    return RLogical.BOXED_NA;
                }
                if (rarr instanceof RDouble) {
                    double rdbl = ((RDouble) rarr).getDouble(0);
                    if (rdbl == RDouble.NA) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(ldbl, rdbl) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                } else if (rarr instanceof RInt) {
                    int rint = ((RInt) rarr).getInt(0);
                    if (rint == RInt.NA) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(ldbl, rint) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                } else {
                    throw new UnexpectedResultException(null);
                }
            } else if (larr instanceof RInt) {
                int lint = ((RInt) larr).getInt(0);
                if (lint == RInt.NA) {
                    return RLogical.BOXED_NA;
                }
                if (rarr instanceof RInt) {
                    int rint = ((RInt) rarr).getInt(0);
                    if (rint == RInt.NA) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(lint, rint) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                } else if (rarr instanceof RDouble) {
                    double rdbl = ((RDouble) rarr).getDouble(0);
                    if (rdbl == RDouble.NA) {
                        return RLogical.BOXED_NA;
                    }
                    return cmp.cmp(lint, rdbl) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                } else {
                    throw new UnexpectedResultException(null);
                }
            } else {
                throw new UnexpectedResultException(null);
            }
        } catch (UnexpectedResultException e) {
            if (DEBUG_CMP) Utils.debug("comparison - optimistic comparison failed, values are not scalar numbers");
            VectorScalarComparison vs = new VectorScalarComparison(ast);
            replace(vs, "specializeNumericVectorScalarComparison");
            return vs.execute(context, frame, lexpr, rexpr);
        }
    }

    class VectorScalarComparison extends BaseR {

        public VectorScalarComparison(ASTNode ast) {
            super(ast);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Object execute(RContext context, RFrame frame) {
            RAny lexpr = (RAny) left.execute(context, frame);
            RAny rexpr = (RAny) right.execute(context, frame);
            return execute(context, frame, lexpr, rexpr);
        }

        public Object execute(RContext context, RFrame frame, RAny lexpr, RAny rexpr) {
            // this version assumes comparison of a numeric (int, double) vector against a scalar
            try {  // FIXME: perhaps should create different nodes for the cases below
                if (DEBUG_CMP) Utils.debug("comparison - assuming numeric (int,double) vector and scalar");
                // we assume that double vector against double scalar is the most common case
                if (lexpr instanceof RDouble) {
                    RDouble ldbl = (RDouble) lexpr;
                    if (rexpr instanceof RDouble) {
                        RDouble rdbl = (RDouble) rexpr;
                        if (rdbl.size() == 1) {
                            if (ldbl.size() >= 1) {
                                return Comparison.this.cmp.cmp(ldbl, rdbl.getDouble(0));
                            } else {
                                throw new UnexpectedResultException(null);
                            }
                        } else {
                            if (rdbl.size() >= 1 && ldbl.size() == 1) {
                                return Comparison.this.cmp.cmp(ldbl.getDouble(0), rdbl);
                            } else {
                                throw new UnexpectedResultException(null);
                            }
                        }
                    }
                }
                // we assume that integer vector against integer scalar is the second most common case
                if (lexpr instanceof RInt) {
                    RInt lint = (RInt) lexpr;
                    if (rexpr instanceof RInt) {
                        RInt rint = (RInt) rexpr;
                        if (rint.size() == 1) {
                            if (lint.size() >= 1) {
                                return Comparison.this.cmp.cmp(lint, rint.getInt(0));
                            } else {
                                throw new UnexpectedResultException(null);
                            }
                        } else {
                            if (rint.size() >= 1 && lint.size() == 1) {
                                return Comparison.this.cmp.cmp(lint.getInt(0), rint);
                            } else {
                                throw new UnexpectedResultException(null);
                            }
                        }
                    }
                }
                // now we know that one of the argument is not double and one is not integer
                if (lexpr instanceof RDouble || rexpr instanceof RDouble) {
                    RDouble ldbl = lexpr.asDouble();
                    RDouble rdbl = rexpr.asDouble();
                    if (rdbl.size() == 1) {
                        return Comparison.this.cmp.cmp(ldbl, rdbl.getDouble(0));
                    } else if (ldbl.size() == 1) {
                        return Comparison.this.cmp.cmp(ldbl.getDouble(0), rdbl);
                    } else {
                        throw new UnexpectedResultException(null);
                    }
                }
                if (lexpr instanceof RInt || lexpr instanceof RInt) {
                    RInt lint = lexpr.asInt();
                    RInt rint = lexpr.asInt();
                    if (rint.size() == 1) {
                        return Comparison.this.cmp.cmp(lint,  rint.getInt(0));
                    } else if (lint.size() == 1) {
                        return Comparison.this.cmp.cmp(lint.getInt(0), rint);
                    } else {
                        throw new UnexpectedResultException(null);
                    }
                }
                throw new UnexpectedResultException(null);

            } catch (UnexpectedResultException e) {
                if (DEBUG_CMP) Utils.debug("comparison - 2nd level comparison failed (not int,double scalar and vector)");
            }
            return null;
        }
    }

    // FIXME: check that calls to cmp are inlined, otherwise we might have to do that manually
    public abstract static class ValueComparison {
        public abstract boolean cmp(int a, int b);
        public abstract boolean cmp(double a, double b);
        public boolean cmp(int a, double b) {
            return cmp((double) a, b);
        }
        public boolean cmp(double a, int b) {
            return cmp(a, (double) b);
        }

        public RLogical cmp(RDouble a, double b) {
            int n = a.size();
            if (b == RDouble.NA) {
                return RLogicalFactory.getNAArray(n);
            }
            RLogical r = RLogicalFactory.getEmptyArray(n);
            for (int i = 0; i < n; i++) {
                double adbl = a.getDouble(i);
                if (adbl == RDouble.NA) {
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(adbl, b) ? RLogical.TRUE : RLogical.FALSE);
                }
            }
            return r;
        }
        public RLogical cmp(double a, RDouble b) {
            int n = b.size();
            if (a == RDouble.NA) {
                return RLogicalFactory.getNAArray(n);
            }
            RLogical r = RLogicalFactory.getEmptyArray(n);
            for (int i = 0; i < n; i++) {
                double bdbl = b.getDouble(i);
                if (bdbl == RDouble.NA) {
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(a, bdbl) ? RLogical.TRUE : RLogical.FALSE);
                }
            }
            return r;
        }
        public RLogical cmp(RInt a, int b) {
            int n = a.size();
            if (b == RInt.NA) {
                return RLogicalFactory.getNAArray(n);
            }
            RLogical r = RLogicalFactory.getEmptyArray(n);
            for (int i = 0; i < n; i++) {
                int aint = a.getInt(i);
                if (aint == RInt.NA) {
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(aint, b) ? RLogical.TRUE : RLogical.FALSE);
                }
            }
            return r;
        }
        public RLogical cmp(int a, RInt b) {
            int n = b.size();
            if (a == RInt.NA) {
                return RLogicalFactory.getNAArray(n);
            }
            RLogical r = RLogicalFactory.getEmptyArray(n);
            for (int i = 0; i < n; i++) {
                int bint = b.getInt(i);
                if (bint == RInt.NA) {
                    r.set(i, RLogical.NA);
                } else {
                    r.set(i, cmp(a, bint) ? RLogical.TRUE : RLogical.FALSE);
                }
            }
            return r;
        }

    }

    public static final class Equal extends ValueComparison {
        @Override
        public boolean cmp(int a, int b) {
            return a == b;
        }
        @Override
        public boolean cmp(double a, double b) {
            return a == b;
        }
    }

    public static final class LessOrEqual extends ValueComparison {
        @Override
        public boolean cmp(int a, int b) {
            return a <= b;
        }
        @Override
        public boolean cmp(double a, double b) {
            return a <= b;
        }
    }

    protected static Equal EQ = new Equal();
    protected static LessOrEqual LE = new LessOrEqual();

}
