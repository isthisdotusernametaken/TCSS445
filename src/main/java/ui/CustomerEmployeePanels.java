package ui;

import javax.swing.*;
import java.awt.*;

public class CustomerEmployeePanels extends JPanel {
    public CustomerEmployeePanels() {

        setPreferredSize(new Dimension(800, 1000));

        JTabbedPane tabbedPane = new JTabbedPane();

        // Customer Panel
        JPanel customerPanel = new JPanel(new GridLayout(4, 1));
        JLabel customerLabel = new JLabel("Customer Label");
        customerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Scenarios customerScenarios = new Scenarios(false);
        customerPanel.add(customerLabel);
        customerPanel.add(customerScenarios);

        // Employee panel
        JPanel employeePanel = new JPanel(new GridLayout(4, 1));
        JLabel employeeLabel1 = new JLabel("Scenarios:");
        employeeLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        Scenarios employeeScenarios = new Scenarios(true);
        JLabel employeeLabel2 = new JLabel("Analytical Queries:");
        employeeLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        AnalyticalQueries employeeAnalticalQueries = new AnalyticalQueries();
        employeePanel.add(employeeLabel1);
        employeePanel.add(employeeScenarios);
        employeePanel.add(employeeLabel2);
        employeePanel.add(employeeAnalticalQueries);

        tabbedPane.addTab("Customer", customerPanel);
        tabbedPane.addTab("Employee", employeePanel);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         new CustomerEmployeePanels();
    //     });
    // }
}
