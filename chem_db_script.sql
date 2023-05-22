CREATE DATABASE tomlin_trevor_db;
GO
USE tomlin_trevor_db;
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

CREATE TABLE ONLINE_TRANSATION (
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
    InitialQuantity DECIMAL(44, 4) NOT NULL,
    RemainingQuantity DECIMAL(44, 4) NOT NULL,
    ShipmentID INT NOT NULL FOREIGN KEY
		REFERENCES SHIPMENT(ShipmentID),
    TotalPurchasePrice DECIMAL(10, 2) NOT NULL,
	FOREIGN KEY (ChemicalTypeID, Purity)
		REFERENCES CHEMICAL_QUALITY(ChemicalTypeID, Purity)
);

CREATE TABLE TRANSACTION_LINE_ITEM (
    TransactionID INT FOREIGN KEY
		REFERENCES [TRANSACTION](TransactionID),
    ChemicalID INT FOREIGN KEY
		REFERENCES CHEMICAL(ChemicalID),
    Quantity DECIMAL(44, 4) NOT NULL,
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

-- 1 (Register)
/* The password hash and salt are to be provided by server-side code, based on
   the password text provided by the user. */
GO
CREATE PROCEDURE RegisterCustomer	@EmailAddress NVARCHAR(320), @PasswordHash BINARY(32), @PasswordSalt BINARY(32),
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


-- 2 (View Products)
GO
CREATE FUNCTION AverageRating	(@ChemicalID INT) -- Helper
RETURNS TABLE AS RETURN (
	SELECT		AVG(R.Stars)
	FROM		REVIEW R
	WHERE		@ChemicalID = R.ChemicalID
);

GO
CREATE FUNCTION PurchaserCount	(@ChemicalID INT) -- Helper
RETURNS TABLE AS RETURN (
	SELECT		SUM(DISTINCT T.CustomerID)
	FROM		TRANSACTION_LINE_ITEM TL, [TRANSACTION] T
	WHERE		T.TransactionID = TL.TransactionID
		AND		@ChemicalID = TL.ChemicalID
);

GO
CREATE FUNCTION SearchProducts	(@ResultsPosition INT, @ResultsCount INT,
								 @ChemicalName STRING,
								 @MinPurity DECIMAL(6, 3), @MaxPurity DECIMAL(6, 3),
								 @StateOfMatter STRING, @Distributor STRING,
								 @FirstSortBy CHAR, @SecondSortBy CHAR, @ThirdSortBy CHAR, @FourthSortBy CHAR,
								 @FirstSortAsc BIT, @SecondSortAsc BIT, @ThirdSortAsc BIT, @FourthSortAsc BIT)
RETURNS TABLE AS RETURN (
	SELECT		CT.ChemicalName, CQ.Purity, CT.StateOfMatterName,
				C.RemainingQuantity, CQ.CostPerUnit,
				M.MeasurementUnitName, M.MeasurementUnitAbbreviation,
				D.DistributorName
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
		CASE WHEN @FirstSortBy = 'R' AND @FirstSortAsc = 1 THEN AverageRating(@ChemicalID) END ASC, -- Rating
		CASE WHEN @FirstSortBy = 'R' AND @FirstSortAsc = 0 THEN AverageRating(@ChemicalID) END DESC,
		CASE WHEN @FirstSortBy = 'N' AND @FirstSortAsc = 1 THEN PurchaserCount(@ChemicalID) END ASC, -- Number of purchasers
		CASE WHEN @FirstSortBy = 'N' AND @FirstSortAsc = 0 THEN PurchaserCount(@ChemicalID) END DESC
	OFFSET @ResultsPosition ROWS
	FETCH NEXT @ResultsCount ROWS ONLY
);



------------------------------
-- Scenarios - End
------------------------------



------------------------------
-- Analytical Queries - Start
------------------------------



------------------------------
-- Analytical Queries - End
------------------------------



------------------------------
-- Example Data - Start
------------------------------



------------------------------
-- Example Data - End
------------------------------



------------------------------
-- Example Queries - Start
------------------------------



------------------------------
-- Example Queries - End
------------------------------