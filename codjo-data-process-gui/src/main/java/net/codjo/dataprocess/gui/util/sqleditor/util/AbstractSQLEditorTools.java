package net.codjo.dataprocess.gui.util.sqleditor.util;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/**
 *
 */
public abstract class AbstractSQLEditorTools implements SQLEditorTools {

    public String[] lineToArray(String line, String sep) {
        List<String> list = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(line, sep);
        while (tokenizer.hasMoreElements()) {
            list.add(tokenizer.nextElement().toString());
        }
        String result[] = new String[list.size()];
        list.toArray(result);
        return result;
    }


    public String extractLine(StringBuffer buffer) {
        int index = buffer.indexOf("\n");
        if (index >= 0) {
            String extracted = buffer.substring(0, index);
            buffer.replace(0, index + "\n".length(), "");
            return extracted;
        }
        else {
            return null;
        }
    }
}
