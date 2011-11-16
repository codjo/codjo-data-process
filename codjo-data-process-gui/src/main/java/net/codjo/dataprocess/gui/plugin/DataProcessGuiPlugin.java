/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.plugin;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.UserClientHelper;
import net.codjo.dataprocess.client.UserLoginTrackerClientHelper;
import net.codjo.dataprocess.client.UtilsClientHelper;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.exception.RepositoryException;
import net.codjo.dataprocess.common.exception.UserManagerException;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.common.userparam.User.Repository;
import net.codjo.dataprocess.common.util.UserLoginTracker;
import net.codjo.dataprocess.gui.repository.AbstractToolbarRepoConfig;
import net.codjo.dataprocess.gui.repository.ToolbarRepoConfig;
import net.codjo.dataprocess.gui.selector.RepositoryComboBox;
import net.codjo.dataprocess.gui.util.ErrorDialog;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.action.RegisterAction;
import net.codjo.dataprocess.gui.util.action.RegisterActionProvider;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.mad.gui.framework.GuiEvent;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.i18n.AbstractInternationalizableGuiPlugin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import static net.codjo.dataprocess.common.DataProcessConstants.MapCommand.GET;
import static net.codjo.dataprocess.common.DataProcessConstants.MapCommand.REMOVE;
/**
 *
 */
public final class DataProcessGuiPlugin extends AbstractInternationalizableGuiPlugin {
    private DataProcessGuiConfiguration configuration;
    private MutableGuiContext ctxt;
    private JPanel toolBarPanel = new JPanel();
    private RepositoryComboBox repositoryComboBox;
    private JPanel repositoryPanel;


    public DataProcessGuiPlugin() {
        configuration = new DataProcessGuiConfiguration(this);
    }


    @Override
    protected void registerLanguageBundles(TranslationManager translationManager) {
        translationManager.addBundle("net.codjo.dataprocess.gui.i18n", Language.FR);
        translationManager.addBundle("net.codjo.dataprocess.gui.i18n", Language.EN);
    }


    @Override
    public void stop() throws Exception {
        User user = configuration.getUser();
        if (user != null) {
            UserClientHelper userClientHelper = new UserClientHelper();
            String repositoryName = user.getCurrentRepository();
            try {
                if (repositoryName != null) {
                    Repository repository = user.getRepository(repositoryName);
                    if (repository != null) {
                        repository.updateExpirydate();
                    }
                    String repositoryId = Integer.toString(RepositoryClientHelper.getRepositoryIdFromName(
                          ctxt, repositoryName));
                    DataProcessGuiPluginHelper.saveDataProcessContext(repositoryId, ctxt, configuration);
                }
                userClientHelper.save(ctxt, user);
            }
            catch (RepositoryException ex) {
                Log.error(DataProcessGuiPluginHelper.class, ex);
            }
            catch (UserManagerException ex) {
                GuiUtils.showErrorDialog(ctxt.getMainFrame(), DataProcessGuiPluginHelper.class,
                                         "La sauvegarde du paramétrage de l'utilisateur a échoué", ex);
            }
        }

        if (ctxt != null) {
            String currentRepositoryId = GuiContextUtils.getCurrentRepository(ctxt);
            if (System.getProperty("user.name").equals(UtilsClientHelper.cmdMapServer(ctxt, GET,
                                                                                      currentRepositoryId,
                                                                                      ""))) {
                UtilsClientHelper.cmdMapServer(ctxt, REMOVE, currentRepositoryId, "");
            }
            UserLoginTrackerClientHelper.removeUser(ctxt, configuration.getUser().getUserName());
        }
    }


