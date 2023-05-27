package util;

public class Util {

    public static int clamp(final int value, final int min, final int max) {
        return value <= min ? min :
               value >= max ? max :
               value;
    }
}
