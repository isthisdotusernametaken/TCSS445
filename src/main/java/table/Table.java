package table;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;

public class Table extends JPanel {

    private final DefaultTableModel tableModel;

    Table(final int preferredWidth, final int preferredHeight,
          final boolean editable,
          final boolean showHorizontalLines, final boolean showVerticalLines,
          final String[] columnNames,
          final ColumnRenderer... columnRenderers) {
        // Define a table style with the specified column count, column names,
        // and cell editability
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };

        // Create a new table with the specified parameters
        var table = new JTable(tableModel);
        table.setShowHorizontalLines(showHorizontalLines);
        table.setShowVerticalLines(showVerticalLines);
        table.setPreferredScrollableViewportSize(new Dimension(
                preferredWidth, preferredHeight
        ));
        table.setFillsViewportHeight(true);

        // Use specified rendering style for each column
        var columnModel = table.getColumnModel();
        for (int i = 0; i < columnRenderers.length; i++)
            columnModel.getColumn(i).setCellRenderer(
                    ColumnFormatFactory.create(columnRenderers[i])
            );

        // Allow vertical scrolling for the table
        add(new JScrollPane(table));
    }

    public void addRow(final Object... row) {
        tableModel.addRow(row);
    }
}
