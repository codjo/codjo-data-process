package net.codjo.dataprocess.client;
import net.codjo.dataprocess.common.codec.ListCodec;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class DependencyClientHelper {
    private DependencyClientHelper() {

    }


    public static void deleteDependency(MutableGuiContext ctxt,
                                        int repositoryId, String executionListIdPrinc,
                                        String executionListIdDep) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("executionListIdPrinc", executionListIdPrinc);
        arg.put("executionListIdDep", executionListIdDep);

        sender.send(ctxt, arg, "deleteDependency");
        //String result = row.getFieldValue("result");
    }


    public static ExecutionListDependency findDependency(MutableGuiContext ctxt,
                                                         int repositoryId,
                                                         String executionListDep)
          throws RequestException {
        boolean isCycle = false;
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("executionListDep", executionListDep);

        Row row = sender.send(ctxt, arg, "findDependency");
        String result = row.getFieldValue("result");
        if (result == null) {
            return null;
        }
        if (result.endsWith("true")) {
            isCycle = true;
            result = result.substring(0, result.length() - ":true".length());
        }
        else if (result.endsWith("false")) {
            isCycle = false;
            result = result.substring(0, result.length() - ":false".length());
        }
        List<String> executionListPrinc = new ListCodec().decode(result, "", ",");
        return new ExecutionListDependency(isCycle, executionListPrinc);
    }


    public static void deleteDependencyPrincOrDep(MutableGuiContext ctxt,
                                                  int repositoryId,
                                                  String executionListName) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("executionListName", executionListName);

        sender.send(ctxt, arg, "deleteDependencyPrincOrDep");
        //String result = row.getFieldValue("result");
    }


    public static void insertDependency(MutableGuiContext ctxt,
                                        int repositoryId, String executionListIdPrinc,
                                        String executionListIdDep) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("executionListIdPrinc", executionListIdPrinc);
        arg.put("executionListIdDep", executionListIdDep);

        sender.send(ctxt, arg, "insertDependency");
        //String result = row.getFieldValue("result");
    }


    public static ExecutionListDependency findImplication(MutableGuiContext ctxt,
                                                          int repositoryId,
                                                          String executionListPrinc)
          throws RequestException {
        boolean isCycle = false;
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("executionListPrinc", executionListPrinc);

        Row row = sender.send(ctxt, arg, "findImplication");
        String result = row.getFieldValue("result");
        if (result == null) {
            return null;
        }
        if (result.endsWith("true")) {
            isCycle = true;
            result = result.substring(0, result.length() - ":true".length());
        }
        else if (result.endsWith("false")) {
            isCycle = false;
            result = result.substring(0, result.length() - ":false".length());
        }
        List<String> executionListDep = new ListCodec().decode(result, "", ",");
        return new ExecutionListDependency(isCycle, executionListDep);
    }


    public static void updateImplication(MutableGuiContext ctxt,
                                         int repositoryId, String executionListIdPrinc,
                                         int status) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("executionListIdPrinc", executionListIdPrinc);
        arg.put("status", Integer.toString(status));

        sender.send(ctxt, arg, "updateImplication");
        //String result = row.getFieldValue("result");
    }


    public static boolean isExecutable(MutableGuiContext ctxt, int repositoryId, String executionListDep)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("executionListDep", executionListDep);

        Row row = sender.send(ctxt, arg, "isExecutable");
        String result = row.getFieldValue("result");
        return "TRUE".equals(result.trim());
    }
}
