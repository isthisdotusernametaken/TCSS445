package ui.table;

import ui.ScrollableTextArea;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public enum InputField {
    TEXT,
    DROPDOWN,
    CHECKBOX;

    // This factory is provided to encapsulate the input components used by
    // FormTable within the table package. External code must use the
    // InputField enum to specify an input component type, ensuring that all
    // used input components are of one of the predetermined valid types and
    // are nonnull
    static JComponent create(final InputField type) {
        return type == null ?
                new ScrollableTextArea() :
                switch (type) {
                    case TEXT -> new ScrollableTextArea();
                    case DROPDOWN -> new JComboBox<String>();
                    case CHECKBOX -> new JCheckBox();
                };
    }
}
