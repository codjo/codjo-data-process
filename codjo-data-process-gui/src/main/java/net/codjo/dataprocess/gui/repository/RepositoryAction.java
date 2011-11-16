package net.codjo.dataprocess.gui.repository;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiPlugin;
import net.codjo.dataprocess.gui.util.std.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
/**
 *
 */
public class RepositoryAction extends AbstractAction {
    private DataProcessGuiPlugin dataProcessGuiPlugin;


    public RepositoryAction(GuiContext ctxt, DataProcessGuiPlugin dataProcessGuiPlugin) {
        super(ctxt, "R�f�rentiels de traitements", "Gestion des r�f�rentiels de traitements");
        this.dataProcessGuiPlugin = dataProcessGuiPlugin;
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        Log.info(getClass(),
                 "Ouverture de la fen�tre de gestion des r�f�rentiels de traitements.");
        JInternalFrame frame = new JInternalFrame("Gestion des r�f�rentiels de traitements",
                                                  true, true, true, true);
        RepositoryWindow repositoryWindow = new RepositoryWindow((MutableGuiContext)getGuiContext(),
                                                                 dataProcessGuiPlugin,
                                                                 frame);
        frame.setContentPane(repositoryWindow.getMainPanel());
        frame.setPreferredSize(new Dimension(1150, 700));
        return frame;
    }
}