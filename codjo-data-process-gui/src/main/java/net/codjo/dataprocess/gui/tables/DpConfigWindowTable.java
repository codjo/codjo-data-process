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
class DpConfigWindowTable extends AbstractWindowTable {
    DpConfigWindowTable(GuiContext ctxt) throws RequestException {
        super(ctxt, "Paramétrage des variables de configuration [PM_DP_CONFIG]", true);
    }


    @Override
    protected Preference getRequestTablePreference() {
        getRequestTable().setEditable(true);
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("cle", "Clé", 50, 1000, 50));
        columns.add(new Column("valeur", "Valeur", 50, 1000, 50));
        preference.setColumns(columns);
        preference.setSelectAllId("selectAllPmDpConfig");
        preference.setDeleteId("deletePmDpConfig");
        preference.setUpdateId("updatePmDpConfig");
        preference.setInsertId("newPmDpConfig");
        return preference;
    }
}