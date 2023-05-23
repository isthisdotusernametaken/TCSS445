CREATE DATABASE tomlin_trevor_db;
GO
USE tomlin_trevor_db;
GO

DROP TYPE IF EXISTS STRING;
GO
CREATE TYPE STRING FROM NVARCHAR(128);
GO

------------------------------
-- Tables - Start
------------------------------

CREATE TABLE ZIPCODE (
    ZIPCode INT PRIMARY KEY
);

CREATE TABLE CUSTOMER (
    CustomerID INT PRIMARY KEY IDENTITY(0, 1),
    EmailAddress NVARCHAR(320) NOT NULL UNIQUE,
    PasswordHash BINARY(512) NOT NULL,
    PasswordSalt BINARY(512) NOT NULL,
    FirstName STRING NOT NULL,
    LastName STRING NOT NULL,
    AddressLine1 STRING NOT NULL,
    AddressLine2 STRING NOT NULL,
    ZIPCode INT NOT NULL FOREIGN KEY
		REFERENCES ZIPCODE(ZIPCode),
    AccountCreationDate DATE NOT NULL
);

CREATE TABLE DISCOUNT (
    DiscountID INT PRIMARY KEY IDENTITY(0, 1) NOT NULL,
    DiscountName STRING NOT NULL,
    Percentage DECIMAL(5, 2) NOT NULL,
    Reusability BIT NOT NULL,
    InitialValidDate DATE NOT NULL,
    ExpirationDate DATE NOT NULL
);

CREATE TABLE [TRANSACTION] (
    TransactionID INT PRIMARY KEY IDENTITY(0, 1),
    CustomerID INT NOT NULL FOREIGN KEY
		REFERENCES CUSTOMER(CustomerID),
    PurchaseDate DATE NOT NULL,
    TaxAmount DECIMAL(10, 2) NOT NULL,
    DiscountID INT NOT NULL FOREIGN KEY
		REFERENCES DISCOUNT(DiscountID)
);

CREATE TABLE ONLINE_TRANSACTION (
    TransactionID INT PRIMARY KEY FOREIGN KEY
		REFERENCES [TRANSACTION](TransactionID),
    ReceiveDate DATE NOT NULL
);

CREATE TABLE STATE_OF_MATTER (
    StateOfMatterName STRING PRIMARY KEY
);

CREATE TABLE MEASUREMENT_UNIT (
    MeasurementUnitName STRING PRIMARY KEY,
    MeasurementUnitAbbreviation NVARCHAR(10) UNIQUE -- NULL if no abbreviation
);

CREATE TABLE MEASUREMENT_UNIT_APPLICABILITY (
    MeasurementUnitName STRING FOREIGN KEY
		REFERENCES MEASUREMENT_UNIT(MeasurementUnitName),
    StateOfMatterName STRING FOREIGN KEY
		REFERENCES STATE_OF_MATTER(StateOfMatterName),
    PRIMARY KEY (MeasurementUnitName, StateOfMatterName)
);

CREATE TABLE CHEMICAL_TYPE (
    ChemicalTypeID INT PRIMARY KEY IDENTITY(0, 1),
    ChemicalName STRING NOT NULL,
    MeasurementUnitName STRING NOT NULL,
    StateOfMatterName STRING NOT NULL,
	FOREIGN KEY (MeasurementUnitName, StateOfMatterName)
		REFERENCES MEASUREMENT_UNIT_APPLICABILITY(MeasurementUnitName, StateOfMatterName),
	CONSTRAINT UQ_Name_Unit_State UNIQUE (ChemicalName, MeasurementUnitName, StateOfMatterName)
);

CREATE TABLE CHEMICAL_QUALITY (
    ChemicalTypeID INT FOREIGN KEY
		REFERENCES CHEMICAL_TYPE(ChemicalTypeID),
    Purity DECIMAL(6, 3),
    CostPerUnit DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (ChemicalTypeID, Purity)
);

CREATE TABLE DISTRIBUTOR (
    DistributorID INT PRIMARY KEY IDENTITY(0, 1),
    DistributorName STRING NOT NULL UNIQUE
);

CREATE TABLE SHIPMENT (
    ShipmentID INT PRIMARY KEY IDENTITY(0, 1),
    DistributorID INT NOT NULL FOREIGN KEY
		REFERENCES DISTRIBUTOR(DistributorID),
    PurchaseDate DATE NOT NULL,
    ReceiveDate DATE NOT NULL -- Estimated future date if not received, actual past date if received
);

