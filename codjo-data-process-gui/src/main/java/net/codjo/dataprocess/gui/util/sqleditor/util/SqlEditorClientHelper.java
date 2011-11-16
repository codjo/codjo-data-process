package net.codjo.dataprocess.gui.util.sqleditor.util;
import net.codjo.dataprocess.client.HandlerCommandSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.framework.GuiContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
/**
 *
 */
public class SqlEditorClientHelper extends AbstractSQLEditorTools {
    private GuiContext ctxt;


    public SqlEditorClientHelper(GuiContext ctxt) {
        this.ctxt = ctxt;
    }


    public StringBuffer executeRequest(String sql, int currentPage, int pageSize) throws Exception {
        StringBuffer resultString = null;
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("sql", sql);
        arg.put("currentPage", Integer.toString(currentPage));
        arg.put("pageSize", Integer.toString(pageSize));
        Result result = sender.sendCommand(ctxt, "dbExecuteSqlRequest", arg);
        if (result.getRowCount() > 0) {
            resultString = new StringBuffer(result.getRow(0).getFieldValue("result"));
        }
        return resultString;
    }


    public List<String> loadMetaData() throws Exception {
        List<String> metas = new ArrayList<String>();
        HandlerCommandSender sender = new HandlerCommandSender();
        Result result = sender.sendCommand(ctxt, "dbGetAllFieldNamesByTable", null);
        if (result.getRowCount() > 0) {
            String metaDataList = result.getRow(0).getFieldValue("result");
            StringTokenizer tokenizer = new StringTokenizer(metaDataList, ",");
            while (tokenizer.hasMoreElements()) {
                String metaData = (String)tokenizer.nextElement();
                metas.add(metaData);
            }
        }
        return metas;
    }
}
