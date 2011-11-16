package net.codjo.dataprocess.server.control;
import net.codjo.control.common.ControlContext;
import net.codjo.control.common.ControlException;
import net.codjo.control.common.Dictionary;
import net.codjo.control.common.MassControl;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.userparam.DefaultUserCodec;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.server.dao.UserManagerDao;
import net.codjo.dataprocess.server.plugin.DataProcessServerPlugin;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class CheckConcurrentImportControlEnd implements MassControl {
    private ControlContext context;


    public void setContext(ControlContext context) {
        this.context = context;
    }


    public void setControlTable(String tabName) {
    }


    public void control(Connection con, Dictionary dico) throws ControlException {
        String result;
        try {
            result = new UserManagerDao(con).manageUser(DataProcessConstants.USER_COMMAND_LOAD,
                                                        context.getUser(), "");
            if (result.startsWith("ERROR")) {
                throw new RuntimeException(result);
            }
            User user = new DefaultUserCodec().fromXml(result);
            String repository = user.getCurrentRepository();

            MutablePicoContainer picoContainer = DataProcessServerPlugin.getPicoContainer();
            DataProcessServerPlugin dataProcessServerPlugin = (DataProcessServerPlugin)picoContainer.
                  getComponentInstance(DataProcessServerPlugin.class);
            Map<String, String> userImportMap = dataProcessServerPlugin.getConfiguration().getUserImportMap();
            userImportMap.remove(repository);
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}