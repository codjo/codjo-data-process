/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.client;
import net.codjo.mad.client.request.CommandRequest;
import net.codjo.mad.client.request.DeleteRequest;
import net.codjo.mad.client.request.Field;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.InsertRequest;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.client.request.UpdateRequest;
import net.codjo.mad.gui.framework.GuiContext;
import java.awt.Cursor;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JInternalFrame;
/**
 *
 */
public class HandlerCommandSender {
    private static final long NO_TIMEOUT_SPECIFIED = -1;


    public HandlerCommandSender() {
    }


    public Row send(GuiContext guiContext, Map<String, String> argumentsMap, String handlerId)
          throws RequestException {
        CommandRequest req = new CommandRequest(handlerId);
        req.setArgs(new FieldsList(argumentsMap));
        return sendSimpleRequest(guiContext, req, NO_TIMEOUT_SPECIFIED);
    }


    public Row send(GuiContext guiContext, Map<String, String> argumentsMap, String handlerId, long timeout)
          throws RequestException {
        CommandRequest req = new CommandRequest(handlerId);
        req.setArgs(new FieldsList(argumentsMap));
        return sendSimpleRequest(guiContext, req, timeout);
    }


    public void execHandlerWithNoReturn(GuiContext ctxt, String handlerId, String paramName,
                                        String paramValue) throws RequestException {
        Map<String, String> args = new HashMap<String, String>();
        args.put(paramName, paramValue);
        send(ctxt, args, handlerId);
    }


    public Result sendCommand(GuiContext ctxt, String handlerId, Map<String, String> argumentsMap)
          throws RequestException {
        try {
            waitCursor(ctxt);
            FieldsList fl = null;
            if (argumentsMap != null) {
                fl = new FieldsList(argumentsMap);
            }
            CommandRequest request = new CommandRequest(handlerId, fl);
            ResultManager rm = ctxt.getSender().getConnectionOperations()
                  .sendRequests(new Request[]{request});
            if (rm.hasError()) {
                throw new RequestException(rm.getErrorResult());
            }
            return rm.getResult(request.getRequestId());
        }
        finally {
            defaultCursor(ctxt);
        }
    }


    public Result sendSqlCommand(GuiContext guiContext,
                                 String handlerId,
                                 Map<String, String> attributes,
                                 Map<String, String> selectors) throws RequestException {
        return getAllSql(guiContext, handlerId, attributes, selectors, Integer.MAX_VALUE);
    }


    public Result sendInsertSqlCommand(GuiContext ctxt,
                                       String handlerId,
                                       Map<String, String> fieldValues) throws RequestException {
        try {
            waitCursor(ctxt);
            Request request = buildInsertSqlRequest(handlerId, fieldValues);
            ResultManager rm = ctxt.getSender().getConnectionOperations()
                  .sendRequests(new Request[]{request});
            if (rm.hasError()) {
                throw new RequestException(rm.getErrorResult());
            }
            return rm.getResult(request.getRequestId());
        }
        finally {
            defaultCursor(ctxt);
        }
    }


    public Result sendDeleteSqlCommand(GuiContext ctxt,
                                       String handlerId,
                                       Map<String, String> fieldValues) throws RequestException {
        try {
            waitCursor(ctxt);
            Request request = buildDeleteSqlRequest(handlerId, fieldValues);
            ResultManager rm = ctxt.getSender().getConnectionOperations()
                  .sendRequests(new Request[]{request});
            if (rm.hasError()) {
                throw new RequestException(rm.getErrorResult());
            }
            return rm.getResult(request.getRequestId());
        }
        finally {
            defaultCursor(ctxt);
        }
    }


    public Result sendUpdateSqlCommand(GuiContext ctxt,
                                       String handlerId,
                                       Map<String, String> primaryKeys,
                                       Map<String, String> fieldValues) throws RequestException {
        try {
            waitCursor(ctxt);
            Request request = buildUpdateSqlRequest(handlerId, primaryKeys, fieldValues);
            ResultManager rm = ctxt.getSender().getConnectionOperations()
                  .sendRequests(new Request[]{request});
            if (rm.hasError()) {
                throw new RequestException(rm.getErrorResult());
            }
            return rm.getResult(request.getRequestId());
        }
        finally {
            defaultCursor(ctxt);
        }
    }


