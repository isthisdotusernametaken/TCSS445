package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import controller.FunctionsAndProcedures;

public class LoginRegisterPanel extends JPanel {

    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel registerPanel;

    public LoginRegisterPanel() {
        setLayout(new BorderLayout());

        mainPanel = createMainPanel();
        loginPanel = createLoginPanel();
        registerPanel = createRegisterPanel();

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(mainPanel);
                add(loginPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(mainPanel);
                add(registerPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeAll();
                add(new EmployeeOrCustomerPanel());
                revalidate();
                repaint();
            }
        });

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
    
        // loginButton.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
                
        //     }
        // });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(loginPanel);
                emailField.setText("");
                passwordField.setText("");
                add(mainPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
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
    
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = emailField.getText();
                String password = new String(passwordField.getPassword());
    
                Object[] loginResult = FunctionsAndProcedures.login(username, password);
                String loginStatus = (String) loginResult[0];
    
                if (loginStatus.equals("SUCCESS")) {
                    int customerID = (int) ((Object[]) loginResult[1])[0];

                    remove(loginPanel);

                    JPanel customerPanel = new JPanel(new GridLayout(4, 1));
                    JLabel customerLabel = new JLabel("Customer Label");
                    customerPanel.setPreferredSize(new Dimension(800, 1000));
                    customerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    Scenarios customerScenarios = new Scenarios(false);
                    customerPanel.add(customerLabel);
                    customerPanel.add(customerScenarios);

                    JButton backButton = new JButton("Back");
                    backButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            removeAll();
                            add(new EmployeeOrCustomerPanel());
                            revalidate();
                            repaint();
                        }
                    });

                    customerPanel.add(backButton);

                    JScrollPane scrollablecustomerPanel = new JScrollPane(customerPanel);
                    scrollablecustomerPanel.setPreferredSize(new Dimension(2000, 500));

                    add(scrollablecustomerPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();

                } else {
                    JOptionPane.showMessageDialog(loginPanel, loginStatus, "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
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
    
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });
    
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
    
                if (result.equals("SUCCESS")) {
                    remove(registerPanel);
                    add(loginPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(registerPanel, result, "Error", JOptionPane.ERROR_MESSAGE);
                }
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

    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(new Runnable() {
    //         public void run() {
    //             Controller.initialize();
    //             new LoginRegisterPanel();
    //         }
    //     });
    // }
}