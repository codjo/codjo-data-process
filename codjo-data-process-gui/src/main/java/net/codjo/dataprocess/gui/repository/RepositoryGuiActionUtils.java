package net.codjo.dataprocess.gui.repository;
import net.codjo.dataprocess.client.DataProcessContextClientHelper;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiPlugin;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.InternalInputDialog;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
/**
 *
 */
public class RepositoryGuiActionUtils {
    private RepositoryWindow repoWin;
    private MutableGuiContext ctxt;
    private JInternalFrame frame;
    private DataProcessGuiPlugin dataProcessGuiPlugin;


    public RepositoryGuiActionUtils(RepositoryWindow repoWin,
                                    DataProcessGuiPlugin dataProcessGuiPlugin,
                                    MutableGuiContext ctxt,
                                    JInternalFrame frame) {
        this.repoWin = repoWin;
        this.ctxt = ctxt;
        this.frame = frame;
        this.dataProcessGuiPlugin = dataProcessGuiPlugin;
    }


    void actionRenameRepository(String repositoryId) {
        try {
            String repositoryName = RepositoryClientHelper.getRepositoryName(ctxt, repositoryId);
            InternalInputDialog internalInputDialog =
                  new InternalInputDialog(frame,
                                          "Renommer un référentiel",
                                          String.format("Nouveau nom du référentiel '%s':", repositoryName),
                                          UIManager.getIcon("dataprocess.edit"));
            String newRepositoryName = internalInputDialog.input();
            if (newRepositoryName == null) {
                return;
            }
            if (repoWin.isThereSomethingToSave()) {
                int result = JOptionPane.showInternalConfirmDialog(frame,
                                                                   "Les modifications vont être sauvegardées avant le renommage. \nVoulez vous continuer ?",
                                                                   "Demande de confirmation",
                                                                   JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    repoWin.actionSaveAll();
                    doRenameRepository(repositoryId, newRepositoryName);
                }
            }
            else {
                doRenameRepository(repositoryId, newRepositoryName);
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, RepositoryGuiActionUtils.class, "Erreur", ex);
        }
    }


    private void doRenameRepository(String repositoryId, String newRepositoryName) throws RequestException {
        RepositoryClientHelper.renameRepository(ctxt, Integer.parseInt(repositoryId), newRepositoryName);
        repoWin.reloadRepositoryComboBox();
        repoWin.createPopupOnGuiTreatmentList();
        repoWin.updateTreatmentList();
    }


    void actionCopyRepository(String repositorySourceId) {
        int result = JOptionPane.showInternalConfirmDialog(frame,
                                                           "Les modifications en cours vont être sauvegardées avant la copie."
                                                           + "\nVoulez vous continuer ?",
                                                           "Demande de confirmation",
                                                           JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }
        repoWin.actionSaveAll();

        try {
            String currentRepositoryId = GuiContextUtils.getCurrentRepository(ctxt);
            try {
                DataProcessContextClientHelper.saveContext(ctxt,
                                                           currentRepositoryId,
                                                           dataProcessGuiPlugin.getConfiguration()
                                                                 .getDataProcessContext());
                Log.info(getClass(), "Sauvegarde du context lié au référentiel de traitement courant.");
            }
            catch (IllegalArgumentException e) {
                ;
            }
        }
        catch (RequestException ex) {
            Log.error(getClass(), "La sauvegarde du context a échoué.", ex);
        }
        final String[] repositoryDestId = new String[1];
        InternalInputDialog inputDialog = new InternalInputDialog(frame,
                                                                  "Copie de référentiel",
                                                                  "Nom de la copie:",
                                                                  UIManager.getIcon("dataprocess.copy")) {
            @Override
            public String input() {
                String value = super.input();
                if (value == null) {
                    return null;
                }
                try {
                    repositoryDestId[0] = RepositoryClientHelper.createRepository(ctxt, value);
                    if (repositoryDestId[0].equals(DataProcessConstants.REPOSITORY_ALREADY_EXISTS)) {
                        JOptionPane.showInternalMessageDialog(frame,
                                                              String.format(
                                                                    "Il existe déjà un référentiel de traitement nommé '%s'.",
                                                                    value),
                                                              "Copie du référentiel impossible",
                                                              JOptionPane.WARNING_MESSAGE);
                        return input();
                    }
                    else {
                        return value;
                    }
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Le traitement n'a pas pu être créé.", ex);
                    return null;
                }
            }
        };
        String repositoryName = inputDialog.input();
        if (repositoryName == null) {
            return;
        }
        try {
            TreatmentClientHelper.copyTreatmentModels(ctxt, repositorySourceId, repositoryDestId[0]);
            repoWin.reloadRepositoryComboBox();
            repoWin.updateTreatmentList();
            repoWin.createPopupOnGuiTreatmentList();
            DataProcessContextClientHelper.duplicateContext(ctxt, repositorySourceId, repositoryDestId[0]);
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame.getDesktopPane(), getClass(), "Erreur interne", ex);
        }
    }


    void actionNewRepository() {
        InternalInputDialog inputDialog = new InternalInputDialog(frame,
                                                                  "Ajout d'un nouveau référentiel",
                                                                  "Nom du référentiel de traitement:",
                                                                  UIManager.getIcon("dataprocess.add2")) {
            @Override
            public String input() {
                String value = super.input();
                if (value == null) {
                    return null;
                }
                try {
                    String repositoryDestId = RepositoryClientHelper.createRepository(ctxt, value);
                    if (repositoryDestId.equals(DataProcessConstants.REPOSITORY_ALREADY_EXISTS)) {
                        JOptionPane.showInternalMessageDialog(frame,
                                                              String.format(
                                                                    "Il existe déjà un référentiel de traitement nommé '%s'.",
                                                                    value),
                                                              "Copie du référentiel impossible",
                                                              JOptionPane.WARNING_MESSAGE);
                        return input();
                    }
                    else {
                        return value;
                    }
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Le traitement n'a pas pu être créé.", ex);
                    return null;
                }
            }
        };
        String repositoryName = inputDialog.input();
        if (repositoryName == null) {
            return;
        }
        try {
            repoWin.reloadRepositoryComboBox();
            repoWin.updateTreatmentList();
            repoWin.createPopupOnGuiTreatmentList();
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame.getDesktopPane(), getClass(), "Erreur interne", ex);
        }
    }


    void actionDeleteRepository(String repositoryId) {
        try {
            String repositoryName = RepositoryClientHelper.getRepositoryName(ctxt, repositoryId);
            int result = JOptionPane.showInternalConfirmDialog(frame,
                                                               String.format(
                                                                     "Voulez vous vraiment supprimer le référentiel '%s' ?",
                                                                     repositoryName),
                                                               "Demande de confirmation",
                                                               JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                RepositoryClientHelper.deleteRepository(ctxt, Integer.parseInt(repositoryId));
                DataProcessContextClientHelper.deleteContextByContextName(ctxt, repositoryId);
                repoWin.reloadRepositoryComboBox();
                repoWin.createPopupOnGuiTreatmentList();
                repoWin.updateTreatmentList();
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
    }
}
