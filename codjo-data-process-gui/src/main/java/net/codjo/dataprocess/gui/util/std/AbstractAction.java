/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.std;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.RequestToolbarAction;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import static net.codjo.dataprocess.common.DataProcessConstants.SHOW_TABLE_NAME_IN_MENU;
/**
 *
 */
public abstract class AbstractAction extends AbstractGuiAction {
    protected CleanUpListener cleanUpListener = new CleanUpListener();
    private JInternalFrame frame;
    private String table;
    private String menuName;
    private String preference;
    private String windowTitle;
    private boolean centerWindow;
    protected List<RequestToolbarAction> requestToolbarActionList = new ArrayList<RequestToolbarAction>();
    protected AbstractRequestTableLoader requestTableLoader;


    protected AbstractAction(GuiContext ctxt, String menuName, String description) {
        this(ctxt, menuName, description, null);
    }


    protected AbstractAction(GuiContext ctxt, String menuName, String description, String iconId) {
        super(ctxt, menuName, description, iconId);
        this.menuName = menuName;
    }


    public void initWindowList(String pWindowTitle, String pPreference, String pTable) {
        this.preference = pPreference;
        this.windowTitle = pWindowTitle;
        setTable(pTable);
    }


    public void initWindowList(String pWindowTitle,
                               String pPreference,
                               String pTable,
                               AbstractRequestTableLoader pRequestTableLoader) {
        this.requestTableLoader = pRequestTableLoader;
        initWindowList(pWindowTitle, pPreference, pTable);
    }


    public String getTable() {
        return table;
    }


    public void setTable(String table) {
        this.table = table;
        if (table != null && getGuiContext().hasProperty(SHOW_TABLE_NAME_IN_MENU)
            && "ON".equalsIgnoreCase((String)getGuiContext().getProperty(SHOW_TABLE_NAME_IN_MENU))) {
            putValue(NAME, menuName + " [" + table + "]");
        }
    }


    public void actionPerformed(ActionEvent event) {
        if (frame == null) {
            if (requestTableLoader == null) {
                requestTableLoader = new DefaultRequestTableLoader();
            }
            if (DataProcessConstants.TABLE_EXPLORATOR.equals(event.getSource())) {
                requestTableLoader.putProperty(DataProcessConstants.WHERE_CLAUSE_KEY,
                                               event.getActionCommand());
            }
            displayNewWindow();
        }
        else {
            try {
                frame.setSelected(true);
            }
            catch (PropertyVetoException ex) {
                ;
            }
        }
    }


    protected JInternalFrame buildFrame(GuiContext pCtxt) throws Exception {
        String title = windowTitle;

        if (pCtxt.hasProperty(SHOW_TABLE_NAME_IN_MENU)
            && "ON".equalsIgnoreCase((String)pCtxt.getProperty(SHOW_TABLE_NAME_IN_MENU))) {
            title = title + (table == null ? "" : "     [" + table + "]");
        }
        return new DefaultListWindow(title,
                                     pCtxt,
                                     preference,
                                     table,
                                     requestToolbarActionList,
                                     requestTableLoader);
    }


    protected void displayNewWindow() {
        try {
            setFrame(buildFrame(getGuiContext()));
            if (frame != null) {
                frame.addInternalFrameListener(cleanUpListener);
                getDesktopPane().add(frame);
                frame.setFrameIcon(UIManager.getIcon("icon"));
                frame.pack();
                if (centerWindow) {
                    GuiUtil.centerWindow(frame);
                }
                frame.setVisible(true);
                frame.setSelected(true);
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(getDesktopPane(), getClass(), "Impossible d'afficher la fenêtre !", ex);
        }
    }


    protected void closeFrame() {
        if (frame != null) {
            frame.dispose();
        }
    }


    public JInternalFrame getFrame() {
        return frame;
    }


    public void setFrame(JInternalFrame frame) {
        this.frame = frame;
    }


    protected void setCenterWindow(boolean centerWindow) {
        this.centerWindow = centerWindow;
    }


    public void addRequestToolbarAction(RequestToolbarAction requestToolbarAction) {
        requestToolbarActionList.add(requestToolbarAction);
    }


    public List<RequestToolbarAction> getRequestToolbarActionList() {
        return requestToolbarActionList;
    }


    protected Icon loadActionIcon(String fileName) {
        URL resource = getClass().getResource(fileName);
        if (resource != null) {
            return new ImageIcon(resource);
        }
        else {
            return null;
        }
    }


    protected class CleanUpListener extends InternalFrameAdapter {
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
