import table.ColumnRenderer;
import table.ReportTable;

import javax.swing.JFrame;
import java.awt.Dimension;

public class Main {
    public static void main(String[] args) {
        var frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 300));
        frame.setPreferredSize(new Dimension(800, 500));

        var table = new ReportTable(
                500, 100,
                true, true,
                new String[]{"Stars", "Col2", "Col3", "Col4"},
                ColumnRenderer.DEFAULT, ColumnRenderer.DEFAULT, ColumnRenderer.DEFAULT, ColumnRenderer.WRAP
        );

        frame.add(table);

        frame.setVisible(true);

        table.addRow(1, 2, 3, "dsssssssssssssssssss assssssssssssssss assssssssssssssssd asdasdas");
        table.addRow(2, 3, 4, "WAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA AAAAAAAAAAAAAAAAAAAAAA AAAAAAAAAAAAAAAAAAAAAAAA");
        table.addRow(3, 4, 5, "f");
        table.addRow(4, 5, 6, "g");
        table.addRow(5, 6, 7, "h");
        table.addRow(6, 4, 8, "i");
        table.addRow(7, 8, 9, "j");
        table.addRow(8, 9, 10, "k");
        table.addRow(9, 10, 11, "l");
        table.addRow(10, 11, 12, "m");
    }
}