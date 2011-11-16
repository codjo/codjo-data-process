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
public class PmDpConfigTableAction extends AbstractAction {
    public PmDpConfigTableAction(GuiContext ctxt) {
        super(ctxt, "Paramétrage des variables de configuration [PM_DP_CONFIG]",
              "Paramétrage des variables de configuration [PM_DP_CONFIG]");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new DpConfigWindowTable(getGuiContext());
    }
}