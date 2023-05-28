package ui.table;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

class TextColumnFormat extends JTextArea implements TableCellRenderer {

    TextColumnFormat() {
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table,
                                                   final Object value,
                                                   final boolean isSelected,
                                                   final boolean hasFocus,
                                                   final int row, final int column) {
        // Set cell color to match DefaultTableCellRenderer
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Update text area
        setText(value == null ? "" : value.toString());

        // Update text area size and row height
        setSize(table.getColumnModel().getColumn(column).getWidth(), table.getRowHeight(row));
        TableUtil.autoFormatRowHeight(table, row, this);

        return this;
    }
}
