package net.codjo.dataprocess.gui.repository;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.TreatmentModelCodec;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ResultTable;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.InternalInputDialog;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
/**
 *
 */
public class TreatmentModelGuiActionUtils {
    private RepositoryWindow repoWin;
    private MutableGuiContext ctxt;
    private JInternalFrame frame;


    public TreatmentModelGuiActionUtils(RepositoryWindow repoWin,
                                        MutableGuiContext ctxt,
                                        JInternalFrame frame) {
        this.repoWin = repoWin;
        this.ctxt = ctxt;
        this.frame = frame;
    }


    void actionDeleteTreatment(String repositoryId) {
        int result = JOptionPane.showInternalConfirmDialog(frame,
                                                           "Voulez vous vraiment supprimer ce traitement ?",
                                                           "Demande de confirmation",
                                                           JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            TreatmentModel treatmentModel;
            try {
                treatmentModel = repoWin.getCurrentTreatmentModel();
            }
            catch (ArrayIndexOutOfBoundsException e1) {
                return;
            }
            try {
                TreatmentClientHelper.manageTreatmentModel(ctxt, DataProcessConstants.Command.DELETE,
                                                           repositoryId,
                                                           TreatmentModelCodec.encode(treatmentModel));
                repoWin.updateTreatmentList();
            }
            catch (RequestException ex) {
                GuiUtils.showErrorDialog(frame, getClass(),
                                         "Une erreur est survenue lors de la suppression de " + treatmentModel
                                               .getId(), ex);
            }
        }
    }


    void actionNewTreatment(String repository) {
        if (repository != null) {
            TreatmentInputDialog treatmentInputDialog =
                  new TreatmentInputDialog(ctxt,
                                           repository,
                                           frame,
                                           repoWin.getGuiTreatmentList(),
                                           "Ajout d'un nouveau traitement",
                                           "Nom du traitement:",
                                           UIManager.getIcon("dataprocess.add2"));
            try {
                String treatmentName = treatmentInputDialog.input();
                if (treatmentName == null) {
                    return;
                }
                TreatmentModel trtModel = new TreatmentModel();
                trtModel.setId(treatmentName);
                trtModel.setTitle(treatmentName);
                trtModel.setComment(treatmentName);
                trtModel.setResultTable(new ResultTable("", ""));
                trtModel.setTarget("");
                trtModel.setType(DataProcessConstants.JAVA_TYPE);
                trtModel.setArguments(new ArgList());
                TreatmentClientHelper.manageTreatmentModel(ctxt, DataProcessConstants.Command.CREATE,
                                                           repository,
                                                           TreatmentModelCodec.encode(trtModel));
                repoWin.updateTreatmentList();
            }
            catch (Exception ex) {
                GuiUtils.showErrorDialog(frame, getClass(), "Le traitement n'a pas pu être créé.", ex);
            }
        }
        else {
            JOptionPane.showInternalMessageDialog(frame,
                                                  "Aucun référentiel de traitement sélectionné !\n",
                                                  "Erreur",
                                                  JOptionPane.ERROR_MESSAGE);
        }
    }


    void actionCopyTreatment(String repositoryDestination, String currentRepositoryId)
          throws RequestException {
        String repositoryId;
        if ("".equals(repositoryDestination)) {
            repositoryId = currentRepositoryId;
        }
        else {
            repositoryId = repositoryDestination;
        }
        if (repositoryId != null) {
            TreatmentModel trtModel = repoWin.getCurrentTreatmentModel();
            boolean alreadyExist = repoWin.isTreatmentInRepository(trtModel, repositoryId);

            if (alreadyExist) {
                String repositoryName = RepositoryClientHelper.getRepositoryName(ctxt, repositoryId);
                JOptionPane.showInternalMessageDialog(frame,
                                                      String.format("Un traitement nommé '%s'"
                                                                    + " existe déjà dans le référentiel de traitement '%s'.",
                                                                    trtModel.getId(), repositoryName),
                                                      "Problème de copie de traitement",
                                                      JOptionPane.ERROR_MESSAGE);
                TreatmentInputDialog treatmentInputDialog =
                      new TreatmentInputDialog(ctxt,
                                               repositoryId,
                                               frame,
                                               repoWin.getGuiTreatmentList(),
                                               "Copie d'un traitement",
                                               "Nom de la copie:",
                                               UIManager.getIcon("dataprocess.copy"));
                String treatmentId = treatmentInputDialog.input();
                if (treatmentId == null) {
                    return;
                }
                TreatmentModel trt = new TreatmentModel();
                trt.setId(treatmentId);
                if (repoWin.isTreatmentInRepository(trt, repositoryId)) {
                    JOptionPane.showInternalMessageDialog(frame,
                                                          String.format("Un traitement nommé '%s'"
                                                                        + " existe déjà dans le référentiel de traitement '%s'.",
                                                                        trtModel.getId(), repositoryName),
                                                          "Erreur",
                                                          JOptionPane.ERROR_MESSAGE);
                }
                else {
                    trtModel.setId(treatmentId);
                    TreatmentClientHelper.manageTreatmentModel(ctxt,
                                                               DataProcessConstants.Command.CREATE,
                                                               repositoryId,
                                                               TreatmentModelCodec.encode(trtModel));
                    repoWin.initTreatmentList();
                }
            }
            else {
                TreatmentClientHelper.manageTreatmentModel(ctxt,
                                                           DataProcessConstants.Command.CREATE,
                                                           repositoryId,
                                                           TreatmentModelCodec.encode(trtModel));
                repoWin.initTreatmentList();
            }
        }
        else {
            JOptionPane.showInternalMessageDialog(frame,
                                                  "Aucun référentiel de traitement sélectionné !\n",
                                                  "Erreur",
                                                  JOptionPane.ERROR_MESSAGE);
        }
    }


    private static class TreatmentInputDialog extends InternalInputDialog {
        private MutableGuiContext ctxt;
        private String repositoryId;
        private JInternalFrame frame;
        private JList guiTreatmentList;


        TreatmentInputDialog(MutableGuiContext ctxt,
                             String repositoryId,
                             JInternalFrame frame,
                             JList guiTreatmentList,
                             String titre,
                             String message,
                             Icon icon) {
            super(frame, titre, message, icon);
            this.ctxt = ctxt;
            this.repositoryId = repositoryId;
            this.frame = frame;
            this.guiTreatmentList = guiTreatmentList;
        }


        @Override
        public String input() {
            String value = super.input();
            if (value == null) {
                return null;
            }
            DefaultListModel listModel = (DefaultListModel)guiTreatmentList.getModel();
            if (listModel.contains(value)) {
                try {
                    String repositoryName = RepositoryClientHelper.getRepositoryName(ctxt, repositoryId);
                    JOptionPane.showInternalMessageDialog(frame,
                                                          String.format(
                                                                "Le traitement '%s' existe déjà dans le référentiel de traitement '%s'.",
                                                                value,
                                                                repositoryName),
                                                          "Erreur",
                                                          JOptionPane.ERROR_MESSAGE);
                    return input();
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                    return null;
                }
            }
            else {
                return value;
            }
        }
    }
}
