package net.codjo.dataprocess.gui.util.sqleditor.loglist;
import net.codjo.dataprocess.gui.tables.AbstractWindowTable;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class DirectSqlLogListWindow extends AbstractWindowTable {
    DirectSqlLogListWindow(GuiContext ctxt) throws RequestException {
        super(ctxt, "Log des appel sql direct     [T_DIRECTSQL_LOG]", false);
        setSize(400, 500);
    }


    /**
     * Préférence pour afficher la table T_DIRECTSQL_LOG.
     *
     * @return preference
     */
    @Override
    protected Preference getRequestTablePreference() {
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("tDirectSqlLogId", "Id num.", 50, 1000, 50));
        columns.add(new Column("initiator", "Utilisateur", 50, 1000, 50));
        columns.add(new Column("flag", "Flag", 50, 1000, 50));
        columns.add(new Column("requestDate", "Date", 50, 1000, 50));
        columns.add(new Column("sqlRequest", "Sql", 50, 1000, 50));
        columns.add(new Column("result", "Résultat", 50, 1000, 50));

        Preference preference = new Preference();
        preference.setColumns(columns);
        preference.setSelectAllId("selectAllTDirectSqlLog");
        preference.setSelectByPkId("selectTDirectSqlLogById");
        preference.setDeleteId("deleteTDirectSqlLog");
        preference.setUpdateId("updateTDirectSqlLog");
        preference.setRequetorId("tDirectSqlLogRequetorHandler");
        preference.setDetailWindowClass(DirectSqlLogDetailWindow.class);

        return preference;
    }
}
