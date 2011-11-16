package net.codjo.dataprocess.gui.param;
import net.codjo.dataprocess.client.DependencyClientHelper;
import net.codjo.dataprocess.client.ExecutionListClientHelper;
import net.codjo.dataprocess.client.ExecutionListDB;
import net.codjo.dataprocess.client.FamilyClientHelper;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.ExecutionListStoreHelper;
import net.codjo.dataprocess.common.model.ExecutionListStoreHelper.ExecListModelToDelete;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.dataprocess.common.util.ExecListParamImportReport;
import net.codjo.dataprocess.gui.selector.RepositoryFamilyPanel;
import net.codjo.dataprocess.gui.util.ComboUpdateEventListener;
import net.codjo.dataprocess.gui.util.DocumentListenerAdapter;
import net.codjo.dataprocess.gui.util.ErrorDialog;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.util.file.FileUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import static net.codjo.dataprocess.gui.param.ExecutionListParamWindow.TokenType.FAMILY;
import static net.codjo.dataprocess.gui.param.ExecutionListParamWindow.TokenType.REPO;
/**
 *
 */
public class ExecutionListParamWindow {
    private JPanel mainPanel;
    private JPanel topPanel;
    private JButton addListButton;
    private JList listOfExecutionJList;
    private JList guiTreatments;
    private JToggleButton alphaSortRepoTreatmentListButton;
    private JToggleButton alphaSortListOfExecutionButton;
    private JTextField filterTextField;
    private JList guiRepoTreatmentList;
    private JTextArea commentTextArea;
    private JButton copyTreatmentsToOtherRepo;
    private JButton fromTrtToListButton;
    private JButton fromListToTrtButton;
    private JButton quitButton;
    private JButton saveButton;
    private JButton deleteListButton;
    private JButton deleteListByRepoAndFamilyButton;
    private JButton downPriorityButton;
    private JButton upPriorityButton;
    private JInternalFrame frame;
    private JButton exportParamButton;
    private JButton importParamButton;
    private RepositoryFamilyPanel repositoryFamilyPanel;
    private ExecutionListStoreHelper listOfExecutionList;
    private List<UserTreatment> repositoryTreatmentList = null;
    private Map<String, ExecutionListStoreHelper> listOfExecutionListMap
          = new HashMap<String, ExecutionListStoreHelper>();
    private Map<String, List<UserTreatment>> repoTreatmentListMap
          = new HashMap<String, List<UserTreatment>>();
    private MutableGuiContext ctxt;
    private boolean modified = false;


    public ExecutionListParamWindow(MutableGuiContext ctxt, final JInternalFrame frame) {
        this.ctxt = ctxt;
        this.frame = frame;
        repositoryFamilyPanel = new RepositoryFamilyPanel(ctxt, true, false);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        buildImportExportPanel();
        guiTreatments.setModel(new DefaultListModel());
        guiTreatments.setSelectionModel(new DefaultListSelectionModel());
        guiTreatments.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        guiTreatments.setCellRenderer(new TrtListCellRenderer());

        guiRepoTreatmentList.setModel(new DefaultListModel());
        guiRepoTreatmentList.setSelectionModel(new DefaultListSelectionModel());
        guiRepoTreatmentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        guiRepoTreatmentList.setCellRenderer(new TrtListCellRenderer());

        listOfExecutionJList.setModel(new DefaultListModel());
        listOfExecutionJList.setSelectionModel(new DefaultListSelectionModel());
        listOfExecutionJList.setCellRenderer(new ListOfExecutionCellRenderer());
        listOfExecutionJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        guiRepoTreatmentList.setName("guiRepoTreatmentList");
        guiTreatments.setName("guiTreatments");
        enableSaveButton(false);
        createListeners();
    }


