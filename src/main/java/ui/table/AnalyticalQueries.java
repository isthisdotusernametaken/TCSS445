package ui.table;

import javax.swing.*;
import java.awt.*;
import controller.FunctionsAndProcedures;

public class AnalyticalQueries extends JPanel {
    private final JComboBox<String> dropdown;
    private final JButton submitButton;
    private final JPanel contentPanel;

    public AnalyticalQueries() {
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        String[] options = {
                "Highly Rated First-Time and Min Reviews Chemicals",
                "Largest Purity Amounts",
                "Highest Ratio of Products to Review",
                "Highest Recent Spenders",
                "Highest Profit Products",
                "Highest Rated Distributor with Min Reviews",
                "Distributor Highest Avg Rating",
                "Percentage Purchase with Discounts"
        };
        dropdown = new JComboBox<>(options);
        panel.add(dropdown);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        submitButton = new JButton("Submit");
        panel.add(submitButton);

        add(panel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> {
            String selectedOption = (String) dropdown.getSelectedItem();
            ReportTable reportTable = null;

            if (selectedOption == null)
                return;

            switch (selectedOption) {
                case "Highly Rated First-Time and Min Reviews Chemicals" -> {
                    FunctionsAndProcedures.HighlyRatedFirstTimeAndMinReviewsChemicals(3, 10, 5);
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
                    );
                }
                case "Largest Purity Amounts" -> {
                    FunctionsAndProcedures.LargestPurityAmounts(1, 10);
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Purity", "Total Amount"}
                    );
                }
                case "Highest Ratio of Products to Review" -> {
                    FunctionsAndProcedures.HighestRatioProductsToReview(10);
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Customer ID", "First Name", "Last Name", "Review To Purchase Ratio"}
                    );
                }
                case "Highest Recent Spenders" -> {
                    FunctionsAndProcedures.HighestRecentSpenders(6, 5);
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Customer ID", "First Name", "Last Name", "Total Spent"}
                    );
                }
                case "Highest Profit Products" -> {
                    FunctionsAndProcedures.HighestProfitProducts(6, 5);
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical Name", "Purity", "Distributor Name", "Profit"}
                    );
                }
                case "Highest Rated Distributor with Min Reviews" -> {
                    FunctionsAndProcedures.HighestRatedDistributorWithMinReviews(5, 10);
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Distributor ID", "Distributor Name", "Review Count", "Average Review Score"}
                    );
                }
                case "Distributor Highest Avg Rating" -> {
                    FunctionsAndProcedures.DistributorHighestAvgRating(0.95, 1, 5);
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Distributor ID", "Distributor Name", "Avg Rating"}
                    );
                }
                case "Percentage Purchase with Discounts" -> {
                    FunctionsAndProcedures.PercentagePurchaseWDiscounts(3);
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Percentage With Discount"}
                    );
                }
                default -> {
                }
            }

            if (reportTable != null) {
                contentPanel.removeAll();
                contentPanel.add(reportTable, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Analytical Queries");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new AnalyticalQueries());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}