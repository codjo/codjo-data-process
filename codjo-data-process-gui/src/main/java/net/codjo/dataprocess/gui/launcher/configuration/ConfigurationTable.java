/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.configuration;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.util.CommonUtils;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListSelectionModel;
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
/**
 *
 */
public class ConfigurationTable extends JTable {
    private boolean editable = true;
    private TreatmentModel treatmentModel;
    private ConfigTableCellRenderer configTableCellRenderer = new ConfigTableCellRenderer();


    public ConfigurationTable() {
        this(0, 0, false);
    }


    public ConfigurationTable(int numRows, int numColumns, boolean editable) {
        super(numRows, numColumns);
        this.editable = editable;
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setDefaultRenderer(new ConfigTableHeaderRenderer());
    }


    public TreatmentModel getTreatmentModel() {
        return treatmentModel;
    }


    public void setTrtMod(TreatmentModel trtMod) {
        this.treatmentModel = trtMod;
    }


    public ArgList getArglist() {
        ArgList argList = new ArgList();
        List<ArgModel> args = new ArrayList<ArgModel>();
        DefaultTableModel defaultListModel = ((DefaultTableModel)getModel());

        for (int i = 0; i < defaultListModel.getRowCount(); i++) {
            ArgModel argument = new ArgModel((String)defaultListModel.getValueAt(i, 0),
                                             (String)defaultListModel.getValueAt(i, 1));
            args.add(argument);
        }
        argList.setArgs(args);
        return argList;
    }


    @Override
    public boolean isCellEditable(int row, int col) {
        if (editable) {
            return col != 0;
        }
        else {
            return false;
        }
    }


    public void refresh(int repositoryId, String executionListName, DataProcessContext dataProcessContext) {
        DefaultTableModel defaultTableModel = ((DefaultTableModel)getModel());
        for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
            String key = (String)defaultTableModel.getValueAt(i, 0);
            if (key.startsWith(DataProcessConstants.LOCAL_VISIBILITY)) {
                key = CommonUtils.localify(repositoryId, executionListName,
                                           key.substring(DataProcessConstants.LOCAL_VISIBILITY.length()));
            }
            defaultTableModel.setValueAt(dataProcessContext.getProperty(key), i, 1);
        }
    }


    public void initConfigurationTableModel() {
        DefaultTableModel execListModel = new DefaultTableModel(new String[]{"Nom", "Valeur"}, 0);
        setModel(execListModel);
        setSelectionModel(new DefaultListSelectionModel());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        EditorAutomaticCloser editorAutomaticCloser =
              new EditorAutomaticCloser((DefaultTableModel)getModel());
        getModel().addTableModelListener(editorAutomaticCloser);
    }


    public void removeAllRows() {
        DefaultTableModel defaultListModel = ((DefaultTableModel)getModel());

        while (defaultListModel.getRowCount() != 0) {
            defaultListModel.removeRow(0);
        }
    }


    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return configTableCellRenderer;
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

    private class ConfigTableHeaderRenderer extends JLabel implements TableCellRenderer {
        ConfigTableHeaderRenderer() {
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
            return this;
        }
    }

    private class ConfigTableCellRenderer extends JLabel implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            setText((String)value);
            setFont(getFont().deriveFont(Font.PLAIN));

            if (treatmentModel != null) {
                List<ArgModel> arguments = treatmentModel.getArguments().getArgs();
                for (ArgModel argument : arguments) {
                    if (argument.isGlobalValue() && argument.getGlobalValue().equals(
                          ConfigurationTable.this.getModel().getValueAt(row, 0))) {
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    }
                }
            }
            return this;
        }
    }
}
