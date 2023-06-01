package ui;

import javax.swing.*;
import java.awt.*;

public class EmployeeOrCustomerPanel extends JPanel {

    private JPanel mainPanel;
    private JPanel loginRegisterPanel;

    public EmployeeOrCustomerPanel() {
        // Use BorderLayout to fill entire window
        setLayout(new BorderLayout());

        mainPanel = new JPanel();

        loginRegisterPanel = new LoginRegisterPanel(() -> {
            removeAll();
            add(mainPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        // Customer button
        JButton customerButton = new JButton("Customer");
        customerButton.addActionListener(e -> showLoginOrRegisterScreen());

        // Employee
        JButton employeeButton = new JButton("Employee");
        employeeButton.addActionListener(e -> showEmployeePanel());

        mainPanel.add(customerButton);
        mainPanel.add(employeeButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void showLoginOrRegisterScreen() {
        removeAll();
        add(loginRegisterPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showEmployeePanel() {

        JPanel employeePanel = new JPanel();
        employeePanel.setLayout(new BoxLayout(employeePanel, BoxLayout.PAGE_AXIS));

        JLabel employeeLabel1 = new JLabel("Scenarios:");
        employeeLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        Scenarios employeeScenarios = new Scenarios(true);
        JLabel employeeLabel2 = new JLabel("Analytical Queries:");
        employeeLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        AnalyticalQueries employeeAnalyticalQueries = new AnalyticalQueries();
        employeePanel.add(employeeLabel1);
        employeePanel.add(employeeScenarios);
        employeePanel.add(employeeLabel2);
        employeePanel.add(employeeAnalyticalQueries);

        // Add back button that returns to the choice between customer and employee
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            removeAll();
            add(mainPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        employeePanel.add(backButton);

        JScrollPane scrollableEmployeePanel = new JScrollPane(employeePanel);
        scrollableEmployeePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        removeAll();
        add(scrollableEmployeePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
