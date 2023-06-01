package ui.table;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class Table extends JPanel {

    private final JTable table;
    // A separate variable is kept to avoid frequent call and cast from
    // TableModel to DefaultTableModel for
    // ((DefaultTableModel) table.getModel()).addRow(row)
    private final DefaultTableModel tableModel;

    private final String[] columnNames;
    private final boolean separateHeader;

    Table(final int preferredWidth, final int preferredHeight,
          final boolean editable,
          final boolean showHorizontalLines, final boolean showVerticalLines,
          final boolean separateHeader,
          final String[] columnNames,
          final ColumnRenderer... columnRenderers) {
        this.columnNames = columnNames;
        this.separateHeader = separateHeader;

        // Define a table style with the specified column count, column names,
        // and cell editability
        tableModel = new CustomEditabilityTableModel(columnNames, editable);

        // Create a new table of the specified size
        table = createJTable(preferredWidth, preferredHeight);

        // Set the table format as requested
        setFormat(showHorizontalLines, showVerticalLines, columnRenderers);

        // Allow vertical scrolling for the table, and make the table fill this
        // JPanel
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    void addRow(final Object... row) {
        tableModel.addRow(row);
    }

    public void addRows(final Object[][] rows) {
        for (var row : rows)
            tableModel.addRow(row);
    }

    public synchronized void clear() { // Synchronized to prevent header from being removed
        tableModel.setRowCount(0);

        if (!separateHeader)
            tableModel.addRow(columnNames);
    }

    public synchronized void replace(final Object[][] rows) {
        clear();
        addRows(rows);
    }

    public void setStrictColumnWidth(final int col, final int width) {
        table.getColumnModel().getColumn(col).setMinWidth(width);
        table.getColumnModel().getColumn(col).setMaxWidth(width);
    }

    private JTable createJTable(final int preferredWidth, final int preferredHeight) {
        // Set JTable size (usually overridden by used BorderLayout)
        var table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(
                preferredWidth, preferredHeight
        ));
        table.setFillsViewportHeight(true);

        // Set JPanel size (may be overridden, depending on parent specified in
        // client code)
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        return table;
    }

    private void setFormat(final boolean showHorizontalLines, final boolean showVerticalLines,
                           final ColumnRenderer[] columnRenderers) {
        // Disable column dragging
        table.getTableHeader().setReorderingAllowed(false);

        // Enable/disable grid lines
        table.setShowHorizontalLines(showHorizontalLines);
        table.setShowVerticalLines(showVerticalLines);

        // Use specified rendering style for each column.
        // Note: if a cell renderer is not set for a column, DefaultTableModel
        // (through AbstractTableModel) still provides a DefaultTableCellRenderer
        // for that column, matching ColumnFormatFactory's DEFAULT value
        var columnModel = table.getColumnModel();
        for (int i = 0; i < columnRenderers.length; i++)
            columnModel.getColumn(i).setCellRenderer(
                    ColumnRenderer.create(columnRenderers[i])
            );

        // Use built-in header, or force header to use same format as rows
        if (!separateHeader) {
            table.setTableHeader(null);
            tableModel.addRow(columnNames);
        }
    }

    private static class CustomEditabilityTableModel extends DefaultTableModel {

        private final boolean isEditable;

        private CustomEditabilityTableModel(final String[] columnNames,
                                            final boolean editable) {
            super(columnNames, 0);

            isEditable = editable;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return isEditable;
        }
    }
}
