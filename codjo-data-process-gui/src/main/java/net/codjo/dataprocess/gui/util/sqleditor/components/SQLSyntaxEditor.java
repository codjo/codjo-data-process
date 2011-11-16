package net.codjo.dataprocess.gui.util.sqleditor.components;
import net.codjo.dataprocess.gui.util.editor.ColoredEditor;
import net.codjo.dataprocess.gui.util.editor.ICompletion;
import net.codjo.dataprocess.gui.util.editor.SyntaxDocument;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 *
 */
public class SQLSyntaxEditor extends ColoredEditor {

    private static final List<String> SQL_WORDS = new ArrayList<String>(
          Arrays.asList("select", "insert", "delete", "update", "from", "inner", "join", "left", "right",
                        "where", "group", "by", "having", "outer", "on", "and", "not", "or", "order",
                        "values", "distinct"));


    public SQLSyntaxEditor() {
        super(new DBCompletion());
    }


    public void setLists(List<String> tablesDotfields, List<String> tablesAndFields) {
        DBCompletion dbCompletion = (DBCompletion)getCompletion();
        dbCompletion.setKeywords(SQL_WORDS);
        dbCompletion.setTablesAndFields(tablesAndFields);
        dbCompletion.setTablesDotfields(tablesDotfields);
        configureEditor(getSyntaxDocument(), tablesAndFields);
    }


    private static void configureEditor(SyntaxDocument document, List<String> tablesAndFields) {

        document.addNewStyle("table.Column", new Color(0, 150, 0), null, true, false, false);
        document.addNewStyle("sqlKeyword", new Color(0, 0, 140), null, true, false, false);

        for (String element : tablesAndFields) {
            document.addKeyWord(element, "table.Column");
        }

        for (String word : SQL_WORDS) {
            document.addKeyWord(word, "sqlKeyword");
        }
    }


    private static class DBCompletion implements ICompletion {
        private List<String> tablesAndFields;
        private List<String> tablesDotfields;
        private List<String> keywords;


        public void setTablesAndFields(List<String> tablesAndFields) {
            this.tablesAndFields = tablesAndFields;
        }


        public void setTablesDotfields(List<String> tablesDotfields) {
            this.tablesDotfields = tablesDotfields;
        }


        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }


        public List<String> getListWithPartial(String partial) {
            List<String> liste = new ArrayList<String>();
            List<String> source;
            if (partial.contains(".")) {
                source = tablesDotfields;
            }
            else {
                source = new ArrayList<String>(tablesAndFields);
                source.addAll(keywords);
            }
            for (String element : source) {
                if (element.toUpperCase().startsWith(partial.toUpperCase())) {
                    liste.add(element);
                }
            }
            return liste;
        }
    }
}
