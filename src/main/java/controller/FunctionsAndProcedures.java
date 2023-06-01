package controller;

import java.util.stream.IntStream;
import static java.sql.Types.*;

import static controller.DBManager.getError;
import static controller.DBManager.hasFailed;
import static controller.DBManager.isEmpty;
import static controller.DBManager.query;
import static controller.DBManager.runFunctionOrProcedure;
import static controller.DBManager.TABLE;
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
    // Error messages can be included as constants at the top of the file, as
    // literals where they are used, or in a separate config file. The first
    // two of these options are shown in this example, and in a professional
    // context the person/organization the system is being developed for may
    // have some preference with regard to ease of modification

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
    private static Signature ADD_CHEMICAL_TYPE_SIG; // S13 Employee
    private static Signature ADD_CHEMICAL_QUALITY_SIG; // S14 Employee
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
        // functions' params and returns.
        // Note: if no array specifying which params are nullable is provided,
        // no params will accept null values

        // Scenarios
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
                        BOOLEAN, BOOLEAN, BOOLEAN, BOOLEAN // Boolean is used to handle bit type here
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
        VIEW_PURCHASES_SIG = Signature.buildFunc(
                "ViewPurchases(?, ?, ?, ?)",
                new int[]{
                        INTEGER, INTEGER,
                        INTEGER, BOOLEAN
                },
                new String[]{
                        "First Result to Show", "Number of Results to Show",
                        "Customer", "Sort Newest First"
                },
                new int[]{
                        DATE, DECIMAL, // PurchaseDate, PurchaseTotal,
                        NVARCHAR, DECIMAL, // DiscountName, ... → * find function with same name
                        INTEGER,                    // in SQL script for more details on return *
                        DATE
                },
                true
        );
        VIEW_SUBPURCHASES_SIG = Signature.buildFunc(
                "ViewSubpurchases(?, ?, ?)",
                new int[]{
                        INTEGER, INTEGER,
                        INTEGER
                },
                new String[]{
                        "First Result to Show", "Number of Results to Show",
                        "Transaction"
                },
                new int[]{
                        NVARCHAR, DECIMAL, DECIMAL, NVARCHAR, NVARCHAR,
                        DECIMAL
                },
                true
        );
        REVIEW_PRODUCT_SIG = Signature.buildProc(
                "ReviewProduct(?, ?, ?, ?)",
                new int[]{INTEGER, INTEGER, INTEGER, NVARCHAR},
                new String[]{"Customer", "Chemical", "Rating", "Text"}
        );
        ADD_DISTRIBUTOR_SIG = Signature.buildProc(
                "AddDistributor(?)",
                new int[]{NVARCHAR},
                new String[]{"Distributor Name"}
        );
        RECORD_SHIPMENT_PURCHASE_SIG = Signature.buildProc(
                "RecordShipmentPurchase(?, ?)",
                new int[]{INTEGER, TABLE},
                new String[]{"Distributor", "Shipment Items"}
        );
        MARK_SHIPMENT_RECEIVED_SIG = Signature.buildProc(
                "MarkShipmentReceived(?)",
                new int[]{INTEGER},
                new String[]{"ShipmentID"}
        );
        ADD_CHEMICAL_TYPE_SIG = Signature.buildProc(
                "AddChemicalType(?, ?, ?)",
                new int[]{NVARCHAR, NVARCHAR, NVARCHAR},
                new String[]{"Chemical Name", "Measurement Unit", "State of Matter"}
        );
        ADD_CHEMICAL_QUALITY_SIG = Signature.buildProc(
                "AddChemicalQuality(?, ?, ?)",
                new int[]{INTEGER, DECIMAL, DECIMAL},
                new String[]{"Chemical Type ID", "Purity", "Cost per Unit"}
        );

        // Analytical queries
        HIGHLY_RATED_FIRST_TIME_AND_MIN_REVIEWS_CHEMICALS_SIG = Signature.buildFunc(
                "HighlyRatedFirstTimeAndMinReviewsChemicals(?, ?, ?)",
                new int[]{INTEGER, INTEGER, INTEGER},
                new String[]{"Number of Months", "Min Number of Reviews", "Number of Results"},
                new int[]{INTEGER, NVARCHAR, DECIMAL, DECIMAL}, // ChemicalID, ChemicalName, Purity, AvgRating
                true
        );
        LARGEST_PURITY_AMOUNTS_SIG = Signature.buildFunc(
                "LargestPurityAmounts(?, ?)",
                new int[]{INTEGER, INTEGER},
                new String[]{"Chemical Type ID", "Number of Results"},
                new int[]{DECIMAL, DECIMAL}, // Purity, TotalQuantity
                true
        );
        HIGHEST_RATIO_PRODUCTS_TO_REVIEW_SIG = Signature.buildFunc(
                "HighestRatioProductsToReview(?)",
                new int[]{INTEGER},
                new String[]{"Number of Top Reviewers"},
                new int[]{
                        INTEGER, // CustomerID
                        NVARCHAR, // FirstName
                        NVARCHAR, // LastName
                        INTEGER, // DistinctProductsReviewed
                        INTEGER, // DistinctProductsPurchased
                        DECIMAL // ReviewToPurchaseRatio
                },
                true
        );
        HIGHEST_RECENT_SPENDERS_SIG = Signature.buildFunc(
                "HighestRecentSpenders(?, ?)",
                new int[]{INTEGER, INTEGER},
                new String[]{"Number of Months", "Number of Top Spenders"},
                new int[]{
                        INTEGER, // CustomerID
                        NVARCHAR, // FirstName
                        NVARCHAR, // LastName
                        DECIMAL // TotalSpent
                },
                true
        );
        HIGHEST_PROFIT_PRODUCTS_SIG = Signature.buildFunc(
                "HighestProfitProducts(?, ?)",
                new int[]{INTEGER, INTEGER},
                new String[]{"Number of Months", "Number of Top Products"},
                new int[]{
                        NVARCHAR, // ChemicalName
                        DECIMAL, // Purity
                        NVARCHAR, // DistributorName
                        DECIMAL // Profit
                },
                true
        );
        HIGHEST_RATED_DISTRIBUTOR_WITH_MIN_REVIEWS_SIG = Signature.buildFunc(
                "HighestRatedDistributorWithMinReviews(?, ?)",
                new int[]{INTEGER, INTEGER},
                new String[]{"Min Review Count", "Number of Top Distributors"},
                new int[]{
                        INTEGER, // DistributorID
                        NVARCHAR, // DistributorName
                        INTEGER, // ReviewCount
                        DECIMAL // AvgRating
                },
                true
        );
        DISTRIBUTOR_HIGHEST_AVG_RATING_SIG = Signature.buildFunc(
                "DistributorHighestAvgRating(?, ?, ?)",
                new int[]{DECIMAL, INTEGER, INTEGER},
                new String[]{"Purity", "Chemical Type", "Number of Top Distributors for This Product"},
                new int[]{
                        INTEGER, // DistributorID
                        NVARCHAR, // DistributorName
                        DECIMAL // AvgRating
                },
                true
        );
        PERCENTAGE_PURCHASE_W_DISCOUNTS_SIG = Signature.buildFunc(
                "PercentagePurchaseWDiscounts(?)",
                new int[]{INTEGER},
                new String[]{"Number of Months"},
                new int[]{
                        INTEGER, // TotalPurchases
                        INTEGER, // DiscountedPurchases
                        DECIMAL // PercentageWithDiscount
                },
                true
        );
    }

    // SCENARIOS - START
    // For readability, for functions/procedures with many parameters, these
    // methods match the parameter names and the organization across lines
    // found in the SQL script. General parameter names are also given in the
    // Signature definitions in initialize()
    //
    // Note: Decimal values are provided as Strings to centralize validating
    // BigDecimal strings within DBManager

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
                                            final String minPurity, final String maxPurity,
                                            final String stateOfMatter, final String Distributor,
                                            final char firstSortBy, final Character secondSortBy, final Character thirdSortBy, final Character fourthSortBy,
                                            final boolean firstSortAsc, final Boolean secondSortAsc, final Boolean thirdSortAsc, final Boolean fourthSortAsc) {
        return runFunctionOrProcedure(SEARCH_PRODUCTS_SIG,
                resultsPosition, resultsCount,
                "".equals(chemicalName) ? null : chemicalName, // Convert empty strings to null to ignore missing search parameters
                "".equals(minPurity) ? null : minPurity, "".equals(maxPurity) ? null : maxPurity,
                "".equals(stateOfMatter) ? null : stateOfMatter, "".equals(Distributor) ? null : Distributor,
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
                new Object[]{SUCCESS, customerIDAndSalt[0][0]}; // If validated, return SUCCESS and CustomerID
    }

    // S5
    // Returns new Object[]{message} on fail,
    // new Object[]{SUCCESS, Subtotal, TaxAmount} on success
    public static Object[] completeTransaction(final int customerID, final String taxPercent, final int discountID,
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
    public static Object[][] viewPurchases(final int startPos, final int rowCnt,
                                           final int customerID, final boolean sortNewestFirst) {
        return runFunctionOrProcedure(VIEW_PURCHASES_SIG,
                startPos, rowCnt,
                customerID, sortNewestFirst
        );
    }

    // S8
    public static Object[][] viewSubpurchases(final int startPos, final int rowCnt,
                                              final int transactionID) {
        // With the system properly split into its component applications, when
        // the transaction ID comes from the UI application, it should be
        // validated by the server application before querying the database
        return runFunctionOrProcedure(VIEW_SUBPURCHASES_SIG,
                startPos, rowCnt,
                transactionID
        );
    }

    // S9
    public static String reviewProduct(final int customerID, final int chemicalID, final int stars, final String text) {
        // Validate rating before calling procedure so that any failure very
        // likely to be for product not being acquired by customer
        if (stars < 0 || stars > Controller.MAX_RATING)
            return "Rating out of range.";

        var output = runFunctionOrProcedure(REVIEW_PRODUCT_SIG,
                customerID, chemicalID, stars, text
        );

        return hasFailed(output) ?
               getError(output) + " You may only review products you have purchased and received." :
               SUCCESS;
    }

    // S10
    public static String addDistributor(final String distributorName) {
        return hasFailed(runFunctionOrProcedure(ADD_DISTRIBUTOR_SIG,
                distributorName
        )) ? "A distributor with this name already exists." : SUCCESS;
    }

    // S11
    public static String recordShipmentPurchase(final int distributorID, final ShipmentCart cart) {
        return hasFailed(runFunctionOrProcedure(RECORD_SHIPMENT_PURCHASE_SIG,
                distributorID, cart
        )) ?
                "One or more listed items are invalid. Ensure there are no" +
                        "duplicates, null values, or nonpositive values" :
                SUCCESS;
    }

    // REQUIRED FOR S11
    // Should be called before recordShipmentPurchase so that employee can
    // choose distributor
    public static Object[][] getDistributors() {
        return query("SELECT * FROM DISTRIBUTOR",
                new int[]{INTEGER, NVARCHAR} // ID and name
        );
    }

    // S12
    public static String markShipmentReceived(final int shipmentID) {
        return hasFailed(runFunctionOrProcedure(MARK_SHIPMENT_RECEIVED_SIG,
                shipmentID
        )) ? "The shipment does not exist or was already received." : SUCCESS;
    }

    // REQUIRED FOR S12
    // Should be called before markShipmentReceived so that employee can
    // choose shipment
    public static Object[][] getPendingShipments() {
        return query("SELECT * FROM PENDING_SHIPMENT",
                new int[]{INTEGER, INTEGER, DATE} // ShipmentID, DistributorID, and PurchaseDate
        );
    }

    // S13
    public static String addChemicalType(final String chemicalName, final String measurementUnit, final String stateOfMatter) {
        return hasFailed(runFunctionOrProcedure(ADD_CHEMICAL_TYPE_SIG,
                chemicalName, measurementUnit, stateOfMatter
        )) ?
                "A chemical type with these values already exists, or one of" +
                        " these values is invalid (including nonexistent " +
                        "measurement units and states of matter)." :
                SUCCESS;
    }

    // S14
    public static String addChemicalQuality(final int chemicalTypeID, final String purity, final String costPerUnit) {
        return hasFailed(runFunctionOrProcedure(ADD_CHEMICAL_QUALITY_SIG,
                chemicalTypeID, purity, costPerUnit
        )) ?
                "A chemical quality already exists for this chemical type " +
                        "and purity level, or one of these values is invalid." :
                SUCCESS;
    }

    // Other scenarios

    // For employees to use customer operations
    public static Object[][] getCustomers() {
        return query("SELECT CustomerID, EmailAddress FROM CUSTOMER",
                new int[]{INTEGER, NVARCHAR}
        );
        // For a professional system with many customers, a PreparedStatement
        // could be used here with search parameters to select a few specific
        // customers, or the results could be paged with offsets and row counts
        // for performance and ease of use
    }

    // For viewing measurement units and states to make chemical types
    public static Object[][] getMeasurementUnitApplicabilities() {
        return query("SELECT * FROM MEASUREMENT_UNIT_APPLICABILITY",
                new int[]{NVARCHAR, NVARCHAR}
        );
    }

    // For viewing chemical types to make chemical qualities
    public static Object[][] getChemicalTypes() {
        return query("SELECT * FROM CHEMICAL_TYPE",
                new int[]{INTEGER, NVARCHAR, NVARCHAR, NVARCHAR}
        );
    }

    // For viewing chemical qualities to add items to shipments
    public static Object[][] getChemicalQualities() {
        return query("SELECT * FROM CHEMICAL_QUALITY",
                new int[]{INTEGER, DECIMAL, DECIMAL}
        );
    }


    // SCENARIOS - END


    // ANALYTICAL QUERIES - START

    // 4.2
    public static Object[][] HighlyRatedFirstTimeAndMinReviewsChemicals(int months, int reviews, int count) {
        return runFunctionOrProcedure(HIGHLY_RATED_FIRST_TIME_AND_MIN_REVIEWS_CHEMICALS_SIG,
                months, reviews, count
        );
    }

    // 4.3
    public static Object[][] LargestPurityAmounts(int chemType, int n) {
        return runFunctionOrProcedure(LARGEST_PURITY_AMOUNTS_SIG,
                chemType, n
        );
    }

    // 4.4
    public static Object[][] HighestRatioProductsToReview(int n) {
        return runFunctionOrProcedure(HIGHEST_RATIO_PRODUCTS_TO_REVIEW_SIG,
                n
        );
    }

    // 4.5
    public static Object[][] HighestRecentSpenders(int months, int n) {
        return runFunctionOrProcedure(HIGHEST_RECENT_SPENDERS_SIG,
                months, n
        );
    }

    // 4.6
    public static Object[][] HighestProfitProducts(int months, int n) {
        return runFunctionOrProcedure(HIGHEST_PROFIT_PRODUCTS_SIG,
                months, n
        );
    }

    // 4.7
    public static Object[][] HighestRatedDistributorWithMinReviews(int n, int m) {
        return runFunctionOrProcedure(HIGHEST_RATED_DISTRIBUTOR_WITH_MIN_REVIEWS_SIG,
                n, m
        );
    }

    // 4.8
    public static Object[][] DistributorHighestAvgRating(String purity, int chemType, int n) {
        return runFunctionOrProcedure(DISTRIBUTOR_HIGHEST_AVG_RATING_SIG,
                purity, chemType, n
        );
    }

    // 4.9
    public static Object[][] PercentagePurchaseWDiscounts(int months) {
        return runFunctionOrProcedure(PERCENTAGE_PURCHASE_W_DISCOUNTS_SIG,
                months
        );
    }

    // ANALYTICAL QUERIES - END
}
