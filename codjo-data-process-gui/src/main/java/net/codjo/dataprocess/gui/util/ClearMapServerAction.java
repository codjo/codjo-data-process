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
 * Classe permettant de r�initialiser la Map du serveur en cas de crash d'un des clients (ex: si un client n'a
 * pas quitt� pas normalement � cause d'un probl�me quelconque, ses infos dans la Map ne seront pas mises �
 * jour comme dans la cas ou on quitte normalement)
 */
public class ClearMapServerAction extends AbstractGuiAction {
    private MutableGuiContext ctxt;


    public ClearMapServerAction(MutableGuiContext ctxt) {
        super(ctxt,
              "R�initialisation de l'acc�s aux fen�tres et de la map serveur",
              "R�initialisation de l'acc�s aux fen�tres et de la map serveur");
        this.ctxt = ctxt;
    }


    public void actionPerformed(ActionEvent evt) {
        try {
            UtilsClientHelper.cmdMapServer(ctxt, DataProcessConstants.MapCommand.CLEAR, "", "");
            JOptionPane.showInternalMessageDialog(ctxt.getDesktopPane(),
                                                  "L'acc�s aux fen�tres et la map serveur ont �t� r�initialis�s avec succ�s.",
                                                  "R�initialisation",
                                                  JOptionPane.INFORMATION_MESSAGE);
        }
        catch (RequestException ex) {
            Log.error(getClass(), ex);
            ErrorDialog.show(getDesktopPane(), "Erreur interne", ex);
        }
    }
}