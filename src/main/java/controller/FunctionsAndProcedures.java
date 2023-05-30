package controller;

import util.Password;

import java.security.spec.InvalidKeySpecException;
import static java.sql.Types.*;

import static controller.DBManager.getError;
import static controller.DBManager.hasFailed;
import static controller.DBManager.runFunctionOrProcedure;

public class FunctionsAndProcedures {

    // Return value indicating no error
    public static final String SUCCESS = "Success";

    // Registration validation
    private static final String EMAIL_REGEX = ".+@.+\\..+";
    private static final String INVALID_EMAIL =
            "Invalid email. Emails must be of the form <>@<>.<>";
    private static final String INVALID_PASSWORD =
            "Invalid password. Passwords must have 10-30 characters, which " +
            "must and can only include 1 or more lowercase letters, 1 or " +
            "more uppercase letters, 1 or more decimal digits, and 1 or " +
            "more special characters";
    private static final String OTHER_INVALID_INFO =
            "An error occurred while creating your account: ";

    private static Signature REGISTER_CUSTOMER_SIG;
    private static Signature VIEW_REVIEWS_SIG;

    // These exceptions prevent the user from accessing the required database
    // functions and procedures, so failures here are unrecoverable. The
    // initialization try-catch structure in Controller handles any kind of
    // exception from this method
    static void initialize()
            throws NegativeArraySizeException, NullPointerException, IllegalArgumentException {
        REGISTER_CUSTOMER_SIG = Signature.buildProc(
                "RegisterCustomer(?, ?, ?, ?, ?, ?, ?, ?)",
                new int[]{
                        NVARCHAR, BINARY, BINARY, NVARCHAR, NVARCHAR,
                        NVARCHAR, NVARCHAR, INTEGER
                },
                new String[]{
                    "Email Address", "Password", "Password", "First Name", "Last Name",
                    "Address Line 1", "Address Line 2", "ZIP Code"
                }
        );
        VIEW_REVIEWS_SIG = Signature.buildFunc(
                "ViewReviews(?, ?, ?)",
                new int[]{INTEGER, INTEGER, INTEGER},
                new String[]{"First Result to Show", "Number of Results to Show", "Chemical"},
                new int[]{NVARCHAR, NVARCHAR, INTEGER, NVARCHAR, DATE},
                true
        );
    }

    public static String registerCustomer(final String emailAddress, final String password,
                                          final String firstName, final String lastName,
                                          final String addressLine1, final String addressLine2,
                                          final int zipCode) { // ADD PARAM FAILURE MESSAGE HANDLING TO SIGNATURE
        if (emailAddress == null || !emailAddress.matches(EMAIL_REGEX))
            return INVALID_EMAIL;

        final byte[][] saltAndHash; // Salt at 0, hash at 1
        try {
            saltAndHash = Password.saltAndHash(password);
        } catch (NullPointerException | IllegalArgumentException e) {
            ProgramDirectoryManager.logError(e, "Invalid hash input", true);
            return INVALID_PASSWORD;
        } catch (InvalidKeySpecException e) {
            ProgramDirectoryManager.logError(e, "Hash algorithm/parameter failure", false);
            return INVALID_PASSWORD; // Never reached, unrecoverable error
        }

        final var output = runFunctionOrProcedure(REGISTER_CUSTOMER_SIG, new Object[]{
                emailAddress, saltAndHash[1], saltAndHash[0], // Hash before salt
                firstName, lastName, addressLine1, addressLine2, zipCode
        });

        return hasFailed(output) ? OTHER_INVALID_INFO + getError(output) :
               SUCCESS;
    }

    public static Object[][] viewReviews(final int startPos, final int rowCnt,
                                         final int chemID) {
        return runFunctionOrProcedure(VIEW_REVIEWS_SIG, new Object[]{startPos, rowCnt, chemID});
    }
}
