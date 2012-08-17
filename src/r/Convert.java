package r;

import r.data.*;

public class Convert {

    public static double string2double(String v) {
        if (v.equals("NA")) {
            return RDouble.NA;
        }
        // FIXME use R rules
        return Double.parseDouble(v);
    }

    public static String double2string(double d) {
        // TODO check what's generated by this method
        if (Double.isNaN(d) && Double.doubleToRawLongBits(d) == Double.doubleToRawLongBits(RDouble.NA)) {
            return "NA";
        }
        // FIXME use R rules
        return Double.toString(d);
    }

    public static int string2lgl(String b) {
        if (b.equals("TRUE") || b.equals("T")) {
            return 1;
        } else if (b.equals("FALSE") || b.equals("F")) {
            return 0;
        } else {
            return RLogical.NA;
        }
    }

    public static String logical2string(int i) {
        switch (i) {
            case 0:
                return "FALSE";
            case RLogical.NA:
                return "NA";
            default:
                return "TRUE";
        }
    }

    public static String int2string(int i) {
        if (i == RInt.NA) {
            return "NA";
        }
        // FIXME use R rules
        return Integer.toString(i) + "L";
    }

    public static int string2int(String v) {
        if (v.equals("NA")) {
            return RInt.NA;
        }
        // FIXME use R rules
        return Integer.parseInt(v);
    }
}
