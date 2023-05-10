package table;

public class FormTable extends Table {
    public FormTable(final int preferredWidth, final int preferredHeight,
                     final boolean showHorizontalLines, final boolean showVerticalLines,
                     final boolean separateHeader,
                     final String[] columnNames,
                     final ColumnRenderer... columnRenderers) {
        super(
                preferredWidth, preferredHeight,
                true, showHorizontalLines, showVerticalLines, separateHeader,
                columnNames, columnRenderers
        );
    }
}
