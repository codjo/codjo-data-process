package net.codjo.dataprocess.gui.repository;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiConfiguration;
import net.codjo.mad.gui.framework.MutableGuiContext;
import javax.swing.JComponent;
/**
 *
 */
public interface ToolbarRepoConfig {
    JComponent build(MutableGuiContext ctxt, DataProcessGuiConfiguration dataProcessGuiConfig);
}
