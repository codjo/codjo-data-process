/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.tables;
import net.codjo.dataprocess.gui.util.std.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import javax.swing.JInternalFrame;
/**
 *
 */
public class RepositoryContentTableAction extends AbstractAction {
    public RepositoryContentTableAction(GuiContext ctxt) {
        super(ctxt, "PM_REPOSITORY_CONTENT", "Table PM_REPOSITORY_CONTENT");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new RepositoryContentWindowTable(getGuiContext());
    }
}
