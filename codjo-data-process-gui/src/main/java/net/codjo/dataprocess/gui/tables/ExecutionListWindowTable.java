/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.tables;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
class ExecutionListWindowTable extends AbstractWindowTable {
    ExecutionListWindowTable(GuiContext ctxt) throws RequestException {
        super(ctxt, "Table PM_EXECUTION_LIST", true);
    }


    @Override
    protected Preference getRequestTablePreference() {
        getRequestTable().setEditable(true, new String[]{"executionListId"});
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("executionListId", "Identifiant", 50, 1000, 50));
        columns.add(new Column("repositoryId", "Id repository", 50, 1000, 50));
        columns.add(new Column("familyId", "Id famille", 50, 1000, 50));
        columns.add(new Column("executionListName", "Nom liste de traitements", 50, 1000, 50));
        columns.add(new Column("listScope", "Scope", 50, 1000, 50));
        Column column = new Column("priority", "Priorité", 50, 1000, 50);
        column.setSorter("Numeric");
        columns.add(column);
        preference.setColumns(columns);
        preference.setSelectAllId("selectAllExecutionList");
        preference.setDeleteId("deleteExecutionList");
        preference.setUpdateId("updateExecutionList");
        preference.setInsertId("newExecutionList");
        return preference;
    }
}
