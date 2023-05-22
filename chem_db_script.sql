CREATE DATABASE tomlin_trevor_db;
GO
USE tomlin_trevor_db;
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
    FirstName NVARCHAR(128) NOT NULL,
    LastName NVARCHAR(128) NOT NULL,
    AddressLine1 NVARCHAR(128) NOT NULL,
    AddressLine2 NVARCHAR(128) NOT NULL,
    ZIPCode INT NOT NULL FOREIGN KEY
		REFERENCES ZIPCODE(ZIPCode),
    AccountCreationDate DATE NOT NULL
);

CREATE TABLE DISCOUNT (
    DiscountID INT PRIMARY KEY IDENTITY(0, 1) NOT NULL,
    DiscountName NVARCHAR(128) NOT NULL,
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
    StateOfMatterName NVARCHAR(128) PRIMARY KEY
);

CREATE TABLE MEASUREMENT_UNIT (
    MeasurementUnitName NVARCHAR(128) PRIMARY KEY,
    MeasurementUnitAbbreviation NVARCHAR(10) UNIQUE -- NULL if no abbreviation
);

CREATE TABLE MEASUREMENT_UNIT_APPLICABILITY (
    MeasurementUnitName NVARCHAR(128) FOREIGN KEY
		REFERENCES MEASUREMENT_UNIT(MeasurementUnitName),
    StateOfMatterName NVARCHAR(128) FOREIGN KEY
		REFERENCES STATE_OF_MATTER(StateOfMatterName),
    PRIMARY KEY (MeasurementUnitName, StateOfMatterName)
);

CREATE TABLE CHEMICAL_TYPE (
    ChemicalTypeID INT PRIMARY KEY IDENTITY(0, 1),
    ChemicalName NVARCHAR(128) NOT NULL,
    MeasurementUnitName NVARCHAR(128) NOT NULL,
    StateOfMatterName NVARCHAR(128) NOT NULL,
	FOREIGN KEY (MeasurementUnitName, StateOfMatterName)
		REFERENCES MEASUREMENT_UNIT_APPLICABILITY(MeasurementUnitName, StateOfMatterName),
	CONSTRAINT UQ_Name_Unit_State UNIQUE (ChemicalName, MeasurementUnitName, StateOfMatterName)
);

CREATE TABLE CHEMICAL_QUALITY (
    ChemicalTypeID INT FOREIGN KEY
		REFERENCES CHEMICAL_TYPE(ChemicalTypeID),
    Purity DECIMAL(10, 2),
    CostPerUnit DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (ChemicalTypeID, Purity)
);

CREATE TABLE DISTRIBUTOR (
    DistributorID INT PRIMARY KEY IDENTITY(0, 1),
    DistributorName NVARCHAR(128) NOT NULL UNIQUE
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
    Purity DECIMAL(10, 2) NOT NULL,
    InitialQuantity DECIMAL(10, 2) NOT NULL,
    RemainingQuantity DECIMAL(10, 2) NOT NULL,
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
    Quantity DECIMAL(10, 2) NOT NULL,
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
									@FirstName NVARCHAR(128), @LastName NVARCHAR(128),
									@AddressLine1 NVARCHAR(128), @AddressLine2 NVARCHAR(128), @ZIPCode INT
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
CREATE FUNCTION 


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