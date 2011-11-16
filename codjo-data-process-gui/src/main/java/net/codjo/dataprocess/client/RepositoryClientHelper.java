package net.codjo.dataprocess.client;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.exception.RepositoryException;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class RepositoryClientHelper {
    private static final String REPOSITORY_NAME_CONTEXT_PROP = "REPOSITORY_NAME_CONTEXT_PROP";


    private RepositoryClientHelper() {
    }


    @SuppressWarnings({"unchecked"})
    public static Map<String, String> getRepositoryNameMap(MutableGuiContext context) {
        return (Map<String, String>)getProperty(context, REPOSITORY_NAME_CONTEXT_PROP);
    }


    public static void putRepositoryNameMap(MutableGuiContext context,
                                            Map<String, String> repositoryNameMap) {
        context.putProperty(REPOSITORY_NAME_CONTEXT_PROP, repositoryNameMap);
    }


    public static void renameRepository(MutableGuiContext ctxt, int repositoryId, String repositoryName)
          throws RequestException {
        clearRepositoryNameMap(ctxt);
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("repositoryName", repositoryName);
        sender.send(ctxt, arg, "renameRepository");
    }


    public static void deleteRepository(MutableGuiContext ctxt, int repositoryId) throws RequestException {
        clearRepositoryNameMap(ctxt);
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        sender.send(ctxt, arg, "cmdDeleteRepository");
    }


    public static String createRepository(MutableGuiContext ctxt, String name) throws RequestException {
        clearRepositoryNameMap(ctxt);
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("name", name);
        Row row = sender.send(ctxt, arg, "cmdNewRepository");
        return row.getFieldValue("result");
    }


    public static void importRepository(MutableGuiContext ctxt,
                                        DataProcessConstants.ImportRepoCommand command,
                                        int repositoryId,
                                        String repositoryName,
                                        String content,
                                        long timeout) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("command", command.toString());
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("repositoryName", repositoryName);
        arg.put("content", content);
        sender.send(ctxt, arg, "importRepository", timeout);
    }


    public static String updateRepository(MutableGuiContext ctxt, int repositoryId, String content)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("content", content);
        Row row = sender.send(ctxt, arg, "cmdUpdateRepository");
        return row.getFieldValue("result");
    }


    public static void reinitializeRepositoryCache(MutableGuiContext ctxt) throws RequestException {
        clearRepositoryNameMap(ctxt);
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        sender.send(ctxt, arg, "reinitializeRepositoryCache");
    }


    public static String getRepositoryContent(MutableGuiContext ctxt, int repositoryId)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        Row row = sender.send(ctxt, arg, "getRepositoryContent");
        return row.getFieldValue("result");
    }


    public static Map<String, String> getAllRepositoryNames(MutableGuiContext ctxt) throws RequestException {
        return getAllRepositoryNames(ctxt, false);
    }


    public static String getRepositoryName(MutableGuiContext ctxt, String repositoryId)
          throws RequestException {
        return RepositoryClientHelper.getAllRepositoryNames(ctxt).get(repositoryId);
    }


    public static boolean isThereAnyRepository(MutableGuiContext ctxt) throws RequestException {
        return !RepositoryClientHelper.getAllRepositoryNames(ctxt).isEmpty();
    }


    public static void clearRepositoryNameMap(MutableGuiContext ctxt) {
        getRepositoryNameMap(ctxt).clear();
    }


    public static Map<String, String> getAllRepositoryNames(MutableGuiContext ctxt, boolean force)
          throws RequestException {
        Map<String, String> repositoryNameMap = getRepositoryNameMap(ctxt);
        if (repositoryNameMap.isEmpty() || force) {
            repositoryNameMap.clear();

            HandlerCommandSender sender = new HandlerCommandSender();
            Result rs = sender.sendSqlCommand(ctxt, "selectAllRepositoryNames", null, null);
            for (int i = 0; i < rs.getRowCount(); i++) {
                repositoryNameMap.put(rs.getValue(i, "repositoryId"), rs.getValue(i, "repositoryName"));
            }
            return repositoryNameMap;
        }
        else {
            Log.debug(RepositoryClientHelper.class,
                      "Récupération du nom des référentiels de traitements à partir du cache.");
            return repositoryNameMap;
        }
    }


    public static int getRepositoryIdFromName(MutableGuiContext ctxt, String repositoryName)
          throws RepositoryException, RequestException {
        Map<String, String> repositoryMap = getAllRepositoryNames(ctxt);
        for (Map.Entry<String, String> entry : repositoryMap.entrySet()) {
            if (entry.getValue().equals(repositoryName)) {
                return Integer.parseInt(entry.getKey());
            }
        }
        throw new RepositoryException("Le repository '" + repositoryName +
                                      "' n'a pas été trouvé dans PM_REPOSITORY.");
    }


    private static Object getProperty(MutableGuiContext context, String key) {
        Object value = context.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException(key + " n'est pas dans le GuiContext");
        }
        return value;
    }
}
