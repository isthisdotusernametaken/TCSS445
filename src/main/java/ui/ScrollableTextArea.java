package ui;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;

public class ScrollableTextArea extends JScrollPane {

    private static final int HEIGHT_MULTIPLIER = 3;

    private final JTextArea text;

    public ScrollableTextArea() {
        text = new JTextArea();
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        setViewportView(text);
        setPreferredSize(new Dimension(
                getPreferredSize().width,
                text.getFont().getSize() * HEIGHT_MULTIPLIER
        ));
    }

    public String getText() {
        return text.getText();
    }
}
