/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.sqleditor.components;
import net.codjo.dataprocess.common.eventsbinder.EventBinderException;
import net.codjo.dataprocess.common.eventsbinder.EventsBinder;
import net.codjo.dataprocess.common.eventsbinder.annotations.OnError;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnGenericEvent;
import net.codjo.dataprocess.gui.util.ErrorDialog;
import net.codjo.dataprocess.gui.util.sqleditor.util.SQLEditorTools;
import net.codjo.gui.toolkit.waiting.WaitingPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
/**
 *
 */
public class ResultPaneLogic {
    private WeakReference<ResultTabbedPane> weakResultTabbedPane;
    private WeakReference<WaitingPanel> weakWaitingPanel;
    private final int pageSize;
    private final SQLEditorTools sqlEditorTools;
    private NavigationPanelLogic navigationPanelLogic;
    private ResultPaneGui resultPaneGui;


    public ResultPaneLogic(EventsBinder eventsBinder, ResultTabbedPane resultTabbedPane,
                           WaitingPanel waitingPanel, int pageSize, SQLEditorTools sqlEditorTools)
          throws EventBinderException {
        this.pageSize = pageSize;
        this.sqlEditorTools = sqlEditorTools;
        weakResultTabbedPane = new WeakReference<ResultTabbedPane>(resultTabbedPane);
        weakWaitingPanel = new WeakReference<WaitingPanel>(waitingPanel);
        navigationPanelLogic = new ResultNavigationPanelLogic(eventsBinder);
        resultPaneGui = new ResultPaneGui();
        eventsBinder.bind(this, resultPaneGui);
    }


    public NavigationPanelLogic getNavigationPanelLogic() {
        return navigationPanelLogic;
    }


    @OnGenericEvent(propertiesBound = "closeButton", listenerClass = ActionListener.class)
    public void onClose(ActionEvent actionEvent) {
        weakResultTabbedPane.get().remove(resultPaneGui);
    }


    public void exceptionRaisedWhileInvokingMethod(Exception ex) {
        ErrorDialog.show(null, "System error", ex);
    }


    public static TableModel createTableModelResult(StringBuffer buffer, SQLEditorTools sqlEditorTools) {
        String columns = sqlEditorTools.extractLine(buffer);
        String[] columnNames = sqlEditorTools.lineToArray(columns, "\t");

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);

        String currentLine = sqlEditorTools.extractLine(buffer);
        while (currentLine != null) {
            model.addRow(sqlEditorTools.lineToArray(currentLine, "\t"));
            currentLine = sqlEditorTools.extractLine(buffer);
        }
        return model;
    }


    public ResultPaneGui getResultPane() {
        return resultPaneGui;
    }


    @OnError
    public void exceptionRaisedWhileInvokingMethod(Throwable ex) {
        ErrorDialog.show(null, "Error", ex);
    }


    class ResultNavigationPanelLogic extends NavigationPanelLogic {
        ResultNavigationPanelLogic(EventsBinder eventsBinder)
              throws EventBinderException {
            super(eventsBinder);
        }


        @Override
        public void onPageChanged(int from, final int to) {
            weakWaitingPanel.get().exec(new Runnable() {
                public void run() {
                    try {
                        StringBuffer stringBuffer =
                              sqlEditorTools.executeRequest(resultPaneGui.getRequestTextArea().getText(), to,
                                                            pageSize);
                        // vire le type
                        sqlEditorTools.extractLine(stringBuffer);
                        //vire le nbcol
                        sqlEditorTools.extractLine(stringBuffer);
//                        int nbRow = Integer.parseInt(sqlEditorTools.extractLine(stringBuffer));
                        resultPaneGui.getTable()
                              .setModel(createTableModelResult(stringBuffer, sqlEditorTools));
                    }
                    catch (Exception e) {
                        ErrorDialog.show(null, "Erreur lors de la requête", e);
                    }
                }
            });
        }


        @Override
        @OnError
        public void exceptionRaisedWhileInvokingMethod(Throwable ex) {
            ErrorDialog.show(null, "Error", ex);
        }
    }
}
