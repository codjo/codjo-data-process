/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.server.handlerhelper.TreatmentHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class GetNotResolvableArgumentsCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();
        int repositoryId = query.getArgumentInteger("repositoryId");
        String executionListModel = query.getArgumentString("executionListModel");
        String context = query.getArgumentString("context");

        try {
            String result = TreatmentHandlerHelper.getNotResolvableArguments(con, repositoryId,
                                                                             executionListModel, context);
            return createResult(result);
        }
        catch (Exception ex) {
            throw new HandlerException(ex.getLocalizedMessage() + " (repositoryId = " + repositoryId + ") ",
                                       ex);
        }
        finally {
            con.close();
        }
    }
}
