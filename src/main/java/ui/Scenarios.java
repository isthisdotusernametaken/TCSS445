package ui;

import javax.swing.*;
import java.awt.*;
import controller.FunctionsAndProcedures;
import ui.table.ReportTable;

public class Scenarios extends JPanel {
    private final JComboBox<String> dropdown;
    private final JButton submitButton;
    private final JPanel contentPanel;

    public Scenarios(boolean isEmployee) {
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        String[] options = {};

        if (isEmployee) {
            options = new String[]{
                "Search Products",
                "View Reviews",
                "Mark Transaction Delivered",
                "View Purchases",
                "View SubPurchases",
                "Add Distributor",
                "Record Shipment Purchase",
                "Mark Shipment Received",
            };
        } else {
            options = new String[]{
                "Search Products",
                "View Reviews",
                "Complete Transaction",
                "View Purchases",
                "View SubPurchases",
                "Review Product",
            };
        }

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
                case "Search Products" -> {
                    //FunctionsAndProcedures.searchProducts();
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
                    );
                }

                case "View Reviews" -> {
                    //FunctionsAndProcedures.viewReviews();
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
                    );
                }

                case "Mark Transaction Delivered" -> {
                    //FunctionsAndProcedures.markTransactionDelivered();
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
                    );
                }

                case "View Purchases" -> {
                    //FunctionsAndProcedures.viewPurchases();
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
                    );
                }

                case "View SubPurchases" -> {
                    //FunctionsAndProcedures.viewSubPurchases();
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
                    );
                }

                case "Add Distributor" -> {
                    //FunctionsAndProcedures.addDistributor();
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
                    );
                }

                case "Record Shipment Purchase" -> {
                    //FunctionsAndProcedures.recordShipmentPurchase();
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
                    );
                }

                case "Mark Shipment Received" -> {
                    //FunctionsAndProcedures.markShipmentReceived();
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
                    );
                }

                case "Complete Transaction" -> {
                    //FunctionsAndProcedures.completeTransaction();
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
                    );
                }

                case "Review Product" -> {
                    //FunctionsAndProcedures.reviewProduct();
                    reportTable = new ReportTable(
                            10,
                            10,
                            false,
                            false,
                            false,
                            new String[]{"Chemical ID", "Purity", "Average Rating"}
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

}