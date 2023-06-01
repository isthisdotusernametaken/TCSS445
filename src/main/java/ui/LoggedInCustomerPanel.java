package ui;

import controller.Controller;
import controller.CustomerSession;
import controller.FunctionsAndProcedures;
import ui.table.ReportTable;
import ui.table.ReviewsReport;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;

import static controller.DBManager.getError;
import static controller.DBManager.hasFailed;

public class LoggedInCustomerPanel extends JPanel {

    private CustomerSession session;

    private final JPanel mainPanel;
    private final JPanel choicePanel;
    private final JPanel browsePanel;
    private final JPanel pastPurchasesPanel;

    private final ReportTable searchResults = new ReportTable(
            600, 200,
            true, true, true,
            new String[]{
                    "Chemical ID",
                    "Chemical Name", "Purity", "State of Matter",
                    "Remaining Quantity", "Cost per Unit",
                    "Unit", "Unit Abbreviation",
                    "Distributor", "Average Rating", "Purchaser Count"
            }
    );
    private final ReviewsReport reviewsTable = new ReviewsReport(600, 200);
    private final ReportTable cartTable = new ReportTable(
            600, 200,
            true, true, true,
            new String[]{"Chemical ID", "Quantity"}
    );
    private final ReportTable viewPurchasesTable = new ReportTable(
            600, 200,
            true, true, true,
            new String[]{
                    "Purchase Date", "Purchase Total",
                    "Discount Name", "Discount Percent (as Decimal)",
                    "Transaction ID",
                    "Receive Date"
            }
    );
    private final ReportTable viewSubpurchasesTable = new ReportTable(
            600, 200,
            true, true, true,
            new String[]{
                    "Chemical Name", "Purity", "Quantity", "Unit", "State of Matter",
                    "Cost"
            }
    );

