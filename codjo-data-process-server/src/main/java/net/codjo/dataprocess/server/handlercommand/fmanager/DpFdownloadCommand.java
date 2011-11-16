package net.codjo.dataprocess.server.handlercommand.fmanager;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import sun.misc.BASE64Encoder;
/**
 *
 */
public class DpFdownloadCommand extends HandlerCommand {

    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        StringBuilder response = new StringBuilder();
        String filePath = query.getArgumentString("filePath");
        int block = query.getArgumentInteger("block");
        int blockSize = query.getArgumentInteger("blockSize");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            long skipped = fis.skip(block * blockSize);
            response.append(skipped).append("\n");
            byte[] toRead = new byte[blockSize];
            int realReader = fis.read(toRead);
            response.append(realReader).append("\n");
            response.append(base64Encoder.encode(toRead));
            fis.close();
        }
        catch (IOException ex) {
            throw new HandlerException(
                  ex.getLocalizedMessage() + " (filePath = " + filePath + ", block = " + block
                  + ", blockSize = " + blockSize + ") ", ex);
        }
        return createResult(response.toString());
    }
}