CREATE TABLE CHEMICAL (
    ChemicalID INT PRIMARY KEY IDENTITY(0, 1),
    ChemicalTypeID INT NOT NULL,
    Purity DECIMAL(6, 3) NOT NULL,
    InitialQuantity DECIMAL(38, 4) NOT NULL,
    RemainingQuantity DECIMAL(38, 4) NOT NULL,
    ShipmentID INT NOT NULL FOREIGN KEY
		REFERENCES SHIPMENT(ShipmentID),
    TotalPurchasePrice DECIMAL(10, 2) NOT NULL, -- Purchase cost from distributor
	FOREIGN KEY (ChemicalTypeID, Purity)
		REFERENCES CHEMICAL_QUALITY(ChemicalTypeID, Purity)
);

CREATE TABLE TRANSACTION_LINE_ITEM (
    TransactionID INT FOREIGN KEY
		REFERENCES [TRANSACTION](TransactionID),
    ChemicalID INT FOREIGN KEY
		REFERENCES CHEMICAL(ChemicalID),
    Quantity DECIMAL(38, 4) NOT NULL,
    CostPerUnitWhenPurchased DECIMAL(10, 2) NOT NULL,
	PRIMARY KEY (TransactionID, ChemicalID)
);

CREATE TABLE REVIEW (
    ReviewID INT PRIMARY KEY IDENTITY(0, 1),
    TransactionID INT NOT NULL,
    ChemicalID INT NOT NULL,
    Stars INT NOT NULL,
    Text VARCHAR(1000) NOT NULL,
    ReviewDate DATE NOT NULL,
	FOREIGN KEY (TransactionID, ChemicalID)
		REFERENCES TRANSACTION_LINE_ITEM(TransactionID, ChemicalID),
	CONSTRAINT CHK_Stars_Count CHECK (Stars BETWEEN 0 AND 5)
);

------------------------------
-- Tables - End
------------------------------



------------------------------
-- Scenarios - Start
------------------------------

-- S1 (Register)
/* The password hash and salt are to be provided by server-side code, based on
   the password text provided by the user. */
GO
CREATE OR ALTER PROCEDURE RegisterCustomer	@EmailAddress NVARCHAR(320), @PasswordHash BINARY(32), @PasswordSalt BINARY(32),
											@FirstName STRING, @LastName STRING,
											@AddressLine1 STRING, @AddressLine2 STRING, @ZIPCode INT
AS
	
INSERT INTO CUSTOMER (EmailAddress, PasswordHash, PasswordSalt,
					  FirstName, LastName,
					  AddressLine1, AddressLine2, ZIPCode,
					  AccountCreationDate)
VALUES (@EmailAddress, @PasswordHash, @PasswordSalt,
		@FirstName, @LastName,
		@AddressLine1, @AddressLine2, @ZIPCode,
		GETDATE());


-- S2 (View Products)
GO
CREATE OR ALTER FUNCTION AverageRating	(@ChemicalID INT) -- Helper
RETURNS DECIMAL(6, 3) AS
BEGIN
	RETURN (
		SELECT		AVG(R.Stars) AS AvgRating
		FROM		REVIEW R
		WHERE		@ChemicalID = R.ChemicalID
	);
END

GO
CREATE OR ALTER FUNCTION PurchaserCount	(@ChemicalID INT) -- Helper
RETURNS INT AS
BEGIN
	RETURN (
		SELECT		COUNT(DISTINCT T.CustomerID)
		FROM		TRANSACTION_LINE_ITEM TL, [TRANSACTION] T
		WHERE		T.TransactionID = TL.TransactionID
			AND		@ChemicalID = TL.ChemicalID
	);
END

GO
CREATE OR ALTER FUNCTION SearchProducts	(@ResultsPosition INT, @ResultsCount INT,
										 @ChemicalName STRING,
										 @MinPurity DECIMAL(6, 3), @MaxPurity DECIMAL(6, 3),
										 @StateOfMatter STRING, @Distributor STRING,
										 @FirstSortBy CHAR, @SecondSortBy CHAR, @ThirdSortBy CHAR, @FourthSortBy CHAR,
										 @FirstSortAsc BIT, @SecondSortAsc BIT, @ThirdSortAsc BIT, @FourthSortAsc BIT)
