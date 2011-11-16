package net.codjo.dataprocess.client;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class FamilyClientHelper {
    public static final String FAMILY_ID = "familyId";
    public static final String FAMILY_NAME = "familyName";
    public static final String REPOSITORY_ID = "repositoryId";
    public static final String REPOSITORY_NAME = "repositoryName";
    public static final String EXECUTION_LIST_ID = "executionListId";
    public static final String EXECUTION_LIST_NAME = "executionListName";
    public static final String PRIORITY = "priority";


    private FamilyClientHelper() {
    }


    public static Map<String, String> getFamilyByRepositoryId(MutableGuiContext ctxt, int repositoryId)
          throws RequestException {
        return getFamilyByRepositoryId(ctxt, repositoryId, false);
    }


    public static Map<String, String> getFamilyByRepositoryId(MutableGuiContext ctxt,
                                                              int repositoryId,
                                                              boolean showVisibleFamilyOnly)
          throws RequestException {
        Map<String, String> familyMap = new HashMap<String, String>();
        Map<String, String> selectors = new HashMap<String, String>();
        HandlerCommandSender sender = new HandlerCommandSender();
        selectors.put("repositoryId", Integer.toString(repositoryId));

        Result rs;
        if (showVisibleFamilyOnly) {
            rs = sender.sendSqlCommand(ctxt, "selectVisibleFamilyByRepositoryId", null, selectors);
        }
        else {
            rs = sender.sendSqlCommand(ctxt, "selectFamilyByRepositoryId", null, selectors);
        }
        for (int i = 0; i < rs.getRowCount(); i++) {
            familyMap.put(rs.getValue(i, FAMILY_ID), rs.getValue(i, FAMILY_NAME));
        }
        return familyMap;
    }


    public static String createFamily(MutableGuiContext ctxt, int repositoryId, String familyName)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("familyName", familyName);
        Row row = sender.send(ctxt, arg, "cmdNewFamily");
        return row.getFieldValue("result");
    }


    public static void deleteFamily(MutableGuiContext ctxt, int familyId) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("familyId", Integer.toString(familyId));
        sender.sendDeleteSqlCommand(ctxt, "deleteFamily", fieldValues);
    }


    public static int getFamilyIdFromName(MutableGuiContext ctxt, int repositoryId, int familyId)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> args = new HashMap<String, String>();
        args.put("repositoryId", Integer.toString(repositoryId));
        args.put("familyId", Integer.toString(familyId));
        Row row = sender.send(ctxt, args, "getFamilyIdFromName");
        String result = row.getFieldValue("result");
        return Integer.parseInt(result);
    }


    public static List<ExecutionListDB> getExecutionListsUsingFamily(MutableGuiContext ctxt, int familyId)
          throws RequestException {
        List<ExecutionListDB> executionList = new ArrayList<ExecutionListDB>();
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> selectors = new HashMap<String, String>();
        selectors.put("familyId", Integer.toString(familyId));
        Result rs = sender.sendSqlCommand(ctxt, "selectExecutionListsUsingFamily", null, selectors);

        for (int i = 0; i < rs.getRowCount(); i++) {
            executionList.add(new ExecutionListDB(rs.getValue(i, EXECUTION_LIST_ID),
                                                  rs.getValue(i, REPOSITORY_ID),
                                                  rs.getValue(i, EXECUTION_LIST_NAME),
                                                  rs.getValue(i, REPOSITORY_NAME),
                                                  rs.getValue(i, FAMILY_ID),
                                                  rs.getValue(i, PRIORITY)));
        }

        return executionList;
    }


    public static Map<String, String> getAllFamilies(MutableGuiContext ctxt) throws RequestException {
        Map<String, String> families = new HashMap<String, String>();
        HandlerCommandSender sender = new HandlerCommandSender();
        Result rs = sender.sendSqlCommand(ctxt, "selectAllFamily", null, null);
        for (int i = 0; i < rs.getRowCount(); i++) {
            families.put(rs.getValue(i, FAMILY_ID), rs.getValue(i, FAMILY_NAME));
        }
        return families;
    }
}
