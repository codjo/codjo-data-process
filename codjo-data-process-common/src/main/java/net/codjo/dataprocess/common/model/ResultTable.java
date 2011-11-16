package net.codjo.dataprocess.common.model;
/**
 *
 */
public class ResultTable {
    private String table;
    private String selectAllHandler;


    public ResultTable() {
    }


    public ResultTable(String table, String selectAllHandler) {
        this.table = table;
        this.selectAllHandler = selectAllHandler;
    }


    public String getTable() {
        if (table != null) {
            return table.trim();
        }
        else {
            return table;
        }
    }


    public void setTable(String table) {
        this.table = table;
    }


    public String getSelectAllHandler() {
        return selectAllHandler;
    }


    public void setSelectAllHandler(String selectAllHandler) {
        this.selectAllHandler = selectAllHandler;
    }


    @Override
    public String toString() {
        return "resultTableName = " + table + ", selectAllHandler = " + selectAllHandler;
    }
}
