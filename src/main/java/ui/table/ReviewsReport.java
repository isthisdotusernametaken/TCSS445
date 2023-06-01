package ui.table;

import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import util.Resources;
import util.Util;
import static controller.Controller.MAX_RATING;

public class ReviewsReport extends ReportTable {

    private static final int REVIEWER_NAME_WIDTH = 70;

    // Pre-generate arrays for star rows, column names, and column renderers
    private static final Object[][] stars = IntStream.range(0, MAX_RATING + 1).mapToObj(
            i -> Collections.nCopies(i, Resources.star()).toArray() // 0 stars, then 1 star, then 2, ...
    ).toArray(Object[][]::new);
    private static final String[] columnNames = Stream.concat(
            Collections.nCopies(MAX_RATING, "").stream(), // Star columns
            Stream.of("Reviewer", "Description") // Reviewer column and description column
    ).toArray(String[]::new);
    private static final ColumnRenderer[] columnRenderers = Stream.concat(
            Collections.nCopies(MAX_RATING, ColumnRenderer.IMAGE).stream(), // Star columns
            Stream.of(ColumnRenderer.WRAP, ColumnRenderer.WRAP) // Reviewer column and description column
    ).toArray(ColumnRenderer[]::new);

    public ReviewsReport(final int preferredWidth, final int preferredHeight) {
        super(
                preferredWidth, preferredHeight,
                false, false, false,
                columnNames, columnRenderers
        );

        for (int i = 0; i < MAX_RATING; i++) // Star columns
            setStrictColumnWidth(i, Resources.STAR_SIZE);
        setStrictColumnWidth(MAX_RATING, REVIEWER_NAME_WIDTH); // Reviewer column
        // Description column resizes to fill remaining space
    }

    public void setReviews(final Object[][] reviews) {
        clear();
        for (var review : reviews)
            addReview((int) review[2], "" + review[0] + " " + review[1], "" + review[3]);
    }

    public void addReview(final int rating,
                          final String reviewer, final String description) {
        // Delimiter between reviews
        var delimiterLine = new Object[MAX_RATING + 2];
        delimiterLine[MAX_RATING + 1] = "__________________________________________";
        addRow(delimiterLine);

        // Rating (clamped to range of valid star counts)
        addRow(stars[Util.clamp(rating, 0, MAX_RATING)]);

        // Reviewer name and review description
        var reviewContent = new Object[MAX_RATING + 2];
        reviewContent[MAX_RATING] = reviewer;
        reviewContent[MAX_RATING + 1] = description;
        addRow(reviewContent);

        // Spacing line
        addRow();
    }
}
