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
class ContextWindowTable extends AbstractWindowTable {
    ContextWindowTable(GuiContext ctxt) throws RequestException {
        super(ctxt, "Table PM_DP_CONTEXT", true);
    }


    @Override
    protected Preference getRequestTablePreference() {
        getRequestTable().setEditable(true, new String[]{"contextId"});
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("contextId", "Identifiant", 50, 1000, 50));
        columns.add(new Column("contextName", "Nom du contexte", 50, 1000, 50));
        columns.add(new Column("contextKey", "Clé", 50, 1000, 50));
        columns.add(new Column("contextValue", "Valeur", 50, 1000, 50));
        preference.setColumns(columns);
        preference.setSelectAllId("selectAllContext");
        preference.setDeleteId("deleteContext");
        preference.setUpdateId("updateContext");
        preference.setInsertId("newContext");
        return preference;
    }
}