RETURNS TABLE AS RETURN ( -- A product is defined as an entry in the CHEMICAL table
	SELECT		C.ChemicalID, -- To add the product to the cart
				CT.ChemicalName, CQ.Purity, CT.StateOfMatterName,
				C.RemainingQuantity, CQ.CostPerUnit,
				M.MeasurementUnitName, M.MeasurementUnitAbbreviation,
				D.DistributorName,
				[dbo].AverageRating(C.ChemicalID) AS AvgRating, [dbo].PurchaserCount(C.ChemicalID) AS PurchaserCnt
	FROM		CHEMICAL C, CHEMICAL_TYPE CT, CHEMICAL_QUALITY CQ,
				MEASUREMENT_UNIT M, SHIPMENT S, DISTRIBUTOR D
	WHERE		C.ChemicalTypeID = CT.ChemicalTypeID -- Match C and CT
		AND		C.ChemicalTypeID = CQ.ChemicalTypeID AND C.Purity = CQ.Purity -- Match C and CQ
		AND		CT.MeasurementUnitName = M.MeasurementUnitName -- Match CT and M
		AND		C.ShipmentID = S.ShipmentID -- Match C and S
		AND		(@ChemicalName IS NULL OR (CT.ChemicalName LIKE '%' + @ChemicalName + '%')) -- If name given, require it in chem names
		AND		(@MinPurity = @MaxPurity OR (C.Purity BETWEEN @MinPurity AND @MaxPurity)) -- Require purity range
		AND		(@StateOfMatter IS NULL OR (CT.StateOfMatterName = @StateOfMatter)) -- If state given, require match
		AND		(@Distributor IS NULL OR (D.DistributorName = @Distributor)) -- If distributor given, require match
	ORDER BY
		CASE WHEN @FirstSortBy = 'C' AND @FirstSortAsc = 1 THEN CQ.CostPerUnit END ASC, -- Cost
		CASE WHEN @FirstSortBy = 'C' AND @FirstSortAsc = 0 THEN CQ.CostPerUnit END DESC,
		CASE WHEN @FirstSortBy = 'P' AND @FirstSortAsc = 1 THEN CQ.Purity END ASC, -- Purity
		CASE WHEN @FirstSortBy = 'P' AND @FirstSortAsc = 0 THEN CQ.Purity END DESC,
		CASE WHEN @FirstSortBy = 'R' AND @FirstSortAsc = 1 THEN [dbo].AverageRating(C.ChemicalID) END ASC, -- Rating
		CASE WHEN @FirstSortBy = 'R' AND @FirstSortAsc = 0 THEN [dbo].AverageRating(C.ChemicalID) END DESC,
		CASE WHEN @FirstSortBy = 'N' AND @FirstSortAsc = 1 THEN [dbo].PurchaserCount(C.ChemicalID) END ASC, -- Number of purchasers
		CASE WHEN @FirstSortBy = 'N' AND @FirstSortAsc = 0 THEN [dbo].PurchaserCount(C.ChemicalID) END DESC
	OFFSET @ResultsPosition ROWS
	FETCH NEXT @ResultsCount ROWS ONLY
);


-- S3 (View Reviews of Product)
GO
CREATE OR ALTER FUNCTION ViewReviews	(@ResultsPosition INT, @ResultsCount INT,
										 @ChemicalID INT)
RETURNS TABLE AS RETURN (
	SELECT		C.FirstName, C.LastName, R.Stars, R.[Text], R.ReviewDate
	FROM		REVIEW R, [TRANSACTION] T, CUSTOMER C
	WHERE		R.TransactionID = T.TransactionID
		AND		T.CustomerID = C.CustomerID
	ORDER BY	R.ReviewDate DESC
	OFFSET		@ResultsPosition ROWS
	FETCH NEXT	@ResultsCount ROWS ONLY
);

-- S4 (Login)
GO
CREATE OR ALTER FUNCTION GetCustomerAndSalt	(@EmailAddress STRING) -- Called first with the provided email
RETURNS TABLE AS
RETURN (
	SELECT		CustomerID, PasswordSalt
	FROM		CUSTOMER
	WHERE		@EmailAddress = EmailAddress
);

GO
CREATE OR ALTER FUNCTION ValidateCustomer	(@CustomerID INT, @PasswordHash BINARY(32)) -- Called after hashing
RETURNS BIT AS
BEGIN
	RETURN IIF((
			SELECT	PasswordHash
			FROM	CUSTOMER
			WHERE	@CustomerID = CustomerID
		) = @PasswordHash,
		1, 0
	);
END

