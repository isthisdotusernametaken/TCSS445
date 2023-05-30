package controller;

import util.Util;

// Note on returnTypes: For scalar-valued functions, returnTypes should be a
// size-1 array containing the returned scalar's type; for table-valued
// functions, returnTypes is the list of column types for the returned table;
// for procedures, returnTypes is null, and the types given for the out-mode
// params in paramTypes are used (after being found with outParamIndices).
//
// Note on outParamIndices: For functions, this field is null; for procedures,
// this should provide the 1-based indices of which params are out-mode,
// exactly corresponding to paramsOut.
//
// Note on null fields: For functions, paramsOut and outParamIndices are both
// null; for procedures, returnColumnTypes is null. procedure() must be checked
// before attempting to access any of these arrays.
public record Signature(boolean procedure, // Whether this is a procedure (not a function)
                        String call, // The proc/func name and parameters, including placeholders
                        int[] paramTypes, // The types the placeholders, in order
                        boolean[] paramsNullable, // Whether each placeholder can take null input
                        String[] paramNames, // Name to return if param's value is invalid
                        boolean[] paramsOut, // Whether each placeholder is an out-mode param
                        int[] returnColumnTypes, // The column types of a function return
                        int[] outParamIndices) { // Indices of only the out-mode placeholders

    public Signature {
        if (call == null || call.isBlank())
            throw new IllegalArgumentException("call must not be null or blank");
        if (paramTypes == null)
            throw new IllegalArgumentException("paramTypes must not be null");
        if (paramsNullable == null)
            throw new IllegalArgumentException("paramTypes must not be null");
        if (paramTypes.length != paramsNullable.length ||
            paramTypes.length != paramNames.length ||
            (paramsOut != null && paramTypes.length != paramsOut.length))
            throw new IllegalArgumentException(
                    "paramTypes, paramsNullable, paramNames, and paramsOut " +
                    "must all have the same length"
            );

        if (procedure) {
            if (returnColumnTypes != null ||
                paramsOut == null || outParamIndices == null)
                throw new IllegalArgumentException(
                        "For procedures, returnColumnTypes must be null, and " +
                        "paramsOut and outParamIndices must be nonnull"
                );
        } else {
            if (returnColumnTypes == null ||
                    paramsOut != null || outParamIndices != null)
                throw new IllegalArgumentException(
                        "For functions, returnColumnTypes must be nonnull, " +
                        "and paramsOut and outParamIndices must be null"
                );
        }
    }

    static Signature buildFunc(final String func,
                               final int[] paramTypes,
                               final boolean[] paramsNullable,
                               final String[] paramNames,
                               final int[] returnColumnTypes,
                               boolean tableValued)
            throws IllegalArgumentException {
        return new Signature(
                false,
                tableValued ? "SELECT * FROM " + func : "SELECT " + func,
                paramTypes, paramsNullable, paramNames, null,
                returnColumnTypes, null
        );
    }

    static Signature buildFunc(final String func,
                               final int[] paramTypes,
                               final String[] paramNames,
                               final int[] returnColumnTypes,
                               boolean tableValued)
            throws IllegalArgumentException {
        return buildFunc(
                func,
                paramTypes, new boolean[paramTypes.length], paramNames,
                returnColumnTypes, tableValued
        );
    }

    static Signature buildProc(final String proc,
                               final int[] paramTypes,
                               final boolean[] paramsNullable,
                               final String[] paramNames,
                               final int[] outParamIndices)
            throws NegativeArraySizeException, NullPointerException, IllegalArgumentException {
        return new Signature(
                true,
                "{call " + proc + "}",
                paramTypes, paramsNullable, paramNames,
                Util.trueOnlyOnIndices(paramTypes.length, outParamIndices),
                null,
                outParamIndices
        );
    }

    static Signature buildProc(final String proc,
                               final int[] paramTypes,
                               final String[] paramNames,
                               final int[] outParamIndices)
            throws NegativeArraySizeException, NullPointerException, IllegalArgumentException {
        // No nullable params
        return buildProc(
                proc,
                paramTypes, new boolean[paramTypes.length], paramNames,
                outParamIndices
        );
    }

    static Signature buildProc(final String proc,
                               final int[] paramTypes,
                               final String[] paramNames)
            throws NegativeArraySizeException, NullPointerException, IllegalArgumentException {
        // No nullable params, no output params
        return buildProc(proc, paramTypes, paramNames, new int[0]);
    }
}
