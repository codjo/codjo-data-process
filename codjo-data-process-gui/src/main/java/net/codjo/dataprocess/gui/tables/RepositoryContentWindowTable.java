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
class RepositoryContentWindowTable extends AbstractWindowTable {
    RepositoryContentWindowTable(GuiContext ctxt) throws RequestException {
        super(ctxt, "Table PM_REPOSITORY_CONTENT", true);
    }


    @Override
    protected Preference getRequestTablePreference() {
        getRequestTable().setEditable(true, new String[]{"repositoryContentId"});
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("repositoryContentId", "Identifiant", 50, 1000, 50));
        columns.add(new Column("repositoryId", "Id repository", 50, 1000, 50));
        columns.add(new Column("treatmentId", "Id traitement", 50, 1000, 50));
        columns.add(new Column("content", "Contenu", 50, 1000, 50));
        preference.setColumns(columns);
        preference.setSelectAllId("selectAllRepositoryContent");
        preference.setDeleteId("deleteRepositoryContent");
        preference.setUpdateId("updateRepositoryContent");
        preference.setInsertId("newRepositoryContent");
        return preference;
    }
}
