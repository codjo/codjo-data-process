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
public class UserWindowTable extends AbstractWindowTable {
    public UserWindowTable(GuiContext ctxt) throws RequestException {
        super(ctxt, "Gestion des droits sur les référentiels de traitement", false);
    }


    @Override
    protected Preference getRequestTablePreference() {
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("userName", "Identifiant de l'utilisateur", 50, 1000, 50));
        columns.add(new Column("userParam", "Paramètres de l'utilisateur", 50, 1000, 50));
        preference.setColumns(columns);
        preference.setSelectByPkId("selectDpUserById");
        preference.setSelectAllId("selectAllDpUser");
        preference.setDeleteId("deleteDpUser");
        preference.setUpdateId("updateDpUser");
        preference.setInsertId("newDpUser");
        preference.setDetailWindowClass(UserDetailWindow.class);
        return preference;
    }
}
