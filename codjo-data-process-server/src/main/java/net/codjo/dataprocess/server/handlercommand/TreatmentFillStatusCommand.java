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
public class TreatmentFillStatusCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();
        String executionListModel = query.getArgumentString("executionListModel");
        int status = query.getArgumentInteger("status");
        String context = query.getArgumentString("context");

        try {
            TreatmentHandlerHelper.updateExecutionListStatus(con, executionListModel, context, status);
            return createResult(DataProcessConstants.NO_RESULT);
        }
        catch (Exception ex) {
            throw new HandlerException(ex.getLocalizedMessage() + " (status = " + status + ") ", ex);
        }
        finally {
            con.close();
        }
    }
}
