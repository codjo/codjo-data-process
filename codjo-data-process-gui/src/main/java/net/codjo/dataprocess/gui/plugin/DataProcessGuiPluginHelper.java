package net.codjo.dataprocess.gui.plugin;
import net.codjo.dataprocess.client.DataProcessContextClientHelper;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.UserClientHelper;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.RepositoryException;
import net.codjo.dataprocess.common.exception.UserManagerException;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.common.userparam.User.Repository;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import com.thoughtworks.xstream.core.BaseException;
import java.util.Map;
/**
 *
 */
public class DataProcessGuiPluginHelper {
    private DataProcessGuiPluginHelper() {
    }


    public static void saveDataProcessContext(String repositoryId,
                                              MutableGuiContext ctxt,
                                              DataProcessGuiConfiguration configuration) {
        if (repositoryId == null) {
            Log.info(DataProcessGuiPluginHelper.class, "Pas de sauvegarde de context (repositoryId = null).");
            return;
        }
        try {
            DataProcessContextClientHelper.saveContext(ctxt, repositoryId,
                                                       configuration.getDataProcessContext());
            if (Log.isInfoEnabled()) {
                Log.info(DataProcessGuiPluginHelper.class,
                         String.format("Sauvegarde du context lié au référentiel '%s'",
                                       RepositoryClientHelper.getRepositoryName(ctxt, repositoryId)));
            }
        }
        catch (Exception ex) {
            String message = "Impossible de sauvegarder le context du référentiel id = " + repositoryId;
            GuiUtils.showErrorDialog(ctxt.getMainFrame(), DataProcessGuiPluginHelper.class, message, ex);
        }
    }


    public static void loadDataProcessContext(String repositoryId,
                                              MutableGuiContext ctxt,
                                              DataProcessGuiConfiguration configuration) {
        if (repositoryId == null) {
            Log.info(DataProcessGuiPluginHelper.class, "Pas de chargement de context (idRepository = null).");
            return;
        }
        try {
            configuration.getDataProcessContext()
                  .setContext(DataProcessContextClientHelper.getDataProcessContext(ctxt, repositoryId));

            if (configuration.getPackageArgumentModifier() != null) {
                configuration.getDataProcessContext()
                      .setProperty(DataProcessContext.PACKAGE_ARGUMENT_MODIFIER,
                                   configuration.getPackageArgumentModifier());
            }
            if (Log.isInfoEnabled()) {
                Log.info(DataProcessGuiPluginHelper.class,
                         String.format("Restauration du context lié au référentiel '%s'",
                                       RepositoryClientHelper.getRepositoryName(ctxt, repositoryId)));
            }
        }
        catch (Exception ex) {
            String message = "La restauration du context lié au référentiel id = " + repositoryId
                             + " est impossible";
            GuiUtils.showErrorDialog(ctxt.getMainFrame(), DataProcessGuiPluginHelper.class, message, ex);
        }
    }


    public static void addAllRepositoryToUser(User user, MutableGuiContext ctxt) {
        try {
            Map<String, String> map = RepositoryClientHelper.getAllRepositoryNames(ctxt);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                user.addRepository(new Repository(entry.getValue()));
            }
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(ctxt.getMainFrame(), DataProcessGuiPluginHelper.class, "Erreur interne",
                                     ex);
        }
    }


    public static String getCurrentRepositoryId(User user, MutableGuiContext ctxt) {
        String currentRepositoryName = user.getCurrentRepository();
        if (currentRepositoryName == null) {
            return null;
        }
        try {
            String repositoryId =
                  Integer.toString(
                        RepositoryClientHelper.getRepositoryIdFromName(ctxt, currentRepositoryName));
            GuiContextUtils.setCurrentRepository(ctxt, repositoryId);
            return repositoryId;
        }
        catch (RepositoryException ex) {
            GuiUtils.showErrorDialog(ctxt.getMainFrame(), DataProcessGuiPluginHelper.class, "Erreur", ex);
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(ctxt.getMainFrame(), DataProcessGuiPluginHelper.class, "Erreur interne",
                                     ex);
        }
        return null;
    }


    public static User userFactory(String userName, UserClientHelper userClientHelper, MutableGuiContext ctxt)
          throws UserManagerException, RequestException {
        User user;
        try {
            if (userClientHelper.isExist(ctxt, userName)) {
                user = userClientHelper.load(ctxt, userName);
                if (user.setDefaultRepository()) {
                    userClientHelper.save(ctxt, user);
                }
            }
            else {
                user = new User(userName);
                userClientHelper.create(ctxt, user);
            }
            if (user.getCurrentRepository() != null) {
                if (Log.isInfoEnabled()) {
                    Log.info(DataProcessGuiPluginHelper.class,
                             "Le référentiel de traitement courant de l'utilisateur est " + user
                                   .getCurrentRepository());
                }
            }
            else {
                Log.warn(DataProcessGuiPluginHelper.class, "Pas de référentiel de traitement courant.");
            }
        }
        catch (BaseException ex) {
            user = new User(userName);
            userClientHelper.save(ctxt, user);
            Log.error(DataProcessGuiPluginHelper.class,
                      String.format(
                            "Erreur lors du traitement du paramétrage (PM_DP_USER) des droits de %s "
                            + "sur les référentiels de traitement."
                            + "\nUn paramétrage par défaut a donc été créé.", userName), ex);
        }
        return user;
    }
}
