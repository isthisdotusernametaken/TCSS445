package table;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class ColumnFormatFactory {

    static TableCellRenderer create(final ColumnRenderer type) {
        return switch (type) {
            case WRAP -> new TextColumnFormat();
            case IMAGE -> new ImageColumnFormat();
            default -> new DefaultTableCellRenderer();
        };
    }
}
