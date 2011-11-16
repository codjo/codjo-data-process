/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.repository;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.gui.util.GenericRenderer;
import java.awt.Component;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import static net.codjo.dataprocess.gui.repository.ArgumentsTable.COLUMN.NOM;
import static net.codjo.dataprocess.gui.repository.ArgumentsTable.COLUMN.POSITION;
import static net.codjo.dataprocess.gui.repository.ArgumentsTable.COLUMN.TYPE;
import static net.codjo.dataprocess.gui.repository.ArgumentsTable.COLUMN.VALEUR;
/**
 *
 */
public class ArgumentsTable extends JTable {
    enum COLUMN {
        NOM(0, "Nom"),
        VALEUR(1, "Valeur"),
        POSITION(2, "Position"),
        TYPE(3, "Type");

        int columnPosition;
        String columnName;


        COLUMN(int columnPosition, String columnName) {
            this.columnPosition = columnPosition;
            this.columnName = columnName;
        }
    }

    public static final boolean SQL_TYPE = true;
    public static final boolean JAVA_TYPE = false;
    private boolean sqlType = true;


    public ArgumentsTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setDefaultRenderer(new MyHeaderRenderer());
    }


    public boolean isSqlType() {
        return sqlType;
    }


    public void initTableModel(boolean pSqlType) {
        String[] columns;
        this.sqlType = pSqlType;
        if (sqlType) {
            columns = new String[]{NOM.columnName, VALEUR.columnName, POSITION.columnName, TYPE.columnName};
        }
        else {
            columns = new String[]{NOM.columnName, VALEUR.columnName};
        }

        DefaultTableModel execListModel = new DefaultTableModel(columns, 0);
        setModel(execListModel);
        setSelectionModel(new DefaultListSelectionModel());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        EditorAutomaticCloser editorAutomaticCloser = new EditorAutomaticCloser(
              (DefaultTableModel)getModel());
        getModel().addTableModelListener(editorAutomaticCloser);

        setCellRenderer(NOM.columnPosition, new MyTableCellRenderer());
        setCellRenderer(VALEUR.columnPosition, new MyTableCellRenderer());
    }


    public ArgList getArglist() {
        ArgList argList = new ArgList();
        List<ArgModel> args = new ArrayList<ArgModel>();
        DefaultTableModel defaultListModel = ((DefaultTableModel)getModel());

        for (int i = 0; i < defaultListModel.getRowCount(); i++) {
            String name = (String)defaultListModel.getValueAt(i, NOM.columnPosition);
            String value = (String)defaultListModel.getValueAt(i, VALEUR.columnPosition);
            int position = 0;
            int type = 0;
            if (sqlType) {
                position = Integer.parseInt((String)defaultListModel.getValueAt(i, POSITION.columnPosition));
                Object valueAt = defaultListModel.getValueAt(i, TYPE.columnPosition);
                try {
                    type = (Integer)valueAt;
                }
                catch (ClassCastException e) {
                    type = Integer.parseInt((String)valueAt);
                }
            }
            ArgModel argument = new ArgModel(name, value, position, type);
            args.add(argument);
        }
        argList.setArgs(args);
        return argList;
    }


    public void initSqlFormatStuff() {
        Map<Integer, String> traductTable = new HashMap<Integer, String>();

        traductTable.put(Types.VARCHAR, "Varchar");
        traductTable.put(Types.NUMERIC, "Numeric");
        traductTable.put(Types.DATE, "Date");
        traductTable.put(Types.TIMESTAMP, "TimeStamp");

        GenericRenderer renderer = new GenericRenderer(traductTable);

        JComboBox typeComboBox = new JComboBox(traductTable.keySet().toArray());
        typeComboBox.setRenderer(renderer);

        setCellRenderer(NOM.columnPosition, new MyTableCellRenderer());
        setCellRenderer(VALEUR.columnPosition, new MyTableCellRenderer());
        setCellRenderer(POSITION.columnPosition, new MyTableCellRenderer());
        setCellRenderer(TYPE.columnPosition, renderer);

        setCellEditor(TYPE.columnPosition, new DefaultCellEditor(typeComboBox));
    }


    public void addRow() {
        DefaultTableModel defaultListModel = ((DefaultTableModel)getModel());
        defaultListModel.addRow(new Object[]{});
    }


    public void removeCurrentRow() {
        DefaultTableModel defaultListModel = ((DefaultTableModel)getModel());
        try {
            defaultListModel.removeRow(selectionModel.getMinSelectionIndex());
        }
        catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
    }


    public void removeAllRows() {
        DefaultTableModel defaultListModel = ((DefaultTableModel)getModel());
        while (defaultListModel.getRowCount() != 0) {
            defaultListModel.removeRow(0);
        }
    }


    private void setCellEditor(int columnPosition, TableCellEditor editor) {
        TableColumn col = getColumnModel().getColumn(columnPosition);
        col.setCellEditor(editor);
    }


    private void setCellRenderer(int columnPosition, TableCellRenderer renderer) {
        TableColumn col = getColumnModel().getColumn(columnPosition);
        col.setCellRenderer(renderer);
    }


    private class EditorAutomaticCloser implements TableModelListener {
        private DefaultTableModel model;


        EditorAutomaticCloser(DefaultTableModel model) {
            this.model = model;
        }


        public void tableChanged(TableModelEvent event) {
            model.removeTableModelListener(this);
            cancelAllEditors();
            model.addTableModelListener(this);
        }


        private void cancelAllEditors() {
            for (int i = 0; i < getColumnCount(); i++) {
                TableCellEditor tce = getColumn(getColumnName(i)).getCellEditor();

                if (tce != null) {
                    tce.cancelCellEditing();
                }
            }
        }
    }

    private class MyHeaderRenderer extends JLabel implements TableCellRenderer {
        MyHeaderRenderer() {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.CENTER);
            setHorizontalTextPosition(SwingConstants.RIGHT);
            setVerticalTextPosition(SwingConstants.CENTER);
            setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        }


        public void changeHeaderRenderer(JTable tbl) {
            int nbColumn = getColumnCount();

            for (int i = 0; i < nbColumn; i++) {
                TableColumn aCol = tbl.getColumn(getColumnName(i));
                aCol.setHeaderRenderer(this);
            }
        }


        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            setText((String)value);
            setEnabled(table.isEnabled());
            return this;
        }
    }

    private static class MyTableCellRenderer extends JLabel implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            setText((String)value);
            setEnabled(table.isEnabled());
            return this;
        }
    }
}
