package controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.FunctionsAndProcedures;

public class AnalyticalQueries extends JPanel {
    private JComboBox<String> dropdown;
    private JButton submitButton;
    private JTable table;

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

        submitButton = new JButton("Submit");
        panel.add(submitButton);

        table = new JTable();

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) dropdown.getSelectedItem();
                switch (selectedOption) {
                    case "Highly Rated First-Time and Min Reviews Chemicals":
                        FunctionsAndProcedures.HighlyRatedFirstTimeAndMinReviewsChemicals(3, 10, 5);
                        break;
                    case "Largest Purity Amounts":
                        FunctionsAndProcedures.LargestPurityAmounts(1, 10);
                        break;
                    case "Highest Ratio of Products to Review":
                        FunctionsAndProcedures.HighestRatioProductsToReview(10);
                        break;
                    case "Highest Recent Spenders":
                        FunctionsAndProcedures.HighestRecentSpenders(6, 5);
                        break;
                    case "Highest Profit Products":
                        FunctionsAndProcedures.HighestProfitProducts(6, 5);
                        break;
                    case "Highest Rated Distributor with Min Reviews":
                        FunctionsAndProcedures.HighestRatedDistributorWithMinReviews(5, 10);
                        break;
                    case "Distributor Highest Avg Rating":
                        FunctionsAndProcedures.DistributorHighestAvgRating(0.95, 1, 5);
                        break;
                    case "Percentage Purchase with Discounts":
                        FunctionsAndProcedures.PercentagePurchaseWDiscounts(3);
                        break;
                    default:
                        break;
                }
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