-- S5 (Complete Transaction)
GO
DROP PROCEDURE IF EXISTS CompleteTransaction;
GO
DROP TYPE IF EXISTS TRANSACTION_CART
GO
CREATE TYPE TRANSACTION_CART AS TABLE (ChemicalID INT, Quantity DECIMAL(38, 4));

GO
CREATE PROCEDURE CompleteTransaction	@CustomerID INT, @TaxAmount DECIMAL(10, 2), @DiscountID INT,
										@Cart TRANSACTION_CART READONLY, @Online BIT
AS
	
INSERT INTO CUSTOMER (EmailAddress, PasswordHash, PasswordSalt,
					  FirstName, LastName,
					  AddressLine1, AddressLine2, ZIPCode,
					  AccountCreationDate)
VALUES (@EmailAddress, @PasswordHash, @PasswordSalt,
		@FirstName, @LastName,
		@AddressLine1, @AddressLine2, @ZIPCode,
		GETDATE());

-- S6 (Mark Delivery Completion)


-- S7 (View Purchases)
/* Note: The server-side application (which, in this basic project, is not
   separated from the UI but would be in practice) stores the current user's
   CustomerID after logging in. The CustomerID should never be sent to the
   client-side application. The CustomerID stored in the server-side
   application for the current user's session should be used here to retrieve
   only that user's purchases. */
GO
CREATE OR ALTER FUNCTION PurchaseTotal	(@TransactionID INT) -- Helper
RETURNS DECIMAL(10, 2) AS
BEGIN
	RETURN (
		SELECT		(T1.TaxAmount + (1.0 - D.[Percentage]) * (
						SELECT	SUM(TL.CostPerUnitWhenPurchased * TL.Quantity) -- Total cost of products in the transaction
						FROM	TRANSACTION_LINE_ITEM TL
						WHERE	TL.TransactionID = @TransactionID
					))
		FROM		[TRANSACTION] T1, DISCOUNT D
		WHERE		T1.TransactionID = @TransactionID
			AND		T1.DiscountID = D.DiscountID
	);
END

GO
CREATE OR ALTER FUNCTION ViewPurchasesWithoutOnlineStatus	(@ResultsPosition INT, @ResultsCount INT,
															 @CustomerID INT, @SortNewestFirst BIT) -- Helper
RETURNS TABLE AS RETURN (
	SELECT		T.PurchaseDate, [dbo].PurchaseTotal(T.TransactionID) AS PurchaseTotal,
				D.DiscountName, D.[Percentage],
				T.TransactionID -- For determining whether the transaction was online and finding subpurchases
	FROM		CUSTOMER C, [TRANSACTION] T, DISCOUNT D
	WHERE		C.CustomerID = T.CustomerID
		AND		T.DiscountID = D.DiscountID
	ORDER BY
		CASE WHEN @SortNewestFirst = 1 THEN T.PurchaseDate END ASC,
		CASE WHEN @SortNewestFirst = 0 THEN T.PurchaseDate END DESC
	OFFSET		@ResultsPosition ROWS
	FETCH NEXT	@ResultsCount ROWS ONLY
);

GO
CREATE OR ALTER FUNCTION ViewPurchases	(@ResultsPosition INT, @ResultsCount INT,
										 @CustomerID INT, @SortAsc BIT)
RETURNS TABLE AS RETURN (
	SELECT		P.PurchaseDate, P.PurchaseTotal,
				P.DiscountName, P.[Percentage],
				P.TransactionID, -- For finding subpurchases
				O.ReceiveDate -- NULL if transaction was in-person, 0 DATE if online and not delivered
	FROM		ViewPurchasesWithoutOnlineStatus(@ResultsPosition, @ResultsCount, @CustomerID, @SortAsc) P
		LEFT OUTER JOIN ONLINE_TRANSACTION O
			ON	P.TransactionID = O.TransactionID
);

-- S8 (View Subpurchases/Line Items)
/* While viewing their purchases, the user can request a detailed description
   of a specific item included in that transaction. */
GO
CREATE OR ALTER FUNCTION ViewSubpurchases	(@ResultsPosition INT, @ResultsCount INT,
											 @TransactionID INT, @SortAsc BIT)
