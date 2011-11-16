/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand.repository;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.server.handlerhelper.RepositoryHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class CmdUpdateRepositoryCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();

        int repositoryId = query.getArgumentInteger("repositoryId");
        String content = query.getArgumentString("content");
        try {
            String result = RepositoryHandlerHelper.updateRepository(con, repositoryId, content);
            return createResult(result);
        }
        catch (TreatmentException ex) {
            throw new HandlerException(ex.getLocalizedMessage() + " (repositoryId = " + repositoryId + ") ",
                                       ex);
        }
        finally {
            con.close();
        }
    }
}
