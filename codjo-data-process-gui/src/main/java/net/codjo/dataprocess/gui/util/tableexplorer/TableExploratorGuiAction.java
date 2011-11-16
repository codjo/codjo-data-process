package net.codjo.dataprocess.gui.util.tableexplorer;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiPlugin;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.std.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 *
 */
public class TableExploratorGuiAction extends AbstractAction {
    private DataProcessGuiPlugin dataProcessGuiPlugin;
    private TableExploratorGui tableExploratorGui;


    public TableExploratorGuiAction(GuiContext ctxt, DataProcessGuiPlugin dataProcessGuiPlugin) {
        super(ctxt, "Explorateur de tables", "Explorateur de tables");
        this.dataProcessGuiPlugin = dataProcessGuiPlugin;
        putValue(SMALL_ICON, loadActionIcon("/images/menu-find.png"));
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        if (dataProcessGuiPlugin.getConfiguration().getUser().getCurrentRepository() != null) {
            Log.info(getClass(), "Ouverture de l'explorateur de tables.");
            JInternalFrame internalFrame = new JInternalFrame("Explorateur de tables",
                                                              true,
                                                              true,
                                                              true,
                                                              true);
            tableExploratorGui = new TableExploratorGui((MutableGuiContext)ctxt,
                                                        internalFrame,
                                                        dataProcessGuiPlugin.getConfiguration());
            internalFrame.setContentPane(tableExploratorGui.getMainPanel());
            internalFrame.addInternalFrameListener(cleanUpListener);
            internalFrame.addInternalFrameListener(new MyCleanUpListener());
            internalFrame.setFrameIcon(UIManager.getIcon("icon"));
            return internalFrame;
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
        try {
            setFrame(buildFrame(getGuiContext()));
            if (tableExploratorGui != null) {
                tableExploratorGui.loadData(true);
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(getDesktopPane(), getClass(), "Impossible d'afficher la fenêtre !", ex);
        }
    }


    private class MyCleanUpListener extends InternalFrameAdapter {

        @Override
        public void internalFrameClosed(InternalFrameEvent event) {
            event.getInternalFrame().removeInternalFrameListener(this);
            tableExploratorGui = null;
        }


        @Override
        public void internalFrameClosing(InternalFrameEvent event) {
            event.getInternalFrame().removeInternalFrameListener(this);
            tableExploratorGui = null;
        }
    }
}
