package net.codjo.dataprocess.gui.util.std;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
/**
 *
 */
public class ExportTextAction extends AbstractGuiAction {
    protected static final Logger LOGGER = Logger.getLogger(ExportTextAction.class);
    protected ExportTextBuilder builder;


    public ExportTextAction(GuiContext ctxt, RequestTable... table) {
        super(ctxt, "Export", "Exporter la page courante", "dataprocess.text");
        createBuilder(table);
        setEnabled(true);
    }


    private void createBuilder(RequestTable... tables) {
        this.builder = new ExportTextBuilder();
        this.builder.setGuiContext(getGuiContext());
        builder.addAll(tables);
    }


    protected void setTableHeaderForBuilder(RequestTable... tables) {
        for (RequestTable table : tables) {
            builder.setHeaderData(table, ExportTextBuilder.createColumnHeader(table));
        }
    }


    public void actionPerformed(ActionEvent evt) {
        try {
            displayWaitCursor();
            setTableHeaderForBuilder(builder.getTableList().toArray(new RequestTable[builder.getTableList()
                  .size()]));
            builder.generate(exportAllPages());
        }
        catch (Exception ex) {
            LOGGER.error("Erreur d'export des données", ex);
            ErrorDialog.show(null, "Erreur d'export des données", ex);
        }
        finally {
            displayDefaultCursor();
        }
    }


    protected boolean exportAllPages() {
        return false;
    }


    @Override
    public void setEnabled(boolean enabled) {
        boolean enableHandler = true;
        for (RequestTable aTable : builder.getTableList()) {
            if ((aTable != null)
                && (aTable.getPreference() != null)
                && (aTable.getPreference().getSelectAll() != null)
                && !getGuiContext().getUser().isAllowedTo(aTable.getPreference().getSelectAll().getId())) {
                enableHandler = false;
                break;
            }
        }
        super.setEnabled(enabled && enableHandler);
    }
}
