package controller;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Types.*;

// Note: With the system properly divided into separate applications (one
// client-side application for customers, one client-side application for
// employees/administrators, and one server-side application), all code for
// interacting with the database should appear in the server-side application,
// The client-side applications should interact with the server-side
// application through a low-privilege interface that only allows operations
// suitable for the security level determined from some validated security
// information that the user provides.
//
// Note: To call a function or procedure, construct a Signature via the factory
// methods in Signature, call runFunctionOrProcedure with that Signature and
// the parameter values, determine whether an error has occurred with
// hasFailed(<output from runFunctionOrProcedure>), and - depending on whether
// an error has occurred - either retrieve the error message with
// getError(<output from runFunctionOrProcedure>) or access the returned values
// according to the format specified in the "Return values" note before
// runFunctionOrProcedure
public class DBManager {

    private static final String CONNECTION_FAIL = "The operation could not be completed";
    private static final String RETURN_FAIL = "The data could not be retrieved from the database";

    // For creating connections
    private static final SQLServerDataSource dataSource = new SQLServerDataSource();

    static void initialize() {
        // Set connection details
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(1433);
        dataSource.setUser("sa"); // In professional project, should come from secure config file
        dataSource.setPassword(""); // " "
        dataSource.setEncrypt("true");
        dataSource.setTrustServerCertificate(true);

        // Create database if it does not exist
        // TODO: ↑

        // Have future connections connect directly to DB instead of only server
        dataSource.setDatabaseName("tomlin_trevor_db");
    }

    static boolean hasFailed(final Object[][] output) {
        // While a returned cell can be null, a returned row is always nonnull,
        // so output[0] == null iff an error condition explicitly handled by
        // runFunctionOrProcedure was met
        return output.length == 1 && output[0] == null;
    }

    static String getError(final Object[][] output) {
        return (String) output[1][0];
    }

