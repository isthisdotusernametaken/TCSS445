package util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Resources {

    private static final ImageIcon EMPTY = new ImageIcon(new BufferedImage(
            1, 1, BufferedImage.TYPE_INT_ARGB
    ));
    private static ImageIcon STAR;

    public static void initialize() {
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
            // TODO: log error
            return EMPTY;
        }

    }

    private static Image getImage(final String filename,
                                  final int width, final int height)
                                    throws NullPointerException, IOException {
        return ImageIO.read(Objects.requireNonNull(
                Resources.class.getClassLoader().getResourceAsStream(
                        "resources/" + filename
                )
        )).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}
