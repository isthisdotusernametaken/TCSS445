package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeOrCustomerPanel extends JPanel {
    public EmployeeOrCustomerPanel() {
        // Set the layout to FlowLayout
        setLayout(new FlowLayout());

        // Customer button
        JButton customerButton = new JButton("Customer");
        customerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginOrRegisterScreen();
            }
        });

        // Employee
        JButton employeeButton = new JButton("Employee");
        employeeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEmployeePanel();
            }
        });

        add(customerButton);
        add(employeeButton);
    }

    private void showLoginOrRegisterScreen() {
        removeAll();
        add(new LoginRegisterPanel());
        revalidate();
        repaint();
    }

    private void showEmployeePanel() {

        JPanel employeePanel = new JPanel(new GridLayout(5, 1));
        employeePanel.setPreferredSize(new Dimension(800, 1000));
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

        // Add back button that removes all panels and addes EmployeeOrCustomerPanel
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeAll();
                add(new EmployeeOrCustomerPanel());
                revalidate();
                repaint();
            }
        });

        employeePanel.add(backButton);

        removeAll();
        add(employeePanel);
        revalidate();
        repaint();
    }
}
