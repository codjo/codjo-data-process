package net.codjo.dataprocess.gui.repository;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.TreatmentModelCodec;
import net.codjo.dataprocess.common.codec.TreatmentRootCodec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.TreatmentRoot;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 *
 */
public class XmlTreatmentLogic {
    private XmlTreatmentWindow gui;
    private String repositoryId;
    private MutableGuiContext ctxt;
    private boolean changeOnly;
    private JInternalFrame frame;
    private boolean contentHasChanged = false;
    private boolean loadingContent = false;


    public XmlTreatmentLogic(String repositoryId, MutableGuiContext ctxt, boolean changeOnly, String title) {
        this.repositoryId = repositoryId;
        this.ctxt = ctxt;
        this.changeOnly = changeOnly;
        frame = new JInternalFrame(title, true, true, true, false);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setFrameIcon(UIManager.getIcon("icon"));
        gui = new XmlTreatmentWindow(frame);
        init();
        frame.pack();
    }


    public void show() {
        ctxt.getDesktopPane().add(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        }
        catch (PropertyVetoException e) {
            ;
        }
    }


    private void init() {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                cancel();
            }
        });
        gui.getButtonOK().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                validate();
            }
        });
        gui.getButtonCancel().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancel();
            }
        });
        gui.getXmlTreatmentsTextArea().getDocument().addDocumentListener(new MyDocumentListener());

        InputMap inputMap = gui.getMainPanel().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        gui.getMainPanel().getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                cancel();
            }
        });
        frame.setSize(800, 500);
    }


    public JInternalFrame getFrame() {
        return frame;
    }


    private void validate() {
        try {
            List<TreatmentModel> treatments = new ArrayList<TreatmentModel>();
            String content = gui.getXmlTreatmentsTextArea().getText();
            content = addMissingRootTag(content);
            TreatmentRoot treatRoot = TreatmentRootCodec.decode(content);
            treatments.addAll(treatRoot.getTreatmentModelList());

            StringBuilder sb = new StringBuilder();
            for (TreatmentModel treatmentModel : treatments) {
                sb.append(alterTreatment(treatmentModel));
            }
            if (sb.length() != 0) {
                RepositoryClientHelper.reinitializeRepositoryCache(ctxt);
                JOptionPane.showMessageDialog(frame, sb.toString(), "Récapitulatif",
                                              JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur", ex);
        }
    }


    private String addMissingRootTag(String content) {
        if (!content.contains("<root>") && !content.contains("</root>")) {
            content = String.format("<root>%s</root>", content);
            setContent(content);
        }
        return content;
    }


    private void cancel() {
        if (contentHasChanged) {
            int result = JOptionPane.showConfirmDialog(frame,
                                                       "Voulez vous enregistrer les modifications ?",
                                                       "Demande de confirmation",
                                                       JOptionPane.YES_NO_CANCEL_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (result == JOptionPane.YES_OPTION) {
                validate();
            }
        }
        frame.dispose();
    }


    private String alterTreatment(TreatmentModel trtModel) {
        if (repositoryId != null) {
            try {
                String repositoryName = RepositoryClientHelper.getRepositoryName(ctxt, repositoryId);
                List<String> treatmentModelIdList =
                      TreatmentClientHelper.getAllTreatmentModelId(ctxt, repositoryId);
                String treatmentId = trtModel.getId().trim();
                if (treatmentModelIdList.contains(treatmentId)) {
                    String message =
                          String.format("Le traitement '%s' existe dans le référentiel de traitement '%s'.",
                                        treatmentId, repositoryName);
                    int result = JOptionPane.showConfirmDialog(frame,
                                                               message + "\nVoulez-vous le mettre à jour ?",
                                                               "Demande de confirmation",
                                                               JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        TreatmentClientHelper.manageTreatmentModel(ctxt,
                                                                   DataProcessConstants.Command.UPDATE,
                                                                   repositoryId,
                                                                   TreatmentModelCodec.encode(trtModel));
                        return String.format("Le traitement %s a été mis à jour.\n", treatmentId);
                    }
                }
                else {
                    if (!changeOnly) {
                        if (treatmentId.length() == 0) {
                            throw new TreatmentException(
                                  "L'identifiant d'un traitement ne peut pas être vide.");
                        }
                        TreatmentClientHelper.manageTreatmentModel(ctxt,
                                                                   DataProcessConstants.Command.CREATE,
                                                                   repositoryId,
                                                                   TreatmentModelCodec.encode(trtModel));
                        return String.format("Le traitement %s a été ajouté.\n", treatmentId);
                    }
                    else {
                        JOptionPane.showMessageDialog(frame, String.format(
                              "Vous ne pouvez pas ajouter de nouveaux traitements ici (%s)"
                              + "\nVous ne pouvez pas modifier un identifiant de traitement.", treatmentId),
                                                      "Action impossible", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            catch (TreatmentException ex) {
                GuiUtils.showErrorDialog(frame, getClass(),
                                         String.format("Le traitement '%s' n'a pas pu être créé/modifié.",
                                                       trtModel.getId()), ex);
            }
            catch (RequestException ex) {
                GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
            }
        }
        else {
            JOptionPane.showInternalMessageDialog(frame, "Aucun référentiel de traitement sélectionné !\n",
                                                  "Problème de création de traitement",
                                                  JOptionPane.ERROR_MESSAGE);
        }
        return "";
    }


    public void setContent(String content) {
        loadingContent = true;
        gui.getXmlTreatmentsTextArea().setText(content);
        gui.getXmlTreatmentsTextArea().setCaretPosition(0);
        loadingContent = false;
    }


    private class MyDocumentListener implements DocumentListener {
        private void contentHasChanged() {
            if (loadingContent) {
                return;
            }
            contentHasChanged = true;
        }


        public void insertUpdate(DocumentEvent e) {
            contentHasChanged();
        }


        public void removeUpdate(DocumentEvent e) {
            contentHasChanged();
        }


        public void changedUpdate(DocumentEvent e) {
            contentHasChanged();
        }
    }
}
