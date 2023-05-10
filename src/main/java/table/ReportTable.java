package table;

public class ReportTable extends Table {
    public ReportTable(final int preferredWidth, final int preferredHeight,
                       final boolean showHorizontalLines, final boolean showVerticalLines,
                       final String[] columnNames,
                       final ColumnRenderer... columnRenderers) {
        super(
                preferredWidth, preferredHeight,
                false, showHorizontalLines, showVerticalLines,
                columnNames, columnRenderers
        );
    }
}
