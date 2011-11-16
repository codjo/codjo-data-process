package net.codjo.dataprocess.server.handlercommand.fmanager;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.io.File;
import java.io.FileFilter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 */
public class DpFexplorerCommand extends HandlerCommand {

    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        StringBuilder response = new StringBuilder();
        String relativePath = query.getArgumentString("relativePath");
        File currentFile = new File("./" + relativePath);
        String realPath = currentFile.getAbsolutePath();
        response.append(realPath).append("\n").append("--DIRS").append("\n");

        File[] dirs = currentFile.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (dirs != null) {
            for (File dir : dirs) {
                response.append(dir.getName()).append("\n");
            }
        }

        response.append("--FILES").append("\n");
        File[] files = currentFile.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return !pathname.isDirectory();
            }
        });
        if (files != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            for (File file : files) {
                Date date = new Date(file.lastModified());
                response.append(file.getName())
                      .append("|").append(file.length())
                      .append("|").append(simpleDateFormat.format(date))
                      .append("\n");
            }
        }
        response.append("--EOC\n");
        return createResult(response.toString());
    }
}
