/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher;
import net.codjo.agent.UserId;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiPlugin;
import net.codjo.dataprocess.gui.util.std.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
/**
 *
 */
public class NewExecutionListLauncherAction extends AbstractAction {
    private NewExecutionListLauncherWindow newExecutionListLauncherWindow;
    private UserId userId;
    private DataProcessGuiPlugin dataProcessGuiPlugin;


    public NewExecutionListLauncherAction(MutableGuiContext ctxt,
                                          UserId userId,
                                          DataProcessGuiPlugin dataProcessGuiPlugin) {
        super(ctxt, "Exécuter des traitements", "Exécuter des traitements");
        this.userId = userId;
        this.dataProcessGuiPlugin = dataProcessGuiPlugin;
        putValue(SMALL_ICON, loadActionIcon("/images/Manager.gif"));
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        if (dataProcessGuiPlugin.getConfiguration().getUser().getCurrentRepository() != null) {
            JInternalFrame frame = new JInternalFrame("Exécution des listes de traitements", true, true, true,
                                                      true);

            Log.info(getClass(), "Ouverture de la fenêtre d'exécution des listes de traitements.");
            newExecutionListLauncherWindow = new NewExecutionListLauncherWindow(new LocalGuiContext(ctxt),
                                                                                userId,
                                                                                false,
                                                                                dataProcessGuiPlugin,
                                                                                frame);
            frame.setContentPane(newExecutionListLauncherWindow.getMainPanel());
            frame.setPreferredSize(new Dimension(1150, 880));
            frame.setMinimumSize(new Dimension(900, 780));
            return frame;
        }
        else {
            JOptionPane.showMessageDialog(getGuiContext().getMainFrame(),
                                          "Vous n'avez actuellement accès à aucun repository.\n"
                                          + "Merci de contacter un responsable de l'application svp.",
                                          "Information importante",
                                          JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }


    @Override
    protected void displayNewWindow() {
        super.displayNewWindow();
        if (newExecutionListLauncherWindow != null) {
            newExecutionListLauncherWindow.loadData();
        }
    }
}
