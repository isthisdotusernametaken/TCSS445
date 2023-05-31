package controller;

import static java.sql.Types.*;

import static controller.DBManager.getError;
import static controller.DBManager.hasFailed;
import static controller.DBManager.isEmpty;
import static controller.DBManager.runFunctionOrProcedure;
import static controller.DBManager.TABLE;
import util.Password;

import java.math.BigDecimal;
import java.util.stream.IntStream;

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
    private static Signature REGISTER_CUSTOMER_SIG; // S1 Customer
    private static Signature SEARCH_PRODUCTS_SIG; // S2 Both
    private static Signature VIEW_REVIEWS_SIG; // S3 Both
    private static Signature GET_CUSTOMER_AND_SALT_SIG; // S4 Customer
    private static Signature VALIDATE_CUSTOMER_SIG; // S4 Customer
    private static Signature COMPLETE_TRANSACTION_SIG; // S5 Customer
    private static Signature MARK_TRANSACTION_DELIVERED_SIG; // S6 Employee
    private static Signature VIEW_PURCHASES_SIG; // S7 Both
    private static Signature VIEW_SUBPURCHASES_SIG; // S8 Both
    private static Signature REVIEW_PRODUCT_SIG; // S9 Customer
    private static Signature ADD_DISTRIBUTOR_SIG; // S10 Employee
    private static Signature RECORD_SHIPMENT_PURCHASE_SIG; // S11 Employee
    private static Signature MARK_SHIPMENT_RECEIVED_SIG; // S12 Employee
    private static Signature HIGHLY_RATED_FIRST_TIME_AND_MIN_REVIEWS_CHEMICALS_SIG; // 4.2 Employee ↓ (all analytical queries are employee-mode only)
    private static Signature LARGEST_PURITY_AMOUNTS_SIG; // 4.3
    private static Signature HIGHEST_RATIO_PRODUCTS_TO_REVIEW_SIG; // 4.4
    private static Signature HIGHEST_RECENT_SPENDERS_SIG; // 4.5
    private static Signature HIGHEST_PROFIT_PRODUCTS_SIG; // 4.6
    private static Signature HIGHEST_RATED_DISTRIBUTOR_WITH_MIN_REVIEWS_SIG; // 4.7
    private static Signature DISTRIBUTOR_HIGHEST_AVG_RATING_SIG; // 4.8
    private static Signature PERCENTAGE_PURCHASE_W_DISCOUNTS_SIG; // 4.9

    // These exceptions prevent the user from accessing the required database
    // functions and procedures in a way that would complicate error handling
    // if the application were to continue with partial functionality, so
    // failures here are unrecoverable (for this application, that is; in some
    // professional applications, it may be desirable to launch with partial
    // functionality). The initialization try-catch structure in Controller
    // handles any kind of exception from this method
    static void initialize()
            throws ArrayIndexOutOfBoundsException, NegativeArraySizeException,
            NullPointerException, IllegalArgumentException {
        // For readability, line separation for param types and names and
        // return types match that of the SQL script for these procedures' and
        // functions' params and returns
        REGISTER_CUSTOMER_SIG = Signature.buildProc(
                "RegisterCustomer(?, ?, ?, ?, ?, ?, ?, ?)",
                new int[]{
                        NVARCHAR, BINARY, BINARY,
                        NVARCHAR, NVARCHAR,
                        NVARCHAR, NVARCHAR, INTEGER
                },
                new String[]{
                        "Email Address", "Password", "Password",
                        "First Name", "Last Name",
                        "Address Line 1", "Address Line 2", "ZIP Code"
                }
        );
        SEARCH_PRODUCTS_SIG = Signature.buildFunc(
                "SearchProducts(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new int[]{ // Param types
                        INTEGER, INTEGER, // Since many params, lines match def in SQL script
                        NVARCHAR,
                        DECIMAL, DECIMAL,
                        NVARCHAR, NVARCHAR,
                        CHAR, CHAR, CHAR, CHAR,
                        BOOLEAN, BOOLEAN, BOOLEAN, BOOLEAN
                },
                IntStream.range(3, 16).filter(i -> i != 8 && i != 12).toArray(), // All search criteria except first sort conditions are nullable
                new String[]{ // Param names for user
                        "First Result to Show", "Number of Results to Show",
                        "Chemical",
                        "Min Purity", "Max Purity",
                        "State of Matter", "Distributor",
                        "First Sorter", "Second Sorter", "Third Sorter", "Fourth Sorter",
                        "First Sort Asc.", "Second Sort Asc.", "Third Sort Asc.", "Fourth Sort Asc."
                },
                new int[]{ // Return column types
                        INTEGER, // Since many params, lines match def in SQL script (see
                        NVARCHAR, DECIMAL, NVARCHAR,            // SearchProducts in script)
                        DECIMAL, DECIMAL,
                        NVARCHAR, NVARCHAR,
                        NVARCHAR,
                        DECIMAL, INTEGER
                },
                true
        );
        VIEW_REVIEWS_SIG = Signature.buildFunc(
                "ViewReviews(?, ?, ?)",
                new int[]{
                        INTEGER, INTEGER,
                        INTEGER
                },
                new String[]{
                        "First Result to Show", "Number of Results to Show",
                        "Chemical"
                },
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
        COMPLETE_TRANSACTION_SIG = Signature.buildProc(
                "CompleteTransaction(?, ?, ?, ?, ?, ?, ?)",
                new int[]{
                        INTEGER, DECIMAL, INTEGER,
                        TABLE, BOOLEAN,
                        DECIMAL, DECIMAL
                },
                new int[]{3}, // Provided DiscountID may be null
                new String[]{
                        "Customer", "Tax Percent", "Discount",
                        "Cart", "Online Status",
                        "Subtotal", "Tax Amount"
                },
                new int[]{6, 7} // Subtotal and tax amount as output
        );
        MARK_TRANSACTION_DELIVERED_SIG = Signature.buildProc(
                "MarkTransactionDelivered(?)",
                new int[]{INTEGER},
                new String[]{"Transaction ID"} // Can refer to IDs for employee
        );
    }

    // SCENARIOS - START
    // For readability, for functions/procedures with many parameters, these
    // methods match the parameter names and the organization across lines
    // found in the SQL script

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
    // Result position and count are nonnullable, and first sorting
    // requirements are nonnullable, but all other params may be null
    public static Object[][] searchProducts(final int resultsPosition, final int resultsCount,
                                            final String chemicalName,
                                            final BigDecimal minPurity, final BigDecimal maxPurity,
                                            final String stateOfMatter, final String Distributor,
                                            final char firstSortBy, final Character secondSortBy, final Character thirdSortBy, final Character fourthSortBy,
                                            final boolean firstSortAsc, final Boolean secondSortAsc, final Boolean thirdSortAsc, final Boolean fourthSortAsc) {
        return runFunctionOrProcedure(SEARCH_PRODUCTS_SIG,
                resultsPosition, resultsCount,
                chemicalName,
                minPurity, maxPurity,
                stateOfMatter, Distributor,
                firstSortBy, secondSortBy, thirdSortBy, fourthSortBy,
                firstSortAsc, secondSortAsc, thirdSortAsc, fourthSortAsc
        );
    }

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
    // Returns new Object[]{message} on fail,
    // new Object[]{SUCCESS, Subtotal, TaxAmount} on success
    public static Object[] completeTransaction(final int customerID, final BigDecimal taxPercent, final int discountID,
                                               final TransactionCart cart, final boolean online) {
        if (cart.itemCount() == 0)
            return new Object[]{"At least one item is required for a transaction."};

        var output = runFunctionOrProcedure(COMPLETE_TRANSACTION_SIG,
                customerID, taxPercent, discountID,
                cart, online,
                null, null // I would correct this with more time, but out-mode params still require entries the param array
        );

        if (hasFailed(output))
            return new Object[]{"Transaction could not be completed: " + getError(output)};

        return new Object[]{SUCCESS, output[0][0], output[0][1]};
    }

    // S6
    public static String markTransactionDelivered(final int transactionID) {
        return hasFailed(
                runFunctionOrProcedure(MARK_TRANSACTION_DELIVERED_SIG, transactionID)
        ) ? "Transaction does not exist, was already delivered, or was in-person." : SUCCESS;
    }

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
