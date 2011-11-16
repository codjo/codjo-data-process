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

import static net.codjo.dataprocess.common.DataProcessConstants.Command;
/**
 *
 */
public class ManageTreatmentModelCommand extends HandlerCommand {

    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();
        String commandStr = query.getArgumentString("command");
        int repositoryId = query.getArgumentInteger("repositoryId");
        String treatmentContentXml = query.getArgumentString("treatmentContentXml");
        try {
            Command command = Command.valueOf(commandStr);
            String result = TreatmentHandlerHelper.manageTreatmentModel(con, command, repositoryId,
                                                                        treatmentContentXml);
            return createResult(result);
        }
        catch (TreatmentException ex) {
            throw new HandlerException(
                  ex.getLocalizedMessage() + " (command = " + commandStr + ", repositoryId = " + repositoryId
                  + ") ", ex);
        }
        finally {
            con.close();
        }
    }
}