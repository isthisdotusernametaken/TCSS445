package controller;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.INTEGER;

import util.Pair;

// The methods of this class are synchronized (separately for each instance) so
// that UI code can safely interact with these objects through events
public class TransactionCart extends TableValuedParameter {

    // LinkedList since values may be removed frequently (and typically small
    // cart sizes will not cause performance issues related to many get(ind)
    // calls)
    private final List<Integer> chemicalIDs = new LinkedList<>();
    private final List<BigDecimal> quantities = new LinkedList<>();

    public synchronized void addRow(final int chemicalID, final BigDecimal quantity) {
        // quantity must be nonnull, but this is not mishandled in this
        // application. Proper null checking and exception handling would be
        // included in a professional application
        chemicalIDs.add(chemicalID);
        quantities.add(quantity);
    }

    public synchronized Pair<List<Integer>, List<BigDecimal>> getRows() {
        // Return two immutable lists (of immutable items) containing the cart items
        return new Pair<>(List.copyOf(chemicalIDs), List.copyOf(quantities));
    }

    public synchronized void removeItem(final int chemicalID) {
        final int ind = chemicalIDs.indexOf(chemicalID);

        // If ind < 0, the item is already not in the cart, which will only
        // happen if this method is called multiple times for a chemical ID
        // that was originally in the cart. This is possible with event-induced
        // calls but (with proper synchronization, as is used here) is not
        // harmful in any way and does not require reporting
        if (ind >= 0) {
            chemicalIDs.remove(ind);
            quantities.remove(ind);
        }
    }

    public synchronized int itemCount() {
        return chemicalIDs.size();
    }

    synchronized SQLServerDataTable convertToTable() throws SQLException {
        SQLServerDataTable table;
        try {
            table = new SQLServerDataTable();
            table.setTvpName("dbo.TRANSACTIONCART");
            table.addColumnMetadata("ChemicalID", INTEGER);
            table.addColumnMetadata("Quantity", DECIMAL);
        } catch (SQLServerException e) {
            // Only possible if this class is constructed incorrectly or the
            // API changes.
            // If this table is unavailable, other functionality (viewing
            // products, accessing analytical queries, etc.) may be unaffected,
            // so the application is not forcibly closed
            throw new SQLException(
                    "Invalid table-valued parameter structure for TransactionCart",
                    e
            );
        }

        int i = 0;
        try {
            for (; i < chemicalIDs.size(); i++) // Iterators would be more performant here
                table.addRow(chemicalIDs.get(i), quantities.get(i));
        } catch (SQLServerException e) {
            // Failure not inherent to table structure, so this failure is recoverable
            throw new SQLException(
                    "Invalid TransactionCart content: " +
                            "(" + chemicalIDs.get(i) + "," + quantities.get(i) + ")",
                    e
            );
        }

        return table;
    }
}
