/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.std;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.gui.util.ClosePanel;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.RequestToolbarAction;
import net.codjo.gui.toolkit.progressbar.ProgressBarLabel;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.FilterPanel;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
/**
 *
 */
public abstract class AbstractListWindow extends JInternalFrame {
    private static final String NOTE_FIELD = "note";
    protected RequestTable requestTable = new RequestTable();
    protected RequestToolBar toolBar = new RequestToolBar();
    private JPanel headerPanel = null;
    private JPanel bottomToolbarPanel = new JPanel();
    private ProgressBarLabel progressBarLabel = new ProgressBarLabel();
    private LocalGuiContext ctxt;
    private String sqlTableName = null;
    private AbstractRequestTableLoader requestTableLoader;
    private JPanel topPanel;
    private ClosePanel closePanel;
    private FilterPanel filterPanel;
    private String handlerFilterName;
    private ListWindowTopPanel listWindowTopPanel;


    protected AbstractListWindow(String title, GuiContext subCtxt, String prefId) {
        this(title, subCtxt, prefId, null, true, null, null);
    }


    protected AbstractListWindow(String title, GuiContext subCtxt, String prefId, String sqlTableName) {
        this(title, subCtxt, prefId, sqlTableName, true, null, null);
    }


    protected AbstractListWindow(String title,
                                 GuiContext subCtxt,
                                 String prefId,
                                 String sqlTableName,
                                 List<RequestToolbarAction> requestToolbarActionList) {
        this(title, subCtxt, prefId, sqlTableName, true, requestToolbarActionList, null);
    }


    protected AbstractListWindow(String title,
                                 GuiContext subCtxt,
                                 String prefId,
                                 String sqlTableName,
                                 List<RequestToolbarAction> requestToolbarActionList,
                                 AbstractRequestTableLoader requestTableLoader) {
        this(title, subCtxt, prefId, sqlTableName, true, requestToolbarActionList, requestTableLoader);
    }


    protected AbstractListWindow(String title,
                                 GuiContext subCtxt,
                                 String prefId,
                                 String sqlTableName,
                                 boolean loadNow,
                                 List<RequestToolbarAction> requestToolbarActionList,
                                 AbstractRequestTableLoader requestTableLoader) {
        super(title, true, true, true, true);
        this.sqlTableName = sqlTableName;
        this.requestTableLoader = requestTableLoader;
        ctxt = new LocalGuiContext(subCtxt, progressBarLabel);
        requestTable.setPreference(PreferenceFactory.getPreference(prefId));
        initListWindow(loadNow, requestToolbarActionList);
    }


    public String getSqlTableName() {
        return sqlTableName;
    }


    public RequestToolBar getToolBar() {
        return toolBar;
    }


    public ListWindowTopPanel getListWindowTopPanel() {
        return listWindowTopPanel;
    }


    protected void initListWindow(boolean loadNow, List<RequestToolbarAction> requestToolbarActionList) {
        if (requestTableLoader == null) {
            requestTableLoader = new DefaultRequestTableLoader();
        }
        requestTableLoader.init(this);
        toolBar.setHasExcelButton(true);
        toolBar.init(ctxt, requestTable);

        AbstractGuiAction action = new ExportTextAllPagesAction(ctxt, requestTable);
        toolBar.replace(RequestToolBar.ACTION_EXPORT_ALL_PAGES, action);

        if (requestToolbarActionList != null) {
            if (!requestToolbarActionList.isEmpty()) {
                toolBar.addSeparator();
            }
            for (RequestToolbarAction requestToolbarAction : requestToolbarActionList) {
                requestToolbarAction.setRequestTable(requestTable);
                toolBar.add(requestToolbarAction)
                      .setName(requestTable.getName() + "." + requestToolbarAction.getClass().getName());
            }
        }

        addClosePanel();
        jbInit();

        if (loadNow) {
            requestTableLoader.refreshTable();
        }
    }


    public void addClosePanel() {
        toolBar.addSeparator();
        closePanel =
              new ClosePanel("Fermer") {
                  @Override
                  protected void dispose() {
                      AbstractListWindow.this.dispose();
                  }
              };
        closePanel.addToRequestToolbar(toolBar);
    }


    public void removeClosePanel() {
        if (closePanel != null) {
            closePanel.removeFromRequestToolbar(toolBar);
        }
    }


    public LocalGuiContext getCtxt() {
        return ctxt;
    }


    public RequestTable getRequestTable() {
        return requestTable;
    }


    public ProgressBarLabel getProgressBarLabel() {
        return progressBarLabel;
    }


    protected void jbInit() {
        resizeColumns();
        setClosable(true);
        setIconifiable(true);
        setResizable(true);
        setPreferredSize(new Dimension(1150, 500));
        getContentPane().setLayout(new BorderLayout());
        progressBarLabel.setText("");
        bottomToolbarPanel.setLayout(new BorderLayout());
        toolBar.setBorder(null);
        bottomToolbarPanel.setBorder(BorderFactory.createEtchedBorder());
        bottomToolbarPanel.add(progressBarLabel, BorderLayout.WEST);
        bottomToolbarPanel.add(toolBar, BorderLayout.CENTER);
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(buildRequestTablePanel(), BorderLayout.CENTER);
        getContentPane().add(bottomToolbarPanel, BorderLayout.SOUTH);
    }


    protected JPanel buildRequestTablePanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(requestTable, null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        GuiUtils.addHorizontalScrollBar(requestTable, scrollPane);
        return mainPanel;
    }


    public void setTopPanel(ListWindowTopPanel panel) {
        setTopPanel(panel, BorderLayout.CENTER);
    }


    public void setTopPanel(ListWindowTopPanel panel, Object constraints) {
        listWindowTopPanel = panel;
        topPanel.add(panel.getPanel(), constraints);
    }


    public JPanel getHeaderPanel() {
        if (headerPanel == null) {
            headerPanel = new JPanel();
            headerPanel.setBorder(BorderFactory.createEtchedBorder());
            getContentPane().add(headerPanel, BorderLayout.NORTH);
        }
        return headerPanel;
    }


    protected void resizeColumns() {
        for (int i = 0; i < requestTable.getColumnCount(); i++) {
            String columnName = requestTable.getColumnName(i);
            if (NOTE_FIELD.equalsIgnoreCase(columnName)) {
                requestTable.getColumn(columnName).setMinWidth(300);
            }
            else {
                requestTable.getColumn(columnName).setMinWidth(100);
            }
        }
    }


    public FilterPanel buildFilterPanel(final String handlerFilterId) {
        if (filterPanel == null || !handlerFilterId.equals(handlerFilterName)) {
            handlerFilterName = handlerFilterId;
            requestTable.setSelectFactoryId(handlerFilterId);
            filterPanel = new FilterPanel(requestTable) {
                @Override
                protected void preSearch(FieldsList selector) throws RequestException {
                    selector.addField(DataProcessConstants.TABLE_NAME_KEY, getSqlTableName());
                }
            };
        }
        return filterPanel;
    }


    public static AbstractListWindow getListWindow(String preferenceId, JDesktopPane desktopPane) {
        Component[] components = desktopPane.getComponents();
        for (Component component : components) {
            if (component instanceof AbstractListWindow) {
                AbstractListWindow abstractListWindow = (AbstractListWindow)component;
                if (preferenceId.equals(abstractListWindow.getRequestTable().getPreference().getId())) {
                    return abstractListWindow;
                }
            }
        }
        return null;
    }


    public static interface ListWindowTopPanel {
        JPanel getPanel();
    }
}
