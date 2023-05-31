package controller;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;

import java.sql.SQLException;

public abstract class TableValuedParameter {
    abstract SQLServerDataTable convertToTable() throws SQLException;
}
