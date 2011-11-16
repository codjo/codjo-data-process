/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand.context;
import net.codjo.dataprocess.server.handlerhelper.ContextHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class SaveContextCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();

        try {
            String contextName = query.getArgumentString("contextName");
            String context = query.getArgumentString("context");
            String result = ContextHandlerHelper.saveContext(con, contextName, context);
            return createResult(result);
        }
        finally {
            con.close();
        }
    }
}
