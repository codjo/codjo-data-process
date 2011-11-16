/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.server.handlerhelper.UserManagerHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class UserManagerCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();
        String command = query.getArgumentString("command");
        String userName = query.getArgumentString("userName");
        String userParam = query.getArgumentString("userParam");
        try {
            String result = UserManagerHandlerHelper.manageUser(con, command, userName, userParam);
            return createResult(result);
        }
        finally {
            con.close();
        }
    }
}
