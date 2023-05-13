package util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import controller.ProgramDirectoryManager;

public class Resources {

    private static final ImageIcon EMPTY = new ImageIcon(new BufferedImage(
            10, 10, BufferedImage.TYPE_INT_ARGB
    ), "Image");
    private static ImageIcon STAR;

    private static final String IMAGE_LOAD_ERROR =
            "An image could not be loaded - ";

    public static void initialize() {
        // Set unloadable textures to a black square
        var graphics = ((BufferedImage) EMPTY.getImage()).createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0,  EMPTY.getIconWidth(), EMPTY.getIconHeight());

        // Load textures
        STAR = read("star.png", 30, 30);
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
