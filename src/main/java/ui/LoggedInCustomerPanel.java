package ui;

import controller.Controller;
import controller.CustomerSession;
import controller.FunctionsAndProcedures;
import ui.table.ReportTable;
import ui.table.ReviewsReport;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

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

        // Menu options for this panel
        JPanel buttonPanel = new JPanel();


        panel.add(buttonPanel, BorderLayout.NORTH);

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

        panel.add(inputPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Contents
        panel.add(cartTable, BorderLayout.CENTER);

        // Menu options for this panel
        JPanel buttonPanel = new JPanel();


        panel.add(buttonPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createViewPurchasesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Contents
        panel.add(viewPurchasesTable, BorderLayout.CENTER);

        // Menu options for this panel
        JPanel buttonPanel = new JPanel();


        panel.add(buttonPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createViewSubpurchasesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Contents
        panel.add(viewSubpurchasesTable, BorderLayout.CENTER);

        // Menu options for this panel
        JPanel buttonPanel = new JPanel();


        panel.add(buttonPanel, BorderLayout.NORTH);

        return panel;
    }
}
