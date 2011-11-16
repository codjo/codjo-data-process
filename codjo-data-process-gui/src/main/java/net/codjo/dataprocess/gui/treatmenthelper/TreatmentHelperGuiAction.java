package net.codjo.dataprocess.gui.treatmenthelper;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.gui.util.std.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
/**
 *
 */
public class TreatmentHelperGuiAction extends AbstractAction {
    public TreatmentHelperGuiAction(GuiContext ctxt) {
        super(ctxt, "Import des référentiels de traitement", "Import des référentiels de traitement");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        Log.info(getClass(), "Ouverture de la fenêtre d'import des référentiels de traitement.");
        JInternalFrame frame = new JInternalFrame("Import des référentiels de traitement", true, true, true,
                                                  true);
        TreatmentHelperGui treatmentHelperGui = new TreatmentHelperGui((MutableGuiContext)getGuiContext(),
                                                                       frame);
        frame.setContentPane(treatmentHelperGui.getMainPanel());
        frame.setPreferredSize(new Dimension(680, 500));
        return frame;
    }
}
