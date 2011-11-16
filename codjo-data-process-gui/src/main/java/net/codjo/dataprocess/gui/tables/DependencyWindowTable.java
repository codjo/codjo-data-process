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
class DependencyWindowTable extends AbstractWindowTable {
    DependencyWindowTable(GuiContext ctxt) throws RequestException {
        super(ctxt, "Table PM_DEPENDENCY", true);
    }


    @Override
    protected Preference getRequestTablePreference() {
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("repositoryId", "Id repository", 50, 1000, 50));
        columns.add(new Column("executionListIdPrinc",
                               "Id liste de traitements principale", 50, 1000, 50));
        columns.add(new Column("executionListIdDep",
                               "Id liste de traitements dependante", 50, 1000, 50));
        preference.setColumns(columns);
        preference.setSelectAllId("selectAllExecutionListDependency");
        preference.setDeleteId("deleteExecutionListDependency");
        preference.setUpdateId("updateExecutionListDependency");
        preference.setInsertId("newExecutionListDependency");
        return preference;
    }
}
