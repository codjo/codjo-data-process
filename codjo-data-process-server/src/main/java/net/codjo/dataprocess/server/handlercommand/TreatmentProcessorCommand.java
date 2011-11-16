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
public class TreatmentProcessorCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getConnection();
        int repositoryId = query.getArgumentInteger("repositoryId");
        String treatmentId = query.getArgumentString("treatmentId");
        String context = query.getArgumentString("context");
        try {
            Object result = TreatmentHandlerHelper.proceedTreatment(con, repositoryId, treatmentId, context);
            return createResult(result);
        }
        catch (TreatmentException ex) {
            throw new HandlerException(
                  ex.getLocalizedMessage() + " (repositoryId = " + repositoryId + ", treatmentId = "
                  + treatmentId + ") ", ex);
        }
        finally {
            con.close();
        }
    }
}
