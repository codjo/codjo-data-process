package net.codjo.dataprocess.gui.activation;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.gui.activation.impl.JMenuIterator;
import net.codjo.dataprocess.gui.activation.repository.CRule;
import net.codjo.dataprocess.gui.activation.spi.RulesProcessor;
import net.codjo.dataprocess.gui.util.DataProcessGuiEvent;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
/**
 *
 */
public class RepositoryMenuManager implements Observer {
    private RulesProcessor activator;
    private Map<String, Object> context;
    private MutableGuiContext ctxt;
    private JFrame frame;
    private final User user;


    public RepositoryMenuManager(MutableGuiContext ctxt, JFrame frame, User user) {
        this.frame = frame;
        this.ctxt = ctxt;
        this.user = user;
    }


    private void init() {
        JMenuBar menuBar = frame.getJMenuBar();
        frame = null;
        context = new HashMap<String, Object>();
        context.put(CRule.SELECTED_REPOSITORTY_KEY, user.getCurrentRepository());
        List<String> repoList = new ArrayList<String>();
        Collection<String> allRepositoryNames = null;
        try {
            allRepositoryNames = RepositoryClientHelper.getAllRepositoryNames(ctxt).values();
        }
        catch (RequestException ex) {
            Log.error(getClass(), "Erreur interne :\n" + ex.getLocalizedMessage());
        }
        repoList.addAll(allRepositoryNames);
        context.put(CRule.REPOSITORTY_LIST_KEY, repoList);
        activator = new RulesProcessor(new JMenuIterator(menuBar), context);
        activator.addRule(new CRule());
    }


    public void update(Observable ob, Object arg) {
        if (frame != null) {
            init();
        }
        if (arg instanceof DataProcessGuiEvent) {
            DataProcessGuiEvent event = (DataProcessGuiEvent)arg;
            String eventName = event.getName();
            if (DataProcessGuiEvent.POST_CHANGE_REPOSITORY_EVENT.equals(eventName)) {
                String repoName = null;
                try {
                    repoName = RepositoryClientHelper.getAllRepositoryNames(ctxt)
                          .get(event.getValue().toString());
                }
                catch (RequestException ex) {
                    Log.error(getClass(), "Erreur interne :\n" + ex.getLocalizedMessage());
                }
                selectCurrentRepoAndManageMenu(repoName);
            }
        }
    }


    public void selectCurrentRepoAndManageMenu(String repoName) {
        if (frame != null) {
            init();
        }
        if (repoName != null) {
            context.put(CRule.SELECTED_REPOSITORTY_KEY, repoName);
        }
        else {
            context.put(CRule.SELECTED_REPOSITORTY_KEY, "");
        }
        if (activator != null) {
            activator.proceed();
        }
    }


    public static void configureRepositoryMenuManager(MutableGuiContext ctxt, final User user) {
        final JFrame mainFrame = ctxt.getMainFrame();

        final RepositoryMenuManager repositoryMenuManager = new RepositoryMenuManager(ctxt, mainFrame, user);
        ctxt.addObserver(repositoryMenuManager);

        new Thread() {
            @Override
            public void run() {
                while (mainFrame.getJMenuBar() == null) {
                    try {
                        Thread.sleep(20);
                    }
                    catch (InterruptedException ex) {
                        Log.error(getClass(), "Erreur interne :\n" + ex.getLocalizedMessage());
                    }
                }
                repositoryMenuManager.selectCurrentRepoAndManageMenu(user.getCurrentRepository());
            }
        }.start();
    }
}