    private static Result getAllSql(GuiContext ctxt,
                                    String handlerId,
                                    Map<String, String> attributes,
                                    Map<String, String> selectors,
                                    int rowCount) throws RequestException {
        try {
            waitCursor(ctxt);
            SelectRequest request = buildSqlRequest(handlerId, attributes, selectors);
            request.setPage(1, rowCount);
            ResultManager rm = ctxt.getSender().getConnectionOperations()
                  .sendRequests(new Request[]{request});
            if (rm.hasError()) {
                throw new RequestException(rm.getErrorResult());
            }
            return rm.getResult(request.getRequestId());
        }
        finally {
            defaultCursor(ctxt);
        }
    }


    private static SelectRequest buildSqlRequest(String id,
                                                 Map<String, String> attributes,
                                                 Map<String, String> selectors) {
        SelectRequest request = new SelectRequest();
        request.setId(id);
        if (attributes != null && !attributes.isEmpty()) {
            request.setAttributes(attributes.keySet().toArray(new String[attributes.keySet().size()]));
        }
        if (selectors != null) {
            FieldsList selectorsFields = new FieldsList(selectors);
            for (Field field : selectorsFields.getFields()) {
                request.addSelector(field.getName(), field.getValue());
            }
        }
        return request;
    }


    private static Request buildInsertSqlRequest(String id, Map<String, String> fieldValues) {
        InsertRequest request = new InsertRequest();
        request.setId(id);
        if (fieldValues != null) {
            request.setRow(new FieldsList(fieldValues));
        }
        return request;
    }


    private static Request buildDeleteSqlRequest(String id, Map<String, String> fieldValues) {
        DeleteRequest request = new DeleteRequest();
        request.setId(id);
        if (fieldValues != null) {
            request.setPrimaryKey(new FieldsList(fieldValues));
        }
        return request;
    }


    private static Request buildUpdateSqlRequest(String id,
                                                 Map<String, String> primaryKeys,
                                                 Map<String, String> fieldValues) {
        UpdateRequest request = new UpdateRequest();
        request.setId(id);
        if (fieldValues != null) {
            request.setPrimaryKey(new FieldsList(primaryKeys));
            request.setRow(new FieldsList(fieldValues));
        }
        return request;
    }


    public static Row sendSimpleRequest(GuiContext ctxt, Request request, long timeout)
          throws RequestException {
        return getFirstRow(sendRequest(ctxt, request, timeout));
    }


    private static Result sendRequest(GuiContext ctxt, Request request, long timeout)
          throws RequestException {
        try {
            waitCursor(ctxt);
            ResultManager manager;
            if (timeout == NO_TIMEOUT_SPECIFIED) {
                manager = ctxt.getSender().getConnectionOperations().sendRequests(new Request[]{request});
            }
            else {
                manager = ctxt.getSender().getConnectionOperations().sendRequests(new Request[]{request},
                                                                                  timeout);
            }
            if (manager.hasError()) {
                throw new RequestException(manager.getErrorResult());
            }
            return manager.getResult(request.getRequestId());
        }
        finally {
            defaultCursor(ctxt);
        }
    }


    private static Row getFirstRow(Result rs) throws RequestException {
        if (rs.getRowCount() != 1) {
            throw new RequestException("La requête renvoie " + rs.getRowCount()
                                       + " ligne(s) alors que une seule ligne était attendue");
        }
        return rs.getRow(0);
    }


    private static void defaultCursor(GuiContext guiCtxt) {
        changeCursorOnAllFrames(guiCtxt, Cursor.DEFAULT_CURSOR);
    }


    private static void waitCursor(GuiContext guiCtxt) {
        changeCursorOnAllFrames(guiCtxt, Cursor.WAIT_CURSOR);
    }


    private static void changeCursorOnAllFrames(GuiContext guiCtxt, int type) {
        try {
            JInternalFrame[] jInternalFrames = guiCtxt.getDesktopPane().getAllFrames();
            for (JInternalFrame frame : jInternalFrames) {
                frame.setCursor(Cursor.getPredefinedCursor(type));
            }
            guiCtxt.getMainFrame().setCursor(Cursor.getPredefinedCursor(type));
        }
        catch (Exception ex) {
            ;
        }
    }
}