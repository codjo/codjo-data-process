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
              "Priorit� des listes de traitements",
              "Configuration de la priorit� des listes de traitements");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        Map repositoryMap = RepositoryClientHelper.getAllRepositoryNames((MutableGuiContext)getGuiContext());
        if (!repositoryMap.isEmpty()) {
            Log.info(getClass(),
                     "Ouverture de la fen�tre de configuration des priorit� des listes de traitements.");
            JInternalFrame frame = new JInternalFrame("Priorit� des listes de traitements", true, true, true,
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
                                          "Il n'y a pas de r�f�rentiel de traitement !\nVeuillez en cr�er au moins un.",
                                          "Priorit� des listes de traitements",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
