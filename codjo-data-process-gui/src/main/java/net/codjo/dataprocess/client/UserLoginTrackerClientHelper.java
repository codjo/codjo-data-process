package net.codjo.dataprocess.client;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.DataProcessConstants.ListCommand;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.util.UserLoginTracker;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class UserLoginTrackerClientHelper {

    private UserLoginTrackerClientHelper() {
    }


    public static void addUser(MutableGuiContext ctxt, UserLoginTracker userLoginTracker)
          throws RequestException {
        userLoginTracker(ctxt, ListCommand.ADD, UserLoginTracker.encode(userLoginTracker));
    }


    public static UserLoginTracker getUser(MutableGuiContext ctxt, String userName) throws RequestException {
        String result = userLoginTracker(ctxt, ListCommand.GET, userName);
        if (DataProcessConstants.NO_RESULT.equals(result)) {
            return null;
        }
        else {
            return UserLoginTracker.decode(result);
        }
    }


    public static void removeUser(MutableGuiContext ctxt, String userName) throws RequestException {
        userLoginTracker(ctxt, ListCommand.REMOVE, userName);
    }


    public static List<UserLoginTracker> getUserList(MutableGuiContext ctxt) throws RequestException {
        String xml = userLoginTracker(ctxt, ListCommand.GET_LIST, "");
        return UserLoginTracker.decodeList(xml);
    }


    public static int getSize(MutableGuiContext ctxt) throws RequestException {
        String result = userLoginTracker(ctxt, ListCommand.GET_SIZE, "");
        return Integer.parseInt(result);
    }


    public static void clearUsers(MutableGuiContext ctxt) throws RequestException {
        userLoginTracker(ctxt, ListCommand.CLEAR, "");
    }


    public static String userLoginTracker(MutableGuiContext ctxt,
                                          DataProcessConstants.ListCommand command,
                                          String value)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("command", command.toString());
        arg.put("value", value);

        if (Log.isDebugEnabled()) {
            Log.debug(UserLoginTrackerClientHelper.class,
                      "Appel de userLoginTracker(arg=" + arg.toString() + ")");
        }
        Row row = sender.send(ctxt, arg, "userLoginTracker");
        return row.getFieldValue("result");
    }
}