    @Override
    public void initGui(GuiConfiguration guiConfiguration) throws Exception {
        super.initGui(guiConfiguration);
        ctxt = guiConfiguration.getGuiContext();

        RegisterActionProvider register = new RegisterAction(guiConfiguration, this);
        RegisterActionHelper.registerAction(register);

        configuration.setGlobalParameter(Arrays.asList(""), ctxt);

        GuiContextUtils.putDataProcessContext(ctxt, configuration.getDataProcessContext());

        RepositoryClientHelper.putRepositoryNameMap(ctxt, configuration.getRepositoryNameMap());
        RepositoryClientHelper.reinitializeRepositoryCache(ctxt);

        try {
            UserClientHelper userClientHelper = new UserClientHelper();
            configuration.setUser(DataProcessGuiPluginHelper.userFactory(System.getProperty("user.name"),
                                                                         userClientHelper,
                                                                         ctxt));
        }
        catch (UserManagerException ex) {
            String errorMsg = "Impossible de récupérer le paramétrage ou d'en créer un nouveau pour "
                              + System.getProperty("user.name");
            Log.error(getClass(), errorMsg, ex);
            ErrorDialog.show(ctxt.getMainFrame(), errorMsg, ex);
            System.exit(-1);
        }

        String repositoryId = DataProcessGuiPluginHelper.getCurrentRepositoryId(configuration.getUser(),
                                                                                ctxt);
        DataProcessGuiPluginHelper.loadDataProcessContext(repositoryId, ctxt, configuration);

        initRepositoryComboBox();
        initToolBar();
        updateToolBar();

        ctxt.addObserver(new RepositoryChangeEventListener(this, configuration.getUser(), ctxt));
        ctxt.addObserver(repositoryComboBox);
        ctxt.addObserver(new LoginEventObserver());

        startServerPingTask();
        initIcons();
    }


    private void startServerPingTask() {
        if (configuration.getServerPingDelay() != 0) {
            new Timer().schedule(new ServerPingTask(), 0, configuration.getServerPingDelay());
        }
        else {
            try {
                UserLoginTrackerClientHelper.addUser(ctxt, buildUserLoginTracker());
            }
            catch (Exception ex) {
                Log.error(getClass(), "Erreur interne concernant le suivie des personnes connectées.", ex);
            }
        }
    }


    private UserLoginTracker buildUserLoginTracker() throws UnknownHostException {
        InetAddress localhostAddress = Inet4Address.getLocalHost();
        String ip = localhostAddress.getHostAddress();
        String hostname = localhostAddress.getHostName();
        User user = configuration.getUser();
        return new UserLoginTracker(user.getUserName(), user.getCurrentRepository(), ip, hostname, null);
    }


    private static void initIcons() {
        UIManager.put("dataprocess.exit",
                      new ImageIcon(DataProcessGuiPlugin.class.getResource("/images/exit.gif")));
        UIManager.put("dataprocess.alpha_sort",
                      new ImageIcon(DataProcessGuiPlugin.class.getResource("/images/alpha_mode.gif")));
        UIManager.put("dataprocess.text",
                      new ImageIcon(DataProcessGuiPlugin.class.getResource("/images/text.gif")));
        UIManager.put("dataprocess.execute",
                      new ImageIcon(DataProcessGuiPlugin.class.getResource("/images/Manager.gif")));
        UIManager.put("dataprocess.configure",
                      new ImageIcon(DataProcessGuiPlugin.class.getResource("/images/configure.gif")));
        UIManager.put("dataprocess.add",
                      new ImageIcon(DataProcessGuiPlugin.class.getResource("/images/include.png")));
        UIManager.put("dataprocess.add2",
                      new ImageIcon(DataProcessGuiPlugin.class.getResource("/images/Add2.gif")));
        UIManager.put("dataprocess.remove",
                      new ImageIcon(DataProcessGuiPlugin.class.getResource("/images/exclude.png")));
        UIManager.put("dataprocess.copy",
                      new ImageIcon(DataProcessGuiPlugin.class.getResource("/images/Copy.gif")));
        UIManager.put("dataprocess.edit",
                      new ImageIcon(DataProcessGuiPlugin.class.getResource("/images/edit.gif")));
    }


