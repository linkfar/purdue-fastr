package r.data;

import java.util.*;

import r.*;
import r.Convert.ConversionStatus;
import r.data.internal.*;

public interface RLogical extends RArray { // FIXME: should extend Number instead?

    String TYPE_STRING = "logical";
    int TRUE = 1;
    int FALSE = 0;
    int NA = Integer.MIN_VALUE;

    ScalarLogicalImpl BOXED_TRUE = RLogicalFactory.getScalar(TRUE);
    ScalarLogicalImpl BOXED_FALSE = RLogicalFactory.getScalar(FALSE);
    ScalarLogicalImpl BOXED_NA = RLogicalFactory.getScalar(NA);

    LogicalImpl EMPTY = (LogicalImpl) RLogicalFactory.getUninitializedArray(0);

    int getLogical(int il);
    RLogical set(int i, int val);
    RLogical materialize();

    public class RLogicalUtils {
        public static int truesInRange(RLogical l, int from, int to) {
            int ntrue = 0;
            for (int i = from; i < to; i++) {
                if (l.getLogical(i) == TRUE) {
                   ntrue++;
                }
            }
            return ntrue;
        }
        public static int nonFalsesInRange(RLogical l, int from, int to) {
            int nnonfalse = 0;
            for (int i = from; i < to; i++) {
                if (l.getLogical(i) != FALSE) {
                   nnonfalse++;
                }
            }
            return nnonfalse;
        }
        public static RRaw logicalToRaw(RLogical value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            byte[] content = new byte[size];
            for (int i = 0; i < size; i++) {
                int lval = value.getLogical(i);
                content[i] = Convert.logical2raw(lval, warn);
            }
            return RRaw.RRawFactory.getFor(content, value.dimensions());
        }
    }

