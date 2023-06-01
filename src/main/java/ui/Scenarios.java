package ui;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

import controller.FunctionsAndProcedures;
import controller.ShipmentCart;
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
                "Add Chemical Quality",
                "Add Chemical Type"
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
                    JTextField resultsPositionField = new JTextField();
                    JTextField resultsCountField = new JTextField();
                    JTextField chemicalNameField = new JTextField();
                    JTextField minPurityField = new JTextField();
                    JTextField maxPurityField = new JTextField();
                    JTextField stateOfMatterField = new JTextField();
                    JTextField distributorField = new JTextField();
                    JTextField firstSortByField = new JTextField();
                    JTextField secondSortByField = new JTextField();
                    JTextField thirdSortByField = new JTextField();
                    JTextField fourthSortByField = new JTextField();
                    JCheckBox firstSortAscCheckbox = new JCheckBox("Ascending");
                    JCheckBox secondSortAscCheckbox = new JCheckBox("Ascending");
                    JCheckBox thirdSortAscCheckbox = new JCheckBox("Ascending");
                    JCheckBox fourthSortAscCheckbox = new JCheckBox("Ascending");
            
                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Results Position:"),
                            resultsPositionField,
                            new JLabel("Results Count:"),
                            resultsCountField,
                            new JLabel("Chemical Name:"),
                            chemicalNameField,
                            new JLabel("Minimum Purity:"),
                            minPurityField,
                            new JLabel("Maximum Purity:"),
                            maxPurityField,
                            new JLabel("State of Matter:"),
                            stateOfMatterField,
                            new JLabel("Distributor:"),
                            distributorField,
                            new JLabel("First Sort By:"),
                            firstSortByField,
                            firstSortAscCheckbox,
                            new JLabel("Second Sort By:"),
                            secondSortByField,
                            secondSortAscCheckbox,
                            new JLabel("Third Sort By:"),
                            thirdSortByField,
                            thirdSortAscCheckbox,
                            new JLabel("Fourth Sort By:"),
                            fourthSortByField,
                            fourthSortAscCheckbox
                    };
            
                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);
            
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int resultsPosition = Integer.parseInt(resultsPositionField.getText());
                            int resultsCount = Integer.parseInt(resultsCountField.getText());
                            String chemicalName = chemicalNameField.getText();
                            String minPurity = minPurityField.getText();
                            String maxPurity = maxPurityField.getText();
                            String stateOfMatter = stateOfMatterField.getText();
                            String distributor = distributorField.getText();
                            char firstSortBy = firstSortByField.getText().charAt(0);
                            Character secondSortBy = (secondSortByField.getText().isEmpty()) ? null : secondSortByField.getText().charAt(0);
                            Character thirdSortBy = (thirdSortByField.getText().isEmpty()) ? null : thirdSortByField.getText().charAt(0);
                            Character fourthSortBy = (fourthSortByField.getText().isEmpty()) ? null : fourthSortByField.getText().charAt(0);
                            boolean firstSortAsc = firstSortAscCheckbox.isSelected();
                            Boolean secondSortAsc = secondSortAscCheckbox.isSelected();
                            Boolean thirdSortAsc = thirdSortAscCheckbox.isSelected();
                            Boolean fourthSortAsc = fourthSortAscCheckbox.isSelected();
            
                            // Call the function with the provided parameters
                            Object[][] data = FunctionsAndProcedures.searchProducts(resultsPosition, resultsCount, chemicalName, minPurity, maxPurity, stateOfMatter,
                                    distributor, firstSortBy, secondSortBy, thirdSortBy, fourthSortBy,
                                    firstSortAsc, secondSortAsc, thirdSortAsc, fourthSortAsc);
            
                            // Display the result
                            if (data == null) {
                                JOptionPane.showMessageDialog(null, "Failed to retrieve search results.");
                            }

                            reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[]{
                                    "Chemical ID",
                                    "Chemical Name",
                                    "Purity",
                                    "State Of Matter Name",
                                    "Remaining Quantity",
                                    "Cost Per Unit",
                                    "Measurement Unit Name",
                                    "Measurement Unit Abbreviation",
                                    "Distruibutor Name",
                                    "Avg Rating",
                                    "Purchaser Count"
                                }
                            );

                            reportTable.addRows(data);

                        } catch (NumberFormatException err) {
                            JOptionPane.showMessageDialog(null, "Invalid input! Please enter valid values.");
                        }
                    }

                   
                }

                case "View Reviews" -> {
                    JTextField startPosField = new JTextField();
                    JTextField rowCntField = new JTextField();
                    JTextField chemIDField = new JTextField();

                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Start Position:"),
                            startPosField,
                            new JLabel("Row Count:"),
                            rowCntField,
                            new JLabel("Chem ID:"),
                            chemIDField
                    };

                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int startPos = Integer.parseInt(startPosField.getText());
                            int rowCnt = Integer.parseInt(rowCntField.getText());
                            int chemID = Integer.parseInt(chemIDField.getText());

                            Object[][] data = FunctionsAndProcedures.viewReviews(startPos, rowCnt, chemID);

                            for (Object[] row : data) {
                                for (Object cell : row) {
                                    System.out.print(cell + " ");
                                }
                                System.out.println();
                            }

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

                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Transaction ID:"),
                            transactionIDField
                    };

                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameter", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int transactionID = Integer.parseInt(transactionIDField.getText());

                            String data = FunctionsAndProcedures.markTransactionDelivered(transactionID);

                            if (data != FunctionsAndProcedures.SUCCESS) {
                                UIUtil.showError("Error");
                            }

                        } catch (NumberFormatException err) {
                            JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid integer.");
                        }
                    }


                }

                case "View Purchases" -> {
                    JTextField startPosField = new JTextField();
                    JTextField rowCntField = new JTextField();
                    JTextField customerIDField = new JTextField();
                    JCheckBox sortNewestFirstCheckbox = new JCheckBox("Sort Newest First");

                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Start Position:"),
                            startPosField,
                            new JLabel("Row Count:"),
                            rowCntField,
                            new JLabel("Customer ID:"),
                            customerIDField,
                            sortNewestFirstCheckbox
                    };

                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int startPos = Integer.parseInt(startPosField.getText());
                            int rowCnt = Integer.parseInt(rowCntField.getText());
                            int customerID = Integer.parseInt(customerIDField.getText());
                            boolean sortNewestFirst = sortNewestFirstCheckbox.isSelected();

                            Object[][] data = FunctionsAndProcedures.viewPurchases(startPos, rowCnt, customerID, sortNewestFirst);

                            if (data == null) {
                                JOptionPane.showMessageDialog(null, "Failed to retrieve purchases.");
                                break;
                            }

                            reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[] {
                                    "Purchase Date",
                                    "Purchase Total",
                                    "Discount Name",
                                    "Percentage",
                                    "Transaction ID",
                                    "Receive Date"
                                }
                            );

                            reportTable.addRows(data);

                        } catch (NumberFormatException err) {
                            JOptionPane.showMessageDialog(null, "Invalid input! Please enter valid values.");
                        }
                    }
                    
                }

                case "View SubPurchases" -> {
                    JTextField startPosField = new JTextField();
                    JTextField rowCntField = new JTextField();
                    JTextField transactionIDField = new JTextField();

                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Start Position:"),
                            startPosField,
                            new JLabel("Row Count:"),
                            rowCntField,
                            new JLabel("Transaction ID:"),
                            transactionIDField
                    };

                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int startPos = Integer.parseInt(startPosField.getText());
                            int rowCnt = Integer.parseInt(rowCntField.getText());
                            int transactionID = Integer.parseInt(transactionIDField.getText());

                            Object[][] data = FunctionsAndProcedures.viewSubpurchases(startPos, rowCnt, transactionID);

                            if (data == null) {
                                JOptionPane.showMessageDialog(null, "Failed to retrieve subpurchases.");
                                break;
                            }

                            reportTable = new ReportTable(
                                10,
                                10,
                                false,
                                false,
                                false,
                                new String[] {
                                    "Chemical Name",
                                    "Purity",
                                    "Quantity",
                                    "Measurement Unit Abbreviation",
                                    "State Of Matter Name",
                                    "Cost",
                                    "Distributor Name"
                                }
                            );

                            reportTable.addRows(data);


                        } catch (NumberFormatException err) {
                            JOptionPane.showMessageDialog(null, "Invalid input! Please enter valid values.");
                        }
                    }

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
                    JTextField distributorIDField = new JTextField();
                    JTextField chemicalTypeIDField = new JTextField();
                    JTextField purityField = new JTextField();
                    JTextField quantityField = new JTextField();
                    JTextField purchasePriceField = new JTextField();

                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Distributor ID:"),
                            distributorIDField,
                            new JLabel("Chemical Type ID:"),
                            chemicalTypeIDField,
                            new JLabel("Purity:"),
                            purityField,
                            new JLabel("Quantity:"),
                            quantityField,
                            new JLabel("Purchase Price:"),
                            purchasePriceField
                    };

                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int distributorID = Integer.parseInt(distributorIDField.getText());
                            int chemicalTypeID = Integer.parseInt(chemicalTypeIDField.getText());
                            BigDecimal purity = new BigDecimal(purityField.getText());
                            BigDecimal quantity = new BigDecimal(quantityField.getText());
                            BigDecimal purchasePrice = new BigDecimal(purchasePriceField.getText());

                            ShipmentCart shipmentCart = new ShipmentCart();
                            shipmentCart.addRow(chemicalTypeID, purity, quantity, purchasePrice);

                            String data = FunctionsAndProcedures.recordShipmentPurchase(distributorID, shipmentCart);

                            if (data != null && data.equals(FunctionsAndProcedures.SUCCESS)) {
                                JOptionPane.showMessageDialog(null, "Shipment purchase recorded successfully.");
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to record shipment purchase.");
                            }
                        } catch (NumberFormatException err) {
                            JOptionPane.showMessageDialog(null, "Invalid input! Please enter valid values.");
                        }
                    }
                }

                case "Mark Shipment Received" -> {
                    JTextField shipmentIDField = new JTextField();

                    JComponent[] inputs = new JComponent[] {
                            new JLabel("Shipment ID:"),
                            shipmentIDField
                    };
            
                    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameter", JOptionPane.OK_CANCEL_OPTION);
            
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int shipmentID = Integer.parseInt(shipmentIDField.getText());
            
                            String data = FunctionsAndProcedures.markShipmentReceived(shipmentID);

                            if (data != null && data.equals(FunctionsAndProcedures.SUCCESS)) {
                                JOptionPane.showMessageDialog(null, "Shipment marked as received successfully.");
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to mark shipment as received.");
                            }
                        } catch (NumberFormatException err) {
                            JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid integer.");
                        }
                    }
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