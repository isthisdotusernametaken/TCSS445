package ui.table;

import util.Resources;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class FormTable extends Table {

    private final JComponent[] inputFields;

    public FormTable(final int preferredWidth, final int preferredHeight,
                     final boolean showHorizontalLines, final boolean showVerticalLines,
                     final boolean separateHeader,
                     final String[] columnNames,
                     final InputField[] inputFieldTypes,
                     final ColumnRenderer... columnRenderers) {
        super(
                preferredWidth, preferredHeight,
                true, showHorizontalLines, showVerticalLines, separateHeader,
                columnNames, columnRenderers
        );

        inputFields = new JComponent[inputFieldTypes.length];
        createNewRowPanel(inputFieldTypes);
    }

    private void createNewRowPanel(final InputField[] inputFieldTypes) {
        var newRowPanel = new JPanel();
        newRowPanel.setLayout(new BoxLayout(newRowPanel, BoxLayout.PAGE_AXIS)); // Vertical layout

        // Add the input fields in the first line
        var fieldPanel = new JPanel(new FlowLayout());
        for (int i = 0; i < inputFieldTypes.length; i++) {
            inputFields[i] = InputField.create(inputFieldTypes[i]);
            fieldPanel.add(inputFields[i]);
            // set size?
        }

        newRowPanel.add(fieldPanel);

        // Add the button for submitting the new row at the end of the second line
        newRowPanel.add(buildSubmitPanel());

        // Add this panel as a small bar above the table
        add(newRowPanel, BorderLayout.PAGE_START);
    }

    private static JPanel buildSubmitPanel() {
        var submitPanel = new JPanel(new BorderLayout());

        var addButton = new JButton(Resources.add());
        addButton.setText("Add Row");
        addButton.setToolTipText("Submit the provided field values as a new row for this table");
        // Horizontal position of text is trailing (left of icon) by default

        submitPanel.add(addButton, BorderLayout.LINE_END); // Add button at end of line

        return submitPanel;
    }
}
