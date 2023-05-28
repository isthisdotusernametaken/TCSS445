package ui.table;

import javax.swing.JTable;
import java.awt.Component;

public class TableUtil {

    static void autoFormatRowHeight(final JTable table, final int row,
                                    final Component determiningComponent) {
        // Updating the row height only if it is currently too small ensures
        // that the row height will be sufficient for the tallest cell in the
        // row, regardless of the order in which cells call this method.
        int preferredHeight = determiningComponent.getPreferredSize().height;
        if (preferredHeight > table.getRowHeight(row))
            table.setRowHeight(row, preferredHeight);
    }
}
