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
class TreatmentStatusWindowTable extends AbstractWindowTable {
    TreatmentStatusWindowTable(GuiContext ctxt) throws RequestException {
        super(ctxt, "Table PM_TREATMENT_STATUS", true);
    }


    @Override
    protected Preference getRequestTablePreference() {
        getRequestTable().setEditable(true, new String[]{"executionListId", "treatmentId"});
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("executionListId", "Id liste de traitements", 50, 1000, 50));
        columns.add(new Column("treatmentId", "Id traitement", 50, 1000, 50));
        columns.add(new Column("status", "Status traitement", 50, 1000, 50));
        columns.add(new Column("executionDate", "Date d'exécution traitement", 50, 1000, 50));
        columns.add(new Column("errorMessage", "Message d'erreur", 50, 1000, 50));
        preference.setColumns(columns);
        preference.setSelectAllId("selectAllTreatmentStatus");
        preference.setDeleteId("deleteTreatmentStatus");
        preference.setUpdateId("updateTreatmentStatus");
        preference.setInsertId("newTreatmentStatus");
        return preference;
    }
}