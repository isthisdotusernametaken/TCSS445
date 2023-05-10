package table;

import javax.swing.JTable;
import java.awt.Component;

public class TableUtil {

    static void autoFormatRowHeight(final JTable table, final int row,
                                    final Component determiningComponent) {
        int preferredHeight = determiningComponent.getPreferredSize().height;
        if (preferredHeight > table.getRowHeight(row))
            table.setRowHeight(row, preferredHeight);
    }
}
