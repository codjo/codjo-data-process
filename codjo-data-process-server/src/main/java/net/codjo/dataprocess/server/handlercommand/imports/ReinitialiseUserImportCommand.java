/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand.imports;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.server.plugin.DataProcessServerPlugin;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.SQLException;
import java.util.Map;
/**
 *
 */
public class ReinitialiseUserImportCommand extends HandlerCommand {
    private final Map<String, String> userImportMap;


    public ReinitialiseUserImportCommand(DataProcessServerPlugin dataProcessServerPlugin) {
        userImportMap = dataProcessServerPlugin.getConfiguration().getUserImportMap();
    }


    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        userImportMap.clear();
        return createResult(DataProcessConstants.NO_RESULT);
    }
}