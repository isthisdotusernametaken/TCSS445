package ui;

import controller.Controller;
import controller.CustomerSession;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class LoggedInCustomerPanel extends JPanel {

    private CustomerSession session;


//            "Search Products",
//            "View Reviews",
//            "Complete Transaction",
//            "View Purchases",
//            "View SubPurchases",
//            "Review Product",

    LoggedInCustomerPanel(final Runnable backFunction) {

        setLayout(new BorderLayout());

        // Side-by-side screens for: searching products, viewing reviews, using
        // the cart, viewing past purchases, and viewing subpurchases;
        addCustomerInteractionPanel();

        addLogoutButton(backFunction);
    }

    void setCustomer(final int customerID) {
        session = new CustomerSession(customerID, Controller.ONLINE);
    }

    private void addCustomerInteractionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5));

        add(panel, BorderLayout.CENTER);
    }

    private void addLogoutButton(final Runnable backFunction) {
        // Used to ensure logout button is not too wide
        JPanel logoutPanel = new JPanel();

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            session = null; // Close the current session

            backFunction.run(); // Return the screen that owns this panel
        });
        logoutPanel.add(logoutButton);

        add(logoutPanel, BorderLayout.SOUTH);
    }
}
