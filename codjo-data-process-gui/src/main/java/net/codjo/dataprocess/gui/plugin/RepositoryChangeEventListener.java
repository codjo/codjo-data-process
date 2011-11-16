package net.codjo.dataprocess.gui.plugin;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.UserClientHelper;
import net.codjo.dataprocess.client.UtilsClientHelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.exception.DataProcessException;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.common.userparam.User.Repository;
import net.codjo.dataprocess.gui.util.DataProcessGuiEvent;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.Observable;
import java.util.Observer;
/**
 *
 */
public class RepositoryChangeEventListener implements Observer {
    private User user;
    private MutableGuiContext ctxt;
    private DataProcessGuiPlugin dataProcessGuiPlugin;


    public RepositoryChangeEventListener(DataProcessGuiPlugin dataProcessGuiPlugin,
                                         User user,
                                         MutableGuiContext ctxt) {
        this.dataProcessGuiPlugin = dataProcessGuiPlugin;
        this.user = user;
        this.ctxt = ctxt;
    }


    public void update(Observable ob, Object arg) {
        if (arg instanceof DataProcessGuiEvent) {
            DataProcessGuiEvent event = (DataProcessGuiEvent)arg;
            String eventName = event.getName();
            DataProcessGuiConfiguration configuration = dataProcessGuiPlugin.getConfiguration();

            try {
                if (DataProcessGuiEvent.PRE_CHANGE_REPOSITORY_EVENT.equals(eventName)) {
                    String oldRepositoryId = event.getValue().toString();
                    DataProcessGuiPluginHelper.saveDataProcessContext(oldRepositoryId, ctxt, configuration);
                    if (user.getUserName().equals(UtilsClientHelper.cmdMapServer(ctxt,
                                                                                 DataProcessConstants.MapCommand.GET,
                                                                                 oldRepositoryId, ""))) {
                        UtilsClientHelper.cmdMapServer(ctxt, DataProcessConstants.MapCommand.REMOVE,
                                                       oldRepositoryId, "");
                    }
                }
                else if (DataProcessGuiEvent.POST_CHANGE_REPOSITORY_EVENT.equals(eventName)) {
                    String selectedRepositoryId = event.getValue().toString();
                    DataProcessGuiPluginHelper.loadDataProcessContext(selectedRepositoryId, ctxt,
                                                                      configuration);
                    String repositoryName = RepositoryClientHelper.getAllRepositoryNames(ctxt)
                          .get(selectedRepositoryId);
                    user.setCurrentRepository(repositoryName);
                    GuiContextUtils.setCurrentRepository(ctxt, selectedRepositoryId);
                    Repository repository = user.getRepository(repositoryName);
                    if (repository != null) {
                        repository.updateExpirydate();
                    }
                    new UserClientHelper().save(ctxt, configuration.getUser());
                    dataProcessGuiPlugin.updateToolBar();
                }
                else if (DataProcessGuiEvent.UPDATE_USER_EVENT.equals(eventName)) {
                    if (user.getCurrentRepository() != null) {
                        DataProcessGuiPluginHelper.saveDataProcessContext(
                              Integer.toString(RepositoryClientHelper.getRepositoryIdFromName(ctxt,
                                                                                              user.getCurrentRepository())),
                              ctxt, configuration);
                    }
                    copyUser((User)event.getValue(), user);
                    String repositoryId = DataProcessGuiPluginHelper.getCurrentRepositoryId(user, ctxt);
                    DataProcessGuiPluginHelper.loadDataProcessContext(repositoryId, ctxt, configuration);
                    new UserClientHelper().save(ctxt, configuration.getUser());
                    dataProcessGuiPlugin.updateToolBar();
                }
            }
            catch (DataProcessException ex) {
                GuiUtils.showErrorDialog(ctxt.getMainFrame(), getClass(), "Erreur", ex);
            }
            catch (RequestException ex) {
                GuiUtils.showErrorDialog(ctxt.getMainFrame(), getClass(), "Erreur interne", ex);
            }
        }
    }


    private static void copyUser(User userFrom, User userTo) {
        userTo.setCurrentRepository(userFrom.getCurrentRepository());
        userTo.removeAllRepository();

        for (Repository repository : userFrom.getRepositoryList()) {
            userTo.addRepository(repository);
        }
    }
}
