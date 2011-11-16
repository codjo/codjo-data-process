package net.codjo.dataprocess.gui.util.LoginTracker;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiPlugin;
import net.codjo.dataprocess.gui.util.std.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
/**
 *
 */
public class LoginTrackerGuiAction extends AbstractAction {
    private DataProcessGuiPlugin dataProcessGuiPlugin;


    public LoginTrackerGuiAction(GuiContext ctxt, DataProcessGuiPlugin dataProcessGuiPlugin) {
        super(ctxt, "Suivie du login des utilisateurs", "Suivie du login des utilisateurs");
        this.dataProcessGuiPlugin = dataProcessGuiPlugin;
        putValue(SMALL_ICON, loadActionIcon("/images/face.png"));
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        Log.info(getClass(), "Ouverture de la fenêtre de suivie du login des utilisateurs.");
        JInternalFrame frameWindow = new JInternalFrame("Suivie du login des utilisateurs", true, true, true,
                                                        true);
        int delay = dataProcessGuiPlugin.getConfiguration().getServerPingDelay();
        LoginTrackerGui loginTrackerGui = new LoginTrackerGui((MutableGuiContext)ctxt, frameWindow, delay);
        loginTrackerGui.getMainPanel();
        frameWindow.setContentPane(loginTrackerGui.getMainPanel());
        frameWindow.setPreferredSize(new Dimension(600, 300));
        return frameWindow;
    }
}
