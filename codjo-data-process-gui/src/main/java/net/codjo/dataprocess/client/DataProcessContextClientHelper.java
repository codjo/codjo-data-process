package net.codjo.dataprocess.client;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class DataProcessContextClientHelper {
    private DataProcessContextClientHelper() {
    }


    public static boolean saveContext(MutableGuiContext ctxt, String contextName, DataProcessContext context)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("contextName", contextName);
        arg.put("context", context.encode());

        Row row = sender.send(ctxt, arg, "saveContext");
        String result = row.getFieldValue("result");
        return result.equals(DataProcessConstants.CONTEXT_SAVE_OK);
    }


    public static void saveContext(MutableGuiContext ctxt,
                                   String contextName,
                                   String contextKey,
                                   String contextValue) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("contextName", contextName);
        arg.put("contextKey", contextKey);
        arg.put("contextValue", contextValue);
        sender.send(ctxt, arg, "saveContextNameKeyValue");
    }


    public static DataProcessContext getDataProcessContext(MutableGuiContext ctxt, String contextName)
          throws RequestException {
        Map<String, String> selectors = new HashMap<String, String>();
        selectors.put("contextName", contextName);
        HandlerCommandSender sender = new HandlerCommandSender();
        Result rs = sender.sendSqlCommand(ctxt, "selectContextByContextName", null, selectors);

        DataProcessContext context = new DataProcessContext();
        for (int i = 0; i < rs.getRowCount(); i++) {
            context.setProperty(rs.getValue(i, "contextKey"), rs.getValue(i, "contextValue"));
        }
        return context;
    }


    public static String getContextValue(MutableGuiContext ctxt, String contextName, String contextKey)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> selectors = new HashMap<String, String>();
        selectors.put("contextName", contextName);
        selectors.put("contextKey", contextKey);
        Result rs = sender.sendSqlCommand(ctxt, "selectValueByKeyAndContextName", null, selectors);
        return rs.getValue(0, "contextValue");
    }


    public static String deleteContextByContextName(MutableGuiContext ctxt, String contextName)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("contextName", contextName);
        Result rs = sender.sendDeleteSqlCommand(ctxt, "deleteContextByContextName", fieldValues);
        return rs.getValue(0, "count");
    }


    public static String duplicateContext(MutableGuiContext ctxt,
                                          String oldContextName,
                                          String newContextName)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("oldContextName", oldContextName);
        fieldValues.put("newContextName", newContextName);
        Result rs = sender.sendInsertSqlCommand(ctxt, "duplicateContext", fieldValues);
        return rs.getValue(0, "count");
    }
}
