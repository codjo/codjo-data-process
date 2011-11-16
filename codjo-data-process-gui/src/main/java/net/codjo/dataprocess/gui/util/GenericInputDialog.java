/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
/**
 *
 */
public class GenericInputDialog extends JDialog implements ActionListener, PropertyChangeListener {
    private String typedText = null;
    private JTextField textField;
    private JOptionPane optionPane;
    private String btnString1 = "Valider";
    private DialogInputControl dialogInputControl;


    /**
     * Creates the reusable dialog.
     */
    public GenericInputDialog(Frame aFrame, String title, String labelInput,
                              DialogInputControl dialogInputControl) {
        super(aFrame, true);
        this.dialogInputControl = dialogInputControl;
        setTitle(title);

        textField = new JTextField(30);

        //Create an array of the text and components to be displayed.
        Object[] array = {labelInput, textField};

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, "Annuler"};

        //Create the JOptionPane.
        optionPane =
              new JOptionPane(array, JOptionPane.QUESTION_MESSAGE,
                              JOptionPane.YES_NO_OPTION, null, options, options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                /*
                * Instead of directly closing the window,
                * we're going to change the JOptionPane's
                * value property.
                */
                optionPane.setValue(JOptionPane.CLOSED_OPTION);
            }
        });

        //Register an eventType handler that puts the text into the option pane.
        textField.addActionListener(this);

        //Register an eventType handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        pack();
    }


    /**
     * Returns null if the typed string was invalid; otherwise, returns the string as the user entered it.
     *
     * @return typed text
     */
    public String getValidatedText() {
        return typedText;
    }


    /**
     * This method handles events for the text field.
     */
    public void actionPerformed(ActionEvent evt) {
        optionPane.setValue(btnString1);
    }


    /**
     * This method reacts to state changes in the option pane.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if (isVisible()
            && (evt.getSource() == optionPane)
            && (JOptionPane.VALUE_PROPERTY.equals(prop)
                || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change eventType will be fired.
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                typedText = textField.getText();

                // Valeur entrée OK
                if (dialogInputControl != null) {
                    if (dialogInputControl.proceedControl(typedText)) {
                        // On ferme la boite de dialogue
                        clearAndHide();
                    }

                    // Valeur entrée KO
                    else {
                        //text was invalid
                        textField.selectAll();
                        JOptionPane.showMessageDialog(GenericInputDialog.this,
                                                      dialogInputControl.getError(), "Erreur de saisie !",
                                                      JOptionPane.ERROR_MESSAGE);
                        typedText = null;
                    }
                }
                else {
                    clearAndHide();
                }
            }
            else { //user closed dialog or clicked cancel
                typedText = null;
                clearAndHide();
            }
        }
    }


    /**
     * This method clears the dialog and hides it.
     */
    public void clearAndHide() {
        textField.setText(null);
        setVisible(false);
    }
}
