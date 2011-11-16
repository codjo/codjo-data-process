package net.codjo.dataprocess.gui.selector;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.common.exception.RepositoryException;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.common.userparam.User.Repository;
import net.codjo.dataprocess.gui.util.DataProcessGuiEvent;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
/**
 *
 */
public class RepositoryComboBox extends RequestComboBox implements Observer {
    private int oldSelectedRepositoryId;
    private MutableGuiContext ctxt;
    private ActionListener repositoryUpdateEventListener = new RepositoryUpdateEventListener(false);
    private Map<String, String> translateMap = new HashMap<String, String>();
    private boolean isLoading = false;


    public void setOldSelectedRepository(int value) {
        oldSelectedRepositoryId = value;
    }


    public RepositoryComboBox(MutableGuiContext ctxt) {
        this.ctxt = ctxt;
        ListDataSource repositoryDataSource = new ListDataSource();
        repositoryDataSource.setColumns(new String[]{"repositoryId", "repositoryName"});
        repositoryDataSource.setLoadFactoryId("selectAllRepository");
        setDataSource(repositoryDataSource);
        initRequestComboBox("repositoryId", "repositoryName", false);

        addActionListener(repositoryUpdateEventListener);
        getDataSource().addDataSourceListener(new DataSourceAdapter() {
            @Override
            public void loadEvent(DataSourceEvent event) {
                translateMap.clear();
            }
        });
    }


    public void updateGuiOnRepositoryChange(boolean active) {
        ((RepositoryUpdateEventListener)repositoryUpdateEventListener).setActive(active);
    }


    public int getSelectedRepositoryId() {
        if (getSelectedItem() != null) {
            return Integer.parseInt((String)getSelectedItem());
        }
        return 0;
    }


    public boolean isLoading() {
        return isLoading;
    }


    public void loadData() {
        isLoading = true;
        try {
            load();
            if (getItemCount() != 0) {
                setSelectedIndex(0);
            }
        }
        catch (RequestException ex) {
            throw new IllegalStateException("Impossible de charger la liste des référentiels de traitement",
                                            ex);
        }
        finally {
            isLoading = false;
        }
    }


    public void loadAuthorisedRepository(User user) throws RequestException {
        load();

        Result newResult = new Result();
        Result loadResult = getDataSource().getLoadResult();

        if (loadResult.getRows() != null) {
            for (Object row : loadResult.getRows()) {
                Repository repository = user.getRepository(((Row)row).getFieldValue("repositoryName"));
                if (repository != null && repository.isValid()) {
                    newResult.addRow((Row)row);
                }
            }
            getDataSource().setLoadResult(newResult);
        }
    }


    public String translateValue(String id) {
        String idColumn = getDataSource().getColumns()[0];
        String nameColumn = getDataSource().getColumns()[1];

        if (translateMap.isEmpty()) {
            Result rs = getDataSource().getLoadResult();
            for (int i = 0; i < rs.getRowCount(); i++) {
                translateMap.put(rs.getValue(i, idColumn), rs.getValue(i, nameColumn));
            }
        }

        return translateMap.get(id);
    }


    public void setSelectedRepository(String repositoryName) throws RepositoryException, RequestException {
        boolean isActived = ((RepositoryUpdateEventListener)repositoryUpdateEventListener).isActived();

        ((RepositoryUpdateEventListener)repositoryUpdateEventListener).setActive(false);

        if (repositoryName != null) {
            String repositoryId = Integer
                  .toString(RepositoryClientHelper.getRepositoryIdFromName(ctxt, repositoryName));
            setSelectedItem(repositoryId);
            GuiContextUtils.setCurrentRepository(ctxt, repositoryId);
        }
        else {
            GuiContextUtils.setCurrentRepository(ctxt, null);
        }

        if (isActived) {
            ((RepositoryUpdateEventListener)repositoryUpdateEventListener).setActive(true);
        }
    }


    public void update(Observable ob, Object arg) {
        if (arg instanceof DataProcessGuiEvent) {
            DataProcessGuiEvent event = (DataProcessGuiEvent)arg;
            String eventName = event.getName();

            if (DataProcessGuiEvent.UPDATE_USER_EVENT.equals(eventName)) {
                User user = (User)event.getValue();
                try {
                    loadAuthorisedRepository(user);
                    GuiUtils.showRepositoryMessage(ctxt, user);
                    setSelectedRepository(user.getCurrentRepository());
                }
                catch (Exception ex) {
                    GuiUtils.showErrorDialog(ctxt.getMainFrame(), getClass(), "Erreur interne", ex);
                }
            }
        }
    }


    private class RepositoryUpdateEventListener implements ActionListener {
        private boolean actived;


        public boolean isActived() {
            return actived;
        }


        public void setActive(boolean actived) {
            this.actived = actived;
        }


        private RepositoryUpdateEventListener(boolean actived) {
            this.actived = actived;
        }


        public void actionPerformed(ActionEvent evt) {
            if (actived) {
                ctxt.sendEvent(new DataProcessGuiEvent(DataProcessGuiEvent.PRE_CHANGE_REPOSITORY_EVENT,
                                                       oldSelectedRepositoryId));
                int selectedRepositoryId = getSelectedRepositoryId();
                GuiContextUtils.setCurrentRepository(ctxt, Integer.toString(selectedRepositoryId));
                ctxt.sendEvent(new DataProcessGuiEvent(DataProcessGuiEvent.POST_CHANGE_REPOSITORY_EVENT,
                                                       selectedRepositoryId));
                oldSelectedRepositoryId = selectedRepositoryId;
            }
        }
    }
}
