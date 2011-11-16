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
public class FamilyTableAction extends AbstractAction {
    public FamilyTableAction(GuiContext ctxt) {
        super(ctxt, "PM_FAMILY", "Table PM_FAMILY");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new FamilyWindowTable(getGuiContext());
    }
}
