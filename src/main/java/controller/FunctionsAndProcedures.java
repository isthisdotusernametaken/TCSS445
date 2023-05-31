package controller;

import static java.sql.Types.*;

import static controller.DBManager.getError;
import static controller.DBManager.hasFailed;
import static controller.DBManager.isEmpty;
import static controller.DBManager.runFunctionOrProcedure;
import util.Password;

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
            "more special characters.";
    private static final String OTHER_REGISTER_FAIL =
            "An error occurred while creating your account. The email " +
            "address may have already been used, or the ZIP code may not be " +
            "supported.";
    // Login validation
    private static final String EMAIL_NOT_USED =
            "This email is not associated with an account.";
    private static final String OTHER_LOGIN_FAIL =
            "An error occurred while logging in: ";
    private static final String INCORRECT_PASSWORD =
            "Incorrect password.";

    // Function and procedure signatures
    private static Signature REGISTER_CUSTOMER_SIG;
    private static Signature VIEW_REVIEWS_SIG;
    private static Signature GET_CUSTOMER_AND_SALT_SIG;
    private static Signature VALIDATE_CUSTOMER_SIG;

    // These exceptions prevent the user from accessing the required database
    // functions and procedures, so failures here are unrecoverable. The
    // initialization try-catch structure in Controller handles any kind of
    // exception from this method
    static void initialize()
            throws ArrayIndexOutOfBoundsException, NegativeArraySizeException,
                   NullPointerException, IllegalArgumentException {
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
        GET_CUSTOMER_AND_SALT_SIG = Signature.buildFunc(
                "GetCustomerAndSalt(?)",
                new int[]{NVARCHAR},
                new String[]{"Email Address"},
                new int[]{INTEGER, BINARY},
                true
        );
        VALIDATE_CUSTOMER_SIG = Signature.buildFunc(
                "ValidateCustomer(?, ?)",
                new int[]{INTEGER, BINARY},
                new String[]{"Customer", "Password"}, // External names for user
                new int[]{BOOLEAN},
                false
        );
    }

    // SCENARIOS - START

    // S1
    public static String registerCustomer(final String emailAddress, final String password,
                                          final String firstName, final String lastName,
                                          final String addressLine1, final String addressLine2,
                                          final int zipCode) {
        if (emailAddress == null || !emailAddress.matches(EMAIL_REGEX)) // Require right format
            return INVALID_EMAIL;

        // Generate random salt and hash password if password is right format
        final var saltAndHash = new byte[2][]; // Hash at 0, salt at 1
        if (!Password.isValid(password) || !Password.saltAndHash(password, saltAndHash))
            return INVALID_PASSWORD;

        final var output = runFunctionOrProcedure(REGISTER_CUSTOMER_SIG,
                emailAddress, saltAndHash[0], saltAndHash[1],
                firstName, lastName, addressLine1, addressLine2, zipCode
        );

        return hasFailed(output) ? OTHER_REGISTER_FAIL : SUCCESS;
    }

    // S2


    // S3
    public static Object[][] viewReviews(final int startPos, final int rowCnt,
                                         final int chemID) {
        return runFunctionOrProcedure(VIEW_REVIEWS_SIG, startPos, rowCnt, chemID);
    }

    // S4
    // Message in output[0]
    // If message is SUCCESS, also CustomerID in output[1]
    public static Object[] login(final String emailAddress, final String password) {
        // Check valid email format
        if (emailAddress == null || !emailAddress.matches(EMAIL_REGEX))
            return new Object[]{INVALID_EMAIL};

        // Check customer exists and get ID and salt;
        // If customer does not exist or email cannot be used, indicate the
        // email cannot be used for logging in; otherwise continue with
        // retrieved ID at position 0 and salt at 1
        final var customerIDAndSalt = runFunctionOrProcedure(GET_CUSTOMER_AND_SALT_SIG,
                emailAddress
        );
        if (hasFailed(customerIDAndSalt))
            return new Object[]{
                    getError(customerIDAndSalt).equals("Email Address") ? EMAIL_NOT_USED :
                    OTHER_LOGIN_FAIL + getError(customerIDAndSalt)
            };
        if (isEmpty(customerIDAndSalt))
            return new Object[]{EMAIL_NOT_USED};

        // Check valid password format and hash password with retrieved salt.
        // In a professional application, the system should not explicitly or
        // implicitly leak information about which of the email and password
        // was invalid or about how it is invalid; this would require carefully
        // ensuring runtimes for this method are as close as possible
        // regardless of the success or failure case.
        final var hashAndSalt = new byte[][]{null, (byte[]) customerIDAndSalt[0][1]};
        if (!Password.isValid(password) || !Password.hash(password, hashAndSalt))
            return new Object[]{INVALID_PASSWORD};

        // Check the calculated hash against the customer's stored hash value
        final var validated = runFunctionOrProcedure(VALIDATE_CUSTOMER_SIG,
                customerIDAndSalt[0][0], hashAndSalt[0]
        );
        return hasFailed(validated) || !((boolean) validated[0][0]) ?
               new Object[]{INCORRECT_PASSWORD} : // If call fails or returns false, do not validate
               new Object[]{SUCCESS, customerIDAndSalt[0]}; // If validated, return SUCCESS and CustomerID
    }

    // S5
//    public static Object[] completeTransaction(final int customerID, final BigDecimal taxPercent, final int discountID,
//                                               )

    // S6

    // S7

    // S8

    // S9

    // S10

    // S11

    // S12

    // SCENARIOS - END


    // ANALYTICAL QUERIES - START

    // 4.2
    public static void HighlyRatedFirstTimeAndMinReviewsChemicals(int months, int reviews, int count) {}

    // 4.3
    public static void LargestPurityAmounts(int chemType, int n) {}

    // 4.4
    public static void HighestRatioProductsToReview(int n) {}

    // 4.5
    public static void HighestRecentSpenders(int months, int n) {}

    // 4.6
    public static void HighestProfitProducts(int months, int n) {}

    // 4.7
    public static void HighestRatedDistributorWithMinReviews(int n, int m) {}

    // 4.8
    public static void DistributorHighestAvgRating(double purity, int chemType, int n) {}

    // 4.9
    public static void PercentagePurchaseWDiscounts(int months) {}

    // ANALYTICAL QUERIES - END
}
