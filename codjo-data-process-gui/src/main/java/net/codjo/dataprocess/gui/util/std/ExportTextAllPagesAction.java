package net.codjo.dataprocess.gui.util.std;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
/**
 *
 */
public class ExportTextAllPagesAction extends ExportTextAction {
    public ExportTextAllPagesAction(GuiContext ctxt, RequestTable... table) {
        super(ctxt, table);
        putValue(SHORT_DESCRIPTION, "Exporter toutes les pages");
    }


    @Override
    protected String getSecurityFunction() {
        if (builder == null
            || builder.getTableList().isEmpty()
            || (builder.getTableList().get(0).getPreference() == null)
            || (builder.getTableList().get(0).getPreference().getSelectAll() == null)) {
            return super.getSecurityFunction();
        }
        return builder.getTableList().get(0).getPreference().getSelectAll().getId();
    }


    @Override
    protected boolean exportAllPages() {
        return true;
    }
}