    private void buildImportExportPanel() {
        JPanel importExportPanel = new JPanel();
        exportParamButton = new JButton();
        GuiUtils.setMaxSize(exportParamButton, 25, 25);
        exportParamButton.setIcon(GuiUtils.loadIcon(getClass(), "/images/export.png"));
        exportParamButton.setToolTipText("Exporter le paramétrage des listes de traitements");

        importParamButton = new JButton();
        GuiUtils.setMaxSize(importParamButton, 25, 25);
        importParamButton.setIcon(GuiUtils.loadIcon(getClass(), "/images/open.png"));
        importParamButton.setToolTipText("Importer le paramétrage des listes de traitements");
        importExportPanel.add(exportParamButton);
        importExportPanel.add(importParamButton);

        topPanel.setLayout(new BorderLayout());
        topPanel.add(repositoryFamilyPanel, BorderLayout.WEST);
        topPanel.add(importExportPanel, BorderLayout.EAST);
    }


    public void load() {
        repositoryFamilyPanel.load();
        try {
            repositoryFamilyPanel.setSelectedRepositoryId(ctxt, GuiContextUtils.getCurrentRepository(ctxt));
        }
        catch (Exception ex) {
            ;
        }
        initAll();
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }


    private void localSaveCurrentList() {
        listOfExecutionListMap.put(getKeyFromOldComboxValue(), listOfExecutionList);
        repoTreatmentListMap.put(getKeyFromOldComboxValue(), repositoryTreatmentList);
    }


    private void localLoadCurrentlist() {
        listOfExecutionList = listOfExecutionListMap.get(getKeyFromComboxValue());
        repositoryTreatmentList = repoTreatmentListMap.get(getKeyFromComboxValue());

        if (listOfExecutionList == null) {
            initListOfExecutionList(getSelectedRepositoryId(), getSelectedFamilyId());
        }
        initListOfExecutionListModel();

        if (repositoryTreatmentList == null) {
            initRepositoryTreatmentList(getSelectedRepositoryId());
        }
        else {
            updateRepositoryGuiComponent();
        }
        ((DefaultListModel)guiTreatments.getModel()).clear();
    }


    private void initAll() {
        listOfExecutionListMap.clear();
        repoTreatmentListMap.clear();

        initListOfExecutionList(getSelectedRepositoryId(), getSelectedFamilyId());
        initListOfExecutionListModel();
        initRepositoryTreatmentList(getSelectedRepositoryId());
        ((DefaultListModel)guiTreatments.getModel()).clear();
    }


    private void filterChanged() {
        updateRepositoryGuiComponent();
    }


    private void updateLocalList() {
        localSaveCurrentList();
        localLoadCurrentlist();
    }


    private void initListOfExecutionList(int repositoryIdSelected, int familyIdSelected) {
        List<ExecutionListModel> list = null;
        listOfExecutionList = new ExecutionListStoreHelper();

        try {
            list = TreatmentClientHelper.getExecutionListModel(ctxt, repositoryIdSelected, familyIdSelected);
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
            frame.dispose();
        }

        if (list != null) {
            ExecutionListModel[] executionListModelArray = list.toArray(new ExecutionListModel[list.size()]);
            Arrays.sort(executionListModelArray, ExecutionListModel.getPriorityComparator());
            list = Arrays.asList(executionListModelArray);

            for (ExecutionListModel executionListModel : list) {
                try {
                    listOfExecutionList.addExecutionList(executionListModel);
                }
                catch (TreatmentException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                }
            }
        }
    }


