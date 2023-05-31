package ui;

import javax.swing.*;
import java.awt.*;
import controller.FunctionsAndProcedures;
import ui.table.ReportTable;

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

                    JPanel inputPanel = new JPanel();
                    inputPanel.setLayout(new FlowLayout());

                    JTextField monthsField = new JTextField(5);
                    JTextField reviewsField = new JTextField(5);
                    JTextField countField = new JTextField(5);

                    inputPanel.add(new JLabel("Months:"));
                    inputPanel.add(monthsField);
                    inputPanel.add(Box.createHorizontalStrut(15));
                    inputPanel.add(new JLabel("Reviews:"));
                    inputPanel.add(reviewsField);
                    inputPanel.add(Box.createHorizontalStrut(15));
                    inputPanel.add(new JLabel("Count:"));
                    inputPanel.add(countField);

                    int result = JOptionPane.showConfirmDialog(null, inputPanel,
                            "Please Enter Months, Reviews, and Count Values", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {

                        int months;
                        int reviews;
                        int count;

                        try {
                            months = Integer.parseInt(monthsField.getText());
                            reviews = Integer.parseInt(reviewsField.getText());
                            count = Integer.parseInt(countField.getText());
                        }
                        catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(null, "Please enter valid integers for Months, Reviews, and Count");
                            return;
                        }

                        FunctionsAndProcedures.HighlyRatedFirstTimeAndMinReviewsChemicals(months, reviews, count);
                        reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[]{"Chemical ID", "Purity", "Average Rating"}
                        );
                    }

                }
                case "Largest Purity Amounts" -> {

                    JPanel inputPanel = new JPanel();
                    inputPanel.setLayout(new FlowLayout());

                    JTextField chemTypeField = new JTextField(5);
                    JTextField nField = new JTextField(5);

                    inputPanel.add(new JLabel("Chem Type:"));
                    inputPanel.add(chemTypeField);
                    inputPanel.add(Box.createHorizontalStrut(15));
                    inputPanel.add(new JLabel("N:"));
                    inputPanel.add(nField);

                    int result = JOptionPane.showConfirmDialog(null, inputPanel,
                            "Please Enter ChemType and N", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {

                        int chemType;
                        int n;

                        try {
                            chemType = Integer.parseInt(chemTypeField.getText());
                            n = Integer.parseInt(nField.getText());
                        }
                        catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(null, "Please enter valid integers for ChemType and N");
                            return;
                        }

                        FunctionsAndProcedures.LargestPurityAmounts(chemType, n);
                        reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[]{"Purity", "Total Amount"}
                        );
                    }

                }
                case "Highest Ratio of Products to Review" -> {

                    JPanel inputPanel = new JPanel();
                    inputPanel.setLayout(new FlowLayout());

                    JTextField nField = new JTextField(5);

                    inputPanel.add(new JLabel("N:"));
                    inputPanel.add(nField);

                    int result = JOptionPane.showConfirmDialog(null, inputPanel,
                            "Please Enter N", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {

                        int n;

                        try {
                            n = Integer.parseInt(nField.getText());
                        }
                        catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(null, "Please enter valid integers for N");
                            return;
                        }

                        FunctionsAndProcedures.HighestRatioProductsToReview(n);
                        reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[]{"Customer ID", "First Name", "Last Name", "Review To Purchase Ratio"}
                        );
                    }
                    
                }
                case "Highest Recent Spenders" -> {

                    JPanel inputPanel = new JPanel();
                    inputPanel.setLayout(new FlowLayout());

                    JTextField monthsField = new JTextField(5);
                    JTextField nField = new JTextField(5);

                    inputPanel.add(new JLabel("Months:"));
                    inputPanel.add(monthsField);
                    inputPanel.add(Box.createHorizontalStrut(15));
                    inputPanel.add(new JLabel("N:"));
                    inputPanel.add(nField);

                    int result = JOptionPane.showConfirmDialog(null, inputPanel,
                            "Please Enter Months and N", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {

                        int months;
                        int n;

                        try {
                            months = Integer.parseInt(monthsField.getText());
                            n = Integer.parseInt(nField.getText());
                        }
                        catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(null, "Please enter valid integers for Months and N");
                            return;
                        }

                        FunctionsAndProcedures.HighestRecentSpenders(months, n);
                        reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[]{"Customer ID", "First Name", "Last Name", "Total Spent"}
                        );
                    }

                }

                case "Highest Profit Products" -> {

                    JPanel inputPanel = new JPanel();
                    inputPanel.setLayout(new FlowLayout());

                    JTextField monthsField = new JTextField(5);
                    JTextField nField = new JTextField(5);

                    inputPanel.add(new JLabel("Months:"));
                    inputPanel.add(monthsField);
                    inputPanel.add(Box.createHorizontalStrut(15));
                    inputPanel.add(new JLabel("N:"));
                    inputPanel.add(nField);

                    int result = JOptionPane.showConfirmDialog(null, inputPanel,
                            "Please Enter Months and N", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {

                        int months;
                        int n;

                        try {
                            months = Integer.parseInt(monthsField.getText());
                            n = Integer.parseInt(nField.getText());
                        }
                        catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(null, "Please enter valid integers for Months and N");
                            return;
                        }

                        FunctionsAndProcedures.HighestProfitProducts(months, n);
                        reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[]{"Chemical Name", "Purity", "Distributor Name", "Profit"}
                        );
                    }

                }
                case "Highest Rated Distributor with Min Reviews" -> {

                    JPanel inputPanel = new JPanel();
                    inputPanel.setLayout(new FlowLayout());

                    JTextField nField = new JTextField(5);
                    JTextField mField = new JTextField(5);

                    inputPanel.add(new JLabel("N:"));
                    inputPanel.add(nField);
                    inputPanel.add(Box.createHorizontalStrut(15));
                    inputPanel.add(new JLabel("M:"));
                    inputPanel.add(mField);

                    int result = JOptionPane.showConfirmDialog(null, inputPanel,
                            "Please Enter N and M", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {

                        int n;
                        int m;

                        try {
                            n = Integer.parseInt(nField.getText());
                            m = Integer.parseInt(mField.getText());
                        }
                        catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(null, "Please enter valid integers for N and M");
                            return;
                        }

                        FunctionsAndProcedures.HighestRatedDistributorWithMinReviews(n, m);
                        reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[]{"Distributor ID", "Distributor Name", "Review Count", "Average Review Score"}
                        );
                    }
                    
                }
                case "Distributor Highest Avg Rating" -> {

                    JPanel inputPanel = new JPanel();
                    inputPanel.setLayout(new FlowLayout());

                    JTextField purityField = new JTextField(5);
                    JTextField chemTypeField = new JTextField(5);
                    JTextField nField = new JTextField(5);

                    inputPanel.add(new JLabel("Purity:"));
                    inputPanel.add(purityField);
                    inputPanel.add(Box.createHorizontalStrut(15));
                    inputPanel.add(new JLabel("Chem Type:"));
                    inputPanel.add(chemTypeField);
                    inputPanel.add(Box.createHorizontalStrut(15));
                    inputPanel.add(new JLabel("N:"));
                    inputPanel.add(nField);

                    int result = JOptionPane.showConfirmDialog(null, inputPanel,
                            "Please Enter Purity, ChemType and N", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {

                        double purity;
                        int chemType;
                        int n;

                        try {
                            purity = Double.parseDouble(purityField.getText());
                            chemType = Integer.parseInt(chemTypeField.getText());
                            n = Integer.parseInt(nField.getText());
                        }
                        catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(null, "Please enter valid integers for Purity, ChemType and N");
                            return;
                        }

                        FunctionsAndProcedures.DistributorHighestAvgRating(purity, chemType, n);
                        reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[]{"Distributor ID", "Distributor Name", "Avg Rating"}
                        );
                    }
      
                }
                case "Percentage Purchase with Discounts" -> {

                    JPanel inputPanel = new JPanel();
                    inputPanel.setLayout(new FlowLayout());

                    JTextField monthsField = new JTextField(5);

                    inputPanel.add(new JLabel("Months:"));
                    inputPanel.add(monthsField);

                    int result = JOptionPane.showConfirmDialog(null, inputPanel,
                            "Please Enter Months", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {

                        int months;

                        try {
                            months = Integer.parseInt(monthsField.getText());
                        }
                        catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(null, "Please enter valid integers for Months");
                            return;
                        }

                        FunctionsAndProcedures.PercentagePurchaseWDiscounts(months);
                        reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[]{"Percentage With Discount"}
                        );
                    }

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