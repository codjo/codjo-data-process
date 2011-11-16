package net.codjo.dataprocess.gui.repository;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiConfiguration;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JPanel;
/**
 *
 */
public abstract class AbstractToolbarRepoConfig implements ToolbarRepoConfig {
    private String repositoryName;


    protected AbstractToolbarRepoConfig(String repositoryName) {
        this.repositoryName = repositoryName;
    }


    public String getRepositoryName() {
        return repositoryName;
    }


    public JComponent build(MutableGuiContext ctxt, DataProcessGuiConfiguration dataProcessGuiConfig) {
        ctxt.getDesktopPane().setBackground(new Color(0, 78, 152));
        return new JPanel();
    }
}
