/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.DataProcessConstants.ListCommand;
import net.codjo.dataprocess.common.util.UserLoginTracker;
import net.codjo.dataprocess.server.plugin.DataProcessServerPlugin;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/**
 *
 */
public class UserLoginTrackerCommand extends HandlerCommand {
    private final List<UserLoginTracker> userLoginTrackerList;


    public UserLoginTrackerCommand(DataProcessServerPlugin dataProcessServerPlugin) {
        userLoginTrackerList = dataProcessServerPlugin.getConfiguration().getUserLoginTrackerList();
    }


    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        String command = query.getArgumentString("command");
        String value = query.getArgumentString("value");

        String result = executeCommand(command, value);
        return createResult(result);
    }


    synchronized private String executeCommand(String commandStr, String value) {
        ListCommand command = ListCommand.valueOf(commandStr);
        switch (command) {
            case ADD:
                UserLoginTracker userLoginTracker = UserLoginTracker.decode(value);
                userLoginTracker.setDate(getCurrentDatetime());
                remove(userLoginTracker.getUserName());
                userLoginTrackerList.add(userLoginTracker);
                break;
            case GET:
                UserLoginTracker loginTracker = getUserLoginTracker(value);
                if (loginTracker != null) {
                    return UserLoginTracker.encode(loginTracker);
                }
                else {
                    return DataProcessConstants.NO_RESULT;
                }
            case REMOVE:
                remove(value);
                break;
            case GET_SIZE:
                return Integer.toString(userLoginTrackerList.size());
            case CLEAR:
                userLoginTrackerList.clear();
                break;
            case GET_LIST:
                return UserLoginTracker.encodeList(new ArrayList<UserLoginTracker>(userLoginTrackerList));
        }
        return DataProcessConstants.NO_RESULT;
    }


    private void remove(String userName) {
        UserLoginTracker userLoginTracker = getUserLoginTracker(userName);
        if (userLoginTracker != null) {
            userLoginTrackerList.remove(userLoginTracker);
        }
    }


    private UserLoginTracker getUserLoginTracker(String userName) {
        synchronized (userLoginTrackerList) {
            for (UserLoginTracker userLoginTracker : userLoginTrackerList) {
                if (userName.equals(userLoginTracker.getUserName())) {
                    return userLoginTracker;
                }
            }
        }
        return null;
    }


    private static String getCurrentDatetime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date dateDebut = Calendar.getInstance().getTime();
        return dateFormat.format(dateDebut);
    }
}