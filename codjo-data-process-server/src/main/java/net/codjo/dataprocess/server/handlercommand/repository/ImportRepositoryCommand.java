package net.codjo.dataprocess.server.handlercommand.repository;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.server.treatmenthelper.TreatmentHelper;
import net.codjo.dataprocess.server.util.SQLUtil;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;

import static net.codjo.dataprocess.common.DataProcessConstants.ImportRepoCommand;
/**
 *
 */
public class ImportRepositoryCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query)
          throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();

        int repositoryId = query.getArgumentInteger("repositoryId");
        String content = query.getArgumentString("content");
        String commandStr = query.getArgumentString("command");
        String repositoryName = query.getArgumentString("repositoryName");
        try {
            ImportRepoCommand command = ImportRepoCommand.valueOf(commandStr);

            if (repositoryId < 1) {
                repositoryId = SQLUtil.getNextId(con, "PM_REPOSITORY", "REPOSITORY_ID");
            }

            if (command.equals(ImportRepoCommand.BEGIN_INSERT)) {
                TreatmentHelper.deleteRepository(con, repositoryId);
            }
            else if (command.equals(ImportRepoCommand.INSERT_PART)) {
                TreatmentHelper.insertRepositoryContent(con, repositoryId, content);
            }
            else if (command.equals(ImportRepoCommand.END_INSERT)) {
                TreatmentHelper.insertRepository(con, repositoryId, repositoryName);
            }
            else if (command.equals(ImportRepoCommand.UPDATE_IMPORT_DATE)) {
                TreatmentHelper.updateDateRepositoryImport(con, content);
            }
            return createResult(DataProcessConstants.NO_RESULT);
        }
        catch (Exception ex) {
            throw new HandlerException(
                  ex.getLocalizedMessage() + " (repositoryId = " + repositoryId + ", commandStr = "
                  + commandStr + ", repositoryName = " + repositoryName + ") ", ex);
        }
        finally {
            con.close();
        }
    }
}
