/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.sqleditor.components;
import net.codjo.dataprocess.common.eventsbinder.EventBinderException;
import net.codjo.dataprocess.common.eventsbinder.EventsBinder;
import net.codjo.dataprocess.common.eventsbinder.annotations.OnError;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnAction;
import net.codjo.dataprocess.gui.util.ErrorDialog;
import java.lang.ref.WeakReference;
import javax.swing.tree.TreePath;
/**
 *
 */
public class DataBasePopupLogic {
    private WeakReference<DatabaseJTree> weakDatabaseJTree;
    private WeakReference<SQLSyntaxEditor> weakSqlEditor;
    private DataBasePopupGui dataBasePopupGui;


    public DataBasePopupLogic(EventsBinder eventsBinder, DatabaseJTree databaseJTree,
                              SQLSyntaxEditor sqlEditor) throws EventBinderException {
        weakDatabaseJTree = new WeakReference<DatabaseJTree>(databaseJTree);
        weakSqlEditor = new WeakReference<SQLSyntaxEditor>(sqlEditor);
        dataBasePopupGui = new DataBasePopupGui();
        eventsBinder.bind(this, dataBasePopupGui);
    }


    @OnAction(propertiesBound = "menuItemGenerateInsertInto")
    public void generateInsertInto() {
        TreePath selectionPath = weakDatabaseJTree.get().getSelectionPath();
        Object table = selectionPath.getLastPathComponent();
        StringBuilder sql = new StringBuilder("insert into ").append(table).append(" \n(");
        StringBuilder values = new StringBuilder(" \nvalues (");
        int count = weakDatabaseJTree.get().getModel().getChildCount(table);
        for (int currentChild = 0; currentChild < count; currentChild++) {
            if (currentChild > 0) {
                sql.append(",");
                values.append(",");
            }
            sql.append(weakDatabaseJTree.get().getModel().getChild(table, currentChild));
            values.append(" ? ");
        }
        sql.append(") ").append(values).append(")");
        weakSqlEditor.get().setText(sql.toString());
    }


    @OnAction(propertiesBound = "menuItemGenerateUpdate")
    public void generateUpdate() {
        TreePath selectionPath = weakDatabaseJTree.get().getSelectionPath();

        Object table;
        table = selectionPath.getPath()[1];

        StringBuilder sql = new StringBuilder("update ").append(table).append(" set \n");

        if (selectionPath.getPath().length == 2) {
            int count = weakDatabaseJTree.get().getModel().getChildCount(table);
            for (int currentChild = 0; currentChild < count; currentChild++) {
                sql.append(weakDatabaseJTree.get().getModel().getChild(table, currentChild)).append(" = ? ");
                if (currentChild < count - 1) {
                    sql.append(",\n");
                }
            }
        }
        else {
            sql.append(selectionPath.getPath()[2]).append(" = ? ");
        }
        sql.append("\nwhere ?\n");
        weakSqlEditor.get().setText(sql.toString());
    }


    @OnAction(propertiesBound = "menuItemGenerateSelectAll")
    public void generateSelectAll() {
        weakSqlEditor.get()
              .setText("select * from " + weakDatabaseJTree.get().getSelectionPath().getLastPathComponent());
    }


    @OnError
    public void exceptionRaisedWhileInvokingMethod(Throwable ex) {
        ErrorDialog.show(null, "Unmanageable exception received", ex);
    }
}
