package net.codjo.dataprocess.gui.util.std;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.SwingRunnable;
import java.util.Observable;

import static net.codjo.dataprocess.common.DataProcessConstants.TABLE_NAME_KEY;
import static net.codjo.dataprocess.common.DataProcessConstants.WHERE_CLAUSE_KEY;
/**
 *
 */
public class DefaultRequestTableLoader extends AbstractRequestTableLoader {

    @Override
    protected void refreshTable() {
        abstractListWindow.getCtxt().executeTask(new DefaultRequestTableLoading());
    }


    public void update(Observable o, Object arg) {
    }


    @Override
    protected void init(AbstractListWindow listWindow) {
        this.abstractListWindow = listWindow;
    }


    private class DefaultRequestTableLoading extends SwingRunnable {
        DefaultRequestTableLoading() {
            super("Chargement des données en cours...");
        }


        public void run() {
            try {
                FieldsList fieldsList = new FieldsList();
                fieldsList.addField(TABLE_NAME_KEY, abstractListWindow.getSqlTableName());
                String whereClause = (String)getProperty(WHERE_CLAUSE_KEY);
                if (whereClause != null && whereClause.trim().length() != 0) {
                    fieldsList.addField(WHERE_CLAUSE_KEY, whereClause);
                    if (!abstractListWindow.getTitle().contains("}")) {
                        abstractListWindow.setTitle(abstractListWindow.getTitle() +
                                                    "     { " + whereClause + " }");
                    }
                }

                abstractListWindow.getRequestTable().setSelector(fieldsList);
                abstractListWindow.getRequestTable().load();
                if (Log.isInfoEnabled()) {
                    Log.info(getClass(),
                             "Affichage de '" + abstractListWindow.getSqlTableName() + "' ("
                             + abstractListWindow.getRequestTable().getDataSource()
                                   .getTotalRowCount() + " rows au total) ; whereClause = " + whereClause);
                }
            }
            catch (RequestException ex) {
                GuiUtils.showErrorDialog(abstractListWindow, getClass(),
                                         "Erreur lors de la récupération des données", ex);
            }
        }
    }
}
