/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand.repository;
import net.codjo.dataprocess.server.handlerhelper.RepositoryHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class GetRepositoryContentCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();
        int repositoryId = query.getArgumentInteger("repositoryId");

        try {
            String result = RepositoryHandlerHelper.getRepositoryContent(con, repositoryId);
            return createResult(result);
        }
        finally {
            con.close();
        }
    }
}
