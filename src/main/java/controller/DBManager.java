package controller;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static java.sql.Types.*;

public class DBManager {

    // Reference value used to indicate param parsing failure
    public static final Object[] PARAM_FAIL = new Object[0];
    // Reference value used to indicate return value that could not be
    // converted to requested return type
    public static final Object[] RETURN_FAIL = new Object[0];

    private static final SQLServerDataSource dataSource = new SQLServerDataSource();

    static void initialize() {
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(1433);
        dataSource.setUser("sa"); // Should come from secure config file if integratedSecurity feature not used
        dataSource.setPassword(""); // " "
        dataSource.setEncrypt("true");
        dataSource.setTrustServerCertificate(true);

        dataSource.setDatabaseName("tomlin_trevor_db");

        runTableFunction(
                "ViewReviews(?, 100, ?)",
                new int[]{INTEGER, INTEGER}, new Object[]{0, 0}, new boolean[]{false, false},
                new int[]{NVARCHAR, NVARCHAR, INTEGER, NVARCHAR, DATE}
        );
    }

    static Object[][] runTableFunction(final String function,
                                       final int[] paramTypes,
                                       final Object[] params,
                                       final boolean[] nullableParams,
                                       final int[] returnColumnTypes) {
        return runFunction(
                function,
                paramTypes, params, nullableParams,
                returnColumnTypes, true
        );
    }

    static Object runScalarFunction(final String function,
                                    final int[] paramTypes,
                                    final Object[] params,
                                    final boolean[] nullableParams,
                                    final int returnType) {
        var output = runFunction(
                function,
                paramTypes, params, nullableParams,
                new int[]{returnType}, false
        );

        return output == null ? null : // Propagate severe failure report from runFunction
               output[0] == PARAM_FAIL || output[0] == RETURN_FAIL ? output : // Propagate param/return type fail w/ ind
               output[0][0]; // No failures, return scalar result
    }

    // Return values:
    //      null to indicate a severe failure while connecting, preparing
    //          the params, or reading the results;
    //      new Object[][]{PARAM_FAIL, new Object[]{i}} to indicate the
    //          argument at the 0-based index i has an invalid value;
    //      new Object[][]{RETURN_FAIL, new Object[]{i}} to indicate the
    //          column at the 0-based index i was given an invalid type by
    //          client code or cannot store a value retrieved for that column;
    //      The result(s) returned from the database when none of the above
    //          failure cases are met. For table-valued functions, the returned
    //          Object[][] is the constructed/retrieved table, and for
    //          scalar-valued functions, the result is returned in output[0][0]
    private static Object[][] runFunction(final String functionName,
                                          final int[] paramTypes,
                                          final Object[] params,
                                          final boolean[] nullableParams,
                                          final int[] returnColumnTypes,
                                          final boolean tableValued) {
        try (Connection con = dataSource.getConnection()) {
            int[] paramInd = new int[]{-1}; // Keep parameter index to report first invalid param value
            int[] returnColInd = new int[]{-1}; // Keep return column index to report first invalid column type

            try (PreparedStatement stmt = con.prepareStatement(
                    tableValued ? "SELECT * FROM " + functionName :
                                  "SELECT " + functionName,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, // Forward or backward scroll (for counting rows)
                    ResultSet.CONCUR_READ_ONLY // Only for data retrieval
            )) {
                setFunctionParams(paramInd, stmt, paramTypes, params, nullableParams);

                // Return results and automatically close connection (without
                // making connection persist until client finishes its
                // arbitrary processing)
                return retrieveFunctionResults(returnColInd, stmt, returnColumnTypes);
            } catch (ClassCastException | IllegalArgumentException e) { // Incl. NumberFormatException from BigDecimal
                // A conversion has failed or a param has an otherwise invalid
                // value (bad type, bad range, etc.). Report first param with
                // invalid value
                return new Object[][]{PARAM_FAIL, new Object[]{paramInd[0]}};
            } catch (IllegalStateException e) {
                // Bad type provided for a column of return
                return new Object[][]{RETURN_FAIL, new Object[]{paramInd[0]}};
            } catch (SQLException e) {
                if (paramInd[0] != -1) // Failure during param parsing
                    return new Object[][]{PARAM_FAIL, new Object[]{paramInd[0]}}; // As above, report first param with invalid value
                if (returnColInd[0] != -1) // Failure during result parsing
                    return new Object[][]{RETURN_FAIL, new Object[]{paramInd[0]}};

                // Exception raised before or after param parsing
                ProgramDirectoryManager.logError(
                        e,
                        "Could not call function " + functionName +
                                " with arguments " + Arrays.toString(params),
                        true
                );
                return null; // Query failed for some other reason
            }
        } catch (SQLException e) {
            ProgramDirectoryManager.logError(
                    e, "Could not establish a connection to the database", true
            );

            // Unknown connection failure, not necessarily permanent, so
            // application not forcibly closed.
            return null;
        }
    }