RETURNS TABLE AS RETURN (
	SELECT		CT.ChemicalName, C.Purity, TL.Quantity, M.MeasurementUnitAbbreviation, CT.StateOfMatterName,
				(TL.Quantity * TL.CostPerUnitWhenPurchased) AS Cost,
				D.DistributorName
	FROM		[TRANSACTION] T, TRANSACTION_LINE_ITEM TL,
				CHEMICAL C, CHEMICAL_TYPE CT, MEASUREMENT_UNIT M,
				SHIPMENT S, DISTRIBUTOR D
	WHERE		@TransactionID = T.TransactionID
		AND		T.TransactionID = TL.TransactionID
		AND		TL.ChemicalID = C.ChemicalID
		AND		C.ChemicalTypeID = CT.ChemicalTypeID
		AND		CT.MeasurementUnitName = M.MeasurementUnitName
		AND		C.ShipmentID = S.ShipmentID
		AND		S.DistributorID = D.DistributorID
	ORDER BY	CT.ChemicalName
	OFFSET		@ResultsPosition ROWS
	FETCH NEXT	@ResultsCount ROWS ONLY
);

------------------------------
-- Scenarios - End
------------------------------



------------------------------
-- Analytical Queries - Start
------------------------------

-- 4.1 Find the chemicals that are highly rated and have been purchased in the largest amounts.
GO
CREATE FUNCTION HighlyRatedAndLargeAmtChemicals()
RETURNS TABLE AS RETURN (
	SELECT C.ChemicalID, C.ChemicalTypeID, C.Purity, C.RemainingQuantity, C.TotalPurchasePrice, R.Stars, COUNT(TLI.ChemicalID) AS PurchaseCount
	FROM Chemicals C
	JOIN REVIEW R ON C.ChemicalID = R.ChemicalID
	JOIN TRANSACTION_LINE_ITEM TLI ON C.ChemicalID = TLI.ChemicalID
	GROUP BY C.ChemicalID, C.ChemicalTypeID, C.Purity, C.RemainingQuantity, C.TotalPurchasePrice, R.Stars
	HAVING R.Stars >= 4
	ORDER BY PurchaseCount DESC;
);

-- 4.2 Find the most highly rated new products (available for the first time within the past specified number of months) with a specified minimum number of reviews.
GO
CREATE FUNCTION HighlyRatedFirstTimeAndMinReviewsChemicals(@MONTHS int, @REVIEWS int)
RETURNS TABLE AS RETURN (
	SELECT TOP 5
	    C.ChemicalID,
	    C.ChemicalName,
	    C.Purity,
	    AVG(R.Stars) AS AverageRating
	FROM
	    CHEMICAL C
	JOIN
	    REVIEW R ON C.ChemicalID = R.ChemicalID
	WHERE
	    (SELECT MIN(S.PurchaseDate) FROM SHIPMENT S WHERE S.ShipmentID = C.ShipmentID) >= DATEADD(MONTH, -@MONTHS, GETDATE())
	GROUP BY
	    C.ChemicalID,
	    C.ChemicalName,
	    C.Purity
	HAVING
	    COUNT(R.ReviewID) >= @REVIEWS
	ORDER BY
	    AVG(R.Stars) DESC;
);

-- 4.3 Find which purity levels of a certain type of chemical have been bought in the largest amounts.
GO
CREATE FUNCTION LargestPurityAmounts(@CHEM_TYPE int)
RETURNS TABLE AS RETURN (
	SELECT
	    C.Purity,
	    SUM(TLI.Quantity) AS TotalQuantity
	FROM
	    CHEMICAL C
	JOIN
	    TRANSACTION_LINE_ITEM TLI ON C.ChemicalID = TLI.ChemicalID
	WHERE
	    C.ChemicalTypeID = @CHEM_TYPE
	GROUP BY
	    C.Purity
	ORDER BY
	    TotalQuantity DESC;
);



-- 4.4 Find the customers who have the highest ratio of distinct products reviewed to distinct products purchased.
GO
CREATE FUNCTION HighestRatioProductsToReview(@N int)
RETURNS TABLE AS RETURN (
	SELECT TOP @N
	    C.CustomerID,
	    C.FirstName,
	    C.LastName,
	    COUNT(DISTINCT R.ChemicalID) AS DistinctProductsReviewed,
	    COUNT(DISTINCT TLI.ChemicalID) AS DistinctProductsPurchased,
	    COUNT(DISTINCT R.ChemicalID) * 1.0 / COUNT(DISTINCT TLI.ChemicalID) AS ReviewToPurchaseRatio
	FROM
	    CUSTOMER C
	JOIN
	    REVIEW R ON C.CustomerID = R.CustomerID
	JOIN
	    TRANSACTION_LINE_ITEM TLI ON C.CustomerID = TLI.CustomerID
	GROUP BY
	    C.CustomerID,
	    C.FirstName,
	    C.LastName
	ORDER BY
	    ReviewToPurchaseRatio DESC;
);

