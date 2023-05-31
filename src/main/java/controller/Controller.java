package controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ui.EmployeeOrCustomerPanel;
import ui.LoginRegisterPanel;
import util.Password;
import util.Resources;

import static java.lang.System.exit;

public class Controller {

    public static final int MAX_RATING = 5;

    private static final String FAILURE_MSG =
            "A catastrophic failure occurred, and the application must close";
    private static final String UNLOGGED_ERROR_MSG =
            "There were errors that could not be logged.";

    // No errors
    private static final int SUCCESS_STATUS = 0;
    // Unrecoverable error
    private static final int FAILURE_STATUS = -1;
    // Recoverable error, but logging failed
    private static final int UNLOGGED_ERROR_STATUS = -2;
    // Recoverable error, and logging succeeded
    private static final int RECOVERABLE_ERROR_STATUS = -3;


    private static JFrame frame;
    private static boolean closed = false;

    public static void main(String[] args) {
        initialize();


        frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                close(false);
            }
        });
        frame.setMinimumSize(new Dimension(700, 400));
        frame.setPreferredSize(new Dimension(1000, 500));
        frame.setLayout(new BorderLayout());

        // var table = new ReviewsReport(600, 200);
        // table.addReview(1, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        // table.addReview(2, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        // table.addReview(3, "Joe", "sFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksa ");
        // table.addReview(2, "Floe", "sFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksasFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksasFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksasFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksa ");
        // table.addReview(4, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        // table.addReview(5, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");

//        var table = new FormTable(
//                600, 200,
//                true, true, true,
//                new String[]{"C1", "C2", "C3"},
//                new InputField[]{null, InputField.CHECKBOX, InputField.DROPDOWN}
//        );
//        table.setStrictColumnWidth(0, 100);
//        table.setStrictColumnWidth(1, 100);
//        table.setStrictColumnWidth(2, 100);

        //frame.add(table, BorderLayout.NORTH);


        frame.add(new EmployeeOrCustomerPanel(), BorderLayout.NORTH);

        frame.setVisible(true);
    }

    static void close(final boolean catastrophicFailure) {
        // Skip potential recursive call from WINDOW_CLOSING → WINDOW_CLOSED event hook
        if (closed)
            return;
        closed = true;

        // Perform any standard closing operations
        if (windowClosed())
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

        // Indicate whether the logging process has failed. Realistically, this
        // information would not be shown to the user but would instead be
        // provided to an external, more stable error logging system (e.g.,
        // with a redirected System.err), but no mechanism to capture errors in
        // this way and report them to a central server is available for this
        // basic project
        if (ProgramDirectoryManager.hasLogFailed())
            JOptionPane.showMessageDialog(
                    null,
                    UNLOGGED_ERROR_MSG,
                    "Error", JOptionPane.ERROR_MESSAGE
            );

        exit(
                catastrophicFailure ? // Catastrophic failure
                        FAILURE_STATUS :
                ProgramDirectoryManager.hasLogFailed() ? // Minor failure, log issues
                        UNLOGGED_ERROR_STATUS :
                ProgramDirectoryManager.hasRecoverableErrorOccurred() ? // Minor failure, no log issues
                        RECOVERABLE_ERROR_STATUS :
                SUCCESS_STATUS // No failure
        );
    }

    static void exitForFailure(final String publicMessage) {
        // Display a generic failure message, and if possible, display an error
        // message that can be revealed publicly
        JOptionPane.showMessageDialog(
                windowClosed() ? frame : null,
                FAILURE_MSG + (
                        publicMessage == null || publicMessage.isBlank() ?
                        "." : ": " + publicMessage + "."
                ),
                "Error", JOptionPane.ERROR_MESSAGE
        );

        close(true);
    }

    static void exitForFailure() {
        exitForFailure(null);
    }

    private static boolean windowClosed() {
        return frame != null && frame.isDisplayable();
    }

    private static void initialize() {
        // Initialize program folder and any needed resources, exiting and
        // logging the error if an initialization step fails catastrophically.
        //
        // Note that ProgramDirectoryManager.initialize() internally forces the
        // application to close without logging if a diagnosable failure occurs,
        // and if an undiagnosable error occurs in any initialization step, one
        // final attempt to log the error and exit is made by
        // ProgramDirectoryManager.logError(..., false). It is noted that this
        // measure will also fail if the undiagnosable failure occurs in
        // ProgramDirectoryManager.initialize().
        // If logging subsequently fails due to an undiagnosable error, a final
        // attempt is made to gracefully exit without logging, but if the issue
        // cannot be reported in any way, the system exits with the
        // FAILURE_STATUS code unconditionally to avoid printing exceptions to
        // the console.
        // This last, unlogged, unconditional exit should only be encountered
        // if the JVM does not support basic UI features (e.g., with an
        // outdated or modified JVM).
        try {
            ProgramDirectoryManager.initialize();
            Password.initialize();
            Resources.initialize();
            FunctionsAndProcedures.initialize();
            DBManager.initialize();

        } catch (Exception e1) {
            try {
                ProgramDirectoryManager.logError(e1, "Initialization failure", false);
            } catch (Exception e2) {
                try {
                    exitForFailure();
                } catch (Exception e3) {
                    System.exit(FAILURE_STATUS);
                }
            }
        }
    }
}