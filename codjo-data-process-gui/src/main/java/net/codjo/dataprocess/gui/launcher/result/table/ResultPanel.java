/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.result.table;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.dataprocess.gui.launcher.ExecutionListProgress;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.gui.toolkit.util.StandardDialog;
import net.codjo.workflow.common.message.Arguments;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
/**
 *
 */
public class ResultPanel extends JPanel {
    private TreatmentResultList treatmentResultList = new TreatmentResultList();
    private ResultTableModel resultTableModel;
    private JTable table;
    private JPopupMenu popupMenu = new JPopupMenu();


    public ResultPanel() {
        popupMenu.add(new ShowDetails());
        resultTableModel = new ResultTableModel(treatmentResultList);

        table = new JTable(resultTableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(String.class, new ResultTableCellRenderer());

        TableColumnModel columnModel = table.getColumnModel();
        setColumnPreferredWidth(columnModel.getColumn(0), 300);
        setColumnPreferredWidth(columnModel.getColumn(1), 75);

        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new MyMouseAdapter());
    }


    public void buildTreatmentResult(ExecutionListModel executionListModel) {
        List<UserTreatment> trtList = executionListModel.getSortedTreatmentList();
        for (UserTreatment usrTrt : trtList) {
            treatmentResultList.add(new TreatmentResult(usrTrt.getId(), usrTrt.getTitle()));
        }
        resultTableModel.fireTableChanged();
    }


    public void updateTreatmentResult(Arguments auditArguments,
                                      PropertyChangeSupport propertyChangeListeners) {
        String treatmentId = auditArguments.get(DataProcessConstants.MESSAGE_PROP_TREATMENT_ID);
        String status = auditArguments.get(DataProcessConstants.MESSAGE_PROP_STATUS);
        String error = auditArguments.get(DataProcessConstants.MESSAGE_PROP_ERROR);

        TreatmentResult treatmentResult = treatmentResultList.getTreatmentResultById(treatmentId);
        if (treatmentResult != null) {
            treatmentResult.setStartProcessing();

            if (DataProcessConstants.STATUS_ERROR.equals(status)) {
                treatmentResult.setProcessingError(error);
            }
            else if (DataProcessConstants.STATUS_INFORMATION.equals(status)) {
                String info = auditArguments.get(DataProcessConstants.MESSAGE_INFORMATION);
                treatmentResult.setMessage(info);
                String title = treatmentResult.getTitle();
                if (title == null) {
                    title = "(inconnu)";
                }
                propertyChangeListeners.firePropertyChange(ExecutionListProgress.INFO_EVENT, "",
                                                           title + " : " + info);
            }
            else {
                treatmentResult.setEndProcessing();
            }
            informationChanged();
        }
    }


    private static void setColumnPreferredWidth(TableColumn column, int preferredWidth) {
        if (preferredWidth != 0) {
            column.setMinWidth(50);
            column.setMaxWidth(1000);
            column.setPreferredWidth(preferredWidth);
            column.setWidth(column.getPreferredWidth());
        }
    }


    private static Object[] getAllSelectedDataRows(JTable jTable) {
        int[] idx = jTable.getSelectedRows();
        List<Object> rows = new ArrayList<Object>();
        for (int anIdx : idx) {
            rows.add(jTable.getValueAt(anIdx, -1));
        }
        return rows.toArray();
    }


    private void showTreatmentDetails() {
        if (table.getSelectedRowCount() != 0) {
            TreatmentResult treatmentResult = (TreatmentResult)getAllSelectedDataRows(table)[0];
            String state;
            switch (treatmentResult.getState()) {
                case TreatmentResult.OK:
                    state = "OK";
                    break;
                case TreatmentResult.NOT_STARTED:
                    state = "Pas démarré";
                    break;
                case TreatmentResult.STARTED:
                    state = "En cours...";
                    break;
                default:
                    state = "ERREUR";
            }
            String result = treatmentResult.getTitle() + " : " + state + "\n\n" + treatmentResult
                  .getMessage();
            JFrame parentFrame = (JFrame)SwingUtilities.getWindowAncestor(this);
            StandardDialog standardDialog = new StandardDialog(parentFrame,
                                                               "Détails du résultat du traitement");
            standardDialog.getCancelButton().setVisible(false);
            JScrollPane jScrollPane = new JScrollPane();
            JTextArea textArea = new JTextArea(result);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            jScrollPane.getViewport().add(textArea, null);

            standardDialog.setContentPane(jScrollPane);
            GuiUtils.setMaxSize(standardDialog, 400, 300);
            standardDialog.pack();
            centerWindow(standardDialog);
            standardDialog.setVisible(true);
        }
    }


    private static void centerWindow(Component cp) {
        if (cp == null) {
            throw new IllegalArgumentException();
        }

        Dimension containerSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = cp.getSize();

        if (frameSize.height > containerSize.height) {
            frameSize.height = containerSize.height;
            cp.setSize(frameSize);
        }
        if (frameSize.width > containerSize.width) {
            frameSize.width = containerSize.width;
            cp.setSize(frameSize);
        }
        cp.setLocation((containerSize.width - frameSize.width) / 2,
                       (containerSize.height - frameSize.height) / 2);
    }


    public void informationChanged() {
        resultTableModel.fireTableChanged();
    }


    private void maybeShowPopup(MouseEvent ev) {
        if (ev.isPopupTrigger()) {
            popupMenu.show(ev.getComponent(), ev.getX(), ev.getY());
        }
    }


    private void tableMousePressed(MouseEvent ev) {
        if (SwingUtilities.isRightMouseButton(ev)) {
            int row = table.rowAtPoint(ev.getPoint());
            if (row != -1) {
                table.setRowSelectionInterval(row, row);
            }
            maybeShowPopup(ev);
        }
    }


    private class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent ev) {
            tableMousePressed(ev);
        }


        @Override
        public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                showTreatmentDetails();
            }
        }


        @Override
        public void mouseReleased(MouseEvent ev) {
            maybeShowPopup(ev);
        }
    }

    private class ShowDetails extends AbstractAction {
        ShowDetails() {
            putValue(NAME, "Détail du résultat du traitement");
            putValue(SHORT_DESCRIPTION, "Détail du résultat du traitement");
        }


        public void actionPerformed(ActionEvent arg0) {
            showTreatmentDetails();
        }
    }
}
