/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.server.plugin.DataProcessServerPlugin;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.SQLException;
import java.util.Map;

import static net.codjo.dataprocess.common.DataProcessConstants.MapCommand;
/**
 *
 */
public class CmdMapServerCommand extends HandlerCommand {
    private Map<String, String> map;


    public CmdMapServerCommand(DataProcessServerPlugin dataProcessServerPlugin) {
        map = dataProcessServerPlugin.getConfiguration().getServerMapUtil();
    }


    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        String command = query.getArgumentString("command");
        String key = query.getArgumentString("key");
        String value = query.getArgumentString("value");

        String result = cmdMapServer(command, key, value);
        return createResult(result);
    }


    private String cmdMapServer(String commandStr, String key, String value) throws HandlerException {
        try {
            MapCommand command = MapCommand.valueOf(commandStr);

            if (command.equals(MapCommand.PUT)) {
                map.put(key, value);
            }
            else if (command.equals(MapCommand.GET)) {
                return map.get(key);
            }
            else if (command.equals(MapCommand.REMOVE)) {
                map.remove(key);
            }
            else if (command.equals(MapCommand.CONTAINS_KEY)) {
                return (map.containsKey(key)) ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            }
            else if (command.equals(MapCommand.CONTAINS_VALUE)) {
                return (map.containsValue(value)) ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            }
            else if (command.equals(MapCommand.GET_SIZE)) {
                return Integer.toString(map.size());
            }
            else if (command.equals(MapCommand.CLEAR)) {
                map.clear();
            }
            return DataProcessConstants.NO_RESULT;
        }
        catch (IllegalArgumentException ex) {
            throw new HandlerException("Commande '" + commandStr + "' inexistante.");
        }
    }
}
