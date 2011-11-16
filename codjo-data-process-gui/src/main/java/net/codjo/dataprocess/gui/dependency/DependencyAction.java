package net.codjo.dataprocess.gui.dependency;
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
public class DependencyAction extends AbstractAction {

    public DependencyAction(MutableGuiContext ctxt) {
        super(ctxt, "D�pendances des listes de traitements",
              "Gestion des d�pendances des listes de traitements");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        Map repositoryMap = RepositoryClientHelper.getAllRepositoryNames((MutableGuiContext)getGuiContext());
        if (!repositoryMap.isEmpty()) {
            Log.info(getClass(),
                     "Ouverture de la fen�tre de configuration des d�pendances des listes de traitements.");
            JInternalFrame frame = new JInternalFrame("D�pendance des listes de traitements",
                                                      true, true, true, true);
            DependencyWindow dependencyWindow = new DependencyWindow((MutableGuiContext)getGuiContext(),
                                                                     frame);
            frame.setContentPane(dependencyWindow.getMainPanel());
            frame.setPreferredSize(new Dimension(800, 450));
            frame.setMinimumSize(new Dimension(800, 450));
            return frame;
        }
        else {
            JOptionPane.showMessageDialog(getDesktopPane(),
                                          "Il n'y a pas de r�f�rentiel de traitement !\nVeuillez en cr�er au moins un.",
                                          "D�pendances des listes de traitements",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
