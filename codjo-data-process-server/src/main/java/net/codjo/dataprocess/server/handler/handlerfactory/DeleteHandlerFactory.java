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

import static net.codjo.mad.server.handler.XMLUtils.convertFromStringValue;
/**
 *
 */
public class DeleteHandlerFactory extends AbstractHandlerFactory {

    public DeleteHandlerFactory(MutablePicoContainer container, TableModel tableModel) {
        super(container, tableModel);
    }


    public Handler createHandler() {
        return new DeleteHandler(getTableModel(), getDatabase());
    }


    public String getHandlerId() {
        return getTableModel().getHandlerIdProvider().getDeleteHandlerId();
    }


    private static class DeleteHandler extends AbstractSqlHandler {
        DeleteHandler(TableModel tableModel, Database database) {
            super(tableModel, database,
                  new AbstractQueryFactory() {
                      public String create(TableModel tableModel) {
                          StringBuilder sb = new StringBuilder("delete from ");
                          sb.append(tableModel.getName());
                          sb.append(buildWhereClause(tableModel));
                          String sql = sb.toString();
                          Log.debug(DeleteHandler.class, sql);
                          return sql;
                      }
                  });
        }


        @Override
        protected void fillQuery(PreparedQuery query, Map<String, String> args) throws SQLException {
            int idx = 1;
            for (FieldModel field : getTableModel().getPks()) {
                query.setObject(idx++,
                                convertFromStringValue(field.getJavaType(), args.get(field.getName())),
                                field.getType().getSqlType());
            }
        }


        @Override
        public String getId() {
            return getTableModel().getHandlerIdProvider().getDeleteHandlerId();
        }
    }
}
