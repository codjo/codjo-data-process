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
                     "Ouverture de la fenêtre de configuration des familles de référentiels de traitements.");
            JInternalFrame frame = new JInternalFrame("Gestion des familles des référentiels de traitements",
                                                      true, true, true, true);
            FamilyWindow familyWindow = new FamilyWindow((MutableGuiContext)getGuiContext(), frame);
            frame.setContentPane(familyWindow.getMainPanel());
            frame.setPreferredSize(new Dimension(480, 350));
            frame.setMinimumSize(new Dimension(480, 350));
            return frame;
        }
        else {
            JOptionPane.showMessageDialog(getDesktopPane(),
                                          "Il n'y a pas de référentiel de traitement !\nVeuillez en créer au moins un.",
                                          "Gestion des familles",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
