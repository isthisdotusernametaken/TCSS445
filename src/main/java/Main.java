import javax.swing.*;
import java.awt.*;

import table.ReviewsReport;
import util.Resources;

public class Main {
    public static void main(String[] args) {
        var frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700, 400));
        frame.setPreferredSize(new Dimension(1000, 500));

        Resources.initialize();
        var table = new ReviewsReport(600, 200);
        table.addReview(1, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        table.addReview(2, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        table.addReview(4, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        table.addReview(5, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");

        frame.add(table);

        frame.setVisible(true);
    }
}