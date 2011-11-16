package net.codjo.dataprocess.gui.util;
import net.codjo.dataprocess.client.HandlerCommandSender;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 *
 */
public class DuplicateRowAction extends RequestToolbarAction {
    private String tableName;
    private String handlerId;
    private List<String> primaryKeys;
    private Map<String, String> fieldValuesEx;
    private TableSelectionListener listener = new TableSelectionListener();


    public DuplicateRowAction(GuiContext ctxt) {
        this(ctxt, "copy");
    }


    public DuplicateRowAction(GuiContext ctxt, String actionId) {
        super(ctxt, "Dupliquer", "Dupliquer les enregistrements sélectionnés", actionId);
        setEnabled(false);
    }


    public DuplicateRowAction(GuiContext ctxt, String tableName, String handlerId, List<String> primaryKeys) {
        this(ctxt, tableName, null, handlerId, primaryKeys, "copy");
    }


    public DuplicateRowAction(GuiContext ctxt, String tableName, RequestTable requestTable,
                              String handlerId, List<String> primaryKeys, String actionId) {
        this(ctxt, actionId);
        this.tableName = tableName;
        this.handlerId = handlerId;
        this.primaryKeys = primaryKeys;
        setRequestTable(requestTable);
    }


    public DuplicateRowAction(GuiContext ctxt, String tableName, RequestTable requestTable,
                              String handlerId,
                              List<String> primaryKeys,
                              String actionId,
                              Map<String, String> fieldValuesEx) {
        this(ctxt, actionId);
        this.tableName = tableName;
        this.handlerId = handlerId;
        this.primaryKeys = primaryKeys;
        this.fieldValuesEx = fieldValuesEx;
        setRequestTable(requestTable);
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled & isActivable());
    }


    public void actionPerformed(ActionEvent evt) {
        getRequestTable().cancelAllEditors();
        try {
            Row[] rows = getRequestTable().getAllSelectedDataRows();
            Map<String, String> fieldValues = new HashMap<String, String>();
            for (Row row : rows) {
                fieldValues.clear();
                fieldValues.put(DataProcessConstants.TABLE_NAME_KEY, tableName);
                for (String primaryKey : primaryKeys) {
                    String value = row.getFieldValue(primaryKey);
                    fieldValues.put(primaryKey, value);
                }
                if (fieldValuesEx != null) {
                    fieldValues.putAll(fieldValuesEx);
                }
                duplicate(fieldValues);
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(getRequestTable(), getClass(), "Erreur interne", ex);
        }
    }


    private void duplicate(Map<String, String> fieldValues) {
        try {
            new HandlerCommandSender().sendInsertSqlCommand(getGuiContext(), handlerId, fieldValues);
            getRequestTable().load();
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(getRequestTable(), getClass(), "Impossible de dupliquer la ligne", ex);
        }
    }


    @Override
    public void setRequestTable(RequestTable requestTable) {
        super.setRequestTable(requestTable);
        if (requestTable != null) {
            ListSelectionModel rowSM = requestTable.getSelectionModel();
            rowSM.addListSelectionListener(listener);
        }
    }


    @Override
    protected boolean isActivable() {
        if (getRequestTable() == null) {
            return false;
        }
        ListSelectionModel lsm = getRequestTable().getSelectionModel();
        return !lsm.isSelectionEmpty();
    }


    private class TableSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            setEnabled(true);
        }
    }
}
