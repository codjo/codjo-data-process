/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.server.handlerhelper.RepositoryHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class GetRepositoryIdFromNameCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();

        String repositoryName = query.getArgumentString("repositoryName");
        try {
            int repositoryId = RepositoryHandlerHelper.getRepositoryIdFromName(con, repositoryName);
            return createResult(repositoryId);
        }
        catch (TreatmentException ex) {
            throw new HandlerException(
                  ex.getLocalizedMessage() + " (repositoryName = " + repositoryName + ") ", ex);
        }
        finally {
            con.close();
        }
    }
}