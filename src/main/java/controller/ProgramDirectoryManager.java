package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ProgramDirectoryManager {

    // Statically editable file names
    private static final String PROGRAM_DIR_NAME = "Chemistry Retailer DB App";
    private static final String LOG_NAME = "log.txt";

    // Dynamically calculated paths
    private static String PROGRAM_DIR_PATH;
    private static String LOG_PATH;


    private static final String PROGRAM_DIR_ERROR_MSG =
            "An error occurred while attempting to access the program's data directory.";


    private static boolean LOG_FAILURE = false;

    public static void initialize() {
        try {
            // Use the Appdata folder if possible
            PROGRAM_DIR_PATH = System.getenv("APPDATA") + '\\' + PROGRAM_DIR_NAME;
            LOG_PATH = PROGRAM_DIR_PATH + '\\' + LOG_NAME;

            if (!createProgramDir())
                throw new IllegalStateException();
        } catch (SecurityException | IllegalStateException e) {
            // Otherwise use the execution directory
            PROGRAM_DIR_PATH = PROGRAM_DIR_NAME;
            LOG_PATH = PROGRAM_DIR_PATH + '\\' + LOG_NAME;

            if (!createProgramDir())
                Controller.exitForFailure(PROGRAM_DIR_ERROR_MSG);
        }
    }

    public static void logError(final Exception exception, final String message,
                                final boolean recoverable) {
        try (FileWriter fw = new FileWriter(LOG_PATH, true);
             PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {
            pw.println();

            pw.print("Timestamp: ");
            pw.println(System.currentTimeMillis());
            pw.print("Recoverable: ");
            pw.println(recoverable ? "yes" : "no");

            pw.println(message);

            if (exception != null)
                exception.printStackTrace(pw);

            pw.println();
        } catch (IOException ignored) {
            // An error occurred and could not be logged, but whether the
            // application should exit still depends on the severity of the
            // error (given by recoverable). Also, even though not reporting
            // the specified error here can complicate debugging, no safe way
            // exists to report the error without revealing detailed
            // information about common errors to the user since the logging
            // process has failed. Minimal information that an error occurred
            // is provided by having the application specify on exit that the
            // there were errors that could not be logged
            LOG_FAILURE = true;
        }

        if (!recoverable)
            Controller.exitForFailure();
    }

    public static void logError(final String message, final boolean recoverable) {
        logError(null, message, recoverable);
    }

    static boolean hasLogFailed() {
        return LOG_FAILURE;
    }

    private static boolean createProgramDir() {
        final File dir = new File(PROGRAM_DIR_PATH);

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        if (dir.exists()) {
            // Create log file before any errors need to be logged to prevent
            // leaking information about when the first recoverable error occurs
            try {
                var log = new File(LOG_PATH);
                //noinspection ResultOfMethodCallIgnored
                log.createNewFile();

                return log.exists();
            } catch (IOException | SecurityException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
