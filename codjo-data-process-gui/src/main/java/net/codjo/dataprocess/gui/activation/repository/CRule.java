package net.codjo.dataprocess.gui.activation.repository;
import net.codjo.dataprocess.gui.activation.spi.JComponentPod;
import net.codjo.dataprocess.gui.activation.spi.Rule;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class CRule implements Rule {
    public static final String SELECTED_REPOSITORTY_KEY = "selectedRepository";
    public static final String REPOSITORTY_LIST_KEY = "repositoryListRepository";


    public boolean applyRuleAndFinish(JComponentPod component, Map<String, Object> activationContext) {
        String path = component.getUnikKey().toLowerCase();
        String selectedRepository = (String)activationContext.get(SELECTED_REPOSITORTY_KEY);
        List repositoryList = (List)activationContext.get(REPOSITORTY_LIST_KEY);
        for (Object aRepositoryList : repositoryList) {
            String repositoryName = (String)aRepositoryList;
            if (path.endsWith(":" + repositoryName.toLowerCase())) {
                if (repositoryName.equals(selectedRepository)) {
                    component.getJcomponent().setVisible(true);
                    return true;
                }
                else {
                    component.getJcomponent().setVisible(false);
                    return true;
                }
            }
        }
        return false;
    }
}
