package util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import controller.ProgramDirectoryManager;

public class Resources {

    private static final int EMPTY_SIZE = 10;
    public static final int STAR_SIZE = 10;


    private static final ImageIcon EMPTY = new ImageIcon(new BufferedImage(
            EMPTY_SIZE, EMPTY_SIZE, BufferedImage.TYPE_INT_ARGB
    ), "Image");
    // These fields are initialized via the initialize method and not via
    // built-in class initialization so that Controller can explicitly enforce
    // the order of initialization events. As a consequence, the fields cannot
    // be made final and thus cannot safely be non-private, so they are instead
    // accessed with public getter methods.
    private static ImageIcon STAR;


    private static final String IMAGE_LOAD_ERROR =
            "An image could not be loaded - ";

    public static void initialize() {
        // Set unloadable textures to a black square
        var graphics = ((BufferedImage) EMPTY.getImage()).createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0,  EMPTY_SIZE, EMPTY_SIZE);

        // Load textures
        STAR = read("star.png", STAR_SIZE, STAR_SIZE);
    }

    public static ImageIcon star() {
        return STAR;
    }

    private static ImageIcon read(final String filename,
                                  final int width, final int height) {
        try {
            return new ImageIcon(getImage(filename, width, height), "Image");
        } catch (IOException | NullPointerException e) {
            ProgramDirectoryManager.logError(e, IMAGE_LOAD_ERROR + filename, true);
            return EMPTY;
        }
    }

    private static Image getImage(final String filename,
                                  final int width, final int height)
                                    throws NullPointerException, IOException {
        return ImageIO.read(Objects.requireNonNull(
                Resources.class.getClassLoader().getResourceAsStream(filename)
        )).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}
