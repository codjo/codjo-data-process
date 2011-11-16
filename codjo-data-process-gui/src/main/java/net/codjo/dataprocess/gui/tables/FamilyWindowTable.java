/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.tables;
import net.codjo.gui.toolkit.swing.CheckBoxEditor;
import net.codjo.gui.toolkit.swing.CheckBoxRenderer;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
class FamilyWindowTable extends AbstractWindowTable {
    FamilyWindowTable(GuiContext ctxt) throws RequestException {
        super(ctxt, "Table PM_FAMILY", true);
    }


    @Override
    protected void doInitStuff() {
        getRequestTable().setCellRenderer("visible", new CheckBoxRenderer());
        getRequestTable().setCellEditor("visible", new CheckBoxEditor());
    }


    @Override
    protected Preference getRequestTablePreference() {
        getRequestTable().setEditable(true, new String[]{"familyId"});
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("familyId", "Identifiant", 50, 1000, 50));
        columns.add(new Column("repositoryId", "Id repository", 50, 1000, 50));
        columns.add(new Column("familyName", "Nom de la famille", 50, 1000, 50));
        columns.add(new Column("visible", "Visible", 50, 1000, 50));
        preference.setColumns(columns);
        preference.setSelectAllId("selectAllFamily");
        preference.setDeleteId("deleteFamily");
        preference.setUpdateId("updateFamily");
        preference.setInsertId("newFamily");
        return preference;
    }
}
