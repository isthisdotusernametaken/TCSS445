package table;

import java.util.stream.IntStream;

import static table.ColumnRenderer.IMAGE;
import static table.ColumnRenderer.WRAP;
import util.Resources;

public class ReviewsReport extends ReportTable {

    private static final int MAX_STARS = 5;
    private static final Object[][] stars = IntStream.range(1, MAX_STARS + 1).mapToObj(
            i -> IntStream.range(0, i).mapToObj(j -> Resources.star()).toArray()
    ).toArray(Object[][]::new);

    public ReviewsReport(final int preferredWidth, final int preferredHeight) {
        super(
                preferredWidth, preferredHeight,
                false, false, false,
                new String[]{"", "", "", "", "", "Reviewer", "Description"},
                IMAGE, IMAGE, IMAGE, IMAGE, IMAGE, WRAP, WRAP
        );

        setStrictColumnWidth(0, 30);
        setStrictColumnWidth(1, 30);
        setStrictColumnWidth(2, 30);
        setStrictColumnWidth(3, 30);
        setStrictColumnWidth(4, 30);
        setStrictColumnWidth(5, 70);
    }

    public void addReview(final int rating,
                          final String reviewer, final String description) {
        addRow(stars[rating >= 1 && rating <= MAX_STARS ? rating - 1 : 0]);

        addRow("", "", "", "", "", reviewer, description);
        addRow();
        addRow();
    }
}
