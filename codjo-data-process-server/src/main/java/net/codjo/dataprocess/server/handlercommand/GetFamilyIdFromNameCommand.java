/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.server.handlerhelper.FamilyHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class GetFamilyIdFromNameCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();

        int repositoryId = query.getArgumentInteger("repositoryId");
        String familyName = query.getArgumentString("familyName");
        try {
            int familyId = FamilyHandlerHelper.getFamilyIdFromName(con, repositoryId, familyName);
            return createResult(familyId);
        }
        catch (TreatmentException ex) {
            throw new HandlerException(
                  ex.getLocalizedMessage() + " (repositoryId = " + repositoryId + ", familyName = "
                  + familyName + ") ", ex);
        }
        finally {
            con.close();
        }
    }
}