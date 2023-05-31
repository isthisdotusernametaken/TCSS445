package ui.table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginRegisterPanel extends JPanel {
    private JPanel initialPanel;
    private JPanel loginPanel;
    private JPanel registerPanel;

    public LoginRegisterPanel() {
        setLayout(new BorderLayout());

        initialPanel = new JPanel();
        initialPanel.setLayout(new FlowLayout());

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        initialPanel.add(loginButton);
        initialPanel.add(registerButton);

        loginPanel = createLoginPanel();

        registerPanel = createRegisterPanel();

        add(initialPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(initialPanel);
                add(loginPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(initialPanel);
                add(registerPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
    
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
    
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
    
        JButton loginButton = new JButton("Login");
    
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
    
        return loginPanel;
    }

    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel();
    
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
    
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
    
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
    
        JButton registerButton = new JButton("Register");
    
        registerPanel.add(usernameLabel);
        registerPanel.add(usernameField);
        registerPanel.add(passwordLabel);
        registerPanel.add(passwordField);
        registerPanel.add(emailLabel);
        registerPanel.add(emailField);
        registerPanel.add(registerButton);
    
        return registerPanel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Login and Register Panel Example");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new LoginRegisterPanel());
                frame.setSize(400, 300);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}