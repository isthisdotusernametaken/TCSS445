package ui.table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginRegisterPanel extends JFrame {

    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel registerPanel;

    public LoginRegisterPanel() {
        setTitle("Login and Register Panel Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        mainPanel = createMainPanel();
        loginPanel = createLoginPanel();
        registerPanel = createRegisterPanel();

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
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

        mainPanel.add(loginButton);
        mainPanel.add(registerButton);

        return mainPanel;
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(loginPanel);
                add(mainPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(backButton);

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
        JButton backButton = new JButton("Back");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(registerPanel);
                add(loginPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(registerPanel);
                add(mainPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });

        registerPanel.add(usernameLabel);
        registerPanel.add(usernameField);
        registerPanel.add(passwordLabel);
        registerPanel.add(passwordField);
        registerPanel.add(emailLabel);
        registerPanel.add(emailField);
        registerPanel.add(registerButton);
        registerPanel.add(backButton);

        return registerPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginRegisterPanel();
            }
        });
    }
}