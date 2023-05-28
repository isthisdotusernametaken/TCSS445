package ui.table;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

public class ImageColumnFormat extends DefaultTableCellRenderer {

    ImageColumnFormat() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof Icon) {
            setText("");
            setIcon((Icon) value);
        } else {
            // Allow non-image values to be represented by strings
            super.setValue(value);
            setIcon(null);
        }
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table,
                                                   final Object value,
                                                   final boolean isSelected,
                                                   final boolean hasFocus,
                                                   final int row, final int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        TableUtil.autoFormatRowHeight(table, row, this);

        return this;
    }
}
