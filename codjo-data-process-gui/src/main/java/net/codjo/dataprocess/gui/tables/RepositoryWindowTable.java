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
class RepositoryWindowTable extends AbstractWindowTable {
    RepositoryWindowTable(GuiContext ctxt) throws RequestException {
        super(ctxt, "Table PM_REPOSITORY", true);
    }


    @Override
    protected Preference getRequestTablePreference() {
        getRequestTable().setEditable(true, new String[]{"repositoryId"});
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("repositoryId", "Identifiant", 50, 1000, 50));
        columns.add(new Column("repositoryName", "Nom du repository", 50, 1000, 50));
        preference.setColumns(columns);
        preference.setSelectAllId("selectAllRepository");
        preference.setDeleteId("deleteRepository");
        preference.setUpdateId("updateRepository");
        preference.setInsertId("newRepository");
        return preference;
    }
}
