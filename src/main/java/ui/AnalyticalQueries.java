package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static controller.DBManager.getError;
import static controller.DBManager.hasFailed;
import controller.FunctionsAndProcedures;
import ui.table.ReportTable;

public class AnalyticalQueries extends JPanel {

    private final JComboBox<String> dropdown;
    private final JPanel contentPanel;
    private final JPanel[] queryPanels = new JPanel[8];
    private final ArrayList<ArrayList<JTextField>> queryInputFields = new ArrayList<>(8);
    private final ReportTable[] queryTables = new ReportTable[8];

    public AnalyticalQueries() {
        // Initialize lists for input fields
        for (int i = 0; i < queryPanels.length; i++)
            queryInputFields.add(new ArrayList<>());
        // Initialize query panels
        createQuery0Panel();
        createQuery1Panel();
        createQuery2Panel();
        createQuery3Panel();
        createQuery4Panel();
        createQuery5Panel();
        createQuery6Panel();
        createQuery7Panel();

        // Build UI
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        String[] options = {
                "Most-highly rated new products (with min review count)", // 4.2
                "Most-purchased purity levels of a chemical type", // 4.3
                "Customers with highest ratio of distinct product purchases to distinct products reviewed", // 4.4
                "Highest recent spenders", // 4.5
                "Highest-profit products", // 4.6
                "Highest-rated distributors (with min review count)", // 4.7
                "Distributors with highest average rating for a certain chemical type and purity level", // 4.8
                "Percentage of recent purchases made with discounts" // 4.9
        };

        dropdown = new JComboBox<>(options);
        panel.add(dropdown);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JButton submitButton = new JButton("Submit");
        panel.add(submitButton);

        add(panel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> {
            int selected = dropdown.getSelectedIndex();

            Object[][] resultTable = switch (selected) {
                case 0 -> query0Input(); // 4.2
                case 1 -> query1Input(); // 4.3
                case 2 -> query2Input(); // 4.4
                case 3 -> query3Input(); // 4.5
                case 4 -> query4Input(); // 4.6
                case 5 -> query5Input(); // 4.7
                case 6 -> query6Input(); // 4.8
                case 7 -> query7Input(); // 4.9
                default -> null; // No selection
            };

            if (resultTable != null) {
                ReportTable reportTable = queryTables[selected];
                if (hasFailed(resultTable)) {
                    UIUtil.showError(getError(resultTable));
                }
                else {
                    // Update the display with the query results
                    reportTable.clear();
                    reportTable.addRows(resultTable);
                }

                contentPanel.removeAll();
                contentPanel.add(reportTable, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });
    }

    private void createQuery0Panel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JTextField monthsField = new JTextField(5); // Text field 0
        JTextField reviewsField = new JTextField(5); // Text field 1
        JTextField countField = new JTextField(5); // Text field 2

        inputPanel.add(new JLabel("Months:"));
        inputPanel.add(monthsField);
        inputPanel.add(Box.createHorizontalStrut(15));
        inputPanel.add(new JLabel("Reviews:"));
        inputPanel.add(reviewsField);
        inputPanel.add(Box.createHorizontalStrut(15));
        inputPanel.add(new JLabel("Count:"));
        inputPanel.add(countField);

        queryPanels[0] = inputPanel;
        queryInputFields.get(0).add(monthsField);
        queryInputFields.get(0).add(reviewsField);
        queryInputFields.get(0).add(countField);
        queryTables[0] = new ReportTable(
                10, 10, // Not used here
                true, true, true,
                new String[]{"Chemical ID", "Chemical Name", "Purity", "Average Rating"}
        );
    }

    private Object[][] query0Input() {
        int result = JOptionPane.showConfirmDialog(null, queryPanels[0],
                "Please Enter Months, Reviews, and Count Values", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int months, reviews, count;

            try {
                months = Integer.parseInt(queryInputFields.get(0).get(0).getText());
                reviews = Integer.parseInt(queryInputFields.get(0).get(1).getText());
                count = Integer.parseInt(queryInputFields.get(0).get(2).getText());
            }
            catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(null, "Please enter valid integers for Months, Reviews, and Count");
                return null; // Do not update the display with a new table
            }

            return FunctionsAndProcedures.HighlyRatedFirstTimeAndMinReviewsChemicals(months, reviews, count);
        }

        return null; // Do not update the display with a new table
    }

