/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.param;
import net.codjo.dataprocess.client.HandlerCommandSender;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.framework.GuiContext;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
/**
 *
 */
public class SelectRepositoryDialog {
    private JInternalFrame frame;
    private int repositorySrcId;
    private Map<String, Integer> repositoryModel = new HashMap<String, Integer>();


    public SelectRepositoryDialog(GuiContext ctxt, JInternalFrame frame, int repositorySrcId)
          throws RequestException {
        this.frame = frame;
        this.repositorySrcId = repositorySrcId;
        HandlerCommandSender sender = new HandlerCommandSender();
        Result rs = sender.sendSqlCommand(ctxt, "selectAllRepositoryNames", null, null);
        for (int i = 0; i < rs.getRowCount(); i++) {
            repositoryModel.put(rs.getValue(i, "repositoryName"),
                                Integer.valueOf(rs.getValue(i, "repositoryId")));
        }
    }


    public int input() {
        Object[] repoNames = repositoryModel.keySet().toArray();
        Object obj = JOptionPane.showInternalInputDialog(frame,
                                                         "Référentiel:",
                                                         "Sélection du référentiel de destination",
                                                         JOptionPane.QUESTION_MESSAGE,
                                                         null,
                                                         repoNames,
                                                         repoNames.length > 0 ? repoNames[0] : null);
        if (obj == null) {
            return 0;
        }
        else {
            int repositoryDestId = repositoryModel.get(obj.toString());
            if (repositoryDestId == repositorySrcId) {
                JOptionPane.showInternalMessageDialog(frame,
                                                      "Les référentiels de départ et de destination sont identiques !",
                                                      "Erreur",
                                                      JOptionPane.ERROR_MESSAGE);
                return input();
            }
            else {
                return repositoryDestId;
            }
        }
    }
}
