package ui;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

import controller.FunctionsAndProcedures;
import controller.TransactionCart;
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

                    // Make a popup that has the following: final int startPos, final int rowCnt, final int chemID
                    // Create the input fields for parameters
                        JTextField startPosField = new JTextField();
                        JTextField rowCntField = new JTextField();
                        JTextField chemIDField = new JTextField();

                        // Create an array of the input fields
                        JComponent[] inputs = new JComponent[] {
                                new JLabel("Start Position:"),
                                startPosField,
                                new JLabel("Row Count:"),
                                rowCntField,
                                new JLabel("Chem ID:"),
                                chemIDField
                        };

                        // Show the popup dialog to get the parameters
                        int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);

                        // Check if the user clicked "OK"
                        if (result == JOptionPane.OK_OPTION) {
                            try {
                                // Parse the input values
                                int startPos = Integer.parseInt(startPosField.getText());
                                int rowCnt = Integer.parseInt(rowCntField.getText());
                                int chemID = Integer.parseInt(chemIDField.getText());

                                // Call the function with the provided parameters
                                Object[][] data = FunctionsAndProcedures.viewReviews(startPos, rowCnt, chemID);

                                for (Object[] row : data) {
                                    for (Object cell : row) {
                                        System.out.print(cell + " ");
                                    }
                                    System.out.println();
                                }

                                // Display the result
                                if (data == null) {
                                    JOptionPane.showMessageDialog(null, "Function returned null");
                                    break;
                                }

                                reportTable = new ReportTable(
                                    10,
                                    10,
                                    false,
                                    false,
                                    false,
                                    new String[]{"First Name", "Last Name", "Stars", "Review Text", "Review data"}
                                );

                                reportTable.addRows(data);

                            } catch (NumberFormatException exp) {
                                JOptionPane.showMessageDialog(null, "Invalid input! Please enter valid integers.");
                            }
                        }

                }

                case "Mark Transaction Delivered" -> {
                    JTextField transactionIDField = new JTextField();

                    // Create an array of the input field
                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Transaction ID:"),
                            transactionIDField
                    };

                    // Show the popup dialog to get the parameter
                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameter", JOptionPane.OK_CANCEL_OPTION);

                    // Check if the user clicked "OK"
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            // Parse the input value
                            int transactionID = Integer.parseInt(transactionIDField.getText());

                            // Call the function with the provided parameter
                            String data = FunctionsAndProcedures.markTransactionDelivered(transactionID);

                            // Display the result
                            if (data != FunctionsAndProcedures.SUCCESS) {
                                UIUtil.showError("Error");
                            }

                        } catch (NumberFormatException err) {
                            JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid integer.");
                        }
                    }


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
                    JTextField distributorNameField = new JTextField();

                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Distributor Name:"),
                            distributorNameField
                    };
            
                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameter", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        String distributorName = distributorNameField.getText();
            
                        String data = FunctionsAndProcedures.addDistributor(distributorName);

                        if (data != null && data.equals(FunctionsAndProcedures.SUCCESS)) {
                            JOptionPane.showMessageDialog(null, "Distributor added successfully.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to add distributor.");
                        }
                    }
            
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

                    JTextField customerIDField = new JTextField();
                    JTextField taxPercentField = new JTextField();
                    JTextField discountIDField = new JTextField();
                    JTextField cartField = new JTextField();
                    JTextField onlineField = new JTextField();
                    JTextField chemicalIDField = new JTextField();
                    JTextField quantityField = new JTextField();

                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Customer ID:"),
                            customerIDField,
                            new JLabel("Tax Percent:"),
                            taxPercentField,
                            new JLabel("Discount ID:"),
                            discountIDField,
                            new JLabel("Cart:"),
                            cartField,
                            new JLabel("Online (true/false):"),
                            onlineField,
                            new JLabel("Chemical ID:"),
                            chemicalIDField,
                            new JLabel("Quantity:"),
                            quantityField,

                    };

                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        try {
                        
                            int customerID = Integer.parseInt(customerIDField.getText());
                            String taxPercent = taxPercentField.getText();
                            int discountID = Integer.parseInt(discountIDField.getText());

                            TransactionCart cart = new TransactionCart();
                            cart.addRow(Integer.parseInt(chemicalIDField.getText()), BigDecimal.valueOf(Double.parseDouble(quantityField.getText())));

                            boolean online = Boolean.parseBoolean(onlineField.getText());

                            Object[] data = FunctionsAndProcedures.completeTransaction(customerID, taxPercent, discountID, cart, online);

                            if (data == null) {
                                JOptionPane.showMessageDialog(null, "Function returned null");
                                break;
                            }

                            reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[]{"SubTotal", "TaxAmount"}
                            );

                            Object[][] rows = new Object[1][];
                            rows[0][0] = data[1];
                            rows[0][1] = data[2];
                            reportTable.addRows(rows);

                        } catch (NumberFormatException exp) {
                            JOptionPane.showMessageDialog(null, "Invalid input! Please enter valid values.");
                        }
                    }
                    
                }

                case "Review Product" -> {
                    JTextField customerIDField = new JTextField();
                    JTextField chemicalIDField = new JTextField();
                    JTextField starsField = new JTextField();
                    JTextField textField = new JTextField();
            
                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Customer ID:"),
                            customerIDField,
                            new JLabel("Chemical ID:"),
                            chemicalIDField,
                            new JLabel("Stars:"),
                            starsField,
                            new JLabel("Text:"),
                            textField
                    };
            
                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);
            
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int customerID = Integer.parseInt(customerIDField.getText());
                            int chemicalID = Integer.parseInt(chemicalIDField.getText());
                            int stars = Integer.parseInt(starsField.getText());
                            String text = textField.getText();
            
                            String data = FunctionsAndProcedures.reviewProduct(customerID, chemicalID, stars, text);
            
                            if (data != FunctionsAndProcedures.SUCCESS) {
                                JOptionPane.showMessageDialog(null, "Function returned null");
                            }

                        } catch (NumberFormatException err) {
                            JOptionPane.showMessageDialog(null, "Invalid input! Please enter valid values.");
                        }
                    }
                }

                case "Add Chemical Type" -> {
                    JTextField chemicalNameField = new JTextField();
                    JTextField measurementUnitField = new JTextField();
                    JTextField stateOfMatterField = new JTextField();

                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Chemical Name:"),
                            chemicalNameField,
                            new JLabel("Measurement Unit:"),
                            measurementUnitField,
                            new JLabel("State of Matter:"),
                            stateOfMatterField
                    };

                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        String chemicalName = chemicalNameField.getText();
                        String measurementUnit = measurementUnitField.getText();
                        String stateOfMatter = stateOfMatterField.getText();

                        String data = FunctionsAndProcedures.addChemicalType(chemicalName, measurementUnit, stateOfMatter);

                        if (data != null && data.equals(FunctionsAndProcedures.SUCCESS)) {
                            JOptionPane.showMessageDialog(null, "Chemical type added successfully.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to add chemical type.");
                        }
                    }

                }

                case "Add Chemical Quality"  -> {
                    JTextField chemicalTypeIDField = new JTextField();
                    JTextField purityField = new JTextField();
                    JTextField costPerUnitField = new JTextField();

                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Chemical Type ID:"),
                            chemicalTypeIDField,
                            new JLabel("Purity:"),
                            purityField,
                            new JLabel("Cost per Unit:"),
                            costPerUnitField
                    };

                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int chemicalTypeID = Integer.parseInt(chemicalTypeIDField.getText());
                            String purity = purityField.getText();
                            String costPerUnit = costPerUnitField.getText();
                            String data = FunctionsAndProcedures.addChemicalQuality(chemicalTypeID, purity, costPerUnit);

                            if (data != null && data.equals(FunctionsAndProcedures.SUCCESS)) {
                                JOptionPane.showMessageDialog(null, "Chemical quality added successfully.");
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to add chemical quality.");
                            }
                        } catch (NumberFormatException err) {
                            JOptionPane.showMessageDialog(null, "Invalid input! Please enter valid values.");
                        }
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

}