    private void createQuery1Panel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JTextField chemTypeField = new JTextField(5); // Text field 0
        JTextField nField = new JTextField(5); // Text field 1

        inputPanel.add(new JLabel("Chem Type:"));
        inputPanel.add(chemTypeField);
        inputPanel.add(Box.createHorizontalStrut(15));
        inputPanel.add(new JLabel("Number of Results To Get:"));
        inputPanel.add(nField);

        queryPanels[1] = inputPanel;
        queryInputFields.get(1).add(chemTypeField);
        queryInputFields.get(1).add(nField);
        queryTables[1] = new ReportTable(
                10, 10, // Not used here
                true, true, true,
                new String[]{"Purity", "Total Amount"}
        );
    }

    private Object[][] query1Input() {
        int result = JOptionPane.showConfirmDialog(null, queryPanels[1],
                "Please Enter Chem Type and Number of Results To Get", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int chemType, n;

            try {
                chemType = Integer.parseInt(queryInputFields.get(1).get(0).getText());
                n = Integer.parseInt(queryInputFields.get(1).get(1).getText());
            }
            catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(null, "Please enter valid integers for ChemType and Number of Results To Get");
                return null;
            }

            return FunctionsAndProcedures.LargestPurityAmounts(chemType, n);
        }

        return null;
    }

    private void createQuery2Panel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JTextField nField = new JTextField(5);

        inputPanel.add(new JLabel("Number of Results To Get:"));
        inputPanel.add(nField);

        queryPanels[2] = inputPanel;
        queryInputFields.get(2).add(nField);
        queryTables[2] = new ReportTable(
                10, 10, // Not used here
                true, true, true,
                new String[]{
                        "Customer ID", "First Name", "Last Name",
                        "Distinct Products Reviewed", "Distinct Products Purchased", "Review To Purchase Ratio"
                }
        );
    }

    private Object[][] query2Input() {
        int result = JOptionPane.showConfirmDialog(null, queryPanels[2],
                "Please Enter Number of Results To Get", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int n;

            try {
                n = Integer.parseInt(queryInputFields.get(2).get(0).getText());
            }
            catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(null, "Please enter valid integers for Number of Results To Get");
                return null; // Do not update the display with a new table
            }

            return FunctionsAndProcedures.HighestRatioProductsToReview(n);
        }

        return null; // Do not update the display with a new table
    }

    private void createQuery3Panel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JTextField monthsField = new JTextField(5);
        JTextField nField = new JTextField(5);

        inputPanel.add(new JLabel("Months:"));
        inputPanel.add(monthsField);
        inputPanel.add(Box.createHorizontalStrut(15));
        inputPanel.add(new JLabel("Number of Results To Get:"));
        inputPanel.add(nField);

        queryPanels[3] = inputPanel;
        queryInputFields.get(3).add(monthsField);
        queryInputFields.get(3).add(nField);
        queryTables[3] = new ReportTable(
                10, 10, // Not used here
                true, true, true,
                new String[]{"Customer ID", "First Name", "Last Name", "Total Spent"}
        );
    }

    private Object[][] query3Input() {
        int result = JOptionPane.showConfirmDialog(null, queryPanels[3],
                "Please Enter Months and Number of Results To Get", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int months, n;

            try {
                months = Integer.parseInt(queryInputFields.get(3).get(0).getText());
                n = Integer.parseInt(queryInputFields.get(3).get(1).getText());
            }
            catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(null, "Please enter valid integers for Months and Number of Results To Get");
                return null; // Do not update the display with a new table
            }

            return FunctionsAndProcedures.HighestRecentSpenders(months, n);
        }

        return null; // Do not update the display with a new table
    }

    private void createQuery4Panel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JTextField monthsField = new JTextField(5);
        JTextField nField = new JTextField(5);

        inputPanel.add(new JLabel("Months:"));
        inputPanel.add(monthsField);
        inputPanel.add(Box.createHorizontalStrut(15));
        inputPanel.add(new JLabel("Number of Results To Get:"));
        inputPanel.add(nField);

        queryPanels[4] = inputPanel;
        queryInputFields.get(4).add(monthsField);
        queryInputFields.get(4).add(nField);
        queryTables[4] = new ReportTable(
                10, 10, // Not used here
                true, true, true,
                new String[]{"Chemical Name", "Purity", "Distributor Name", "Profit"}
        );
    }

    private Object[][] query4Input() {
        int result = JOptionPane.showConfirmDialog(null, queryPanels[4],
                "Please Enter Months and Number of Results To Get", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int months, n;

            try {
                months = Integer.parseInt(queryInputFields.get(4).get(0).getText());
                n = Integer.parseInt(queryInputFields.get(4).get(1).getText());
            }
            catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(null, "Please enter valid integers for Months and Number of Results To Get");
                return null; // Do not update the display with a new table
            }

            return FunctionsAndProcedures.HighestProfitProducts(months, n);
        }

        return null; // Do not update the display with a new table
    }

    private void createQuery5Panel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JTextField nField = new JTextField(5);
        JTextField mField = new JTextField(5);

        inputPanel.add(new JLabel("Min Review Count:"));
        inputPanel.add(nField);
        inputPanel.add(Box.createHorizontalStrut(15));
        inputPanel.add(new JLabel("Number of Results To Get:"));
        inputPanel.add(mField);

        queryPanels[5] = inputPanel;
        queryInputFields.get(5).add(nField);
        queryInputFields.get(5).add(mField);
        queryTables[5] = new ReportTable(
                10, 10, // Not used here
                true, true, true,
                new String[]{"Distributor ID", "Distributor Name", "Review Count", "Average Review Score"}
        );
    }

    private Object[][] query5Input() {
        int result = JOptionPane.showConfirmDialog(null, queryPanels[5],
                "Please Enter N and Number of Results To Get", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int n, m;

            try {
                n = Integer.parseInt(queryInputFields.get(5).get(0).getText());
                m = Integer.parseInt(queryInputFields.get(5).get(1).getText());
            }
            catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(null, "Please enter valid integers for Min Review Count and Number of Results To Get");
                return null; // Do not update the display with a new table
            }

            return FunctionsAndProcedures.HighestRatedDistributorWithMinReviews(n, m);
        }

        return null; // Do not update the display with a new table
    }

    private void createQuery6Panel() {
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
        inputPanel.add(new JLabel("Number of Results To Get:"));
        inputPanel.add(nField);

        queryPanels[6] = inputPanel;
        queryInputFields.get(6).add(purityField);
        queryInputFields.get(6).add(chemTypeField);
        queryInputFields.get(6).add(nField);
        queryTables[6] = new ReportTable(
                10, 10, // Not used here
                true, true, true,
                new String[]{"Distributor ID", "Distributor Name", "Average Rating"}
        );
    }

    private Object[][] query6Input() {
        int result = JOptionPane.showConfirmDialog(null, queryPanels[6],
                "Please Enter Purity, ChemType and Number of Results To Get", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String purity;
            int chemType, n;

            try {
                purity = queryInputFields.get(6).get(0).getText();
                chemType = Integer.parseInt(queryInputFields.get(6).get(1).getText());
                n = Integer.parseInt(queryInputFields.get(6).get(2).getText());
            }
            catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(
                        null,
                        "Please enter a valid decimal between 0 and 100 for Purity and valid integers for Chem Type and Number of Results To Get"
                );
                return null; // Do not update the display with a new table
            }

            return FunctionsAndProcedures.DistributorHighestAvgRating(purity, chemType, n);
        }

        return null; // Do not update the display with a new table
    }

    private void createQuery7Panel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JTextField monthsField = new JTextField(5);

        inputPanel.add(new JLabel("Months:"));
        inputPanel.add(monthsField);

        queryPanels[7] = inputPanel;
        queryInputFields.get(7).add(monthsField);
        queryTables[7] = new ReportTable(
                10, 10, // Not used here
                true, true, true,
                new String[]{"Total Purchases", "Discounted Purchases", "Percentage With Discount"}
        );
    }

    private Object[][] query7Input() {
        int result = JOptionPane.showConfirmDialog(null, queryPanels[7],
                "Please Enter Months", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int months;

            try {
                months = Integer.parseInt(queryInputFields.get(7).get(0).getText());
            }
            catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(null, "Please enter valid integers for Months");
                return null; // Do not update the display with a new table
            }

            return FunctionsAndProcedures.PercentagePurchaseWDiscounts(months);
        }

        return null; // Do not update the display with a new table
    }
}