package net.codjo.dataprocess.gui.util.sqleditor.util;
import java.util.List;
/**
 *
 */
public interface SQLEditorTools {

    List<String> loadMetaData() throws Exception;


    StringBuffer executeRequest(String sql, int currentPage, int pageSize) throws Exception;


    String[] lineToArray(String line, String sep);


    String extractLine(StringBuffer buffer);
}
