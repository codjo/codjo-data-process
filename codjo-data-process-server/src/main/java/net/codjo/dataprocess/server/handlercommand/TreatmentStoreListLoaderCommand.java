/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.server.handlerhelper.TreatmentHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class TreatmentStoreListLoaderCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();
        int repositoryId = query.getArgumentInteger("repositoryId");
        int familyId = query.getArgumentInteger("familyId");
        String executionListName = query.getArgumentString("executionListName");

        try {
            String result = TreatmentHandlerHelper.getExecutionListModel(con,
                                                                         executionListName,
                                                                         repositoryId,
                                                                         familyId);
            return createResult(result);
        }
        catch (TreatmentException ex) {
            throw new HandlerException(
                  ex.getLocalizedMessage() + " (repositoryId = " + repositoryId + ", familyId = " + familyId
                  + ", executionListName = " + executionListName + ") ", ex);
        }
        finally {
            con.close();
        }
    }
}