    private static void setFunctionParams(final int[] paramInd,
                                          final PreparedStatement stmt,
                                          final int[] paramTypes,
                                          final Object[] params,
                                          final boolean[] nullableParams)
            throws SQLException, IllegalArgumentException, ClassCastException {
        for (int i = 0; i < paramTypes.length; i++) {
            paramInd[0] = i;

            // Allow user to set certain params to null; client code must
            // specify which params are nullable to prevent user error from
            // propagating
            if (params[i] == null) {
                if (!nullableParams[i]) // This param cannot be null
                    throw new IllegalArgumentException();

                stmt.setNull(i + 1, paramTypes[i]);
            }

            // Set next param with given nonnull value, requiring value to be
            // convertible to intended type. Otherwise, ClassCastException,
            // IllegalArgumentException (also accounting for
            // NumberFormatException), or SQLException thrown.
            //
            // Switch used instead of stmt.setObject(ind, obj, type) to avoid
            // AssertionError thrown by setObject if given invalid type.
            //
            // Convert param indices from Java's 0-based to required 1-based
            switch (paramTypes[i]) {
                case INTEGER -> stmt.setInt(i + 1, (int) params[i]);
                case DECIMAL -> stmt.setBigDecimal(i + 1, new BigDecimal((String) params[i]));
                case NVARCHAR -> stmt.setNString(i + 1, (String) params[i]);
                case CHAR -> stmt.setString(i + 1, "" + (char) params[i]);
                case BOOLEAN -> stmt.setBoolean(i + 1, (boolean) params[i]);
                case BINARY -> stmt.setBytes(i + 1, (byte[]) params[i]);
                case DATE -> stmt.setDate(i + 1, (Date) params[i]);
                default -> throw new IllegalArgumentException(); // Unknown type
            }
        }

        // Params parsed successfully, so subsequent errors are not for param
        // parsing
        paramInd[0] = -1;
    }

    private static Object[][] retrieveFunctionResults(final int[] returnColInd,
                                                      final PreparedStatement stmt,
                                                      final int[] returnColumnTypes)
            throws SQLException, IllegalStateException {
        var results = stmt.executeQuery();
        var output = new Object[results.last() ? results.getRow() : 0]
                               [results.getMetaData().getColumnCount()];
        results.beforeFirst(); // Reset position to start (after change by results.last())

        // Same structure and justifications of switch as given in setFunctionParams
        for (int i = 0; results.next(); i++) {
            for (int j = 0; j < output[0].length; j++) {
                returnColInd[0] = j;

                output[i][j] = switch (returnColumnTypes[j]) {
                    case INTEGER -> results.getInt(j + 1);
                    case DECIMAL -> results.getBigDecimal(j + 1);
                    case NVARCHAR -> results.getNString(j + 1);
                    case CHAR -> {
                        var str = results.getString(j + 1);
                        if (str == null) // May return null, so check to avoid NPE
                            yield null;
                        yield str.charAt(0);
                    }
                    case BOOLEAN -> results.getBoolean(j + 1);
                    case BINARY -> results.getBytes(j + 1);
                    case DATE -> results.getDate(j + 1);
                    default -> throw new IllegalStateException(); // Unknown type
                };

                // For SQL NULL, return Java null instead of default values
                if (results.wasNull())
                    output[i][j] = null;
            }
        }

        // Results read successfully, so subsequent errors are not for result
        // parsing.
        // Resetting returnColInd is not strictly necessary because no code
        // follows the call to this function in runFunction; this is added
        // defensively in case the code is modified in the future
        returnColInd[0] = -1;

        return output;
    }
}
