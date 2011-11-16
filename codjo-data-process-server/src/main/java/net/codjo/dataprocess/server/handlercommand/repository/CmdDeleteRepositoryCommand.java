/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand.repository;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.server.handlerhelper.RepositoryHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class CmdDeleteRepositoryCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();

        try {
            int repositoryId = query.getArgumentInteger("repositoryId");
            RepositoryHandlerHelper.deleteRepository(con, repositoryId);
            return createResult(DataProcessConstants.NO_RESULT);
        }
        finally {
            con.close();
        }
    }
}
