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
public class TreatmentTableAction extends AbstractAction {
    public TreatmentTableAction(GuiContext ctxt) {
        super(ctxt, "PM_TREATMENT", "Table PM_TREATMENT");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new TreatmentWindowTable(getGuiContext());
    }
}
