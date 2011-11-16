package net.codjo.dataprocess.gui.util;
import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
/**
 *
 */
public class InternalInputDialog {
    private JInternalFrame frame;
    private String titre;
    private String message;
    private Icon icon;


    public InternalInputDialog(JInternalFrame frame, String titre, String message, Icon icon) {
        this.frame = frame;
        this.titre = titre;
        this.message = message;
        this.icon = icon;
    }


    public String input() {
        Object obj = JOptionPane.showInternalInputDialog(frame,
                                                         message,
                                                         titre,
                                                         JOptionPane.QUESTION_MESSAGE,
                                                         icon,
                                                         null,
                                                         null);
        if (obj == null) {
            return null;
        }
        String value = obj.toString().trim();
        if (value.length() == 0) {
            JOptionPane.showInternalMessageDialog(frame,
                                                  "Veuillez saisir une valeur svp.",
                                                  "Erreur",
                                                  JOptionPane.ERROR_MESSAGE);
            return input();
        }
        return value;
    }
}