    // Return values:
    //      new Object[][]{null, new Object[]{message}} (that is output[0] is
    //          null and output[1][0] is the error message) to indicate a
    //          failure while connecting, preparing the params (including
    //          failures from invalid arg values), executing the statement, or
    //          reading the results (including invalid result types specified
    //          in client code), with a corresponding externally readable error
    //          message.
    //          Ensure output.length != 0 before attempting to read output[0];
    //      The result(s) returned from the database when none of the above
    //          failure cases are met. For table-valued functions, the returned
    //          Object[][] is the constructed/retrieved table (indexed by row
    //          then by table), WHICH MAY HAVE 0 ROWS (so checking the length
    //          of the returned Object[][] is required before checking whether
    //          the first element is null to detect errors); for scalar-valued
    //          functions, the result is returned in output[0][0]; for
    //          procedures, all requested out-mode params are returned as an
    //          Object[] in output[0]
    static Object[][] runFunctionOrProcedure(final Signature sig,
                                             final Object[] params) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = prepareStatement(con, sig.procedure(), sig.call())) {
            // Keep parameter index to report first invalid param value
            int[] paramInd = new int[]{-1};
            // Keep return/return column index to report first invalid return
            // type. For functions, this corresponds to the SQL function's
            // return type(s); for procedures, the return types are the types
            // of the SQL procedure's out-mode params that have been requested
            // in sig.call()
            int[] returnInd = new int[]{-1};

            try {
                setFunctionParams(
                        paramInd, stmt,
                        sig.paramTypes(), params, sig.paramsNullable(), sig.paramsOut(),
                        sig.procedure()
                );

                // Return results and automatically close connection (without
                // making connection persist until client finishes its
                // arbitrary processing)
                return sig.procedure() ?
                        retrieveProcedureResults(
                                returnInd, (CallableStatement) stmt,
                                sig.paramTypes(), sig.outParamIndices()
                        ) :
                        retrieveFunctionResults(
                                returnInd, stmt,
                                sig.returnColumnTypes()
                        );
            } catch (ClassCastException | IllegalArgumentException e) { // Incl. NumberFormatException from BigDecimal
                // A conversion has failed, a param has an otherwise invalid
                // value (bad type, bad range, etc.), or an exception was
                // thrown in the SQL function/procedure. Report first param with
                // invalid value or report that the user's input is invalid
                return paramOrSQLFail(paramInd[0], sig.paramNames());
            } catch (IllegalStateException e) {
                // Bad type provided for a return column/variable
                return returnFail(sig.procedure(), sig.call(), e, returnInd[0]);
            } catch (SQLException e) {
                if (returnInd[0] != -1) // Failure during result parsing
                    return returnFail(sig.procedure(), sig.call(), e, returnInd[0]);

                // Failure during param parsing, or exception raised when
                // processing the statement
                return paramOrSQLFail(paramInd[0], sig.paramNames());
            }
        } catch (SQLException e) {
            // Unknown access failure from getConnection or
            // prepareStatement/prepareCall. Not necessarily permanent, so
            // application not forcibly closed
            ProgramDirectoryManager.logError(
                    e, "Could not establish a connection to the database", true
            );

            return failWithMessage(CONNECTION_FAIL);
        }
    }

    private static PreparedStatement prepareStatement(final Connection con,
                                                      final boolean isProcedure,
                                                      final String call)
            throws SQLException {
        if (isProcedure) // Required in case out-mode params are requested
            return con.prepareCall(
                    call,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

        return con.prepareStatement(
                call,
                ResultSet.TYPE_SCROLL_INSENSITIVE, // Forward or backward scroll (for counting rows)
                ResultSet.CONCUR_READ_ONLY // Returned table readonly
        );
    }

    private static void setFunctionParams(final int[] paramInd,
                                          final PreparedStatement stmt,
                                          final int[] paramTypes,
                                          final Object[] params,
                                          final boolean[] nullableParams,
                                          final boolean[] outParams,
                                          final boolean isProcedure)
            throws SQLException, IllegalArgumentException, ClassCastException {
        var cstmt = isProcedure ? (CallableStatement) stmt : null;

        for (int i = 0; i < paramTypes.length; i++) {
            paramInd[0] = i;

            if (isProcedure && outParams[i])
                cstmt.registerOutParameter(i + 1, paramTypes[i]);
            else
                setInParam(stmt, paramTypes[i], params[i], nullableParams[i], i + 1);
        }

        // Params parsed successfully, so subsequent errors are not for param
        // parsing
        paramInd[0] = -1;
    }

    private static void setInParam(final PreparedStatement stmt,
                                   final int paramType,
                                   final Object param,
                                   final boolean nullable,
                                   final int ind)
            throws SQLException {
        // Allow user to set certain params to null; client code must
        // specify which params are nullable to prevent user error from
        // propagating
        if (param == null) {
            if (!nullable) // This param cannot be null
                throw new IllegalArgumentException();

            stmt.setNull(ind, paramType);
            return;
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
        switch (paramType) {
            case INTEGER -> stmt.setInt(ind, (int) param);
            case DECIMAL -> stmt.setBigDecimal(ind, new BigDecimal((String) param));
            case NVARCHAR -> stmt.setNString(ind, (String) param);
            case CHAR -> stmt.setString(ind, "" + (char) param);
            case BOOLEAN -> stmt.setBoolean(ind, (boolean) param);
            case BINARY -> stmt.setBytes(ind, (byte[]) param);
            case DATE -> stmt.setDate(ind, (Date) param);
            default -> throw new IllegalArgumentException(); // Unknown type
        }
    }

    private static Object[][] retrieveProcedureResults(final int[] returnInd,
                                                       final CallableStatement stmt,
                                                       final int[] paramTypes,
                                                       final int[] outParamIndices)
            throws SQLException, IllegalStateException {
        stmt.execute();

        var output = new Object[outParamIndices.length];
        int i = -1;
        for (int outParamIndex : outParamIndices) {
            i++;
            returnInd[0] = outParamIndex - 1; // outParamIndices uses 1-based indices

            // Same structure and justifications of switch as given in setInParam
            output[i] = switch (paramTypes[outParamIndex - 1]) {
                case INTEGER -> stmt.getInt(outParamIndex);
                case DECIMAL -> stmt.getBigDecimal(outParamIndex);
                case NVARCHAR -> stmt.getNString(outParamIndex);
                case CHAR -> {
                    var str = stmt.getString(outParamIndex);
                    if (str == null) // May return null, so check to avoid NPE
                        yield null;
                    yield str.charAt(0);
                }
                case BOOLEAN -> stmt.getBoolean(outParamIndex);
                case BINARY -> stmt.getBytes(outParamIndex);
                case DATE -> stmt.getDate(outParamIndex);
                default -> throw new IllegalStateException(); // Unknown type
            };

            // For SQL NULL, return Java null instead of default values
            if (stmt.wasNull())
                output[i] = null;
        }

        // Results read successfully, so subsequent errors are not for result
        // parsing.
        // Resetting returnInd is not strictly necessary because no code
        // follows the call to this function in runFunction; this is added
        // defensively in case the code is modified in the future
        returnInd[0] = -1;

        return new Object[][]{output};
    }

    private static Object[][] retrieveFunctionResults(final int[] returnColInd,
                                                      final PreparedStatement stmt,
                                                      final int[] returnColumnTypes)
            throws SQLException, IllegalStateException {
        var results = stmt.executeQuery();
        var output = new Object[results.last() ? results.getRow() : 0]
                               [results.getMetaData().getColumnCount()];
        results.beforeFirst(); // Reset position to start (after change by results.last())

        for (int i = 0; results.next(); i++) {
            for (int j = 0; j < output[0].length; j++) {
                returnColInd[0] = j;

                // Same structure and justifications of switch as given in setInParam
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
        // Resetting returnInd is not strictly necessary because no code
        // follows the call to this function in runFunction; this is added
        // defensively in case the code is modified in the future
        returnColInd[0] = -1;

        return output;
    }

    private static Object[][] paramOrSQLFail(final int paramInd,
                                             final String[] paramNames) {
        // Exception in Java code parsing and supplying params
        if (paramInd != -1)
            return failWithMessage("Invalid " + paramNames[paramInd]);

        // Access exception or exception in SQL code. An access exception
        // should be logged, but the JDBC interface does not provide a means to
        // clearly distinguish SQL exceptions (which are used to consistently
        // indicate invalid param/update conditions for both procedures and
        // functions) and access failures from the execute() or executeQuery()
        // methods. To avoid cluttering the log file, these exceptions are not
        // logged, but a professional application would find or create a way to
        // distinguish these cases and ensure errors are always properly logged
        return failWithMessage(
                "Invalid input. This request is inconsistent with current data."
        );
    }

    private static Object[][] returnFail(final boolean isProcedure, final String call,
                                         final Exception e, final int returnInd) {
        // A return fail indicates an incongruence between the constructed
        // Signature and the corresponding function/procedure in the database
        // and thus must always be logged. Other parts of the application may
        // still work, so the application does not forcibly close
        ProgramDirectoryManager.logError(
                e,
                (isProcedure ? "Return value " : "Value from cell in column ") +
                        (returnInd + 1) + " invalid for call \"" + call + '\"',
                true
        );

        return failWithMessage(RETURN_FAIL);
    }

    private static Object[][] failWithMessage(final String message) {
        return new Object[][]{null, new Object[]{message}};
    }
}
