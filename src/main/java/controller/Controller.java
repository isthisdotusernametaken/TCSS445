package controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

import table.ReviewsReport;
import util.Resources;

import static java.lang.System.exit;

public class Controller {

    private static final String FAILURE_MSG =
            "A catastrophic failure occurred, and the application must close";
    private static final String UNLOGGED_ERROR_MSG =
            "There were errors that could not be logged.";
    private static final int SUCCESS_STATUS = 0;
    private static final int FAILURE_STATUS = -1;
    private static final int UNLOGGED_ERROR_STATUS = -2;

    private static JFrame frame;

    public static void main(String[] args) {
        ProgramDirectoryManager.initialize();
        Resources.initialize();


        frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700, 400));
        frame.setPreferredSize(new Dimension(1000, 500));
        frame.setLayout(new BorderLayout());

        var table = new ReviewsReport(600, 200);
        table.addReview(1, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        table.addReview(2, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        table.addReview(3, "Joe", "sFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksa ");
        table.addReview(2, "Floe", "sFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksasFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksasFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksasFADS sdaf dfsgSA FdSAd saDFsf asdAW Fasdawe dldjksa ");
        table.addReview(4, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");
        table.addReview(5, "Jim", "hahaha hash ahds odasd jkasdkl asldkas ldjksa ");

        frame.add(table, BorderLayout.NORTH);

        frame.setVisible(true);


        close(SUCCESS_STATUS);
    }

    static void close(final int status) {
        // Perform any standard closing operations
        if (isNotClosed())
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
                status == SUCCESS_STATUS && ProgramDirectoryManager.hasLogFailed() ?
                UNLOGGED_ERROR_STATUS : status
        );
    }

    static void exitForFailure(final String publicMessage) {
        // Display a generic failure message, and if possible, display an error
        // message that can be revealed publicly
        JOptionPane.showMessageDialog(
                isNotClosed() ? frame : null,
                FAILURE_MSG + (
                        publicMessage == null || publicMessage.isBlank() ?
                        "." : ": " + publicMessage + "."
                ),
                "Error", JOptionPane.ERROR_MESSAGE
        );

        close(FAILURE_STATUS);
    }

    static void exitForFailure() {
        exitForFailure(null);
    }

    private static boolean isNotClosed() {
        return frame != null && frame.isDisplayable();
    }
}