    private void quitCommand() {
        if (modified) {
            int result = JOptionPane.showInternalConfirmDialog(frame,
                                                               "Voulez vous enregistrer les modifications ?",
                                                               "Demande de confirmation",
                                                               JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (result == JOptionPane.YES_OPTION) {
                if (saveListOfExecution()) {
                    frame.dispose();
                }
                else {
                    return;
                }
            }
        }
        frame.dispose();
    }


    private static String getToken(String key, int pos) {
        String[] stringTokens = key.split("\\$");
        return stringTokens[pos];
    }


    private String getKeyFromComboxValue() {
        return Integer.toString(getSelectedRepositoryId()) + '$' + getSelectedFamilyId();
    }


    private String getKeyFromOldComboxValue() {
        return Integer.toString(repositoryFamilyPanel.getOldRepository()) + '$'
               + repositoryFamilyPanel.getOldFamily();
    }


    public int getSelectedRepositoryId() {
        return repositoryFamilyPanel.getSelectedRepositoryId();
    }


    private int getSelectedFamilyId() {
        return repositoryFamilyPanel.getSelectedFamilyId();
    }


    private boolean saveListOfExecution() {
        int repositoryId;
        int familyId;

        listOfExecutionListMap.put(getKeyFromComboxValue(), listOfExecutionList);
        repoTreatmentListMap.put(getKeyFromComboxValue(), repositoryTreatmentList);

        if (getSelectedFamilyId() == 0) {
            JOptionPane.showInternalMessageDialog(frame, "Aucune famille n'est définie !",
                                                  "Erreur de paramétrage", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        for (Entry<String, ExecutionListStoreHelper> entry : listOfExecutionListMap.entrySet()) {
            repositoryId = Integer.parseInt(getToken(entry.getKey(), REPO.value));
            familyId = Integer.parseInt(getToken(entry.getKey(), FAMILY.value));

            ExecutionListStoreHelper executionListStoreHelper = entry.getValue();
            List<ExecutionListModel> tmpList = new ArrayList<ExecutionListModel>();

            int priority = 1;
            for (ExecutionListModel executionListModel : executionListStoreHelper.getRepository()) {
                executionListModel.setPriority(priority);
                tmpList.add(executionListModel);
                priority++;
            }

            try {
                TreatmentClientHelper.saveExecutionListModel(ctxt, repositoryId, tmpList, familyId);
                for (ExecListModelToDelete execListModelToDelete : executionListStoreHelper.getExecListsToDelete()) {
                    String name = execListModelToDelete.getExecutionListModel().getName().trim();
                    DependencyClientHelper.deleteDependencyPrincOrDep(ctxt,
                                                                      execListModelToDelete.getRepositoryId(),
                                                                      name);
                }
            }
            catch (RequestException ex) {
                enableSaveButton(true);
                GuiUtils.showErrorDialog(frame, getClass(), "Problème de sauvegarde de la liste", ex);
                return false;
            }
        }
        enableSaveButton(false);
        return true;
    }


    private void enableSaveButton(boolean bb) {
        this.modified = bb;
        saveButton.setEnabled(bb);
    }


    private void fromTrtToListButtonActionPerformed() {
        if (getSelectedFamilyId() == 0) {
            JOptionPane.showInternalMessageDialog(frame,
                                                  "Aucune famille n'est définie pour le référenciel sélectionné.",
                                                  "Erreur de paramétrage",
                                                  JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (listOfExecutionJList.getModel().getSize() == 0) {
            JOptionPane.showInternalMessageDialog(frame,
                                                  "Veuillez créer une liste de traitements svp",
                                                  "Erreur de paramétrage", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ExecutionListModel trtExecMod = (ExecutionListModel)listOfExecutionJList.getSelectedValue();
        if (trtExecMod == null) {
            JOptionPane.showInternalMessageDialog(frame,
                                                  "Veuillez sélectionner la liste de traitements de destination svp",
                                                  "Erreur de paramétrage",
                                                  JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (guiRepoTreatmentList.getSelectedValue() != null) {
            Runnable runnable = new Runnable() {
                public void run() {
                    addUsertTreatmentToList();
                    oneListToAnother(guiRepoTreatmentList, guiTreatments);
                    deleteCommentTextArea();
                    enableSaveButton(true);
                    updateRepositoryGuiComponent();
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }


    private void fromListToTrtButtonActionPerformed() {
        if (guiTreatments.getSelectedValue() != null) {
            Runnable runnable = new Runnable() {
                public void run() {
                    deleteUserTreatmentToList();
                    oneListToAnother(guiTreatments, guiRepoTreatmentList);
                    updatePriorities();
                    deleteCommentTextArea();
                    enableSaveButton(true);
                    updateRepositoryGuiComponent();
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }


    private void listOfExecutionJListActionPerformed() {
        ExecutionListModel trt = (ExecutionListModel)listOfExecutionJList.getSelectedValue();

        if (trt != null) {
            updateExecutionList(trt);
        }
        else {
            ((DefaultListModel)guiTreatments.getModel()).removeAllElements();
            int selectedRepositoryId = getSelectedRepositoryId();
            initRepositoryTreatmentList(selectedRepositoryId);
        }
        filterChanged();
    }


    private void upPriorityButtonActionPerformed() {
        modifyPriority(true);
    }


    private void downPriorityButtonActionPerformed() {
        modifyPriority(false);
    }


    private void fillCommentArea(final JList jlist) {
        Runnable runnable = new Runnable() {
            public void run() {
                int index = jlist.getSelectedIndex();
                if (index != -1) {
                    UserTreatment userTrt = (UserTreatment)jlist.getModel().getElementAt(index);
                    commentTextArea.setText(userTrt.getComment());
                    commentTextArea.setCaretPosition(0);
                }
                else {
                    deleteCommentTextArea();
                }
            }
        };
        SwingUtilities.invokeLater(runnable);
    }


    private void deleteCommentTextArea() {
        commentTextArea.setText("");
    }


    private void updatePriorities() {
        ExecutionListModel trtExecMod = (ExecutionListModel)listOfExecutionJList.getSelectedValue();
        for (int i = 0; i < guiTreatments.getModel().getSize(); i++) {
            UserTreatment trt = (UserTreatment)guiTreatments.getModel().getElementAt(i);
            trtExecMod.addUserTreatment(trt, i);
        }
    }


    private void modifyPriority(boolean isUpPriority) {
        int badIndex = 0;
        int newIndex;

        if (!isUpPriority) {
            badIndex = guiTreatments.getModel().getSize() - 1;
        }

        Object[] srcValues = guiTreatments.getSelectedValues();
        if (srcValues.length == 0) {
            return;
        }
        if (srcValues.length > 1) {
            ErrorDialog.show(frame, "Erreur de priorité ", "Veuillez ne sélectionner qu'un seul traitement");
            return;
        }

        UserTreatment selectedValue = (UserTreatment)srcValues[0];
        int selectedIndex = guiTreatments.getSelectedIndex();
        if (selectedIndex == badIndex) {
            return;
        }
        if (isUpPriority) {
            newIndex = selectedIndex - 1;
        }
        else {
            newIndex = selectedIndex + 1;
        }

        UserTreatment prevValue = (UserTreatment)guiTreatments.getModel().getElementAt(newIndex);
        ((DefaultListModel)guiTreatments.getModel()).setElementAt(selectedValue, newIndex);
        ((DefaultListModel)guiTreatments.getModel()).setElementAt(prevValue, selectedIndex);

        ExecutionListModel executionListModel = (ExecutionListModel)listOfExecutionJList.getSelectedValue();
        executionListModel.addUserTreatment(selectedValue, newIndex);
        executionListModel.addUserTreatment(prevValue, selectedIndex);
        guiTreatments.setSelectedIndex(newIndex);
        enableSaveButton(true);
    }


    private void createListeners() {
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                quitCommand();
            }
        });
        deleteListByRepoAndFamilyButton.addActionListener(new DeleteListByRepoAndFamilyAction());
        copyTreatmentsToOtherRepo.addActionListener(new CopyTreatmentsFromRepoToRepo());
        repositoryFamilyPanel.addRepositoryEventListener(new ComboUpdateEventListener() {
            public void executeUpdate() {
                if (repositoryFamilyPanel.getSelectedRepositoryId() != 0) {
                    updateLocalList();
                    try {
                        String repositoryId = String.valueOf(getSelectedRepositoryId());
                        String repositoryName = RepositoryClientHelper.getRepositoryName(ctxt, repositoryId);
                        exportParamButton.setToolTipText(
                              "Exporter le paramétrage des listes de traitements de " + repositoryName);
                    }
                    catch (RequestException ex) {
                        GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                    }
                }
            }
        });
        repositoryFamilyPanel.addFamilyEventListener(new ComboUpdateEventListener() {
            public void executeUpdate() {
                if (repositoryFamilyPanel.getSelectedFamilyId() != 0) {
                    updateLocalList();
                }
            }
        });
        filterTextField.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            protected void actionPerformed(DocumentEvent evt) {
                filterChanged();
            }
        });
        listOfExecutionJList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    listOfExecutionJListActionPerformed();
                }
            }
        });
        fromTrtToListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                fromTrtToListButtonActionPerformed();
            }
        });
        fromListToTrtButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                fromListToTrtButtonActionPerformed();
            }
        });
        addListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                addExecutionListActionPerformed();
            }
        });
        deleteListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                deleteExecutionListActionPerformed();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                saveListOfExecution();
            }
        });
        exportParamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exportParamAction();
            }
        });
        importParamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                importParamAction();
            }
        });
        downPriorityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                downPriorityButtonActionPerformed();
            }
        });
        upPriorityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                upPriorityButtonActionPerformed();
            }
        });
        guiRepoTreatmentList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    fillCommentArea(guiRepoTreatmentList);
                }
            }
        });
        guiTreatments.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    fillCommentArea(guiTreatments);
                }
            }
        });
        alphaSortRepoTreatmentListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                initRepositoryTreatmentList(getSelectedRepositoryId());
                listOfExecutionJListActionPerformed();
            }
        });
        alphaSortListOfExecutionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                initListOfExecutionListModel();
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                quitCommand();
            }
        });

        InputMap inputMap = frame.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        frame.getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                quitCommand();
            }
        });
    }


    private String showChooserForImport(String title) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle(title);
        jFileChooser.addChoosableFileFilter(new XmlFileFilter());

        int result = jFileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            return jFileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }


    private void exportParamAction() {
        if (modified) {
            int result = JOptionPane.showInternalConfirmDialog(frame,
                                                               "Les modifications vont être enregistrées. Voulez vous continuez ?",
                                                               "Demande de confirmation",
                                                               JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.NO_OPTION) {
                return;
            }
            if (result == JOptionPane.YES_OPTION) {
                if (!saveListOfExecution()) {
                    return;
                }
            }
        }
        exportParam();
    }


    private void exportParam() {
        FileWriter fileWriter = null;
        try {
            int repositoryId = getSelectedRepositoryId();
            String repositoryName = RepositoryClientHelper.getRepositoryName(ctxt,
                                                                             String.valueOf(repositoryId));
            String path = GuiUtils.showChooserForExport(repositoryName + "_param.xml",
                                                        "Sauvegarde du paramétrage des listes de traitements pour '"
                                                        + repositoryName + "'",
                                                        "xml",
                                                        "xml",
                                                        frame);
            if (path == null) {
                return;
            }
            String paramXml = ExecutionListClientHelper.executionListParamExport(ctxt, repositoryId);
            fileWriter = new FileWriter(path);
            fileWriter.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + paramXml);
            JOptionPane.showMessageDialog(frame,
                                          String.format("Le paramétrage des listes de traitements liées"
                                                        + " à %s a bien été exporté.",
                                                        repositoryName),
                                          "Récapitulatif", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
        finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                }
                catch (IOException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                }
            }
        }
    }


    private void importParamAction() {
        if (modified) {
            int result = JOptionPane.showInternalConfirmDialog(frame,
                                                               "Les modifications vont être enregistrées. Voulez vous continuez ?",
                                                               "Demande de confirmation",
                                                               JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.NO_OPTION) {
                return;
            }
            if (result == JOptionPane.YES_OPTION) {
                if (!saveListOfExecution()) {
                    return;
                }
            }
        }
        importParam();
    }


    private void importParam() {
        try {
            String filePath = showChooserForImport("Chargement du paramétrage des listes de traitements");
            if (filePath == null) {
                return;
            }
            String content = FileUtil.loadContent(new File(filePath));
            ExecListParamImportReport report = ExecutionListClientHelper.executionListParamImport(ctxt,
                                                                                                  content,
                                                                                                  true);
            if (report.hasError()) {
                switch (report.getErrorType()) {
                    case FAMILY_DONT_EXIST:
                        throw new TreatmentException(report.getErrorMessage() + " :\n"
                                                     + report.getMissingFamilyList());
                    case EXECUTION_LIST_ALLREADY_EXIST:
                        throw new TreatmentException(report.getErrorMessage() + " :\n"
                                                     + report.getAllreadyExistExecutionList());
                    case NO_FAMILY:
                        throw new TreatmentException(report.getErrorMessage());
                    case NO_ERROR:
                        break;
                }
            }
            else {
                JOptionPane.showMessageDialog(frame, "Le paramétrage a bien été importé.", "Récapitulatif",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
            load();
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur survenue lors de l'import du paramétrage",
                                     ex);
        }
    }


    private void deleteExecutionListActionPerformed() {
        try {
            localSaveCurrentList();
            ExecutionListModel executionListModel = (ExecutionListModel)listOfExecutionJList
                  .getSelectedValue();
            if (executionListModel != null) {
                int result = JOptionPane.showInternalConfirmDialog(frame,
                                                                   "Voulez vous vraiment supprimer cette liste de traitements ?",
                                                                   "Confirmation de suppression",
                                                                   JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    listOfExecutionList.deleteExecutionList(getSelectedRepositoryId(), executionListModel);
                    ((DefaultListModel)listOfExecutionJList.getModel())
                          .removeElementAt(listOfExecutionJList.getSelectedIndex());
                    listOfExecutionJListActionPerformed();
                    enableSaveButton(true);
                }
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(),
                                     "Impossible de supprimer la liste de traitements", ex);
        }
    }


    private void addExecutionListActionPerformed() {
        try {
            Map<String, String> familiesMap =
                  FamilyClientHelper.getFamilyByRepositoryId(ctxt, getSelectedRepositoryId());
            if (familiesMap.isEmpty()) {
                JOptionPane.showInternalMessageDialog(frame,
                                                      "Il n'y a pas de famille pour le référentiel de traitement "
                                                      + repositoryFamilyPanel.getSelectedRepositoryName()
                                                      + " !\nVeuillez en créer au moins une.",
                                                      frame.getTitle(),
                                                      JOptionPane.ERROR_MESSAGE);
                return;
            }

            localSaveCurrentList();

            CreateExecutionListDialog createExecutionListDialog =
                  new CreateExecutionListDialog(new ExecListParamWindowAdapter(), frame);
            String executionListName = createExecutionListDialog.input();
            if (executionListName != null) {
                ExecutionListModel executionListModel = new ExecutionListModel();
                executionListModel.setName(executionListName);
                listOfExecutionList.addExecutionList(executionListModel);
                ((DefaultListModel)listOfExecutionJList.getModel()).addElement(executionListModel);
                initListOfExecutionListModel();
                listOfExecutionJList.setSelectedValue(executionListModel, true);
                enableSaveButton(true);
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Impossible d'afficher la fenêtre", ex);
        }
    }


    public boolean isAlreadyExistExecutionList(String executionListName) throws RequestException {
        int repositoryId = getSelectedRepositoryId();
        List<ExecutionListDB> executionLists = TreatmentClientHelper.getExecListsByNameAndRepo(ctxt,
                                                                                               executionListName,
                                                                                               repositoryId);
        if (!executionLists.isEmpty()) {
            return true;
        }

        for (Entry<String, ExecutionListStoreHelper> entry : listOfExecutionListMap.entrySet()) {
            String keyTemp = getToken(entry.getKey(), REPO.value);
            if (keyTemp.equals(Integer.toString((repositoryId)))) {
                ExecutionListStoreHelper executionListStoreHelper = entry.getValue();
                for (ExecutionListModel executionListModel : executionListStoreHelper.getRepository()) {
                    if (executionListModel.getName().equals(executionListName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private void initRepositoryTreatmentList(int repositoryIdSelected) {
        try {
            repositoryTreatmentList = TreatmentClientHelper.getAllTreatments(ctxt, repositoryIdSelected);
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
            frame.dispose();
        }
        updateRepositoryGuiComponent();
    }


    private void updateRepositoryGuiComponent() {
        DefaultListModel srcListModel = (DefaultListModel)guiRepoTreatmentList.getModel();
        srcListModel.clear();

        if (alphaSortRepoTreatmentListButton.isSelected()) {
            if (repositoryTreatmentList != null) {
                UserTreatment[] repoTreatmentListArray = repositoryTreatmentList
                      .toArray(new UserTreatment[repositoryTreatmentList.size()]);
                Arrays.sort(repoTreatmentListArray, new Comparator<UserTreatment>() {
                    public int compare(UserTreatment ut1, UserTreatment ut2) {
                        return ut1.getId().compareToIgnoreCase(ut2.getId());
                    }
                });
                repositoryTreatmentList = Arrays.asList(repoTreatmentListArray);
            }
        }
        String filter = filterTextField.getText().trim();
        if (repositoryTreatmentList != null) {
            DefaultListModel targetListModel = (DefaultListModel)guiTreatments.getModel();

            for (UserTreatment userTrt : repositoryTreatmentList) {
                if (filter.length() != 0) {
                    if (filter.startsWith("*")) {
                        if (userTrt.getId().toLowerCase().contains(filter.substring(1).toLowerCase())) {
                            addToModel(srcListModel, targetListModel, userTrt);
                        }
                    }
                    else {
                        if (userTrt.getId().toLowerCase().startsWith(filter.toLowerCase())) {
                            addToModel(srcListModel, targetListModel, userTrt);
                        }
                    }
                }
                else {
                    addToModel(srcListModel, targetListModel, userTrt);
                }
            }
        }
    }


    private static void addToModel(DefaultListModel srcListModel,
                                   DefaultListModel targetListModel,
                                   UserTreatment userTrt) {
        if (!targetListModel.contains(userTrt)) {
            srcListModel.addElement(userTrt);
        }
    }


    private void updateExecutionList(ExecutionListModel trtEx) {
        Map<UserTreatment, Integer> priorityMap = trtEx.getPriorityMap();
        DefaultListModel execListModel = (DefaultListModel)guiTreatments.getModel();

        execListModel.removeAllElements();
        DefaultListModel trtListModel = (DefaultListModel)guiRepoTreatmentList.getModel();
        trtListModel.removeAllElements();

        for (UserTreatment trti : repositoryTreatmentList) {
            boolean hasElement = false;

            for (UserTreatment item : priorityMap.keySet()) {
                if ((item.getId().equals(trti.getId()))) {
                    hasElement = true;
                    break;
                }
            }

            if (!hasElement) {
                trtListModel.addElement(trti);
            }
        }

        if (priorityMap != null) {
            for (int i = 0; i < priorityMap.size(); i++) {
                UserTreatment trt = trtEx.getTreatmentByPriority(i);

                if (trt != null) {
                    execListModel.addElement(trt);
                }
            }
        }
    }


    private void initListOfExecutionListModel() {
        DefaultListModel listModel = (DefaultListModel)listOfExecutionJList.getModel();
        listModel.clear();
        List<ExecutionListModel> rep = listOfExecutionList.getRepository();

        if (alphaSortListOfExecutionButton.isSelected()) {
            ExecutionListModel[] repArray = rep.toArray(new ExecutionListModel[rep.size()]);
            Arrays.sort(repArray, new Comparator<ExecutionListModel>() {
                public int compare(ExecutionListModel elm1, ExecutionListModel elm2) {
                    return elm1.getName().compareToIgnoreCase(elm2.getName());
                }
            });
            rep = Arrays.asList(repArray);
        }
        for (ExecutionListModel executionListModel : rep) {
            listModel.addElement(executionListModel);
        }
    }


    private static void oneListToAnother(JList srcList, JList targetList) {
        if (srcList.getSelectedIndex() >= 0) {
            DefaultListModel targetModel = (DefaultListModel)targetList.getModel();
            DefaultListModel srcModel = (DefaultListModel)srcList.getModel();

            Object[] srcValues = srcList.getSelectedValues();
            for (Object srcValue : srcValues) {
                UserTreatment usrTrt = (UserTreatment)srcValue;
                targetModel.addElement(usrTrt);
            }

            for (Object srcValue : srcValues) {
                srcModel.removeElement(srcValue);
            }
        }
    }


    private void addUsertTreatmentToList() {
        if (guiRepoTreatmentList.getSelectedIndex() >= 0) {
            Object[] srcValues = guiRepoTreatmentList.getSelectedValues();
            ExecutionListModel trtExecMod = (ExecutionListModel)listOfExecutionJList.getSelectedValue();

            if (trtExecMod != null) {
                for (Object srcValue : srcValues) {
                    UserTreatment usrTrt = (UserTreatment)srcValue;
                    trtExecMod.addUserTreatment(usrTrt);
                }
            }
        }
    }


    private void deleteUserTreatmentToList() {
        if (guiTreatments.getSelectedIndex() >= 0) {
            Object[] srcValues = guiTreatments.getSelectedValues();
            ExecutionListModel trtExecMod = (ExecutionListModel)listOfExecutionJList.getSelectedValue();

            for (Object srcValue : srcValues) {
                UserTreatment usrTrt = (UserTreatment)srcValue;
                trtExecMod.removeUserTreatment(usrTrt);
            }
        }
    }


    private static class TrtListCellRenderer extends JLabel implements ListCellRenderer {
        private DefaultListCellRenderer renderer = new DefaultListCellRenderer();


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            return renderer.getListCellRendererComponent(list, getRendererValue(value),
                                                         index, isSelected, cellHasFocus);
        }


        private static Object getRendererValue(Object value) {
            UserTreatment usrt = (UserTreatment)value;
            return usrt.getId();
        }
    }

    private static class ListOfExecutionCellRenderer extends JLabel implements ListCellRenderer {
        private DefaultListCellRenderer renderer = new DefaultListCellRenderer();


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            return renderer.getListCellRendererComponent(list, getRendererValue(value),
                                                         index, isSelected, cellHasFocus);
        }


        private static Object getRendererValue(Object value) {
            if (value != null) {
                ExecutionListModel usrt = (ExecutionListModel)value;
                return usrt.getName();
            }
            return "";
        }
    }

    private class CopyTreatmentsFromRepoToRepo implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                SelectRepositoryDialog selectRepositoryDialog =
                      new SelectRepositoryDialog(ctxt, frame, getSelectedRepositoryId());
                int repositoryId = selectRepositoryDialog.input();
                if (repositoryId != 0) {
                    int result =
                          JOptionPane.showInternalConfirmDialog(frame,
                                                                "Les modifications déjà effectuées sur les listes de traitements doivent être enregistrées avant de poursuivre. \nVoulez vous les enregistrer ?",
                                                                "Confirmation",
                                                                JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        if (saveListOfExecution()) {
                            try {
                                TreatmentClientHelper.copyExecutionListsFromRepoToRepo(ctxt,
                                                                                       getSelectedRepositoryId(),
                                                                                       repositoryId);
                                initAll();
                            }
                            catch (RequestException ex) {
                                GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                            }
                        }
                    }
                }
            }
            catch (RequestException ex) {
                GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
            }
        }
    }

    private class DeleteListByRepoAndFamilyAction implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            int result = JOptionPane.showInternalConfirmDialog(frame,
                                                               "Voulez vous supprimer toutes les listes de traitements créées pour ce référentiel de traitement et cette famille ?",
                                                               "Confirmation de suppression",
                                                               JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                result = JOptionPane.showInternalConfirmDialog(frame,
                                                               "Les modifications déjà effectuées sur les listes de traitements doivent être enregistrées avant de poursuivre. \nVoulez vous les enregistrer ?",
                                                               "Confirmation",
                                                               JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    if (saveListOfExecution()) {
                        try {
                            TreatmentClientHelper.deleteExecutionLists(ctxt, getSelectedRepositoryId(),
                                                                       getSelectedFamilyId());
                            initAll();
                        }
                        catch (RequestException ex) {
                            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                        }
                    }
                }
            }
        }
    }

    public class ExecListParamWindowAdapter {
        public boolean isAlreadyExistExecutionList(String executionListName) throws RequestException {
            return ExecutionListParamWindow.this.isAlreadyExistExecutionList(executionListName);
        }
    }

    private static class XmlFileFilter extends FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return file.isDirectory() || filename.endsWith(".xml");
        }


        @Override
        public String getDescription() {
            return "*.xml";
        }
    }

    enum TokenType {
        REPO(0),
        FAMILY(1);

        private int value;


        TokenType(int value) {
            this.value = value;
        }
    }
}
