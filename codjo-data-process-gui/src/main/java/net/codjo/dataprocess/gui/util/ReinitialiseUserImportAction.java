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
              "R�initialisation du contr�le de la concurrence d'acc�s aux imports",
              "R�initialisation du contr�le de la concurrence d'acc�s aux imports");
        this.ctxt = ctxt;
    }


    public void actionPerformed(ActionEvent evt) {
        try {
            UtilsClientHelper.reinitialiseUserImport(ctxt);
            JOptionPane.showInternalMessageDialog(ctxt.getDesktopPane(),
                                                  "Le contr�le de la concurrence d'acc�s aux imports a �t� r�initialis� avec succ�s.",
                                                  "R�initialisation",
                                                  JOptionPane.INFORMATION_MESSAGE);
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(getDesktopPane(), getClass(), "Erreur interne", ex);
        }
    }
}