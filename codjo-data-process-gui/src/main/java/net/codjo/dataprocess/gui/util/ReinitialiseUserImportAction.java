package net.codjo.dataprocess.gui.util;
import net.codjo.dataprocess.client.UtilsClientHelper;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
/**
 *
 */
public class ReinitialiseUserImportAction extends AbstractGuiAction {
    private MutableGuiContext ctxt;


    public ReinitialiseUserImportAction(MutableGuiContext ctxt) {
        super(ctxt,
              "Réinitialisation du contrôle de la concurrence d'accès aux imports",
              "Réinitialisation du contrôle de la concurrence d'accès aux imports");
        this.ctxt = ctxt;
    }


    public void actionPerformed(ActionEvent evt) {
        try {
            UtilsClientHelper.reinitialiseUserImport(ctxt);
            JOptionPane.showInternalMessageDialog(ctxt.getDesktopPane(),
                                                  "Le contrôle de la concurrence d'accès aux imports a été réinitialisé avec succès.",
                                                  "Réinitialisation",
                                                  JOptionPane.INFORMATION_MESSAGE);
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(getDesktopPane(), getClass(), "Erreur interne", ex);
        }
    }
}