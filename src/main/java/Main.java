import javax.swing.*;
import java.awt.*;

import table.ReviewsReport;
import util.Resources;

public class Main {

    public static void main(String[] args) {
        Resources.initialize();

        var frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700, 400));
        frame.setPreferredSize(new Dimension(1000, 500));
        frame.setLayout(new BorderLayout());

        var table = new ReviewsReport(600, 200);
        table.addReview(1, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        table.addReview(2, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        table.addReview(3, "Joe", "sFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksa ");
        table.addReview(2, "Floe", "sFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksasFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksasFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksasFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksa ");
        table.addReview(4, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        table.addReview(5, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");

        frame.add(table, BorderLayout.NORTH);

        frame.setVisible(true);
    }
}