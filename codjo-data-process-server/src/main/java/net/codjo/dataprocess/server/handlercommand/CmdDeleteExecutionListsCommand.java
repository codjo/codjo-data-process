/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.server.handlerhelper.TreatmentHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class CmdDeleteExecutionListsCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();

        try {
            int repositoryId = query.getArgumentInteger("repositoryId");
            int familyId = query.getArgumentInteger("familyId");
            TreatmentHandlerHelper.deleteExecutionLists(con, repositoryId, familyId);
            return createResult(DataProcessConstants.NO_RESULT);
        }
        finally {
            con.close();
        }
    }
}