-- 4.5 Find the customers who have spent the most on purchases within the past X months (given an integer number of months X).
GO
CREATE FUNCTION HighestRatioProductsToReview(@MONTH int, @N int)
RETURNS TABLE AS RETURN (
	SELECT TOP @N
	    C.CustomerID,
	    C.FirstName,
	    C.LastName,
	    SUM(T.TotalPurchasePrice) AS TotalSpent
	FROM
	    CUSTOMER C
	JOIN
	    TRANSACTION T ON C.CustomerID = T.CustomerID
	WHERE
	    T.PurchaseDate >= DATEADD(MONTH, -@MONTH, GETDATE())
	GROUP BY
	    C.CustomerID,
	    C.FirstName,
	    C.LastName
	ORDER BY
	    SUM(T.TotalPurchasePrice) DESC;
);

-- 4.6 Find the products (distinguishing by chemical type, purity, and distributor) that have made the highest profit (considering the total amount received in purchases and the total amount paid to the distributor for the purchased amounts) within the past X months.
GO
CREATE FUNCTION HighestProfitProducts(@MONTH int, @N int)
RETURNS TABLE AS RETURN (
	SELECT TOP @N
	    CT.ChemicalTypeName,
	    C.Purity,
	    D.DistributorName,
	    SUM(T.TotalPurchasePrice) AS TotalAmountReceived,
	    SUM(TLI.Quantity * TLI.CostPerUnitWhenPurchased) AS TotalAmountPaidToDistributor,
	    SUM(T.TotalPurchasePrice - (TLI.Quantity * TLI.CostPerUnitWhenPurchased)) AS Profit
	FROM
	    CHEMICAL C
	JOIN
	    CHEMICAL_TYPE CT ON C.ChemicalTypeID = CT.ChemicalTypeID
	JOIN
	    DISCTRIBUTOR D ON C.DistributorID = D.DistributorID
	JOIN
	    TRANSACTION T ON C.ChemicalID = T.ChemicalID
	JOIN
	    TRANSACTION_LINE_ITEM TLI ON T.TransactionID = TLI.TransactionID
	WHERE
	    T.PurchaseDate >= DATEADD(MONTH, -@MONTH, GETDATE())
	GROUP BY
	    CT.ChemicalTypeName,
	    C.Purity,
	    D.DistributorName
	ORDER BY
	    Profit DESC;
);

-- 4.7 Find each distributor that has received a specified minimum number of reviews across all of its products and that has received the highest overall average review score across all of its products.
GO
CREATE FUNCTION DistributorWithMinReviews(@N int)
RETURNS TABLE AS RETURN (
	SELECT TOP 1
	    D.DistributorID,
	    D.DistributorName,
	    COUNT(R.ReviewID) AS ReviewCount,
	    AVG(R.Stars) AS AverageReviewScore
	FROM
	    DISTRIBUTOR D
	JOIN
	    CHEMICAL C ON D.DistributorID = C.DistributorID
	LEFT JOIN
	    REVIEW R ON C.ChemicalID = R.ChemicalID
	GROUP BY
	    D.DistributorID,
	    D.DistributorName
	HAVING
	    COUNT(R.ReviewID) >= @N
	ORDER BY
	    AVG(R.Stars) DESC;
);

-- 4.8 Find the distributors that have received the highest average rating for a specified chemical and specified purity level.
GO
CREATE FUNCTION DistributorHighestAvgRating(@PURITY int, @CHEM_TYPE int)
RETURNS TABLE AS RETURN (
	SELECT TOP 1
	    D.DistributorID,
	    D.DistributorName,
	    AVG(R.Stars) AS AverageRating
	FROM
	    DISTRIBUTOR D
	JOIN
	    CHEMICAL C ON D.DistributorID = C.DistributorID
	JOIN
	    REVIEW R ON C.ChemicalID = R.ChemicalID
	WHERE
	    C.ChemicalTypeID = @CHEM_TYPE
	    AND C.Purity = @PURITY
	GROUP BY
	    D.DistributorID,
	    D.DistributorName
	ORDER BY
	    AVG(R.Stars) DESC;
);

