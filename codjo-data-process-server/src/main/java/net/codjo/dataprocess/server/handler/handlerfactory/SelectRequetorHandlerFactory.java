package net.codjo.dataprocess.server.handler.handlerfactory;
import net.codjo.database.api.Database;
import net.codjo.dataprocess.common.table.model.FieldModel;
import net.codjo.dataprocess.common.table.model.TableModel;
import net.codjo.mad.server.handler.Handler;
import net.codjo.mad.server.handler.requetor.AbstractRequetorHandler;
import java.util.List;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class SelectRequetorHandlerFactory extends AbstractHandlerFactory {

    public SelectRequetorHandlerFactory(MutablePicoContainer container, TableModel tableModel) {
        super(container, tableModel);
    }


    public String getHandlerId() {
        return getTableModel().getHandlerIdProvider().getSelectRequetorHandlerId();
    }


    public Handler createHandler() {
        return new SelectRequetorHandler(getTableModel(), getDatabase());
    }


    private static class SelectRequetorHandler extends AbstractRequetorHandler {
        private TableModel table;


        SelectRequetorHandler(TableModel table, Database database) {
            super(table.getName(), table.getPkAsStrArray(), database);
            this.table = table;

            final List<FieldModel> fields = table.getFields();
            for (FieldModel field : fields) {
                wrappers.put(field.getName(), new SqlWrapper(field.getName()));
            }
        }


        protected SelectRequetorHandler(String sqlTableName, String[] pks, Database database) {
            super(sqlTableName, pks, database);
        }


        public String getId() {
            return table.getHandlerIdProvider().getSelectRequetorHandlerId();
        }
    }
}
