package ui.table;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public enum ColumnRenderer {
    DEFAULT,
    WRAP, // Plain text with text wrapping
    IMAGE;

    // This factory is provided to encapsulate the actual TableCellRenderer
    // objects used within the table package. External code must use the
    // ColumnRenderer enum to specify a renderer type, ensuring that all
    // TableCellRenderer objects used in Table are valid and nonnull
    static TableCellRenderer create(final ColumnRenderer type) {
        return type == null ?
                new DefaultTableCellRenderer() :
                switch (type) {
                    case DEFAULT -> new DefaultTableCellRenderer();
                    case WRAP -> new TextColumnFormat();
                    case IMAGE -> new ImageColumnFormat();
                };
    }
}