-- 4.9 Find what percentage of purchases in the past X months have been made with discounts.
GO
CREATE FUNCTION DistributorHighestAvgRating(@MONTH int)
RETURNS TABLE AS RETURN (
	SELECT
	    COUNT(DISTINCT T.TransactionID) AS TotalPurchases,
	    COUNT(DISTINCT CASE WHEN T.DiscountID IS NOT NULL THEN T.TransactionID END) AS DiscountedPurchases,
	    (COUNT(DISTINCT CASE WHEN T.DiscountID IS NOT NULL THEN T.TransactionID END) * 100.0) / COUNT(DISTINCT T.TransactionID) AS PercentageWithDiscount
	FROM
	    TRANSACTION T
	WHERE
	    T.PurchaseDate >= DATEADD(MONTH, -@MONTH, GETDATE());
);

------------------------------
-- Analytical Queries - End
------------------------------

GO


------------------------------
-- Example Data - Start
------------------------------

-- ZipCode
INSERT INTO ZIPCode (ZIPCode)
VALUES (12345);

INSERT INTO ZIPCode (ZIPCode)
VALUES (23423);

INSERT INTO ZIPCode (ZIPCode)
VALUES (15232);

-- Customer
EXEC RegisterCustomer 'john@example.com', 0x0123456789abcdef0123456789abcdef, 0xfedcba9876543210fedcba9876543210,
                       'John', 'Doe',
                       '123 Main St', 'Apt 4B', 0;

EXEC RegisterCustomer 'jane@example.com', 0xabcdef0123456789abcdef0123456789, 0x3210fedcba9876543210fedcba987654,
                       'Jane', 'Smith',
                       '456 Elm St', 'Apt 7C', 1;

EXEC RegisterCustomer 'alex@example.com', 0x9876543210fedcba9876543210fedcba, 0x6543210fedcba9876543210fedcba987,
                       'Alex', 'Johnson',
                       '789 Oak St', 'Apt 2A', 2;

-- Distributors
INSERT INTO DISTRIBUTOR (DistributorName)
VALUES ('ABC Distributors');

INSERT INTO DISTRIBUTOR (DistributorName)
VALUES ('Chemical Creators');

INSERT INTO DISTRIBUTOR (DistributorName)
VALUES ('Chemistry Inc.');

-- Shipments
INSERT INTO SHIPMENT (DistributorID, PurchaseDate, ReceiveDate)
VALUES ('0', '2023-05-21', '2023-05-22');

INSERT INTO SHIPMENT (DistributorID, PurchaseDate, ReceiveDate)
VALUES ('1', '2023-05-23', '2023-05-24');

INSERT INTO SHIPMENT (DistributorID, PurchaseDate, ReceiveDate)
VALUES ('2', '2023-05-25', '2023-05-26');

-- Discounts
INSERT INTO DISCOUNT (DiscountName, Percentage, Reusability, InitialValidDate, ExpirationDate)
VALUES ('Summer Sale', 20, 1, '2023-06-01', '2023-06-30');

INSERT INTO DISCOUNT (DiscountName, Percentage, Reusability, InitialValidDate, ExpirationDate)
VALUES ('Holiday Special', 15, 0, '2023-12-01', '2023-12-31');

INSERT INTO DISCOUNT (DiscountName, Percentage, Reusability, InitialValidDate, ExpirationDate)
VALUES ('New Year Discount', 10, 1, '2024-01-01', '2024-01-31');

-- Transactions
INSERT INTO [TRANSACTION] (CustomerID, PurchaseDate, TaxAmount, DiscountID)
VALUES ('0', '2023-05-21', 10.50, '0');

INSERT INTO [TRANSACTION] (CustomerID, PurchaseDate, TaxAmount, DiscountID)
VALUES ('1', '2023-05-22', 5.75, '1');

INSERT INTO [TRANSACTION] (CustomerID, PurchaseDate, TaxAmount, DiscountID)
VALUES ('2', '2023-05-23', 8.20, '2');

-- Online Transactions
INSERT INTO ONLINE_TRANSACTION (TransactionID, ReceiveDate)
VALUES ('0', '2023-05-21');

INSERT INTO ONLINE_TRANSACTION (TransactionID, ReceiveDate)
VALUES ('1', '2023-05-22');

INSERT INTO ONLINE_TRANSACTION (TransactionID, ReceiveDate)
VALUES ('2', '2023-05-23');

-- States of Matter
INSERT INTO STATES_OF_MATTER (StateOfMatterName)
VALUES ('Solid');

