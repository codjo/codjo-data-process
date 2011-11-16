/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
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
public class ExecutionListParamAction extends AbstractAction {
    private ExecutionListParamWindow executionListParamWindow;


    public ExecutionListParamAction(MutableGuiContext ctxt) {
        super(ctxt, "Listes de traitements", "Gestion des listes de traitements");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        Map repositoryMap = RepositoryClientHelper
              .getAllRepositoryNames((MutableGuiContext)getGuiContext());
        if (!repositoryMap.isEmpty()) {
            Log.info(getClass(), "Ouverture de la fenêtre de gestion des listes de traitements.");
            JInternalFrame frame = new JInternalFrame("Gestion des listes de traitements", true, true, true,
                                                      true);
            executionListParamWindow = new ExecutionListParamWindow((MutableGuiContext)getGuiContext(),
                                                                    frame);
            frame.setContentPane(executionListParamWindow.getMainPanel());
            frame.setPreferredSize(new Dimension(1150, 700));
            frame.setMinimumSize(new Dimension(900, 600));
            return frame;
        }
        else {
            JOptionPane.showMessageDialog(getDesktopPane(),
                                          "Il n'y a pas de référentiel de traitement !\nVeuillez en créer au moins un.",
                                          "Paramétrage des listes de traitements",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }


    @Override
    protected void displayNewWindow() {
        super.displayNewWindow();
        if (executionListParamWindow != null) {
            executionListParamWindow.load();
        }
    }
}
