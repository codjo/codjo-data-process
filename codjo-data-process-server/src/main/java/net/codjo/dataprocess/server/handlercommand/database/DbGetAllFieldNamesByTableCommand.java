/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand.database;
import net.codjo.dataprocess.server.util.DatabaseTools;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.SQLException;
/**
 *
 */
public class DbGetAllFieldNamesByTableCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        return createResult(DatabaseTools.getAllFieldNamesByTable(getContext().getConnection()));
    }
}
