package net.codjo.dataprocess.server.handler.handlerfactory;
import net.codjo.database.api.Database;
import net.codjo.database.api.query.PreparedQuery;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.table.model.FieldModel;
import net.codjo.dataprocess.common.table.model.TableModel;
import net.codjo.mad.server.handler.Handler;
import java.sql.SQLException;
import java.util.Map;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class InsertHandlerFactory extends AbstractHandlerFactory {

    public InsertHandlerFactory(MutablePicoContainer container, TableModel tableModel) {
        super(container, tableModel);
    }


    public String getHandlerId() {
        return getTableModel().getHandlerIdProvider().getInsertHandlerId();
    }


    public Handler createHandler() {
        return new InsertHandler(getTableModel(), getDatabase());
    }


    private static class InsertHandler extends AbstractSqlHandler {
        InsertHandler(TableModel table, Database database) {
            super(table, database,
                  new AbstractQueryFactory() {
                      public String create(TableModel tableModel) {
                          String sql = String.format("insert into %s (%s) %s",
                                                     tableModel.getName(),
                                                     buildFieldNameList(tableModel.getNotPks()),
                                                     buildFieldValuesList(tableModel.getNotPks().size()));
                          Log.debug(InsertHandler.class, sql);
                          return sql;
                      }
                  });
        }


        @Override
        protected void fillQuery(PreparedQuery query, Map<String, String> args) throws SQLException {
            int idx = 1;
            for (FieldModel field : getTableModel().getNotPks()) {
                String value = args.get(field.getName());
                int sqlType = field.getType().getSqlType();
                if (value == null || "null".equals(value)) {
                    query.setObject(idx++, null, sqlType);
                }
                else {
                    query.setObject(idx++, value, sqlType);
                }
            }
        }


        @Override
        public String getId() {
            return getTableModel().getHandlerIdProvider().getInsertHandlerId();
        }
    }
}
