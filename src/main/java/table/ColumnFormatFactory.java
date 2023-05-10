package table;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class ColumnFormatFactory {

    // This factory is provided to encapsulate the actual cell renderers used
    // within the table package. External code must use the ColumnRenderer enum
    // to specify a renderer type, ensuring that all used renderers are valid
    // and nonnull
    static TableCellRenderer create(final ColumnRenderer type) {
        return switch (type) {
            case WRAP -> new TextColumnFormat();
            case IMAGE -> new ImageColumnFormat();
            default -> new DefaultTableCellRenderer();
        };
    }
}
