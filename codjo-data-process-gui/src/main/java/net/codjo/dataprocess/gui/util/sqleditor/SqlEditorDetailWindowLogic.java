/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.sqleditor;
import net.codjo.dataprocess.common.eventsbinder.EventsBinder;
import net.codjo.dataprocess.common.eventsbinder.annotations.OnError;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnAction;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnKey;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnMouse;
import net.codjo.dataprocess.gui.util.ErrorDialog;
import net.codjo.dataprocess.gui.util.sqleditor.components.DataBasePopupGui;
import net.codjo.dataprocess.gui.util.sqleditor.components.DataBasePopupLogic;
import net.codjo.dataprocess.gui.util.sqleditor.components.SQLSyntaxEditor;
import net.codjo.dataprocess.gui.util.sqleditor.util.SQLEditorTools;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.tree.TreePath;
/**
 *
 */
public class SqlEditorDetailWindowLogic {
    private WeakReference<SqlEditorDetailWindowGui> weakGui;
    private EventsBinder eventsBinder;
    private final SQLEditorTools sqlEditorTools;


    public SqlEditorDetailWindowLogic(EventsBinder eventsBinder, SQLEditorTools sqlEditorTools)
          throws Exception {
        this(eventsBinder, sqlEditorTools, new SqlEditorDetailWindowGui());
    }


    public SqlEditorDetailWindowLogic(EventsBinder eventsBinder, SQLEditorTools sqlEditorTools,
                                      SqlEditorDetailWindowGui gui) throws Exception {
        this.eventsBinder = eventsBinder;
        this.sqlEditorTools = sqlEditorTools;
        eventsBinder.bind(this, gui);

        List<String> metaData = sqlEditorTools.loadMetaData();
        gui.getDatabaseTree().setData(metaData);
        configureEditor(metaData, gui);

        DataBasePopupLogic dataBasePopupLogic =
              new DataBasePopupLogic(eventsBinder, gui.getDatabaseTree(), gui.getSqlEditor());
        DataBasePopupGui dataBasePopupGui = new DataBasePopupGui();
        eventsBinder.bind(dataBasePopupLogic, dataBasePopupGui);

        gui.setDatabasePopupMenu(dataBasePopupGui);
        weakGui = new WeakReference<SqlEditorDetailWindowGui>(gui);
    }


    public SqlEditorDetailWindowGui getGui() {
        return weakGui.get();
    }


    @OnAction(propertiesBound = SqlEditorDetailWindowGui.TAGS.HISTORY)
    public void selectOldRequest(SqlEditorDetailWindowGui gui) {
        gui.getSqlEditor().setText(gui.getSqlHistory().getSelectedItem().toString());
    }


    @OnAction(propertiesBound = "QuitButton")
    public void quitAction(SqlEditorDetailWindowGui gui) {
        gui.dispose();
    }


    @OnMouse(value = SqlEditorDetailWindowGui.TAGS.DB_TREE, eventType = OnMouse.EventType.ALL,
             popupTriggered = OnMouse.PopupType.TRUE)
    public void rightButtonOnTree(MouseEvent event, SqlEditorDetailWindowGui gui) {
        gui.getDatabasePopupMenu().show(event.getComponent(), event.getX(), event.getY());

        int selRow = gui.getDatabaseTree().getRowForLocation(event.getX(), event.getY());
        if (selRow >= 0) {
            TreePath selPath = gui.getDatabaseTree().getPathForLocation(event.getX(), event.getY());
            gui.getDatabaseTree().setSelectionPath(selPath);
            gui.getDatabasePopupMenu().getMenuItemGenerateInsertInto()
                  .setEnabled(selPath.getPath().length == 2);
            gui.getDatabasePopupMenu().getMenuItemGenerateSelectAll()
                  .setEnabled(selPath.getPath().length == 2);
            gui.getDatabasePopupMenu().getMenuItemGenerateUpdate().setEnabled(selPath.getPath().length >= 2);
        }
    }


    @OnMouse(value = SqlEditorDetailWindowGui.TAGS.DB_TREE, eventType = OnMouse.EventType.CLICKED,
             clickCount = 2)
    public void copyNameFromTreeToSqlEditor(MouseEvent event, SqlEditorDetailWindowGui gui)
          throws BadLocationException {
        int selRow = gui.getDatabaseTree().getRowForLocation(event.getX(), event.getY());
        if (selRow >= 0) {
            TreePath selPath = gui.getDatabaseTree().getPathForLocation(event.getX(), event.getY());
            SQLSyntaxEditor sqlEditor = gui.getSqlEditor();
            int pos = sqlEditor.getCaretPosition();
            sqlEditor.getSyntaxDocument()
                  .insertString(pos, selPath.getLastPathComponent().toString(), new SimpleAttributeSet());
        }
    }


    private static void configureEditor(List<String> metaData, SqlEditorDetailWindowGui gui) {
        List<String> tableDotFields = new ArrayList<String>();
        List<String> tableAndFields = new ArrayList<String>();
        for (String line : metaData) {
            int dotIndex = line.indexOf('.');
            String table = line.substring(0, dotIndex);
            String column = line.substring(dotIndex + 1);
            if (!tableAndFields.contains(table)) {
                tableAndFields.add(table);
            }
            if (!tableAndFields.contains(column)) {
                tableAndFields.add(column);
            }
            if (!tableDotFields.contains(line)) {
                tableDotFields.add(line);
            }
        }
        gui.getSqlEditor().setLists(tableDotFields, tableAndFields);
    }


    private static int getPageSize(SqlEditorDetailWindowGui gui) {
        try {
            return Integer.parseInt(gui.getPageSize().getText());
        }
        catch (Exception ex) {
            return 1000;
        }
    }


    @OnAction(propertiesBound = SqlEditorDetailWindowGui.TAGS.EXEC_BUTTON)
    @OnKey(propertiesBound = SqlEditorDetailWindowGui.TAGS.SQL_EDITOR, eventType = OnKey.EventType.PRESSED,
           modifiers = KeyEvent.CTRL_MASK, keyCode = 10)
    public void executeSql(final SqlEditorDetailWindowGui gui) {
        if (gui.getSqlEditor().getText().trim().length() > 0) {
            gui.getWaitingPanel().exec(new Runnable() {
                public void run() {
                    String sql = gui.getSqlEditor().getText();
                    gui.getSqlHistory().historize(sql);
                    StringBuffer resultString;
                    try {
                        resultString = sqlEditorTools.executeRequest(sql, 1, getPageSize(gui));
                        gui.getSqlResultTabs()
                              .addResult(eventsBinder, resultString, sql, gui.getWaitingPanel(),
                                         getPageSize(gui), sqlEditorTools);
                    }
                    catch (Exception e) {
                        ErrorDialog.show(null, "Error de l'execution de la requête", e);
                    }
                }
            });
        }
    }


    @OnError
    public void exceptionRaisedWhileInvokingMethod(Throwable ex) {
        ErrorDialog.show(null, "System error", ex);
    }
}
