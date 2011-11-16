/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.sqleditor.components;
import net.codjo.dataprocess.common.eventsbinder.EventBinderException;
import net.codjo.dataprocess.common.eventsbinder.EventsBinder;
import net.codjo.dataprocess.gui.util.sqleditor.util.SQLEditorTools;
import net.codjo.gui.toolkit.waiting.WaitingPanel;
import javax.swing.JTabbedPane;
import org.apache.commons.lang.StringUtils;
/**
 *
 */
public class ResultTabbedPane extends JTabbedPane {

    public void addResult(EventsBinder eventsBinder,
                          StringBuffer resultString,
                          String sql,
                          WaitingPanel waitingPanel,
                          int pageSize,
                          SQLEditorTools sqlEditorTools)
          throws EventBinderException {
        ResultPaneLogic paneLogic = new ResultPaneLogic(eventsBinder, this, waitingPanel, pageSize,
                                                        sqlEditorTools);
        ResultPaneGui paneGui = paneLogic.getResultPane();
        paneGui.init(sql, resultString, pageSize, paneLogic.getNavigationPanelLogic(), sqlEditorTools);
        add(StringUtils.abbreviate(sql, 30), paneGui);
        setSelectedIndex(getTabCount() - 1);
    }
}
