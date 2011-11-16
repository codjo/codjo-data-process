package net.codjo.dataprocess.client;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.ListCodec;
import net.codjo.dataprocess.common.codec.ListXmlCodec;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.ResultFactory;
import net.codjo.mad.client.request.ResultFactory.BuildException;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class UtilsClientHelper {
    private UtilsClientHelper() {
    }


    public static String cmdMapServer(MutableGuiContext ctxt,
                                      DataProcessConstants.MapCommand command, String key)
          throws RequestException {
        return cmdMapServer(ctxt, command, key, "");
    }


    public static String cmdMapServer(MutableGuiContext ctxt,
                                      DataProcessConstants.MapCommand command, String key, String value)
          throws RequestException {
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("command", command.toString());
        arg.put("key", key);
        arg.put("value", value);

        if (Log.isDebugEnabled()) {
            Log.debug(UtilsClientHelper.class, "Appel de cmdMapServer (arg=" + arg.toString() + ")");
        }
        Row row = new HandlerCommandSender().send(ctxt, arg, "cmdMapServer");
        return row.getFieldValue("result");
    }


    public static void reinitialiseUserImport(MutableGuiContext ctxt) throws RequestException {
        Map<String, String> arg = new HashMap<String, String>();

        if (Log.isDebugEnabled()) {
            Log.debug(UtilsClientHelper.class, "Appel de reinitialiseUserImport");
        }
        new HandlerCommandSender().send(ctxt, arg, "reinitialiseUserImport");
    }


    public static String exportSqlQueryToStringFormat(MutableGuiContext ctxt,
                                                      String sql, String separator,
                                                      String quote, boolean column)
          throws RequestException {
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("sql", sql);
        arg.put("separator", separator);
        arg.put("quote", quote);
        arg.put("column", Boolean.toString(column));
        Row row = new HandlerCommandSender().send(ctxt, arg, "exportSqlQueryToStringFormat");
        return row.getFieldValue("result");
    }


    public static ResultManager getResultSqlQuery(MutableGuiContext ctxt,
                                                  String requestID, String sql,
                                                  List<String> primaryKeys, List<String> fieldnames)
          throws RequestException, BuildException {
        return getResultSqlQuery(ctxt, requestID, sql,
                                 new ListCodec().encode(primaryKeys, "", ","),
                                 new ListCodec().encode(fieldnames, "", ","));
    }


    private static ResultManager getResultSqlQuery(MutableGuiContext ctxt,
                                                   String requestID, String sql,
                                                   String primaryKeys, String fieldnames)
          throws RequestException, BuildException {
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("requestId", requestID);
        arg.put("sql", sql);
        arg.put("primaryKeys", primaryKeys);
        arg.put("fieldnames", fieldnames);
        Row row = new HandlerCommandSender().send(ctxt, arg, "getResultSqlQuery");
        return ResultFactory.buildResultManager(row.getFieldValue("result"));
    }


    public static String executeSql(MutableGuiContext ctxt, String query) throws RequestException {
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("query", query);
        Row row = new HandlerCommandSender().send(ctxt, arg, "executeSql");
        return row.getFieldValue("result");
    }


    public static String createExportConfigFromTemplate(MutableGuiContext ctxt, String periode)
          throws RequestException {
        String templateDelete = "%_[09]%.%";
        String templateSelect = "%_$periode$.%";
        return createExportConfigFromTemplate(ctxt,
                                              periode,
                                              Arrays.asList(templateDelete),
                                              Arrays.asList(templateSelect));
    }


    public static String createExportConfigFromTemplate(MutableGuiContext ctxt,
                                                        String periode,
                                                        List<String> templateDelete,
                                                        List<String> templateSelect) throws RequestException {
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("periode", periode);
        arg.put("templateDelete", ListXmlCodec.encode(templateDelete));
        arg.put("templateSelect", ListXmlCodec.encode(templateSelect));
        Row row = new HandlerCommandSender().send(ctxt, arg, "createExportConfigFromTemplate");
        return row.getFieldValue("result");
    }
}
