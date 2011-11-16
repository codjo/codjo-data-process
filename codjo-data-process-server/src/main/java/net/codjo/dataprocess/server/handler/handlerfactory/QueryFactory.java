package net.codjo.dataprocess.server.handler.handlerfactory;
import net.codjo.dataprocess.common.table.model.TableModel;
/**
 *
 */
public interface QueryFactory {
    String create(TableModel tableModel);
}
