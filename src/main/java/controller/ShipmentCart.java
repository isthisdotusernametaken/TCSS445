package controller;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.INTEGER;

import util.Triple;

// The methods of this class are synchronized (separately for each instance) so
// that UI code can safely interact with these objects through events
public class ShipmentCart extends TableValuedParameter {

    // ArrayList since will have elements added to end frequently and be
    // scanned with get(ind), potentially with large lists (for large shipments)
    private final List<ChemicalQuality> qualities = new ArrayList<>();
    private final List<BigDecimal> quantities = new ArrayList<>();
    private final List<BigDecimal> purchasePrices = new ArrayList<>();

    public synchronized void addRow(final int chemicalTypeID, final BigDecimal purity,
                                    final BigDecimal quantity, final BigDecimal purchasePrice) {
        // BigDecimal values must be nonnull, but this is not mishandled in
        // this application. Proper null checking and exception handling would
        // be included in a professional application
        qualities.add(new ChemicalQuality(chemicalTypeID, purity));
        quantities.add(quantity);
        purchasePrices.add(purchasePrice);
    }

    public synchronized Triple<List<ChemicalQuality>, List<BigDecimal>, List<BigDecimal>> getRows() {
        // Return three immutable lists (of immutable items) containing the cart items
        return new Triple<>(List.copyOf(qualities), List.copyOf(quantities), List.copyOf(purchasePrices));
    }

    public synchronized void removeItem(final ChemicalQuality quality) {
        final int ind = qualities.indexOf(quality);

        // If ind < 0, the item is already not in the cart, which will only
        // happen if this method is called multiple times for a quality
        // that was originally in the cart. This is possible with event-induced
        // calls but (with proper synchronization, as is used here) is not
        // harmful in any way and does not require reporting
        if (ind >= 0) {
            qualities.remove(ind);
            quantities.remove(ind);
            purchasePrices.remove(ind);
        }
    }

    public synchronized int itemCount() {
        return quantities.size();
    }

    synchronized SQLServerDataTable convertToTable() throws SQLException {
        SQLServerDataTable table;
        try {
            table = new SQLServerDataTable();
            table.setTvpName("dbo.SHIPMENTCART");
            table.addColumnMetadata("ChemicalTypeID", INTEGER);
            table.addColumnMetadata("Purity", DECIMAL);
            table.addColumnMetadata("Quantity", DECIMAL);
            table.addColumnMetadata("PurchasePrice", DECIMAL);
        } catch (SQLServerException e) {
            // Only possible if this class is constructed incorrectly or the
            // API changes.
            // If this table is unavailable, other functionality (viewing
            // products, accessing analytical queries, etc.) may be unaffected,
            // so the application is not forcibly closed
            throw new SQLException(
                    "Invalid table-valued parameter structure for ShipmentCart",
                    e
            );
        }

        int i = 0;
        try {
            ChemicalQuality quality;
            for (; i < qualities.size(); i++) {
                quality = qualities.get(i);
                table.addRow(
                        quality.chemicalTypeID, quality.purity,
                        quantities.get(i), purchasePrices.get(i)
                );
            }

        } catch (SQLServerException e) {
            // Failure not inherent to table structure, so this failure is recoverable
            throw new SQLException(
                    "Invalid ShipmentCart content: " +
                            "(" +
                            qualities.get(i).chemicalTypeID + "," + qualities.get(i).purity + "," +
                            quantities.get(i) + "," + purchasePrices.get(i) +
                            ")",
                    e
            );
        }

        return table;
    }

    private record ChemicalQuality(int chemicalTypeID, BigDecimal purity) {}
}