    public class RLogicalFactory {
        public static ScalarLogicalImpl getScalar(int value) {
            return new ScalarLogicalImpl(value);
        }
        public static RLogical getScalar(int value, int[] dimensions) {
            if (dimensions == null) {
                return new ScalarLogicalImpl(value);
            } else {
                return getFor(new int[] {value}, dimensions);
            }
        }
        public static RLogical getArray(int... values) {
            if (values.length == 1) {
                return new ScalarLogicalImpl(values[0]);
            }
            return new LogicalImpl(values);
        }
        public static RLogical getArray(int[] values, int[] dimensions) {
            if (values.length == 1) {
                return new ScalarLogicalImpl(values[0]);
            }
            return new LogicalImpl(values, dimensions);
        }
        public static RLogical getUninitializedArray(int size) {
            if (size == 1) {
                return new ScalarLogicalImpl(0);
            }
            return new LogicalImpl(size);
        }
        public static RLogical getUninitializedArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return new ScalarLogicalImpl(0);
            }
            return new LogicalImpl(new int[size], dimensions, false);
        }
        public static RLogical getNAArray(int size) {
            return getNAArray(size, null);
        }
        public static RLogical getNAArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return BOXED_NA;
            }
            int[] content = new int[size];
            Arrays.fill(content, NA);
            return new LogicalImpl(content, dimensions, false);
        }
        public static LogicalImpl getMatrixFor(int[] values, int m, int n) {
            return new LogicalImpl(values, new int[] {m, n}, false);
        }
        public static RLogical copy(RLogical l) {
            if (l.size() == 1 && l.dimensions() == null) {
                return new ScalarLogicalImpl(l.getLogical(0));
            }
            return new LogicalImpl(l);
        }
        public static RLogical getFor(int[] values) {  // re-uses values!
            return getFor(values, null);
        }
        public static RLogical getFor(int[] values, int[] dimensions) {  // re-uses values!
            if (values.length == 1 && dimensions == null) {
                return new ScalarLogicalImpl(values[0]);
            }
            return new LogicalImpl(values, dimensions, false);
        }
        public static RLogical exclude(int excludeIndex, RLogical orig) {
            return new RLogicalExclusion(excludeIndex, orig);
        }
        public static RLogical subset(RLogical value, RInt index) {
            return new RLogicalSubset(value, index);
        }
    }

    public static class RStringView extends View.RStringView implements RString {

        final RLogical orig;
        public RStringView(RLogical orig) {
            this.orig = orig;
        }

        @Override
        public int size() {
            return orig.size();
        }

        @Override
        public RList asList() {
            return orig.asList();
        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble() {
            return orig.asDouble();
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical() {
            return orig;
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public RAttributes getAttributes() {
            return orig.getAttributes();
        }

        @Override
        public String getString(int i) {
            int v = orig.getLogical(i);
            return Convert.logical2string(v);
        }

        @Override
        public boolean isSharedReal() {
            return orig.isShared();
        }

        @Override
        public void ref() {
            orig.ref();
        }

        @Override
        public int[] dimensions() {
            return orig.dimensions();
        }
    }

    public static class RComplexView extends View.RComplexView implements RComplex {

        final RLogical orig;
        public RComplexView(RLogical orig) {
            this.orig = orig;
        }

        @Override
        public int size() {
            return orig.size();
        }

        @Override
        public RAttributes getAttributes() {
            return orig.getAttributes();
        }

        @Override
        public RList asList() {
            return orig.asList();
        }

        @Override
        public RString asString() {
            return orig.asString();
        }

        @Override
        public RDouble asDouble() {
            return orig.asDouble();
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical() {
            return orig;
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return orig.asString();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public double getReal(int i) {
            int ll = orig.getLogical(i);
            return Convert.logical2double(ll);
        }

        @Override
        public double getImag(int i) {
            return 0;
        }

        @Override
        public boolean isSharedReal() {
            return orig.isShared();
        }

        @Override
        public void ref() {
            orig.ref();
        }

        @Override
        public int[] dimensions() {
            return orig.dimensions();
        }
    }

    public static class RDoubleView extends View.RDoubleView implements RDouble {

        final RLogical orig;
        public RDoubleView(RLogical orig) {
            this.orig = orig;
        }

        @Override
        public int size() {
            return orig.size();
        }

        @Override
        public RAttributes getAttributes() {
            return orig.getAttributes();
        }

        @Override
        public RList asList() {
            return orig.asList();
        }

        @Override
        public RString asString() {
            return orig.asString();
        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical() {
            return orig;
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return orig.asString();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public double getDouble(int i) {
            int ll = orig.getLogical(i);
            return Convert.logical2double(ll);
        }

        @Override
        public boolean isSharedReal() {
            return orig.isShared();
        }

        @Override
        public void ref() {
            orig.ref();
        }

        @Override
        public int[] dimensions() {
            return orig.dimensions();
        }
    }

    public static class RIntView extends View.RIntView implements RInt {

        final RLogical l;
        public RIntView(RLogical l) {
            this.l = l;
        }

        @Override
        public int size() {
            return l.size();
        }

        @Override
        public RList asList() {
            return l.asList();
        }

        @Override
        public RString asString() {
            return l.asString();
        }

        @Override
        public RComplex asComplex() {
            return l.asComplex();
        }

        @Override
        public RDouble asDouble() {
            return l.asDouble();
        }

        @Override
        public RLogical asLogical() {
            return l;
        }

        @Override
        public RRaw asRaw() {
            return l.asRaw();
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return l.asString();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return l.asComplex();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return l.asDouble();
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return l;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return l.asRaw(warn);
        }

        @Override
        public RAttributes getAttributes() {
            return l.getAttributes();
        }

        @Override
        public int getInt(int i) {
            return Convert.logical2int(l.getLogical(i));
        }

        @Override
        public boolean isSharedReal() {
            return l.isShared();
        }

        @Override
        public void ref() {
            l.ref();
        }

        @Override
        public int[] dimensions() {
            return l.dimensions();
        }
    }

    public static class RRawView extends View.RRawView implements RRaw {

        final RLogical l;
        public RRawView(RLogical l) {
            this.l = l;
        }

        @Override
        public int size() {
            return l.size();
        }

        @Override
        public RAttributes getAttributes() {
            return l.getAttributes();
        }

        @Override
        public RList asList() {
            return l.asList();
        }

        @Override
        public RString asString() {
            return l.asString();
        }

        @Override
        public RComplex asComplex() {
            return l.asComplex();
        }

        @Override
        public RDouble asDouble() {
            return l.asDouble();
        }

        @Override
        public RInt asInt() {
            return l.asInt();
        }

        @Override
        public RLogical asLogical() {
            return l;
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return l.asString();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return l.asComplex();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return l.asDouble();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return l.asInt();
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return l;
        }

        @Override
        public byte getRaw(int i) {
            int ll = l.getLogical(i);
            return Convert.logical2raw(ll);
        }

        @Override
        public boolean isSharedReal() {
            return l.isShared();
        }

        @Override
        public void ref() {
            l.ref();
        }

        @Override
        public int[] dimensions() {
            return l.dimensions();
        }
    }

    public static class RLogicalExclusion extends View.RLogicalView implements RLogical {

        final RLogical orig;
        final int excludeIndex;
        final int size;

        public RLogicalExclusion(int excludeIndex, RLogical orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int getLogical(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getLogical(i);
            } else {
                return orig.getLogical(i + 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return orig.isShared();
        }

        @Override
        public void ref() {
            orig.ref();
        }
    }

    // indexes must all be positive
    //   but can be out of bounds ==> NA's are returned in that case
    public static class RLogicalSubset extends View.RLogicalView implements RLogical {

        final RLogical value;
        final int vsize;
        final RInt index;
        final int isize;

        public RLogicalSubset(RLogical value, RInt index) {
            this.value = value;
            this.index = index;
            this.isize = index.size();
            this.vsize = value.size();
        }

        @Override
        public int size() {
            return isize;
        }

        @Override
        public int getLogical(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RLogical.NA;
            } else {
                return value.getLogical(j - 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return value.isShared() || index.isShared();
        }

        @Override
        public void ref() {
            value.ref();
            index.ref();
        }
    }
}
