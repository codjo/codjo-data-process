package net.codjo.dataprocess.gui.param;
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
public class ExecutionListPriorityAction extends AbstractAction {

    public ExecutionListPriorityAction(MutableGuiContext ctxt) {
        super(ctxt,
              "Priorité des listes de traitements",
              "Configuration de la priorité des listes de traitements");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        Map repositoryMap = RepositoryClientHelper.getAllRepositoryNames((MutableGuiContext)getGuiContext());
        if (!repositoryMap.isEmpty()) {
            Log.info(getClass(),
                     "Ouverture de la fenêtre de configuration des priorité des listes de traitements.");
            JInternalFrame frame = new JInternalFrame("Priorité des listes de traitements", true, true, true,
                                                      true);
            ExecutionListPriorityWindow executionListPriorityWindow =
                  new ExecutionListPriorityWindow((MutableGuiContext)getGuiContext(), frame);
            frame.setContentPane(executionListPriorityWindow.getMainPanel());
            frame.setPreferredSize(new Dimension(800, 600));
            frame.setMinimumSize(new Dimension(800, 600));
            return frame;
        }
        else {
            JOptionPane.showMessageDialog(getDesktopPane(),
                                          "Il n'y a pas de référentiel de traitement !\nVeuillez en créer au moins un.",
                                          "Priorité des listes de traitements",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
