package net.codjo.dataprocess.gui.util.table;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.RequestTable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class RequestTableTools {
    private RequestTableTools() {
    }


    public static List<String> setEditableColumns(RequestTable requestTable,
                                                  List<String> columnNamesEditable) {
        List<Column> columns = requestTable.getPreference().getColumns();
        List<String> columnNamesNotEditable = new ArrayList<String>();
        for (Column column : columns) {
            columnNamesNotEditable.add(column.getFieldName());
        }
        columnNamesNotEditable.removeAll(columnNamesEditable);
        String[] columnNamesNotEditableArray = columnNamesNotEditable
              .toArray(new String[columnNamesNotEditable.size()]);
        requestTable.setEditable(true, columnNamesNotEditableArray);
        return columnNamesNotEditable;
    }


    public static void resizeColumn(RequestTable requestTable, Map<String, Integer> map) {
        for (int i = 0; i < requestTable.getColumnCount(); i++) {
            String columnName = requestTable.getColumnName(i);
            if (map.containsKey(columnName)) {
                requestTable.getColumn(columnName).setMinWidth(map.get(columnName));
            }
        }
    }
}
