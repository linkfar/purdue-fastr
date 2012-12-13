package r.data;

import java.util.*;

import r.*;
import r.Convert.*;
import r.data.internal.*;

public interface RDouble extends RNumber {
    String TYPE_STRING = "double";
    long NA_LONGBITS = 0x7ff00000000007a2L; // R's NA is a special instance of IEEE's NaN
    double NA = Double.longBitsToDouble(NA_LONGBITS);
    double NaN = Double.NaN;
    double EPSILON = Math.pow(2.0, -52.0);
    double NEG_INF = Double.NEGATIVE_INFINITY;

    DoubleImpl EMPTY = (DoubleImpl) RDoubleFactory.getUninitializedArray(0);
    ScalarDoubleImpl BOXED_ZERO = RDoubleFactory.getScalar(0);
    ScalarDoubleImpl BOXED_NA = RDoubleFactory.getScalar(NA);
    ScalarDoubleImpl BOXED_NEG_INF = RDoubleFactory.getScalar(Double.NEGATIVE_INFINITY);
    ScalarDoubleImpl BOXED_POS_INF = RDoubleFactory.getScalar(Double.POSITIVE_INFINITY);

    RDouble set(int i, double val);
    double getDouble(int i);
    RDouble materialize();

    public class RDoubleUtils {
        public static boolean isNA(double d) {
            return Double.doubleToRawLongBits(d) == NA_LONGBITS;
        }
        public static boolean fitsRInt(double d) {
            return d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE;
        }
        public static boolean isNAorNaN(double d) {
            return Double.isNaN(d);
        }
        public static boolean isFinite(double d) {
            return !isNAorNaN(d) && !Double.isInfinite(d);
        }
        public static RInt double2int(RDouble value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            int[] content = new int[size];
            for (int i = 0; i < size; i++) {
                double d = value.getDouble(i);
                content[i] = Convert.double2int(d, warn);
            }
            return RInt.RIntFactory.getFor(content, value.dimensions());
        }
        public static RRaw doubleToRaw(RDouble value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            byte[] content = new byte[size];
            for (int i = 0; i < size; i++) {
                double dval = value.getDouble(i);
                content[i] = Convert.double2raw(dval, warn);
            }
            return RRaw.RRawFactory.getFor(content, value.dimensions());
        }
    }
    public class RDoubleFactory {
        public static ScalarDoubleImpl getScalar(double value) {
            return new ScalarDoubleImpl(value);
        }
        public static RDouble getScalar(double value, int[] dimensions) {
            if (dimensions == null) {
                return new ScalarDoubleImpl(value);
            } else {
                return getFor(new double[] {value}, dimensions);
            }
        }
        public static RDouble getArray(double... values) {
            if (values.length == 1) {
                return new ScalarDoubleImpl(values[0]);
            }
            return new DoubleImpl(values);
        }
        public static RDouble getArray(double[] values, int[] dimensions) {
            if (dimensions == null && values.length == 1) {
                return new ScalarDoubleImpl(values[0]);
            }
            return new DoubleImpl(values, dimensions);
        }
        public static RDouble getUninitializedArray(int size) {
            if (size == 1) {
                return new ScalarDoubleImpl(0);
            }
            return new DoubleImpl(size);
        }
        public static RDouble getUninitializedArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return new ScalarDoubleImpl(0);
            }
            return new DoubleImpl(new double[size], dimensions, false);
        }
        public static RDouble getNAArray(int size) {
            return getNAArray(size, null);
        }
        public static RDouble getNAArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return BOXED_NA;
            }
            double[] content = new double[size];
            Arrays.fill(content, NA);
            return new DoubleImpl(content, dimensions, false);
        }
        public static DoubleImpl getMatrixFor(double[] values, int m, int n) {
            return new DoubleImpl(values, new int[] {m, n}, false);
        }
        public static RDouble copy(RDouble d) {
            if (d.size() == 1 && d.dimensions() == null) {
                return new ScalarDoubleImpl(d.getDouble(0));
            }
            return new DoubleImpl(d);
        }
        public static RDouble getFor(double[] values) { // re-uses values!
            return getFor(values, null);
        }
        public static RDouble getFor(double[] values, int[] dimensions) {  // re-uses values!
            if (values.length == 1 && dimensions == null) {
                return new ScalarDoubleImpl(values[0]);
            }
            return new DoubleImpl(values, dimensions, false);
        }
        public static RDouble exclude(int excludeIndex, RDouble orig) {
            return new RDoubleExclusion(excludeIndex, orig);
        }
        public static RDouble subset(RDouble value, RInt index) {
            return new RDoubleSubset(value, index);
        }
    }

    public static class RComplexView extends View.RComplexView implements RComplex {

        RDouble orig;

        public RComplexView(RDouble orig) {
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
            return orig;
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
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
            return orig;
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt(warn);
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public double getReal(int i) {
            return orig.getDouble(i);
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

    public static class RStringView extends View.RStringView implements RString {

        RDouble orig;

        public RStringView(RDouble orig) {
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
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble() {
            return orig;
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt(warn);
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public String getString(int i) {
            return Convert.double2string(orig.getDouble(i));
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

    public static class RIntView extends View.RIntView implements RInt { // FIXME: remove this? it breaks warnings

        RDouble orig;

        public RIntView(RDouble orig) {
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
        public RDouble asDouble() {
            return orig;
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
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
        public RDouble asDouble(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public int getInt(int i) {
            return Convert.double2int(orig.getDouble(i));
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

    public static class RLogicalView extends View.RLogicalView implements RLogical {

        RDouble orig;

        public RLogicalView(RDouble orig) {
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
        public RDouble asDouble() {
            return orig;
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
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
        public RDouble asDouble(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt(warn);
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public int getLogical(int i) {
            return Convert.double2logical(orig.getDouble(i));
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

    public static class RRawView extends View.RRawView implements RRaw { // FIXME: remove this? it breaks warnings

        RDouble orig;

        public RRawView(RDouble orig) {
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
        public RDouble asDouble() {
            return orig;
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
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
        public RDouble asDouble(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt(warn);
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public byte getRaw(int i) {
            return Convert.double2raw(orig.getDouble(i));
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

    public static class RDoubleExclusion extends View.RDoubleView implements RDouble {

        final RDouble orig;
        final int excludeIndex;
        final int size;

        public RDoubleExclusion(int excludeIndex, RDouble orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public double getDouble(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getDouble(i);
            } else {
                return orig.getDouble(i + 1);
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
    public static class RDoubleSubset extends View.RDoubleView implements RDouble {

        final RDouble value;
        final int vsize;
        final RInt index;
        final int isize;

        public RDoubleSubset(RDouble value, RInt index) {
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
        public double getDouble(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RDouble.NA;
            } else {
                return value.getDouble(j - 1);
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
