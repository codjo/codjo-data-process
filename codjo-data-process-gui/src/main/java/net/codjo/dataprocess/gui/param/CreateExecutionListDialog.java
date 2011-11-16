/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.param;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.InternalInputDialog;
import net.codjo.mad.client.request.RequestException;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
/**
 *
 */
public class CreateExecutionListDialog extends InternalInputDialog {
    private static final int MAX_LENGTH = 50;
    private ExecutionListParamWindow.ExecListParamWindowAdapter execListParamWindowAdapter;
    private JInternalFrame frame;


    public CreateExecutionListDialog(ExecutionListParamWindow.ExecListParamWindowAdapter execListParamWindowAdapter,
                                     JInternalFrame frame) {
        super(frame,
              "Ajout d'une liste de traitements",
              "Nouvelle liste de traitements:",
              UIManager.getIcon("dataprocess.add2"));
        this.execListParamWindowAdapter = execListParamWindowAdapter;
        this.frame = frame;
    }


    @Override
    public String input() {
        String value = super.input();
        if (value == null) {
            return null;
        }
        try {
            if (value.length() <= MAX_LENGTH) {
                if (execListParamWindowAdapter.isAlreadyExistExecutionList(value)) {
                    JOptionPane.showInternalMessageDialog(frame,
                                                          "Il y a déjà une liste de traitements nommée '"
                                                          + value + "' dans ce référentiel de traitement.",
                                                          "Création de la liste de traitements impossible",
                                                          JOptionPane.ERROR_MESSAGE);
                    return input();
                }
                else {
                    return value;
                }
            }
            else {
                JOptionPane.showInternalMessageDialog(frame, "Vous avez saisi " + value.length()
                                                             + " caractères.\nVous ne devez pas saisir plus de "
                                                             + MAX_LENGTH + " caractères.",
                                                      "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return input();
            }
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
            return null;
        }
    }
}