INSERT INTO STATES_OF_MATTER (StateOfMatterName)
VALUES ('Liquid');

INSERT INTO STATES_OF_MATTER (StateOfMatterName)
VALUES ('Gas');

-- Measurement Units
INSERT INTO MEASUREMENT_UNIT (MeasurementUnitName, MeasurementUnitAbbreviation)
VALUES ('Gram', 'g');

INSERT INTO MEASUREMENT_UNIT (MeasurementUnitName, MeasurementUnitAbbreviation)
VALUES ('Milliliter', 'ml');

INSERT INTO MEASUREMENT_UNIT (MeasurementUnitName, MeasurementUnitAbbreviation)
VALUES ('Kilogram', 'kg');

-- Measurement Unit Applicability
INSERT INTO MEASUREMENT_UNIT_APPLICABILITY (MeasurementUnitName, StateOfMatterName)
VALUES ('Gram', 'Solid');

INSERT INTO MEASUREMENT_UNIT_APPLICABILITY (MeasurementUnitName, StateOfMatterName)
VALUES ('Milliliter', 'Liquid');

INSERT INTO MEASUREMENT_UNIT_APPLICABILITY (MeasurementUnitName, StateOfMatterName)
VALUES ('Kilogram', 'Solid');

-- Chemical Types
INSERT INTO CHEMICAL_TYPE (ChemicalName, MeasurementUnitName, StateOfMatterName)
VALUES ('Acetone', 'Milliliter', 'Liquid');

INSERT INTO CHEMICAL_TYPE (ChemicalName, MeasurementUnitName, StateOfMatterName)
VALUES ('Sodium Chloride', 'Gram', 'Solid');

INSERT INTO CHEMICAL_TYPE (ChemicalName, MeasurementUnitName, StateOfMatterName)
VALUES ('Ethanol', 'Milliliter', 'Liquid');

-- Chemical Qualities
INSERT INTO CHEMICAL_QUALITY (ChemicalTypeID, Purity, CostPerUnit)
VALUES ('0', '99.9%', 5.99);

INSERT INTO CHEMICAL_QUALITY (ChemicalTypeID, Purity, CostPerUnit)
VALUES ('1', '98.5%', 2.99);

INSERT INTO CHEMICAL_QUALITY (ChemicalTypeID, Purity, CostPerUnit)
VALUES ('2', '99.5%', 3.99);

-- Chemicals
INSERT INTO CHEMICAL (ChemicalTypeID, Purity, InitialQuantity, RemainingQuantity, ShipmentID, TotalPurchasePrice)
VALUES ('0', '99.9%', 100, 100, '0', 599.00);

INSERT INTO CHEMICAL (ChemicalTypeID, Purity, InitialQuantity, RemainingQuantity, ShipmentID, TotalPurchasePrice)
VALUES ('1', '98.5%', 200, 150, '1', 449.00);

INSERT INTO CHEMICAL (ChemicalTypeID, Purity, InitialQuantity, RemainingQuantity, ShipmentID, TotalPurchasePrice)
VALUES ('2', '99.5%', 50, 50, '2', 199.50);

-- Transaction Line Item
INSERT INTO TRANSACTION_LINE_ITEM (TransactionID, ChemicalID, Quantity, CostPerUnitWhenPurchased)
VALUES ('0', 0', 5, 10.99);

INSERT INTO TRANSACTION_LINE_ITEM (TransactionID, ChemicalID, Quantity, CostPerUnitWhenPurchased)
VALUES ('1', 1, 2, 7.99);

INSERT INTO TRANSACTION_LINE_ITEM (TransactionID, ChemicalID, Quantity, CostPerUnitWhenPurchased)
VALUES ('2', 2, 10, 15.99);

-- Reviews
INSERT INTO REVIEW (TransactionID, ChemicalID, Stars, Text, ReviewDate)
VALUES ('0', '0', 5, 'Excellent product!', '2023-05-01');

INSERT INTO REVIEW (TransactionID, ChemicalID, Stars, Text, ReviewDate)
VALUES ('1', '1', 4, 'Good quality, but expensive.', '2023-05-05');

INSERT INTO REVIEW (TransactionID, ChemicalID, Stars, Text, ReviewDate)
VALUES ('2', '2', 3, 'Average product, needs improvement.', '2023-05-10');

------------------------------
-- Example Data - End
------------------------------



------------------------------
-- Example Queries - Start
------------------------------



------------------------------
-- Example Queries - End
------------------------------
