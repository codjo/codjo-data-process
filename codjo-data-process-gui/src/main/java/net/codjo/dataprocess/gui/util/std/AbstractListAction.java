/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.std;
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
import org.apache.log4j.Logger;
/**
 *
 */
public class AbstractListAction extends AbstractGuiAction {
    private static final Logger LOG = Logger.getLogger(AbstractAction.class);
    private CleanUpListener cleanUpListener = new CleanUpListener();
    private JInternalFrame frame;
    private String table;
    private String menuName;
    private String preference;
    private String windowTitle;
    private boolean centerWindow;


    protected AbstractListAction(GuiContext ctxt, String menuName, String description) {
        super(ctxt, menuName, description);
        this.menuName = menuName;
    }


    protected AbstractListAction(GuiContext ctxt, String menuName, String description, String iconId) {
        super(ctxt, menuName, description, iconId);
        this.menuName = menuName;
    }


    public void initWindowList(String pWindowTitle,
                               String pPreference,
                               String pTable,
                               boolean showTableNameInMenu) {
        this.preference = pPreference;
        this.windowTitle = pWindowTitle;
        setTable(pTable, showTableNameInMenu);
    }


    protected void configureGuiCtxtEventListener() {
    }


    public String getTable() {
        return table;
    }


    public void setTable(String table, boolean showTableNameInMenu) {
        this.table = table;
        if (table != null && showTableNameInMenu) {
            putValue(NAME, menuName + " [" + table + "]");
        }
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


    /**
     */
    protected JInternalFrame buildFrame(GuiContext pCtxt, boolean showTableNameInMenu) throws Exception {
        if (showTableNameInMenu) {
            return new DefaultListWindow(windowTitle + (table == null ? "" : "     [" + table + "]"),
                                         pCtxt, preference, table);
        }
        return new DefaultListWindow(windowTitle, pCtxt, preference, table);
    }


    private void displayNewWindow() {
        try {
            frame = buildFrame(getGuiContext(), false);
            frame.addInternalFrameListener(cleanUpListener);
            configureGuiCtxtEventListener();
            getDesktopPane().add(frame);
            frame.setFrameIcon(UIManager.getIcon("icon"));
            frame.pack();
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
