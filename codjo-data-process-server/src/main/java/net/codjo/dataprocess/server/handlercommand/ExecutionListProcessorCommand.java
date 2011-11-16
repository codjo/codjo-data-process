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
public class ExecutionListProcessorCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getConnection();
        String executionListName = query.getArgumentString("executionListName");
        int repositoryId = query.getArgumentInteger("repositoryId");
        int familyId = query.getArgumentInteger("familyId");
        String context = query.getArgumentString("context");
        boolean loadDefaultContext = query.getArgumentBoolean("loadDefaultContext");
        if ("NULL".equalsIgnoreCase(context)) {
            context = null;
        }
        try {
            String result = TreatmentHandlerHelper.proceedExecutionList(con,
                                                                        repositoryId,
                                                                        familyId,
                                                                        executionListName,
                                                                        context,
                                                                        loadDefaultContext);
            return createResult(result);
        }
        catch (TreatmentException ex) {
            throw new HandlerException(ex.getLocalizedMessage() + " (executionListName = " + executionListName
                                       + ", repositoryId = " + repositoryId + ", familyId = " + familyId
                                       + ") ", ex);
        }
        finally {
            con.close();
        }
    }
}