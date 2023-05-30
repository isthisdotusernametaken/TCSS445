package util;

public class Util {

    public static int clamp(final int value, final int min, final int max) {
        return value <= min ? min :
               value >= max ? max :
               value;
    }

    public static boolean[] trueOnlyOnIndices(final int length, final int[] trueIndices)
            throws NegativeArraySizeException, NullPointerException, IllegalArgumentException {
        final var flags = new boolean[length]; // Throws exception if length < 0

        for (int trueIndex : trueIndices) {
            if (trueIndex < 1 || trueIndex > length)
                throw new IllegalArgumentException(
                        "trueIndices must only contain 1-based indices" +
                                "within the specified boolean array size"
                );

            flags[trueIndex - 1] = true;
        }

        return flags;
    }
}
