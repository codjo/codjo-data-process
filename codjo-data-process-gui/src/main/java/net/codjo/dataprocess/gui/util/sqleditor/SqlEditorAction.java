package net.codjo.dataprocess.gui.util.sqleditor;
import net.codjo.dataprocess.common.eventsbinder.EventsBinder;
import net.codjo.dataprocess.gui.util.ErrorDialog;
import net.codjo.dataprocess.gui.util.sqleditor.util.SqlEditorClientHelper;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.apache.log4j.Logger;
/**
 *
 */
public class SqlEditorAction extends AbstractGuiAction {
    private static final Logger LOG = Logger.getLogger(SqlEditorAction.class);
    private CleanUpListener cleanUpListener = new CleanUpListener();
    private JInternalFrame frame;
    private boolean centerWindow;


    public SqlEditorAction(GuiContext ctxt) {
        super(ctxt, "Editeur SQL", "Editeur SQL");
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
                LOG.error(ex);
            }
        }
    }


    private void displayNewWindow() {
        try {
            EventsBinder eventsBinder = new EventsBinder();
            SqlEditorDetailWindowLogic logic =
                  new SqlEditorDetailWindowLogic(eventsBinder, new SqlEditorClientHelper(getGuiContext()));
            frame = logic.getGui();
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
            LOG.error(ex);
            ErrorDialog.show(getDesktopPane(), "Impossible d'afficher l'IHM", ex);
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
