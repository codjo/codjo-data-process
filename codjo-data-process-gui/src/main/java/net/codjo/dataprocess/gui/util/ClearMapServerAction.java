package net.codjo.dataprocess.gui.util;
import net.codjo.dataprocess.client.UtilsClientHelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
/**
 * Classe permettant de réinitialiser la Map du serveur en cas de crash d'un des clients (ex: si un client n'a
 * pas quitté pas normalement à cause d'un problème quelconque, ses infos dans la Map ne seront pas mises à
 * jour comme dans la cas ou on quitte normalement)
 */
public class ClearMapServerAction extends AbstractGuiAction {
    private MutableGuiContext ctxt;


    public ClearMapServerAction(MutableGuiContext ctxt) {
        super(ctxt,
              "Réinitialisation de l'accès aux fenêtres et de la map serveur",
              "Réinitialisation de l'accès aux fenêtres et de la map serveur");
        this.ctxt = ctxt;
    }


    public void actionPerformed(ActionEvent evt) {
        try {
            UtilsClientHelper.cmdMapServer(ctxt, DataProcessConstants.MapCommand.CLEAR, "", "");
            JOptionPane.showInternalMessageDialog(ctxt.getDesktopPane(),
                                                  "L'accès aux fenêtres et la map serveur ont été réinitialisés avec succès.",
                                                  "Réinitialisation",
                                                  JOptionPane.INFORMATION_MESSAGE);
        }
        catch (RequestException ex) {
            Log.error(getClass(), ex);
            ErrorDialog.show(getDesktopPane(), "Erreur interne", ex);
        }
    }
}