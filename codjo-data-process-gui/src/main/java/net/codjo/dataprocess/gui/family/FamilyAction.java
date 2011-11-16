package net.codjo.dataprocess.gui.family;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.gui.util.std.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Dimension;
import java.util.Map;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
/**
 *
 */
public class FamilyAction extends AbstractAction {

    public FamilyAction(MutableGuiContext ctxt) {
        super(ctxt, "Familles", "Gestion des familles");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        Map repositoryMap = RepositoryClientHelper.getAllRepositoryNames((MutableGuiContext)getGuiContext());
        if (!repositoryMap.isEmpty()) {
            Log.info(getClass(),
                     "Ouverture de la fen�tre de configuration des familles de r�f�rentiels de traitements.");
            JInternalFrame frame = new JInternalFrame("Gestion des familles des r�f�rentiels de traitements",
                                                      true, true, true, true);
            FamilyWindow familyWindow = new FamilyWindow((MutableGuiContext)getGuiContext(), frame);
            frame.setContentPane(familyWindow.getMainPanel());
            frame.setPreferredSize(new Dimension(480, 350));
            frame.setMinimumSize(new Dimension(480, 350));
            return frame;
        }
        else {
            JOptionPane.showMessageDialog(getDesktopPane(),
                                          "Il n'y a pas de r�f�rentiel de traitement !\nVeuillez en cr�er au moins un.",
                                          "Gestion des familles",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
