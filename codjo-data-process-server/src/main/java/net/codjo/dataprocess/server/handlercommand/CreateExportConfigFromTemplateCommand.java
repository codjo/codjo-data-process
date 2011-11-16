package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.ListXmlCodec;
import net.codjo.dataprocess.server.handlerhelper.BroadcastHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
/**
 *
 */
public class CreateExportConfigFromTemplateCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();
        try {
            String periode = query.getArgumentString("periode");
            String templateDeleteArg = query.getArgumentString("templateDelete");
            String templateSelectArg = query.getArgumentString("templateSelect");

            List<String> templateDelete = ListXmlCodec.decode(templateDeleteArg);
            List<String> templateSelect = ListXmlCodec.decode(templateSelectArg);
            BroadcastHandlerHelper.createExportConfigFromTemplate(con,
                                                                  periode,
                                                                  templateDelete,
                                                                  templateSelect);
            return createResult(DataProcessConstants.NO_RESULT);
        }
        finally {
            con.close();
        }
    }
}
