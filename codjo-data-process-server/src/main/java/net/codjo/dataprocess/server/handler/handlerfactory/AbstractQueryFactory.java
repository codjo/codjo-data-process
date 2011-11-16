package net.codjo.dataprocess.server.handler.handlerfactory;
import net.codjo.dataprocess.common.table.model.FieldModel;
import net.codjo.dataprocess.common.table.model.TableModel;
import net.codjo.util.string.StringUtil;
import java.util.Iterator;
import java.util.List;
/**
 *
 */
public abstract class AbstractQueryFactory implements QueryFactory {
    protected String buildSelectClause(TableModel table) {
        int idx = 0;
        StringBuilder sb = new StringBuilder();
        for (FieldModel field : table.getFields()) {
            if (idx > 0) {
                sb.append(", ");
            }
            sb.append(field.getSqlName());
            idx++;
        }
        return String.format("select %s from %s", sb.toString(), table.getName());
    }


    protected String buildWhereClause(TableModel tableModel) {
        StringBuilder sb = new StringBuilder(" where ");
        for (Iterator<FieldModel> it = tableModel.getPks().iterator(); it.hasNext(); ) {
            sb.append(StringUtil.javaToSqlName(it.next().getName())).append(" = ? ");
            if (it.hasNext()) {
                sb.append(" and ");
            }
        }
        return sb.toString();
    }


    protected String buildFieldValuesList(int nbValues) {
        StringBuilder sb = new StringBuilder(" values (");
        for (int i = 0; i < nbValues; i++) {
            sb.append("?");
            if (i < nbValues - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }


    protected String buildFieldNameList(List<FieldModel> fields) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<FieldModel> it = fields.iterator(); it.hasNext(); ) {
            sb.append(it.next().getSqlName());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
