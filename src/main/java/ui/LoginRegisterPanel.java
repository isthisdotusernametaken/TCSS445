package ui;

import java.awt.*;
import java.util.Objects;
import javax.swing.*;

import controller.FunctionsAndProcedures;

import static controller.FunctionsAndProcedures.SUCCESS;

public class LoginRegisterPanel extends JPanel {

    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel registerPanel;
    private LoggedInCustomerPanel customerPanel;

    public LoginRegisterPanel(final Runnable backFunction) {
        setLayout(new BorderLayout());

        mainPanel = createMainPanel(backFunction);
        loginPanel = createLoginPanel();
        registerPanel = createRegisterPanel();
        customerPanel = new LoggedInCustomerPanel(() -> {
            removeAll();
            add(mainPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createMainPanel(final Runnable backFunction) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            remove(mainPanel);
            add(loginPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            remove(mainPanel);
            add(registerPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> backFunction.run());

        mainPanel.add(loginButton);
        mainPanel.add(registerButton);
        mainPanel.add(backButton);

        return mainPanel;
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
    
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
    
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
    
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        backButton.addActionListener(e -> {
            remove(loginPanel);
            add(mainPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(emailLabel);
        centerPanel.add(emailField);
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(loginButton);
        centerPanel.add(backButton);
        centerPanel.add(Box.createVerticalGlue());
    
        loginPanel.add(Box.createVerticalGlue());
        loginPanel.add(centerPanel);
        loginPanel.add(Box.createVerticalGlue());
    
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            Object[] loginResult = FunctionsAndProcedures.login(email, password);
            String loginStatus = (String) loginResult[0];

            if (Objects.equals(loginStatus, SUCCESS)) {
                login((int) loginResult[1]); // Login with retrieved CustomerID

                emailField.setText("");
                passwordField.setText("");
            }
            else
                JOptionPane.showMessageDialog(loginPanel, loginStatus, "Error", JOptionPane.ERROR_MESSAGE);
        });
    
        return loginPanel;
    }

    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
    
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
    
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
    
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(20);
    
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(20);
    
        JLabel addressLine1Label = new JLabel("Address Line 1:");
        JTextField addressLine1Field = new JTextField(20);
    
        JLabel addressLine2Label = new JLabel("Address Line 2:");
        JTextField addressLine2Field = new JTextField(20);
    
        JLabel zipCodeLabel = new JLabel("Zip Code:");
        JTextField zipCodeField = new JTextField(20);
    
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");
    
        backButton.addActionListener(e -> {
            remove(registerPanel);
            emailField.setText("");
            passwordField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            addressLine1Field.setText("");
            addressLine2Field.setText("");
            zipCodeField.setText("");
            add(mainPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    
        registerButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String addressLine1 = addressLine1Field.getText();
            String addressLine2 = addressLine2Field.getText();
            int zipCode;
            try {
                zipCode = Integer.parseInt(zipCodeField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(registerPanel, "Zip Code must be a number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String result = FunctionsAndProcedures.registerCustomer(email, password, firstName, lastName, addressLine1, addressLine2, zipCode);
            System.out.println(result);
            if (Objects.equals(result, SUCCESS)) { // Objects.equals is null-safe, unlike ==
                remove(registerPanel);
                add(loginPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(registerPanel, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        registerPanel.add(emailLabel);
        registerPanel.add(emailField);
        registerPanel.add(passwordLabel);
        registerPanel.add(passwordField);
        registerPanel.add(firstNameLabel);
        registerPanel.add(firstNameField);
        registerPanel.add(lastNameLabel);
        registerPanel.add(lastNameField);
        registerPanel.add(addressLine1Label);
        registerPanel.add(addressLine1Field);
        registerPanel.add(addressLine2Label);
        registerPanel.add(addressLine2Field);
        registerPanel.add(zipCodeLabel);
        registerPanel.add(zipCodeField);
        registerPanel.add(registerButton);
        registerPanel.add(backButton);
    
        return registerPanel;
    }

    private void login(final int customerID) {
        customerPanel.setCustomer(customerID);

        removeAll();
        add(customerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}