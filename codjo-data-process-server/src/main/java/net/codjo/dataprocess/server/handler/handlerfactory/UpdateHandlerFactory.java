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
public class UpdateHandlerFactory extends AbstractHandlerFactory {

    public UpdateHandlerFactory(MutablePicoContainer container, TableModel tableModel) {
        super(container, tableModel);
    }


    public String getHandlerId() {
        return getTableModel().getHandlerIdProvider().getUpdateHandlerId();
    }


    public Handler createHandler() {
        return new UpdateHandler(getTableModel(), getDatabase());
    }


    private static class UpdateHandler extends AbstractSqlHandler {
        UpdateHandler(TableModel table, Database database) {
            super(table, database,
                  new AbstractQueryFactory() {
                      public String create(TableModel tableModel) {
                          int idx = 0;
                          StringBuilder sb = new StringBuilder();
                          for (FieldModel field : tableModel.getNotPks()) {
                              if (idx > 0) {
                                  sb.append(", ");
                              }
                              sb.append(String.format("%s = ?", field.getSqlName()));
                              idx++;
                          }
                          String sql = String.format("update %s set %s %s",
                                                     tableModel.getName(),
                                                     sb.toString(),
                                                     buildWhereClause(tableModel));
                          Log.debug(UpdateHandler.class, sql);
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
                if (value != null && !"null".equals(value)) {
                    query.setObject(idx++, value, sqlType);
                }
                else {
                    query.setObject(idx++, null, sqlType);
                }
            }
            for (FieldModel field : getTableModel().getPks()) {
                query.setObject(idx++,
                                convertFromStringValue(field.getJavaType(), args.get(field.getName())),
                                field.getType().getSqlType());
            }
        }


        @Override
        public String getId() {
            return getTableModel().getHandlerIdProvider().getUpdateHandlerId();
        }
    }
}