    public void updateToolBar() {
        String repositoryId = configuration.getUser().getCurrentRepository();
        ToolbarRepoConfig toolbarRepoConfig = configuration.getToolBarRepoConfig(repositoryId);

        toolBarPanel.invalidate();
        toolBarPanel.removeAll();

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.LINE_AXIS));
        jPanel.add(repositoryPanel);
        jPanel.add(Box.createRigidArea(new Dimension(100, 0)));

        toolBarPanel.add(jPanel, BorderLayout.EAST);
        if (toolbarRepoConfig != null) {
            toolBarPanel.add(toolbarRepoConfig.build(ctxt, configuration), BorderLayout.WEST);
        }
        else {
            toolBarPanel.add(new DefaultToolbarRepoConfig().build(ctxt, configuration), BorderLayout.WEST);
        }
        toolBarPanel.validate();
    }


    private void initToolBar() {
        toolBarPanel.setOpaque(false);
        toolBarPanel.setLayout(new BorderLayout());
        ctxt.putProperty("toolbarPanel", toolBarPanel);
    }


    private void initRepositoryComboBox() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.ipadx = 0;
        gridBagConstraints1.insets = new Insets(1, 1, 1, 1);
        gridBagConstraints1.gridx = 0;

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.ipadx = 100;
        gridBagConstraints2.fill = GridBagConstraints.NONE;
        gridBagConstraints2.insets = new Insets(0, 6, 0, 0);
        gridBagConstraints2.gridx = 1;

        repositoryComboBox = new RepositoryComboBox(ctxt);
        repositoryComboBox.updateGuiOnRepositoryChange(true);
        try {
            repositoryComboBox.loadAuthorisedRepository(configuration.getUser());
        }
        catch (RequestException ex) {
            String errorMsg = "Impossible de charger la liste des référentiels de traitement";
            Log.error(getClass(), errorMsg + " :\n" + ex.getLocalizedMessage());
            ErrorDialog.show(ctxt.getMainFrame(), errorMsg, ex);
        }

        panel.add(new JLabel("Référentiel:"), gridBagConstraints1);
        panel.add(repositoryComboBox, gridBagConstraints2);
        repositoryPanel = panel;

        try {
            if (configuration.getUser().getCurrentRepository() != null) {
                repositoryComboBox.setOldSelectedRepository(RepositoryClientHelper.
                      getRepositoryIdFromName(ctxt, configuration.getUser().getCurrentRepository()));
                repositoryComboBox.setSelectedRepository(configuration.getUser().getCurrentRepository());
            }
        }
        catch (RepositoryException ex) {
            GuiUtils.showErrorDialog(ctxt.getMainFrame(), getClass(), "Erreur", ex);
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(ctxt.getMainFrame(), getClass(), "Erreur interne", ex);
        }

        repositoryComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        repositoryComboBox.setForeground(new Color(0, 0, 128));
    }


    public DataProcessGuiConfiguration getConfiguration() {
        return configuration;
    }


    private static class DefaultToolbarRepoConfig extends AbstractToolbarRepoConfig {
        private DefaultToolbarRepoConfig() {
            super("");
        }
    }

    private class ServerPingTask extends TimerTask {
        @Override
        public void run() {
            try {
                UserLoginTrackerClientHelper.addUser(ctxt, buildUserLoginTracker());
            }
            catch (Exception ex) {
                Log.error(getClass(), "Erreur interne concernant le suivie des personnes connectées.", ex);
            }
        }
    }

    private class LoginEventObserver implements Observer {
        public void update(Observable ob, Object arg) {
            if (arg instanceof GuiEvent) {
                GuiEvent guiEvent = (GuiEvent)arg;
                if (guiEvent.getName().equals(GuiEvent.LOGIN.getName())) {
                    GuiUtils.showRepositoryMessage(ctxt, configuration.getUser());
                }
            }
        }
    }
}
