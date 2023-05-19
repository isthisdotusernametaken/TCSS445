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
    CustomerID INT PRIMARY KEY,
    EmailAddress NVARCHAR(320),
    PasswordHash VARBINARY(512),
    PasswordSalt VARBINARY(512),
    FirstName NVARCHAR(128),
    LastName NVARCHAR(128),
    AddressLine1 NVARCHAR(128),
    AddressLine2 NVARCHAR(128),
    ZIPCode INT,
    AccountCreationDate DATE,
    FOREIGN KEY (ZIPCode) REFERENCES ZIPCODE(ZIPCode)
    FOREIGN KEY (AddressID) REFERENCES ADDRESS(AddressID)
);

CREATE TABLE DISCOUNT (
    DiscountID INT PRIMARY KEY,
    DiscountName NVARCHAR(128),
    Percentage DECIMAL(5, 2),
    Reusability BIT,
    InitialValidDate DATE,
    ExpirationDate DATE
);

CREATE TABLE [TRANSACTION] (
    TransactionID INT PRIMARY KEY,
    CustomerID INT,
    PurchaseDate DATE,
    TaxAmount DECIMAL(10, 2),
    DiscountID INT,
    FOREIGN KEY (CustomerID) REFERENCES CUSTOMER(CustomerID),
    FOREIGN KEY (DiscountID) REFERENCES DISCOUNT(DiscountID)
);

CREATE TABLE ONLINE_TRANSATION (
    TransactionID INT PRIMARY KEY,
    ReceiveDate DATE,
    FOREIGN KEY (TransactionID) REFERENCES [TRANSACTION](TransactionID)
);

CREATE TABLE STATE_OF_MATTER (
    StateOfMatterName NVARCHAR(128) PRIMARY KEY
);

CREATE TABLE MEASUREMENT_UNIT (
    MeasurementUnitName NVARCHAR(128) PRIMARY KEY,
    MeasurementUnitAbbreviation NVARCHAR(10)
);

CREATE TABLE MEASUREMENT_UNIT_APPLICABILITIES (
    MeasurementUnitName NVARCHAR(128),
    StateOfMatterName NVARCHAR(128),
    PRIMARY KEY (MeasurementUnitName, StateOfMatterName),
    FOREIGN KEY (MeasurementUnitName) REFERENCES MEASUREMENTUNIT(MeasurementUnitName),
    FOREIGN KEY (StateOfMatterName) REFERENCES STATEOFMATTER(StateOfMatterName)
);

CREATE TABLE CHEMICAL_TYPE (
    ChemicalTypeID INT PRIMARY KEY,
    ChemicalName NVARCHAR(128),
    MeasurementUnitName NVARCHAR(128),
    StateOfMatterName NVARCHAR(128),
    FOREIGN KEY (MeasurementUnitName) REFERENCES MEASUREMENTUNIT(MeasurementUnitName),
    FOREIGN KEY (StateOfMatterName) REFERENCES STATEOFMATTER(StateOfMatterName)
);

CREATE TABLE CHEMICAL_QUALITY (
    ChemicalTypeID INT,
    Purity DECIMAL(10, 2),
    CostPerUnit DECIMAL(10, 2),
    PRIMARY KEY (ChemicalTypeID),
    FOREIGN KEY (ChemicalTypeID) REFERENCES CHEMICALTYPE(ChemicalTypeID)
);

CREATE TABLE DISTRIBUTOR (
    DistributorID INT PRIMARY KEY,
    DistributorName NVARCHAR(128)
);

CREATE TABLE SHIPMENT (
    ShipmentID INT PRIMARY KEY,
    DistributorID INT,
    PurchaseDate DATE,
    ReceiveDate DATE,
    FOREIGN KEY (DistributorID) REFERENCES DISTRIBUTOR(DistributorID)
);

CREATE TABLE CHEMICAL (
    ChemicalID INT PRIMARY KEY,
    ChemicalTypeID INT,
    Purity DECIMAL(10, 2),
    InitialQuantity DECIMAL(10, 2),
    RemainingQuantity DECIMAL(10, 2),
    ShipmentID INT,
    TotalPurchasePrice DECIMAL(10, 2),
    FOREIGN KEY (ChemicalTypeID) REFERENCES CHEMICALTYPE(ChemicalTypeID),
    FOREIGN KEY (ShipmentID) REFERENCES SHIPMENT(ShipmentID)
);

CREATE TABLE TRANSACTION_LINE_ITEM (
    TransactionLineItemID INT PRIMARY KEY,
    TransactionID INT,
    ChemicalID INT,
    Quantity DECIMAL(10, 2),
    CostPerUnitWhenPurchased DECIMAL(10, 2),
    FOREIGN KEY (TransactionID) REFERENCES [TRANSACTION](TransactionID),
    FOREIGN KEY (ChemicalID) REFERENCES CHEMICAL(ChemicalID)
);

CREATE TABLE REVIEW (
    ReviewID INT PRIMARY KEY,
    TransactionID INT,
    ChemicalID INT,
    Stars INT,
    Text VARCHAR(1000),
    ReviewDate DATE,
    FOREIGN KEY (TransactionID) REFERENCES [TRANSACTION](TransactionID),
    FOREIGN KEY (ChemicalID) REFERENCES CHEMICAL(ChemicalID)
);


------------------------------
-- Tables - End
------------------------------



------------------------------
-- Scenarios - Start
------------------------------

CREATE PROCEDURE RegisterCustomer @EmailAddress NVARCHAR(320), @PasswordHash , @FirstName NVARCHAR(128), @LastName NVARCHAR(128), @AddressLine1 NVARCHAR(128), @AddressLine2 NVARCHAR(128), @ZIPCode INT

------------------------------
-- Scenarios - End
------------------------------



------------------------------
-- Analytical Queries - Start
------------------------------

------------------------------
-- Analytical Queries - End
------------------------------
