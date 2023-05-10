package table;

public class ReportTable extends Table {
    public ReportTable(final int preferredWidth, final int preferredHeight,
                       final boolean showHorizontalLines, final boolean showVerticalLines,
                       final boolean separateHeader,
                       final String[] columnNames,
                       final ColumnRenderer... columnRenderers) {
        super(
                preferredWidth, preferredHeight,
                false, showHorizontalLines, showVerticalLines, separateHeader,
                columnNames, columnRenderers
        );
    }
}
