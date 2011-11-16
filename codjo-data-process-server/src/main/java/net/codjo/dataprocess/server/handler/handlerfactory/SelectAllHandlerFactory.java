package net.codjo.dataprocess.server.handler.handlerfactory;
import net.codjo.database.api.Database;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.table.model.TableModel;
import net.codjo.mad.server.handler.Handler;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class SelectAllHandlerFactory extends AbstractHandlerFactory {

    public SelectAllHandlerFactory(MutablePicoContainer container, TableModel tableModel) {
        super(container, tableModel);
    }


    public String getHandlerId() {
        return getTableModel().getHandlerIdProvider().getSelectAllHandlerId();
    }


    public Handler createHandler() {
        return new SelectAllHandler(getTableModel(), getDatabase());
    }


    private static class SelectAllHandler extends AbstractSqlHandler {
        SelectAllHandler(TableModel table, Database database) {
            super(table, database,
                  new AbstractQueryFactory() {
                      public String create(TableModel tableModel) {
                          String sql = buildSelectClause(tableModel);
                          Log.debug(SelectAllHandler.class, sql);
                          return sql;
                      }
                  });
            addGetters();
        }


        @Override
        public String getId() {
            return getTableModel().getHandlerIdProvider().getSelectAllHandlerId();
        }
    }
}
