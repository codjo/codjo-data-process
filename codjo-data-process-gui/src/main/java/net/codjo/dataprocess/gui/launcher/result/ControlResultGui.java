/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.result;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.model.ResultTable;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.std.ExportTextAllPagesAction;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
/**
 *
 */
public class ControlResultGui implements ResultTreatmentGui {
    private RequestTable requestTable = new RequestTable();
    private JPanel mainPanel;
    private String title;
    private boolean hasResultTable;
    private String preferenceId;
    private GuiContext ctxt;
    private ResultTable resultTable;
    private String executionListName;


    public ControlResultGui(GuiContext ctxt,
                            ResultTable resultTable,
                            String executionListName,
                            String title) {
        this.ctxt = ctxt;
        this.resultTable = resultTable;
        this.executionListName = executionListName;
        this.title = title;
        this.preferenceId = resultTable.getTable();

        buildGui();
    }


    private static void modifySelectAllHandler(Preference preference, String selectAllHandler) {
        if (!"".equals(selectAllHandler) && null != selectAllHandler) {
            preference.setSelectAllId(selectAllHandler);
        }
    }


    public String getTitle() {
        return "    " + title;
    }


    public void refreshTable() {
        try {
            if (hasResultTable) {
                requestTable.load();
                if (Log.isInfoEnabled()) {
                    Log.info(getClass(), "Affichage de la table de contrôle '" + preferenceId + "' ("
                                         + requestTable.getDataSource().getTotalRowCount()
                                         + " rows au total)");
                }
            }
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(getMainComponent(), getClass(), "Erreur interne", ex);
        }
    }


    public RequestTable getRequestTable() {
        return requestTable;
    }


    public JComponent getMainComponent() {
        return mainPanel;
    }


    public String getExecutionListName() {
        return executionListName;
    }


    private void buildGui() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.putClientProperty(ResultTreatmentGui.RESULT_TRT_GUI_PROP, this);

        RequestToolBar toolBar = new RequestToolBar();
        toolBar.setHasExcelButton(true);
        toolBar.init(ctxt, requestTable);
        AbstractGuiAction action = new ExportTextAllPagesAction(ctxt, requestTable);
        toolBar.replace(RequestToolBar.ACTION_EXPORT_ALL_PAGES, action);

        JScrollPane jScrollPane = new JScrollPane();
        mainPanel.add(jScrollPane, BorderLayout.CENTER);
        jScrollPane.getViewport().add(requestTable, null);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        topPanel.add(toolBar, BorderLayout.CENTER);

        if (!"".equals(preferenceId) && preferenceId != null) {
            Preference preference = PreferenceFactory.getPreference(preferenceId);
            modifySelectAllHandler(preference, resultTable.getSelectAllHandler());
            requestTable.setPreference(preference);
            hasResultTable = true;
        }
    }


    public void customizeTitle(JComponent component) {
        component.setFont(new Font(component.getFont().getFontName(),
                                   Font.PLAIN,
                                   component.getFont().getSize()));
    }


    public void load() {
        refreshTable();
    }
}
