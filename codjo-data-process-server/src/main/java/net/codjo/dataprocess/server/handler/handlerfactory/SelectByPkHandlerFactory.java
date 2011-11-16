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
public class SelectByPkHandlerFactory extends AbstractHandlerFactory {

    public SelectByPkHandlerFactory(MutablePicoContainer container, TableModel tableModel) {
        super(container, tableModel);
    }


    public String getHandlerId() {
        return getTableModel().getHandlerIdProvider().getSelectHandlerById();
    }


    public Handler createHandler() {
        return new SelectByPkHandler(getTableModel(), getDatabase());
    }


    private static class SelectByPkHandler extends AbstractSqlHandler {
        SelectByPkHandler(TableModel table, Database database) {
            super(table, database,
                  new AbstractQueryFactory() {
                      public String create(TableModel table) {
                          String sql = buildSelectClause(table) + buildWhereClause(table);
                          Log.debug(SelectByPkHandler.class, sql);
                          return sql;
                      }
                  });
            addGetters();
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
            return getTableModel().getHandlerIdProvider().getSelectHandlerById();
        }
    }
}
