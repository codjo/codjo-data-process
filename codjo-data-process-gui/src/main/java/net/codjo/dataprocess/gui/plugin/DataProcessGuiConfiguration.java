package net.codjo.dataprocess.gui.plugin;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.gui.repository.AbstractToolbarRepoConfig;
import net.codjo.dataprocess.gui.repository.ToolbarRepoConfig;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
/**
 *
 */
public class DataProcessGuiConfiguration {
    private static final String DEFAULT_REPOSITORY_TECH = "TECH";
    private static final String DEFAULT_MAINTENANCE_ROLE_NAME = "Maintenance";
    private static final int DEFAULT_SERVER_PING_DELAY = 10 * 60 * 1000;

    private Map<String, String> repositoryNameMap = new HashMap<String, String>();
    private Map<String, ToolbarRepoConfig> toolbarRepoConfigMap = new HashMap<String, ToolbarRepoConfig>();
    private DataProcessContext dataProcessContext = new DataProcessContext();
    private List<String> globalParameters = new ArrayList<String>();
    private TableExploratorConfig tableExploratorConfig = new TableExploratorConfig();
    private int serverPingDelay = DEFAULT_SERVER_PING_DELAY;
    private String maintenanceRoleName = DEFAULT_MAINTENANCE_ROLE_NAME;
    private String repositoryTech = DEFAULT_REPOSITORY_TECH;
    private String packageArgumentModifier;
    private DataProcessGuiPlugin dataProcessGuiPlugin;
    private User user;


    public DataProcessGuiConfiguration(DataProcessGuiPlugin dataProcessGuiPlugin) {
        this.dataProcessGuiPlugin = dataProcessGuiPlugin;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    public void setGlobalParameter(List<String> globalParameters, MutableGuiContext ctxt) {
        this.globalParameters = globalParameters;
    }


    public List<String> getGlobalParameters() {
        return globalParameters;
    }


    public void setMaintenanceRoleName(String maintenanceRoleName) {
        this.maintenanceRoleName = maintenanceRoleName;
    }


    public String getMaintenanceRoleName() {
        return maintenanceRoleName;
    }


    public void setPackageArgumentModifier(String packageArgumentModifier) {
        this.packageArgumentModifier = packageArgumentModifier;

        if (packageArgumentModifier != null) {
            dataProcessContext.setProperty(DataProcessContext.PACKAGE_ARGUMENT_MODIFIER,
                                           packageArgumentModifier);
        }
    }


    public String getPackageArgumentModifier() {
        return packageArgumentModifier;
    }


    public void setDataProcessContext(DataProcessContext dataProcessContext) {
        this.dataProcessContext = dataProcessContext;
    }


    public DataProcessContext getDataProcessContext() {
        return dataProcessContext;
    }


    public Map<String, String> getRepositoryNameMap() {
        return repositoryNameMap;
    }


    public ToolbarRepoConfig getToolBarRepoConfig(String repositoryId) {
        return toolbarRepoConfigMap.get(repositoryId);
    }


    public void addToolbarRepoConfig(AbstractToolbarRepoConfig toolbarRepoConfig) {
        toolbarRepoConfigMap.put(toolbarRepoConfig.getRepositoryName(), toolbarRepoConfig);
    }


    public int getServerPingDelay() {
        return serverPingDelay;
    }


    public void setServerPingDelay(int serverPingDelay) {
        this.serverPingDelay = serverPingDelay;
    }


    public String getRepositoryTech() {
        return repositoryTech;
    }


    public void setRepositoryTech(String repositoryTech) {
        this.repositoryTech = repositoryTech;
    }


    public TableExploratorConfig getTableExploratorConfig() {
        return tableExploratorConfig;
    }


    public static class TableExploratorConfig {
        private Set<String> exclusionRuleSet = new TreeSet<String>();
        private Map<String, String> tableExploratorActionMap = new HashMap<String, String>();


        public Set<String> getExclusionRuleSet() {
            return exclusionRuleSet;
        }


        public void excludeTable(String table) {
            exclusionRuleSet.add("^" + table + "$");
        }


        public void includeTable(String table) {
            exclusionRuleSet.remove("^" + table + "$");
        }


        public void addExclusionRule(String table) {
            exclusionRuleSet.add(table);
        }


        public void removeExclusionRule(String table) {
            exclusionRuleSet.remove(table);
        }


        public void addTableAction(String table, String action) {
            tableExploratorActionMap.put(table, action);
        }


        public String getTableAction(String table) {
            return tableExploratorActionMap.get(table);
        }
    }
}
