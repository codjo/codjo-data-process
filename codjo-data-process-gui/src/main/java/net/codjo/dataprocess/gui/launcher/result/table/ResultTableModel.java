/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.result.table;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
/**
 *
 */
public class ResultTableModel implements TableModel {
    private static final String[] HEADER_LABEL = new String[]{"Traitement", "Status", "Message"};
    private final TreatmentResultList treatmentResultList;
    private List<TableModelListener> tableModelListeners = new ArrayList<TableModelListener>();


    public ResultTableModel(TreatmentResultList treatmentResultList) {
        this.treatmentResultList = treatmentResultList;
    }


    public int getRowCount() {
        return treatmentResultList.getSize();
    }


    public int getColumnCount() {
        return 3;
    }


    public String getColumnName(int columnIndex) {
        if (columnIndex > HEADER_LABEL.length - 1) {
            return "#ERROR: unknown column...";
        }
        return HEADER_LABEL[columnIndex];
    }


    public Class getColumnClass(int columnIndex) {
        return String.class;
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        return treatmentResultList.getTreatmentResultByIndex(rowIndex);
    }


    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }


    public void addTableModelListener(TableModelListener listener) {
        tableModelListeners.add(listener);
    }


    public void removeTableModelListener(TableModelListener listener) {
        tableModelListeners.remove(listener);
    }


    public void fireTableChanged() {
        for (TableModelListener tableModelListener : tableModelListeners) {
            tableModelListener.tableChanged(new TableModelEvent(this));
        }
    }
}