    LoggedInCustomerPanel(final Runnable backFunction) {
        setLayout(new BorderLayout());

        mainPanel = new JPanel();

        // Panel for viewing products and reviews and making purchases
        browsePanel = new JPanel();
        // Side-by-side screens for: searching products, viewing reviews, using
        // the cart
        buildBrowsePanel();

        // Panel for viewing past purchases
        pastPurchasesPanel = new JPanel();
        // Side-by-side screens for: viewing past purchases and viewing subpurchases
        buildPastPurchasesPanel();

        choicePanel = new JPanel();
        JButton browse = new JButton("Browse Products");
        browse.addActionListener(e -> {
            mainPanel.removeAll();
            mainPanel.add(browsePanel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        choicePanel.add(browse);
        JButton past = new JButton("View Past Purchases");
        past.addActionListener(e -> {
            mainPanel.removeAll();
            mainPanel.add(pastPurchasesPanel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        choicePanel.add(past);

        mainPanel.add(choicePanel, BorderLayout.CENTER);


        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Always shown
        addRefreshAndLogoutButtons(backFunction);
    }

    void setCustomer(final int customerID) {
        session = new CustomerSession(customerID, Controller.ONLINE);
    }

    private void buildBrowsePanel() {
        browsePanel.setLayout(new GridLayout(1, 3));

//            "Search Products",
//            "View Reviews",
//            "Complete Transaction",
//            "View Purchases",
//            "View SubPurchases",
//            "Review Product",
        browsePanel.add(createSearchPanel());
        browsePanel.add(createReviewPanel());
        browsePanel.add(createCartPanel());
    }

    private void buildPastPurchasesPanel() {
        pastPurchasesPanel.setLayout(new GridLayout(1, 2));

//            "Search Products",
//            "View Reviews",
//            "Complete Transaction",
//            "View Purchases",
//            "View SubPurchases",
//            "Review Product",
        pastPurchasesPanel.add(createViewPurchasesPanel());
        pastPurchasesPanel.add(createViewSubpurchasesPanel());
    }

    private void addRefreshAndLogoutButtons(final Runnable backFunction) {
        // Ensure button(s) not too wide
        JPanel logoutPanel = new JPanel();

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            session = null; // Close the current session

            backFunction.run(); // Return the screen that owns this panel
        });
        logoutPanel.add(logoutButton);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> { // Return to choice screen for browsing or viewing past purchases
            mainPanel.removeAll();
            mainPanel.add(choicePanel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        logoutPanel.add(refresh);

        add(logoutPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Contents
        panel.add(searchResults, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();

        JButton searchButton = new JButton("Search Products");

        JTextField resultsPositionField = new JTextField(5);
        JTextField resultsCountField = new JTextField(5);
        JTextField chemicalNameField = new JTextField(5);
        JTextField minPurityField = new JTextField(5);
        JTextField maxPurityField = new JTextField(5);
        JTextField stateOfMatterField = new JTextField(5);
        JTextField distributorField = new JTextField(5);
        JComboBox<String> firstSortByComboBox = new JComboBox<>(new String[] {"", "C", "P", "R", "N"});
        JComboBox<String> secondSortByComboBox = new JComboBox<>(new String[] {"", "C", "P", "R", "N"});
        JComboBox<String> thirdSortByComboBox = new JComboBox<>(new String[] {"", "C", "P", "R", "N"});
        JComboBox<String> fourthSortByComboBox = new JComboBox<>(new String[] {"", "C", "P", "R", "N"});
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
                firstSortByComboBox,
                firstSortAscCheckbox,
                new JLabel("Second Sort By:"),
                secondSortByComboBox,
                secondSortAscCheckbox,
                new JLabel("Third Sort By:"),
                thirdSortByComboBox,
                thirdSortAscCheckbox,
                new JLabel("Fourth Sort By:"),
                fourthSortByComboBox,
                fourthSortAscCheckbox
        };

        for (JComponent input : inputs) {
            inputPanel.add(input);
        }

        searchButton.addActionListener(e -> {
            try {
                int resultsPosition = Integer.parseInt(resultsPositionField.getText());
                int resultsCount = Integer.parseInt(resultsCountField.getText());
                String chemicalName = chemicalNameField.getText();
                String minPurity = minPurityField.getText();
                String maxPurity = maxPurityField.getText();
                String stateOfMatter = stateOfMatterField.getText();
                String distributor = distributorField.getText();
                Character firstSortBy = null;
                Character secondSortBy = null;
                Character thirdSortBy = null;
                Character fourthSortBy = null;

                String firstSortBySelection = (String) firstSortByComboBox.getSelectedItem();
                if (firstSortBySelection != null && !firstSortBySelection.isEmpty()) {
                    firstSortBy = firstSortBySelection.charAt(0);
                } else {
                    throw new IllegalArgumentException("First sort must be nonnull.");
                }

                String secondSortBySelection = (String) secondSortByComboBox.getSelectedItem();
                if (secondSortBySelection != null && !secondSortBySelection.isEmpty()) {
                    secondSortBy = secondSortBySelection.charAt(0);
                }

                String thirdSortBySelection = (String) thirdSortByComboBox.getSelectedItem();
                if (thirdSortBySelection != null && !thirdSortBySelection.isEmpty()) {
                    thirdSortBy = thirdSortBySelection.charAt(0);
                }

                String fourthSortBySelection = (String) fourthSortByComboBox.getSelectedItem();
                if (fourthSortBySelection != null && !fourthSortBySelection.isEmpty()) {
                    fourthSortBy = fourthSortBySelection.charAt(0);
                }
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
            }
            catch (IllegalArgumentException err) {
                JOptionPane.showMessageDialog(null, "Invalid input.");
            }
        });
        inputPanel.add(searchButton);



        panel.add(inputPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createReviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Contents
        panel.add(reviewsTable, BorderLayout.CENTER);

        // Menu options for this panel
        JPanel inputPanel = new JPanel();
        JButton viewReviewsButton = new JButton("View Reviews");

        inputPanel.add(new JLabel("Chemical ID:"));
        JTextField chemID = new JTextField(5);
        inputPanel.add(chemID);

        inputPanel.add(new JLabel("Start Position in Results:"));
        JTextField startPos = new JTextField(5);
        inputPanel.add(startPos);

        inputPanel.add(new JLabel("Review Count:"));
        JTextField reviewCount = new JTextField(5);
        inputPanel.add(reviewCount);

        viewReviewsButton.addActionListener(e -> {
            try {
                var output = FunctionsAndProcedures.viewReviews(
                        Integer.parseInt(startPos.getText()),
                        Integer.parseInt(reviewCount.getText()),
                        Integer.parseInt(chemID.getText())
                );
                if (hasFailed(output))
                    UIUtil.showError(getError(output));
                else
                    reviewsTable.setReviews(output);
            } catch (NumberFormatException ex) {
                UIUtil.showError("Enter only valid integers.");
            }
        });
        inputPanel.add(viewReviewsButton);

        JButton addReviewButton = new JButton("Add Review");

        inputPanel.add(new JLabel("Chemical ID (to Review):"));
        JTextField chemIDtoReview = new JTextField(5);
        inputPanel.add(chemIDtoReview);

        inputPanel.add(new JLabel("Rating (0-5):"));
        JTextField stars = new JTextField(5);
        inputPanel.add(stars);

        inputPanel.add(new JLabel("Review Text:"));
        ScrollableTextArea text = new ScrollableTextArea();
        inputPanel.add(text);

        addReviewButton.addActionListener(e -> {
            try {
                UIUtil.showMessage(session.reviewProduct(
                        Integer.parseInt(chemIDtoReview.getText()),
                        Integer.parseInt(stars.getText()),
                        text.getText()
                ));
            } catch (NumberFormatException ex) {
                UIUtil.showError("Enter only valid integers.");
            }
        });
        inputPanel.add(addReviewButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Contents
        panel.add(cartTable, BorderLayout.CENTER);

        // Menu options for this panel
        JPanel inputPanel = new JPanel();
        JButton addToCartbutton = new JButton("Add to Cart");

        inputPanel.add(new JLabel("Chemical ID:"));
        JTextField chemID = new JTextField(5);
        inputPanel.add(chemID);

        inputPanel.add(new JLabel("Quantity:"));
        JTextField quantityField = new JTextField(5);
        inputPanel.add(quantityField);

        inputPanel.add(new JLabel("Discount ID:"));
        JTextField discountIDField = new JTextField(5);
        inputPanel.add(discountIDField);

        inputPanel.add(new JLabel("Remove ID From Cart:"));
        JTextField removeIDField = new JTextField(5);
        inputPanel.add(removeIDField);

        JButton removeButton = new JButton("Remove from Cart");

        removeButton.addActionListener(e -> {
            try {
                var output = Integer.parseInt(removeIDField.getText());

                session.removeItemFromCart(output);

                cartTable.replace(session.viewCart());

            } catch (NumberFormatException ex) {
                UIUtil.showError("Enter only valid integers.");
            }
        });

        addToCartbutton.addActionListener(e -> {
            try {
                var output = Integer.parseInt(chemID.getText());

                var quantity = BigDecimal.valueOf(Double.parseDouble(quantityField.getText()));

                session.addItemToCart(output, quantity);

                cartTable.replace(session.viewCart());

            } catch (NumberFormatException ex) {
                UIUtil.showError("Enter only valid integers.");
            }
        });

        inputPanel.add(removeButton);
        inputPanel.add(addToCartbutton);

        JButton completeTransaction = new JButton("Complete Transaction");

        completeTransaction.addActionListener(e -> {
            try {
                var discountID = discountIDField.getText().isBlank() ? null : Integer.parseInt(discountIDField.getText());

                Object[] output =  session.completeTransaction(Controller.TAX_PERCENT, discountID);

                if (output[0] != FunctionsAndProcedures.SUCCESS) {
                    System.out.println(output[0]);
                    UIUtil.showError("Transaction failed.");
                    return;
                }

                cartTable.replace(session.viewCart());

                UIUtil.showMessage("Transaction completed successfully. Subtotal: " + output[1] + " Tax: " + output[2]);

            } catch (NumberFormatException ex) {
                UIUtil.showError("Enter only valid integers.");
            }
        });

        inputPanel.add(completeTransaction);

        panel.add(inputPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createViewPurchasesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Contents
        panel.add(viewPurchasesTable, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        JButton viewPurchasesButton = new JButton("View Purchases");

        inputPanel.add(new JLabel("Start Position in Results:"));
        JTextField startPosField = new JTextField(5);
        inputPanel.add(startPosField);

        inputPanel.add(new JLabel("Row Count:"));
        JTextField rowCntField = new JTextField(5);
        inputPanel.add(rowCntField);

        inputPanel.add(new JLabel("Sort Newest First:"));
        JCheckBox sortNewestFirstCheckbox = new JCheckBox("Sort Newest First");
        inputPanel.add(sortNewestFirstCheckbox);

        viewPurchasesButton.addActionListener(e -> {
            try {

                var output = session.viewPurchases(
                        Integer.parseInt(startPosField.getText()),
                        Integer.parseInt(rowCntField.getText()),
                        sortNewestFirstCheckbox.isSelected()
                );

                if (hasFailed(output))
                    UIUtil.showError(getError(output));
                else
                    viewPurchasesTable.addRows(output);
            } catch (NumberFormatException ex) {
                UIUtil.showError("Enter only valid integers.");
            }
        });
        inputPanel.add(viewPurchasesButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createViewSubpurchasesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Contents
        panel.add(viewSubpurchasesTable, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        JButton viewPurchasesButton = new JButton("View Purchases");

        inputPanel.add(new JLabel("Start Position in Results:"));
        JTextField startPosField = new JTextField(5);
        inputPanel.add(startPosField);

        inputPanel.add(new JLabel("Row Count:"));
        JTextField rowCntField = new JTextField(5);
        inputPanel.add(rowCntField);

        inputPanel.add(new JLabel("Transaction ID:"));
        JTextField transactionIDField = new JTextField(5);
        inputPanel.add(transactionIDField);

        viewPurchasesButton.addActionListener(e -> {
            try {
                var output = FunctionsAndProcedures.viewSubpurchases(
                        Integer.parseInt(startPosField.getText()),
                        Integer.parseInt(rowCntField.getText()),
                        Integer.parseInt(transactionIDField.getText())
                );
                if (hasFailed(output))
                    UIUtil.showError(getError(output));
                else
                    viewSubpurchasesTable.addRows(output);
            } catch (NumberFormatException ex) {
                UIUtil.showError("Enter only valid integers.");
            }
        });
        inputPanel.add(viewPurchasesButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        return panel;
    }

}
