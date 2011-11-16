package net.codjo.dataprocess.server.handler.handlerfactory;
import net.codjo.database.api.Database;
import net.codjo.dataprocess.common.table.model.FieldModel;
import net.codjo.dataprocess.common.table.model.TableModel;
import net.codjo.mad.server.handler.sql.Getter;
import net.codjo.mad.server.handler.sql.SqlHandler;
/**
 *
 */
public abstract class AbstractSqlHandler extends SqlHandler {
    private TableModel tableModel;


    protected AbstractSqlHandler(TableModel tableModel, Database database, QueryFactory queryFactory) {
        super(tableModel.getPkAsStrArray(), queryFactory.create(tableModel), database);
        this.tableModel = tableModel;
    }


    protected void addGetters() {
        int idx = 1;
        for (FieldModel field : tableModel.getFields()) {
            addGetter(field.getName(), new Getter(idx++));
        }
    }


    protected TableModel getTableModel() {
        return tableModel;
    }
}
