package table;

import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static table.ColumnRenderer.IMAGE;
import static table.ColumnRenderer.WRAP;
import util.Resources;

public class ReviewsReport extends ReportTable {

    private static final int MAX_STARS = 5;
    private static final int REVIEWER_NAME_WIDTH = 70;

    // Pre-generate arrays for star rows, column names, and column renderers
    private static final Object[][] stars = IntStream.range(0, MAX_STARS + 1).mapToObj(
            i -> Collections.nCopies(i, Resources.star()).toArray() // 0 stars, then 1 star, then 2, ...
    ).toArray(Object[][]::new);
    private static final String[] columnNames = Stream.concat(
            Collections.nCopies(MAX_STARS, "").stream(), // Star columns
            Stream.of("Reviewer", "Description") // Reviewer column and description column
    ).toArray(String[]::new);
    private static final ColumnRenderer[] columnRenderers = Stream.concat(
            Collections.nCopies(MAX_STARS, IMAGE).stream(), // Star columns
            Stream.of(WRAP, WRAP) // Reviewer column and description column
    ).toArray(ColumnRenderer[]::new);

    public ReviewsReport(final int preferredWidth, final int preferredHeight) {
        super(
                preferredWidth, preferredHeight,
                false, false, false,
                columnNames, columnRenderers
        );

        for (int i = 0; i < MAX_STARS; i++) // Star columns
            setStrictColumnWidth(i, Resources.STAR_SIZE);
        setStrictColumnWidth(MAX_STARS, REVIEWER_NAME_WIDTH); // Reviewer column
        // Description column resizes to fill remaining space
    }

    public void addReview(final int rating,
                          final String reviewer, final String description) {
        // Delimiter between reviews
        var delimiterLine = new Object[MAX_STARS + 2];
        delimiterLine[MAX_STARS + 1] = "__________________________________________";
        addRow(delimiterLine);

        // Rating
        addRow(rating >= 1 && rating <= MAX_STARS ? stars[rating] : stars[0]);

        // Reviewer name and review description
        var reviewContent = new Object[MAX_STARS + 2];
        reviewContent[MAX_STARS] = reviewer;
        reviewContent[MAX_STARS + 1] = description;
        addRow(reviewContent);

        // Spacing line
        addRow();
    }
}
