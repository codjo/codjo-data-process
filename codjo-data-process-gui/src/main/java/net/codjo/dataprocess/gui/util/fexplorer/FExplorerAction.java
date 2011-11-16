package net.codjo.dataprocess.gui.util.fexplorer;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 *
 */
public class FExplorerAction extends AbstractGuiAction {
    private CleanUpListener cleanUpListener = new CleanUpListener();
    private JInternalFrame frame;
    private GuiContext ctxt;
    private boolean centerWindow;


    public FExplorerAction(GuiContext ctxt) {
        super(ctxt, "Explorateur de fichiers", "Explorateur de fichiers");
        this.ctxt = ctxt;
    }


    public void actionPerformed(ActionEvent event) {
        if (frame == null) {
            displayNewWindow();
        }
        else {
            try {
                frame.setSelected(true);
            }
            catch (PropertyVetoException ex) {
                Log.error(getClass(), ex);
            }
        }
    }


    private void displayNewWindow() {
        try {
            frame = new FExplorerDetailWindow(ctxt);
            frame.addInternalFrameListener(cleanUpListener);
            getDesktopPane().add(frame);
            frame.setFrameIcon(UIManager.getIcon("icon"));
            if (centerWindow) {
                GuiUtil.centerWindow(frame);
            }
            frame.setVisible(true);
            frame.setSelected(true);
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(getDesktopPane(), getClass(), "Impossible d'afficher l'IHM", ex);
        }
    }


    public JInternalFrame getFrame() {
        return frame;
    }


    protected void setCenterWindow(boolean centerWindow) {
        this.centerWindow = centerWindow;
    }


    private class CleanUpListener extends InternalFrameAdapter {
        @Override
        public void internalFrameClosed(InternalFrameEvent event) {
            event.getInternalFrame().removeInternalFrameListener(this);
            frame = null;
        }


        @Override
        public void internalFrameClosing(InternalFrameEvent event) {
            event.getInternalFrame().removeInternalFrameListener(this);
            frame = null;
        }
    }
}
