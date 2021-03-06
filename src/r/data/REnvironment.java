package r.data;

import r.data.internal.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

public interface REnvironment extends RAny {
    String TYPE_STRING = "environment";

    REnvironment EMPTY = new EnvironmentImpl.Empty();
    REnvironment GLOBAL = new EnvironmentImpl.Global();


    DummyFunction DUMMY_FUNCTION = new DummyFunction(); // a placeholder for no local variables, for Frames that do not belong to a real function

    Frame frame();
    void assign(RSymbol name, RAny value, boolean inherits, ASTNode ast);
    void delayedAssign(RSymbol name, RPromise value, ASTNode ast);
    RAny get(RSymbol name, boolean inherits);
    Object localGetNotForcing(RSymbol name);
    boolean exists(RSymbol name, boolean inherits);
    RCallable match(RSymbol name);
    RSymbol[] ls(boolean includingHidden);

    public static class DummyFunction implements RFunction {

        @Override
        public int positionInLocalWriteSet(RSymbol sym) {
            return -1;
        }

        @Override
        public int positionInLocalReadSet(RSymbol sym) {
            return -1;
        }

        @Override
        public EnclosingSlot getLocalReadSetEntry(RSymbol sym) {
            return null;
        }

        @Override
        public boolean hasLocalOrEnclosingSlot(RSymbol sym) {
            return false;
        }

        @Override
        public RFunction enclosingFunction() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public RSymbol[] paramNames() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public RNode[] paramValues() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public RNode body() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public RClosure createClosure(Frame frame) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ASTNode getSource() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int nlocals() {
            return 0;
        }

        @Override
        public int nparams() {
            return 0;
        }

        private static RSymbol[] emptySet = new RSymbol[0];

        @Override
        public RSymbol[] localWriteSet() {
            return emptySet;
        }

        @Override
        public int localSlot(RSymbol sym) {
            return -1;
        }

        @Override
        public EnclosingSlot enclosingSlot(RSymbol sym) {
            return null;
        }

        @Override
        public int dotsIndex() {
            return -1;
        }

        public FrameDescriptor frameDescriptor() {
            return NoSlotsFrame.NO_SLOTS_DESCRIPTOR;
        }

        public Object call(Frame frame) {
            return null;
        }

        public Frame createFrame(Frame callerFrame) {
            return null;
        }

        public boolean hasLocalSlot(RSymbol sym) {
            return false;
        }

        public Object callNoDefaults(Frame frame) {
            return null;
        }
    }

}
