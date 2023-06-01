package ui;

import javax.swing.JOptionPane;

public class UIUtil {

    static void showError(final String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Error", JOptionPane.ERROR_MESSAGE
        );
    }

    static void showMessage(final String message) {
        JOptionPane.showMessageDialog(
                null,
                message
        );
    }
}
