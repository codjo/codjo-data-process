package net.codjo.dataprocess.gui.launcher;
import net.codjo.agent.UserId;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.client.UtilsClientHelper;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.TreatmentModelCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.RepositoryException;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ArgModelHelper;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.ExecutionListStoreHelper;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.dataprocess.common.util.CommonUtils;
import net.codjo.dataprocess.gui.launcher.configuration.ConfigurationDialog;
import net.codjo.dataprocess.gui.launcher.configuration.ConfigurationTable;
import net.codjo.dataprocess.gui.launcher.result.ControlResultGui;
import net.codjo.dataprocess.gui.launcher.result.ResultTreatmentGui;
import net.codjo.dataprocess.gui.launcher.result.TreatmentResultListener;
import net.codjo.dataprocess.gui.launcher.result.TreatmentStepGui;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiPlugin;
import net.codjo.dataprocess.gui.repository.XmlTreatmentLogic;
import net.codjo.dataprocess.gui.selector.RepositoryFamilyPanel;
import net.codjo.dataprocess.gui.util.ComboUpdateEventListener;
import net.codjo.dataprocess.gui.util.DataProcessGuiEvent;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.gui.toolkit.util.Modal;
import net.codjo.gui.toolkit.waiting.WaitingPanel;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import static net.codjo.dataprocess.common.DataProcessConstants.DONE;
import static net.codjo.dataprocess.common.DataProcessConstants.FAILED;
import static net.codjo.dataprocess.common.DataProcessConstants.FAILED_DEPENDENCY;
import static net.codjo.dataprocess.common.DataProcessConstants.KEY_CONFIRMATION_OF_TREATMENT_EXEC;
import static net.codjo.dataprocess.common.DataProcessConstants.LOCAL_VISIBILITY;
import static net.codjo.dataprocess.common.DataProcessConstants.MapCommand.GET;
import static net.codjo.dataprocess.common.DataProcessConstants.MapCommand.PUT;
import static net.codjo.dataprocess.common.DataProcessConstants.MapCommand.REMOVE;
import static net.codjo.dataprocess.common.DataProcessConstants.TO_DO;
/**
 *
 */
public class NewExecutionListLauncherWindow {
    private JPanel mainPanel;
    private JButton reinitExecutionButton;
    private JButton execButton;
    private JButton configButton;
    private JList trtList;
    private JButton quitButton;
    private RequestTable requestTable;
    private ConfigurationTable argumentsTable;
    private JList resultTreatmentList;
    private JPanel topPanel;
    private JPanel resultPanel;
    private RepositoryFamilyPanel repositoryFamilyPanel;
    private JPopupMenu popupMenuTrtList = new JPopupMenu();
    private JPopupMenu popupMenuResultTrtList = new JPopupMenu();
    private WaitingPanel waitingPanel = new WaitingPanel("Chargement des données en cours ...");
    private TreatmentStepGui currentTreatmentStepGui;
    private Map<ExecutionListModel, List<ResultTreatmentGui>> panelList
          = new HashMap<ExecutionListModel, List<ResultTreatmentGui>>();
    private Map<Integer, ExecutionListModel> executionListsMap = new HashMap<Integer, ExecutionListModel>();
    private LocalGuiContext ctxt;
    private boolean enableRepositoryCombobox = true;
    private DataProcessGuiPlugin dataProcessGuiPlugin;
    private UserId userId;
    private RepositoryUpdateEventListener repositoryUpdateEventListener;
    private Map<String, TreatmentModel> treatmentModelMap = new HashMap<String, TreatmentModel>();
    private WindowState windowState;
    private JInternalFrame frame;

    public static final String WINDOW_TITLE = "Exécution des listes de traitements";
    public static final String READONLY = "READONLY";
    public static final String TITLE_READONLY = "TITLE_READONLY";


    public NewExecutionListLauncherWindow(LocalGuiContext ctxt,
                                          UserId userId,
                                          boolean readOnly,
                                          DataProcessGuiPlugin dataProcessGuiPlugin,
                                          final JInternalFrame frame) {
        this.ctxt = ctxt;
        this.frame = frame;
        this.userId = userId;
        this.dataProcessGuiPlugin = dataProcessGuiPlugin;
        windowState = new WindowState();
        windowState.setReadOnly(readOnly);
        enableRepository(false);

        repositoryFamilyPanel = new RepositoryFamilyPanel(ctxt, enableRepositoryCombobox, true);
        buildGui();
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }


    public void loadData() {
        waitingPanel.exec(new Runnable() {
            public void run() {
                repositoryFamilyPanel.load();
                treatmentModelMap.clear();
                initAll();
                updateExecButton();
                updatePopupMenuTrtList();
            }
        });
    }


    private void updateExecButton() {
        windowState.setReadOnly(false);
        if (isImportWizardOpen()) {
            windowState.setReadOnly(true);
            execButton.setEnabled(false);
            reinitExecutionButton.setEnabled(false);
            frame.setTitle(WINDOW_TITLE + " - En lecture seule car l'assistant d'import est ouvert");
            return;
        }
        if (ctxt.hasProperty(READONLY) && "TRUE".equals(ctxt.getProperty(READONLY))) {
            windowState.setReadOnly(true);
            execButton.setEnabled(false);
            reinitExecutionButton.setEnabled(false);
            if (ctxt.hasProperty(TITLE_READONLY)) {
                frame.setTitle(WINDOW_TITLE + ctxt.getProperty(TITLE_READONLY));
            }
            return;
        }

        try {
            String currentRepositoryId = getCurrentRepository();
            String userName = UtilsClientHelper.cmdMapServer(ctxt, GET, currentRepositoryId, "");
            String systemUserName = System.getProperty("user.name");
            if (!systemUserName.equals(userName) && !"null".equals(userName)) {
                windowState.setReadOnly(true);
                execButton.setEnabled(false);
                reinitExecutionButton.setEnabled(false);
                frame.setTitle(WINDOW_TITLE + String.format(
                      " - En lecture seule : '%s' utilise déjà cette fenêtre pour le référentiel de traitement %s",
                      userName,
                      RepositoryClientHelper.getRepositoryName(ctxt, currentRepositoryId)));
                return;
            }
            else {
                frame.setTitle(WINDOW_TITLE);
                UtilsClientHelper.cmdMapServer(ctxt, PUT, currentRepositoryId, systemUserName);
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }

        if (execButton.isEnabled()) {
            enableImportWizard(false);
        }
    }


    private String getCurrentRepository() {
        return GuiContextUtils.getCurrentRepository(ctxt);
    }


    private boolean isImportWizardOpen() {
        JInternalFrame[] internalFrames = ctxt.getDesktopPane().getAllFrames();
        for (JInternalFrame internalFrame : internalFrames) {
            String frameTitle = internalFrame.getTitle().toLowerCase();
            if (frameTitle.contains("assistant") && frameTitle.contains("import")) {
                return true;
            }
        }
        return false;
    }


    private void enableImportWizard(boolean enable) {
        JFrame mainFrame = ctxt.getMainFrame();
        if (mainFrame != null) {
            JMenuBar menuBar = mainFrame.getJMenuBar();
            activateMenu(menuBar, enable, "assistant", "import");
        }
    }


    private static void activateMenu(JMenuBar menuBar, boolean enable, String menuName1, String menuName2) {
        int menuCount = menuBar.getMenuCount();
        for (int index = 0; index < menuCount; index++) {
            JMenu menu = menuBar.getMenu(index);
            String menuText = menu.getText().toLowerCase();
            if (menuText.contains(menuName1) && menuText.contains(menuName2)) {
                menu.setEnabled(enable);
            }
            if (menu.getSubElements().length > 0) {
                desactivate(menu.getSubElements()[0], enable, menuName1, menuName2);
            }
        }
    }


    private static void desactivate(MenuElement menuElement,
                                    boolean enable,
                                    String menuName1,
                                    String menuName2) {
        MenuElement[] subElements = menuElement.getSubElements();
        for (MenuElement subElement : subElements) {
            if (subElement instanceof JPopupMenu) {
                JPopupMenu popupMenu = (JPopupMenu)subElement;
                desactivate(popupMenu, enable, menuName1, menuName2);
            }
            else if (subElement instanceof JMenu) {
                JMenu subMenu = (JMenu)subElement;
                String menuText = subMenu.getText().toLowerCase();
                if (menuText.contains(menuName1) && menuText.contains(menuName2)) {
                    subMenu.setEnabled(enable);
                }
                if (subMenu.getSubElements().length > 0) {
                    desactivate(subMenu.getSubElements()[0], enable, menuName1, menuName2);
                }
            }
            else if (subElement instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem)subElement;

                String menuText = menuItem.getText().toLowerCase();
                if (menuText.contains(menuName1) && menuText.contains(menuName2)) {
                    menuItem.setEnabled(enable);
                }
            }
        }
    }


    protected void enableRepository(boolean enable) {
        enableRepositoryCombobox = enable;
    }


    public TreatmentStepGui getCurrentTreatmentStepGui() {
        return currentTreatmentStepGui;
    }


    private void initRequestTable() {
        requestTable.setPreference(getRequestTablePreference());
        requestTable.getSelectionModel().addListSelectionListener(new ExecutionListListener());
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        new ManagerStateModifier(ctxt, new ExecListLauncherWindowAdapter(), dataProcessGuiPlugin);

        DefaultTableCellRenderer statusRenderer = new MyDefaultTableCellRenderer();
        statusRenderer.setHorizontalAlignment(JLabel.CENTER);
        requestTable.getColumn("Statut").setCellRenderer(statusRenderer);
    }


    private static Preference getRequestTablePreference() {
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        Column priority = new Column("priority", "Priorité", 50, 0, 50);
        priority.setSorter("Numeric");
        columns.add(priority);
        columns.add(new Column("executionListName", "Titre", 400, 0, 400));
        columns.add(new Column("status", "Statut", 53, 0, 53));
        Column executionDate = new Column("executionDate", "Date et heure", 135, 0, 135);
        executionDate.setFormat("timestamp(dd-MM-yyyy HH:mm:ss)");
        columns.add(executionDate);

        preference.setColumns(columns);
        preference.setSelectAllId("selectAllExecutionListStatusByRepositoryAndFamily");
        return preference;
    }


    private void refreshRequestTable(int repositoryId, int familyId) {
        FieldsList fieldsList = new FieldsList();
        fieldsList.addField("repositoryId", Integer.toString(repositoryId));
        fieldsList.addField("familyId", Integer.toString(familyId));
        requestTable.setSelector(fieldsList);

        try {
            requestTable.load();
        }
        catch (RequestException ex) {
            Log.error(getClass(), ex);
        }
    }


    private void initAll() {
        int selectedRepositoryId = 0;
        int selectedFamilyId = 0;
        String currentRepositoryId = getCurrentRepository();

        try {
            selectedRepositoryId = Integer.parseInt(currentRepositoryId);
            selectedFamilyId = repositoryFamilyPanel.getSelectedFamilyId();
        }
        catch (Exception ex) {
            Log.error(getClass(), ex);
        }
        initExecutionListsMap(selectedRepositoryId, selectedFamilyId);
        if (selectedRepositoryId != 0) {
            refreshRequestTable(selectedRepositoryId, selectedFamilyId);
        }
        trtList.setModel(new DefaultListModel());
        trtList.setSelectionModel(new DefaultListSelectionModel());

        emptyResultPanel();
        panelList.clear();
    }


    private void initExecutionListsMap(int repositoryId, int familyId) {
        ExecutionListStoreHelper executionListStoreHelper = new ExecutionListStoreHelper();
        try {
            List<ExecutionListModel> list = TreatmentClientHelper.getExecutionListModel(ctxt,
                                                                                        repositoryId,
                                                                                        familyId);
            for (ExecutionListModel executionListModel : list) {
                try {
                    executionListStoreHelper.addExecutionList(executionListModel);
                }
                catch (TreatmentException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                }
            }
            List<ExecutionListModel> rep = executionListStoreHelper.getRepository();
            for (ExecutionListModel executionListModel : rep) {
                executionListsMap.put(executionListModel.getId(), executionListModel);
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
    }


    private void quitCommand() {
        enableImportWizard(true);
        String currentRepositoryId = getCurrentRepository();
        try {
            if (System.getProperty("user.name").equals(
                  UtilsClientHelper.cmdMapServer(ctxt, GET, currentRepositoryId, ""))) {
                UtilsClientHelper.cmdMapServer(ctxt, REMOVE, currentRepositoryId, "");
            }
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
        ctxt.removeObserver(repositoryUpdateEventListener);
        frame.dispose();
    }


    private void reinitExecutionListActionPerformed() {
        try {
            int result = JOptionPane.showInternalConfirmDialog(frame,
                                                               "Voulez-vous vraiment réinitialiser l'état des listes de traitement pour le référentiel de traitement courant ?",
                                                               "Demande de confirmation",
                                                               JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                TreatmentClientHelper.reinitExecutionList(ctxt, getCurrentRepository());
                initAll();
            }
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
    }


    private void configButtonActionPerformed() {
        try {
            Row[] rows = requestTable.getAllSelectedDataRows();
            if (rows.length != 0) {
                ExecutionListModel executionListModel =
                      executionListsMap.get(new Integer(rows[0].getFieldValue("executionListId")));
                int repositoryId = Integer.valueOf(getCurrentRepository());
                ConfigurationDialog configurationDialog =
                      new ConfigurationDialog(ctxt, repositoryId,
                                              dataProcessGuiPlugin.getConfiguration().getDataProcessContext(),
                                              executionListModel,
                                              (UserTreatment)trtList.getSelectedValue(),
                                              dataProcessGuiPlugin.getConfiguration().getGlobalParameters());
                GuiUtil.centerWindow(configurationDialog);
                configurationDialog.setVisible(true);
                argumentsTable.refresh(repositoryId,
                                       executionListModel.getName(),
                                       dataProcessGuiPlugin.getConfiguration().getDataProcessContext());
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame.getDesktopPane(), getClass(), "Impossible d'afficher la fenêtre",
                                     ex);
        }
    }


    private void updateExecutionList(ExecutionListModel trtEx) {
        DefaultListModel trtListModel = (DefaultListModel)trtList.getModel();
        trtListModel.clear();

        List<UserTreatment> sortedList = trtEx.getSortedTreatmentList();
        for (int i = 0; i < sortedList.size(); i++) {
            UserTreatment usrTrt = sortedList.get(i);
            trtListModel.add(i, usrTrt);
        }

        try {
            updateConfigurationTable();
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Traitement inexistant", ex);
        }
    }


    private void createListeners() {
        resultTreatmentList.addListSelectionListener(new ResultTreatmentListSelectionListener());
        trtList.addMouseListener(new TrtListMouseAdapter(ctxt,
                                                         trtList,
                                                         popupMenuTrtList,
                                                         dataProcessGuiPlugin.getConfiguration()
                                                               .getMaintenanceRoleName(),
                                                         windowState, frame));
        resultTreatmentList.addMouseListener(new ResultTreatmentMouseAdapter(resultTreatmentList,
                                                                             popupMenuResultTrtList));
        repositoryFamilyPanel.addFamilyEventListener(new ComboUpdateEventListener() {
            public void executeUpdate() {
                initAll();
            }
        });
        trtList.addListSelectionListener(new TreatmentListSelectionListener());
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                quitCommand();
            }
        });
        reinitExecutionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                reinitExecutionListActionPerformed();
            }
        });
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                quitCommand();
            }
        });
        execButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (requestTable.getAllSelectedDataRows().length != 0) {
                    askConfirmationOfExecution();
                }
            }
        });
        configButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                configButtonActionPerformed();
            }
        });
    }


    private void buildGui() {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setGlassPane(waitingPanel);

        topPanel.setLayout(new BorderLayout());
        topPanel.add(repositoryFamilyPanel, BorderLayout.WEST);

        resultTreatmentList.setModel(new DefaultListModel());
        resultTreatmentList.setSelectionModel(new DefaultListSelectionModel());
        resultTreatmentList.setCellRenderer(new ResultTreatmentCellRenderer());
        resultTreatmentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        initRequestTable();
        repositoryUpdateEventListener = new RepositoryUpdateEventListener();
        ctxt.addObserver(repositoryUpdateEventListener);

        popupMenuTrtList.add(new ShowTreatmentAction(trtList, ctxt, frame));
        popupMenuResultTrtList.add(new ResultTreatmentAction());

        createListeners();
        frame.setRequestFocusEnabled(true);

        createExecListScrollPane();
        createTreatmentsPanel();
        createButtonPanel();
        createBottomPanel();
        createPanelArguments();

        frame.setFrameIcon(UIManager.getIcon("dataprocess.execute"));
        GuiUtils.setSize(frame, 1150, 750);
    }


    private void createExecListScrollPane() {
        requestTable.getTableHeader().setReorderingAllowed(false);
        requestTable.getColumn(requestTable.getColumnName(0)).setMaxWidth(10);
        requestTable.getColumn(requestTable.getColumnName(1)).setMaxWidth(10);
        requestTable.getColumn(requestTable.getColumnName(3)).setMaxWidth(10);
        requestTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }


    private void createPanelArguments() {
        argumentsTable.initConfigurationTableModel();
    }


    private void createTreatmentsPanel() {
        trtList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trtList.setCellRenderer(new TrtListCellRenderer(ctxt,
                                                        treatmentModelMap,
                                                        dataProcessGuiPlugin.getConfiguration()
                                                              .getGlobalParameters()));
    }


    private void createBottomPanel() {
        reinitExecutionButton.setEnabled(!windowState.isReadOnly());
    }


    private void createButtonPanel() {
        execButton.setEnabled(!windowState.isReadOnly());
        configButton.setEnabled(!windowState.isReadOnly());
    }


    private void updatePopupMenuTrtList() {
        if (ctxt.getUser().isInRole(dataProcessGuiPlugin.getConfiguration().getMaintenanceRoleName())
            && !windowState.isReadOnly()) {
            enablePopupMenuTrtList(true);
        }
        else {
            enablePopupMenuTrtList(false);
        }
    }


    private void enablePopupMenuTrtList(boolean enable) {
        for (int i = 0; i < popupMenuTrtList.getComponentCount(); i++) {
            Component component = popupMenuTrtList.getComponent(i);
            if (component instanceof JMenuItem) {
                Action action = ((JMenuItem)component).getAction();
                action.setEnabled(enable);
            }
        }
    }


    private void initResultTabPane(ExecutionListModel executionListModel) {
        List<UserTreatment> sortedList = executionListModel.getSortedTreatmentList();
        String title = executionListModel.getName();
        currentTreatmentStepGui = new TreatmentStepGui(ctxt,
                                                       executionListModel.getName(),
                                                       title,
                                                       TreatmentStepGui.TABLE_MODE);
        addToPanelList(executionListModel, currentTreatmentStepGui);
        for (UserTreatment usrTrt : sortedList) {
            if (usrTrt.getResultTable() != null
                && !"".equals(usrTrt.getResultTable().getTable())
                && usrTrt.getResultTable().getTable() != null) {
                title = String.format("%s     [%s]", usrTrt.getTitle(), usrTrt.getResultTable().getTable());
                ControlResultGui crtlResultGui = new ControlResultGui(ctxt,
                                                                      usrTrt.getResultTable(),
                                                                      executionListModel.getName(),
                                                                      title);
                addToPanelList(executionListModel, crtlResultGui);
            }
        }
    }


    private void addToPanelList(ExecutionListModel executionListModel,
                                ResultTreatmentGui resultTreatmentGui) {
        if (panelList.get(executionListModel) == null) {
            panelList.put(executionListModel, new ArrayList<ResultTreatmentGui>());
        }
        panelList.get(executionListModel).add(resultTreatmentGui);
    }


    public TreatmentStepGui addResultTab(String titleParam, ExecutionListModel executionListModel) {
        currentTreatmentStepGui = new TreatmentStepGui(ctxt,
                                                       executionListModel.getName(),
                                                       titleParam,
                                                       TreatmentStepGui.TABLE_MODE);
        if (executionListModel.getName() != null) {
            addToPanelList(executionListModel, currentTreatmentStepGui);
        }
        else {
            addToPanelList(executionListModel, currentTreatmentStepGui);
            showOnResultPanel(currentTreatmentStepGui);
            currentTreatmentStepGui.load();
        }
        ((DefaultListModel)resultTreatmentList.getModel()).addElement(currentTreatmentStepGui);
        resultTreatmentList.setSelectedValue(currentTreatmentStepGui, true);
        return currentTreatmentStepGui;
    }


    private void showOnResultPanel(ResultTreatmentGui resultTreatmentGui) {
        resultPanel.invalidate();
        resultPanel.removeAll();
        resultPanel.add(resultTreatmentGui.getMainComponent(), BorderLayout.CENTER);
        resultPanel.validate();
        resultPanel.repaint();
    }


    private void askConfirmationOfExecution() {
        String repositoryTech = dataProcessGuiPlugin.getConfiguration().getRepositoryTech();
        String treatment = null;
        try {
            treatment = TreatmentClientHelper.getConfigProperty(ctxt, KEY_CONFIRMATION_OF_TREATMENT_EXEC);
            int repositoryId = RepositoryClientHelper.getRepositoryIdFromName(ctxt, repositoryTech);
            String treatmentResult = TreatmentClientHelper.proceedTreatment(ctxt, repositoryId,
                                                                            treatment,
                                                                            new DataProcessContext(),
                                                                            60 * 10 * 1000);
            if (Log.isInfoEnabled()) {
                Log.info(getClass(),
                         String.format("Exécution du traitement technique '%s' du repository '%s'."
                                       + "Il retourne : %s", treatment, repositoryTech, treatmentResult));
            }
            if (!"FALSE".equalsIgnoreCase(treatmentResult)) {
                int result = JOptionPane.showInternalConfirmDialog(frame, treatmentResult + "\n"
                                                                          + "Voulez-vous vraiment lancer l'exécution des traitements sélectionnés ?",
                                                                   "Demande de confirmation avant exécution",
                                                                   JOptionPane.YES_NO_OPTION,
                                                                   JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    executeTreatments();
                }
            }
            else {
                executeTreatments();
            }
        }
        catch (RequestException ex) {
            if (Log.isInfoEnabled()) {
                Log.info(getClass(), String.format("Le traitement technique '%s' est inexistant dans le "
                                                   + "repository '%s' ou bien il comporte des erreurs :\n%s",
                                                   treatment, repositoryTech, ex.getLocalizedMessage()));
            }
            executeTreatments();
        }
        catch (RepositoryException ex) {
            if (Log.isInfoEnabled()) {
                Log.info(getClass(), String.format("Le repository '%s' est inexistant donc le traitement"
                                                   + " technique '%s' n'a pas été exécuté :\n%s",
                                                   repositoryTech,
                                                   treatment,
                                                   ex.getLocalizedMessage()));
            }
            executeTreatments();
        }
    }


    private void executeTreatments() {
        Row[] rows = requestTable.getAllSelectedDataRows();
        if (rows.length != 0) {
            Arrays.sort(rows, new ExecutionListComparator());
            frame.repaint();
            ExecutionListProgress progress = new ExecutionListProgress(ctxt, rows, executionListsMap,
                                                                       new ExecListLauncherWindowAdapter());
            progress.start();
        }
    }


    public int[] preProceed(ExecutionListModel executionListModel,
                            TreatmentResultListener treatmentResultListener) {
        updateExecutionList(executionListModel);
        initResultTabPane(executionListModel);
        currentTreatmentStepGui.buildTreatmentResult(executionListModel, treatmentResultListener);
        return requestTable.getSelectedRows();
    }


    public void postProceed(boolean hasWarning, int[] selectedRows) throws RequestException {
        requestTable.load();
        requestTable.setRowSelectionInterval(selectedRows[0], selectedRows[selectedRows.length - 1]);
    }


    public void lockWindow(boolean lock) {
        execButton.setEnabled(!lock);
        configButton.setEnabled(!lock);
        quitButton.setEnabled(!lock);
        reinitExecutionButton.setEnabled(!lock);
        repositoryFamilyPanel.setEnabled(!lock);
        requestTable.setEnabled(!lock);
    }


    private void updateConfigurationTable() throws RequestException {
        DefaultTableModel model = (DefaultTableModel)argumentsTable.getModel();
        argumentsTable.removeAllRows();

        DefaultListModel trtListModel = (DefaultListModel)trtList.getModel();

        List<String> allVariables = new ArrayList<String>();
        DataProcessContext dataProcessContext =
              dataProcessGuiPlugin.getConfiguration().getDataProcessContext();

        for (int i = 0; i < trtListModel.getSize(); i++) {
            UserTreatment userTreatment = (UserTreatment)trtListModel.getElementAt(i);
            TreatmentModel treatmentModel = getTreatmentModel(ctxt,
                                                              treatmentModelMap,
                                                              userTreatment.getId());
            List<ArgModel> arguments = treatmentModel.getArguments().getArgs();
            for (ArgModel argument : arguments) {
                if (!argument.isFunctionValue()) {
                    stuff(model, allVariables, dataProcessContext, argument.getValue());
                }
                else {
                    List<String> params = argument.getFunctionParams();
                    for (String param : params) {
                        stuff(model, allVariables, dataProcessContext, param);
                    }
                }
            }
        }
    }


    private void stuff(DefaultTableModel model,
                       List<String> allVariables,
                       DataProcessContext dataProcessContext,
                       String value) {
        if (ArgModelHelper.isGlobalValue(value)) {
            String globalValue = ArgModelHelper.getGlobalValue(value);
            if (!allVariables.contains(globalValue)) {
                allVariables.add(globalValue);
                String valueFromContext = dataProcessContext.getProperty(globalValue);
                addToModel(globalValue, valueFromContext, model);
            }
        }
        else if (ArgModelHelper.isLocalValue(value)) {
            value = ArgModelHelper.getLocalValue(value);
            String localValue = LOCAL_VISIBILITY + value;
            if (!allVariables.contains(localValue)) {
                allVariables.add(localValue);
                String valueFromContext = dataProcessContext.getProperty(localify(value));
                addToModel(localValue, valueFromContext, model);
            }
        }
    }


    private static void addToModel(String value, String valueFromContext, DefaultTableModel model) {
        Object[] objectsTable = new Object[]{value, valueFromContext};
        model.addRow(objectsTable);
    }


    private String localify(String value) {
        Row[] rows = requestTable.getAllSelectedDataRows();
        ExecutionListModel execList =
              executionListsMap.get(new Integer(rows[0].getFieldValue("executionListId")));
        return CommonUtils.localify(Integer.parseInt(getCurrentRepository()),
                                    execList.getName(),
                                    value.trim());
    }


    public void showExecListModelGuiResult(ExecutionListModel executionListModel) {
        if (executionListModel == null) {
            return;
        }
        emptyResultPanel();
        List<ResultTreatmentGui> resultTreatmentGuiList = panelList.get(executionListModel);
        ResultTreatmentGui lastTreatmentStepGui = null;
        if (resultTreatmentGuiList != null) {
            for (ResultTreatmentGui aResultTreatmentGui : resultTreatmentGuiList) {
                if (aResultTreatmentGui.getExecutionListName().equals(executionListModel.getName())) {
                    if (aResultTreatmentGui instanceof TreatmentStepGui) {
                        lastTreatmentStepGui = aResultTreatmentGui;
                    }
                    ((DefaultListModel)resultTreatmentList.getModel()).addElement(aResultTreatmentGui);
                    aResultTreatmentGui.load();
                }
            }
        }
        if (lastTreatmentStepGui != null) {
            resultTreatmentList.setSelectedValue(lastTreatmentStepGui, true);
        }
        else {
            resultTreatmentList.setSelectedIndex(resultTreatmentList.getModel().getSize() - 1);
        }
    }


    private ExecutionListModel getSelectedExecutionListModel() {
        if (requestTable.getSelectedRowCount() != 0) {
            Row[] rows = requestTable.getAllSelectedDataRows();
            return executionListsMap.get(new Integer(rows[0].getFieldValue("executionListId")));
        }
        return null;
    }


    private class ExecutionListListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent evt) {
            if (evt.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel lsm = (ListSelectionModel)evt.getSource();
            if (!lsm.isSelectionEmpty()) {
                Row[] rows = requestTable.getAllSelectedDataRows();
                if (rows.length == 1) {
                    updateExecutionList(executionListsMap.get(
                          new Integer(rows[0].getFieldValue("executionListId"))));
                    showExecListModelGuiResult(getSelectedExecutionListModel());
                }
                else {
                    empty();
                }
            }
            else {
                empty();
            }
        }
    }


    private void empty() {
        DefaultListModel trtListModel = (DefaultListModel)trtList.getModel();
        trtListModel.clear();
        argumentsTable.removeAllRows();
        emptyResultPanel();
    }


    private void emptyResultPanel() {
        ((DefaultListModel)resultTreatmentList.getModel()).clear();
        resultPanel.invalidate();
        resultPanel.removeAll();
        resultPanel.validate();
        resultPanel.repaint();
    }


    private static class TrtListCellRenderer extends JLabel implements ListCellRenderer {
        private LocalGuiContext ctxt;
        private Map<String, TreatmentModel> treatmentModelMap;
        private List<String> globalParameters;
        private DefaultListCellRenderer renderer = new DefaultListCellRenderer();


        private TrtListCellRenderer(LocalGuiContext ctxt,
                                    Map<String, TreatmentModel> treatmentModelMap,
                                    List<String> globalParameters) {

            this.ctxt = ctxt;
            this.treatmentModelMap = treatmentModelMap;
            this.globalParameters = globalParameters;
        }


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)renderer.getListCellRendererComponent(list,
                                                                         getRendererValue(value), index,
                                                                         isSelected,
                                                                         cellHasFocus);
            label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN,
                                   label.getFont().getSize()));

            UserTreatment usrTrt = (UserTreatment)list.getModel().getElementAt(index);
            try {
                TreatmentModel trtMod = getTreatmentModel(ctxt, treatmentModelMap, usrTrt.getId());
                if (trtMod.isConfigurable(globalParameters)) {
                    label.setFont(new Font(label.getFont().getFontName(), Font.BOLD,
                                           label.getFont().getSize()));
                }
            }
            catch (RequestException e) {
                ;
            }
            return label;
        }


        private static Object getRendererValue(Object value) {
            UserTreatment usrt = (UserTreatment)value;
            return usrt.getId();
        }
    }


    private static TreatmentModel getTreatmentModel(LocalGuiContext ctxt,
                                                    Map<String, TreatmentModel> treatmentModelMap,
                                                    String treatmentId) throws RequestException {
        TreatmentModel treatmentModel = treatmentModelMap.get(treatmentId);
        if (treatmentModel == null) {
            String treatmentModelXml =
                  TreatmentClientHelper.getTreatmentModel(ctxt,
                                                          GuiContextUtils.getCurrentRepository(ctxt),
                                                          treatmentId);
            treatmentModel = TreatmentModelCodec.decode(treatmentModelXml);
            treatmentModelMap.put(treatmentId, treatmentModel);
        }
        return treatmentModel;
    }


    private static final class ExecutionListComparator implements Comparator<Row> {
        public int compare(Row row1, Row row2) {
            int i1 = Integer.parseInt(row1.getFieldValue("priority"));
            int i2 = Integer.parseInt(row2.getFieldValue("priority"));
            if (i1 == i2) {
                return 0;
            }
            else if (i1 < i2) {
                return -1;
            }
            else {
                return 1;
            }
        }
    }

    private class TreatmentListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent evt) {
            if (!evt.getValueIsAdjusting()) {
                trtListSelectionned();
            }
        }


        private void trtListSelectionned() {
            int selectedIndex = trtList.getSelectedIndex();
            if (selectedIndex != -1) {
                UserTreatment usrTrt = (UserTreatment)trtList.getModel().getElementAt(selectedIndex);
                try {
                    updateConfigurationTable();
                    colorConfigurationTable(usrTrt);
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Traitement inexistant", ex);
                }
            }
            else {
                argumentsTable.setTrtMod(null);
                argumentsTable.repaint();
            }
        }


        private void colorConfigurationTable(UserTreatment userTreatment) throws RequestException {
            TreatmentModel treatmentModel = getTreatmentModel(ctxt, treatmentModelMap, userTreatment.getId());
            argumentsTable.setTrtMod(treatmentModel);
            argumentsTable.repaint();
        }
    }

    private class ResultTreatmentListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent evt) {
            if (!evt.getValueIsAdjusting()) {
                resultTrtListSelectionned();
            }
        }


        private void resultTrtListSelectionned() {
            int selectedIndex = resultTreatmentList.getSelectedIndex();
            if (selectedIndex != -1) {
                ResultTreatmentGui resultTreatmentGui = (ResultTreatmentGui)resultTreatmentList.getModel()
                      .getElementAt(selectedIndex);
                showOnResultPanel(resultTreatmentGui);
            }
            else {
                argumentsTable.setTrtMod(null);
                argumentsTable.repaint();
            }
        }
    }

    private static class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public void setValue(Object value) {
            int status = new Integer(value.toString());

            switch (status) {
                case TO_DO:
                    setText("A faire");
                    setBackground(Color.red);
                    break;
                case DONE:
                    setText("Fait");
                    setBackground(Color.green);
                    break;
                case FAILED:
                    setText("Echec");
                    setBackground(Color.orange);
                    break;
                case FAILED_DEPENDENCY:
                    setText("Dépend");
                    setBackground(Color.magenta);
                    break;
                default:
                    setText("N/A");
                    setBackground(Color.cyan);
            }
        }
    }

    private class RepositoryUpdateEventListener implements Observer {
        public void update(Observable obs, Object arg) {
            if (arg instanceof DataProcessGuiEvent) {
                final DataProcessGuiEvent event = (DataProcessGuiEvent)arg;
                String eventName = event.getName();
                if (DataProcessGuiEvent.POST_CHANGE_REPOSITORY_EVENT.equals(eventName)) {
                    waitingPanel.exec(new Runnable() {
                        public void run() {
                            GuiContextUtils.setCurrentRepository(ctxt, event.getValue().toString());
                            try {
                                repositoryFamilyPanel.setSelectedRepositoryId(ctxt,
                                                                              event.getValue().toString());
                                treatmentModelMap.clear();
                            }
                            catch (Exception ex) {
                                GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                            }
                            initAll();
                            argumentsTable.removeAllRows();
                            updateExecButton();
                            updatePopupMenuTrtList();
                        }
                    });
                }
            }
        }
    }

    private class ExecListLauncherWindowAdapter implements LauncherWindow {

        public void lockWindow(boolean bb) {
            NewExecutionListLauncherWindow.this.lockWindow(bb);
        }


        public int[] preProceed(ExecutionListModel executionListModel,
                                TreatmentResultListener treatmentResultListener) {
            return NewExecutionListLauncherWindow.this.preProceed(executionListModel,
                                                                  treatmentResultListener);
        }


        public void postProceed(boolean hasWarning, int[] selectedRows) throws RequestException {
            NewExecutionListLauncherWindow.this.postProceed(hasWarning, selectedRows);
        }


        public TreatmentStepGui getCurrentTreatmentStepGui() {
            return NewExecutionListLauncherWindow.this.getCurrentTreatmentStepGui();
        }


        public JInternalFrame getFrame() {
            return NewExecutionListLauncherWindow.this.frame;
        }


        public UserId getUserId() {
            return NewExecutionListLauncherWindow.this.userId;
        }


        public DataProcessGuiPlugin getDataProcessGuiPlugin() {
            return NewExecutionListLauncherWindow.this.dataProcessGuiPlugin;
        }


        public TreatmentStepGui addResultTab(String title, ExecutionListModel executionListModel) {
            return NewExecutionListLauncherWindow.this.addResultTab(title, executionListModel);
        }


        public ExecutionListModel getSelectedExecutionListModel() {
            return NewExecutionListLauncherWindow.this.getSelectedExecutionListModel();
        }


        public RequestTable getRequestTable() {
            return requestTable;
        }


        public void showExecListModelGuiResult(ExecutionListModel executionListModel) {
            NewExecutionListLauncherWindow.this.showExecListModelGuiResult(executionListModel);
        }


        public boolean isReadOnly() {
            return NewExecutionListLauncherWindow.this.windowState.isReadOnly();
        }
    }

    private static class ShowTreatmentAction extends AbstractAction {
        private JList trtList;
        private LocalGuiContext ctxt;
        private JInternalFrame frame;


        ShowTreatmentAction(JList trtList, LocalGuiContext ctxt, JInternalFrame frame) {
            this.trtList = trtList;
            this.ctxt = ctxt;
            this.frame = frame;
            putValue(NAME, "Modifier le traitement (format XML)");
            putValue(SHORT_DESCRIPTION, "Modifier le traitement (format XML)");
        }


        public void actionPerformed(ActionEvent evt) {
            if (trtList.getSelectedValue() == null) {
                return;
            }
            StringBuilder result = new StringBuilder("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
                                                     + "<root>");
            try {
                String treatmentModelXml = TreatmentClientHelper.getTreatmentModel(ctxt,
                                                                                   getCurrentRepository(),
                                                                                   ((UserTreatment)trtList.getSelectedValue())
                                                                                         .getId());
                result.append(treatmentModelXml);
            }
            catch (RequestException ex) {
                GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
            }
            result.append("\n</root>");
            XmlTreatmentLogic xmlTreatmentLogic = new XmlTreatmentLogic(getCurrentRepository(), ctxt, true,
                                                                        "Modification d'un traitement (format XML)");
            xmlTreatmentLogic.show();
            xmlTreatmentLogic.setContent(result.toString());
            GuiUtil.centerWindow(xmlTreatmentLogic.getFrame());
            Modal.applyModality(frame, xmlTreatmentLogic.getFrame());
        }


        private String getCurrentRepository() {
            return GuiContextUtils.getCurrentRepository(ctxt);
        }
    }

    private class ResultTreatmentAction extends AbstractAction {

        ResultTreatmentAction() {
            putValue(NAME, "Supprimer de l'affichage");
            putValue(SHORT_DESCRIPTION, "Supprimer de l'affichage");
        }


        public void actionPerformed(ActionEvent evt) {
            if (resultTreatmentList.getSelectedIndex() < 0) {
                return;
            }
            List<ResultTreatmentGui> resultTreatmentGuiList = panelList.get(getSelectedExecutionListModel());
            Object[] selectedValues = resultTreatmentList.getSelectedValues();
            for (Object selectedValue : selectedValues) {
                ResultTreatmentGui resultTreatmentGui = (ResultTreatmentGui)selectedValue;
                resultTreatmentGuiList.remove(resultTreatmentGui);
            }
            showExecListModelGuiResult(getSelectedExecutionListModel());
            resultPanel.invalidate();
            resultPanel.removeAll();
            resultPanel.validate();
            resultPanel.repaint();
        }
    }

    private static class TrtListMouseAdapter extends MouseAdapter {
        private LocalGuiContext ctxt;
        private JList trtList;
        private JPopupMenu popupMenuTrtList;
        private String maintenanceRoleName;
        private WindowState windowState;
        private JInternalFrame frame;


        private TrtListMouseAdapter(LocalGuiContext ctxt,
                                    JList trtList,
                                    JPopupMenu popupMenuTrtList,
                                    String maintenanceRoleName,
                                    WindowState windowState,
                                    JInternalFrame frame) {
            this.ctxt = ctxt;
            this.trtList = trtList;
            this.popupMenuTrtList = popupMenuTrtList;
            this.maintenanceRoleName = maintenanceRoleName;
            this.windowState = windowState;
            this.frame = frame;
        }


        @Override
        public void mousePressed(MouseEvent evt) {
            if (SwingUtilities.isRightMouseButton(evt)) {
                trtList.setSelectedIndex(trtList.locationToIndex(new Point(evt.getPoint())));
                if (evt.isPopupTrigger()) {
                    popupMenuTrtList.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        }


        @Override
        public void mouseClicked(MouseEvent evt) {
            if (ctxt.getUser().isInRole(maintenanceRoleName) && !windowState.isReadOnly()) {
                if (evt.getClickCount() == 2) {
                    new ShowTreatmentAction(trtList, ctxt, frame).actionPerformed(null);
                }
            }
        }


        @Override
        public void mouseReleased(MouseEvent ev) {
            if (ev.isPopupTrigger()) {
                popupMenuTrtList.show(ev.getComponent(), ev.getX(), ev.getY());
            }
        }
    }

    private static class ResultTreatmentMouseAdapter extends MouseAdapter {
        private JList trtList;
        private JPopupMenu popupMenuResultTreatmentList;


        private ResultTreatmentMouseAdapter(JList trtList,
                                            JPopupMenu popupMenuResultTreatmentList) {
            this.trtList = trtList;
            this.popupMenuResultTreatmentList = popupMenuResultTreatmentList;
        }


        @Override
        public void mousePressed(MouseEvent evt) {
            if (SwingUtilities.isRightMouseButton(evt)) {
                if (evt.isPopupTrigger()) {
                    popupMenuResultTreatmentList.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        }


        @Override
        public void mouseReleased(MouseEvent ev) {
            if (ev.isPopupTrigger()) {
                popupMenuResultTreatmentList.show(ev.getComponent(), ev.getX(), ev.getY());
            }
        }
    }

    private static class WindowState {
        private boolean readOnly;


        public void setReadOnly(boolean readOnly) {
            this.readOnly = readOnly;
        }


        public boolean isReadOnly() {
            return readOnly;
        }
    }

    private static class ResultTreatmentCellRenderer extends JLabel implements ListCellRenderer {
        private DefaultListCellRenderer renderer = new DefaultListCellRenderer();


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel jLabel = (JLabel)renderer.getListCellRendererComponent(list,
                                                                          getRendererValue(value), index,
                                                                          isSelected,
                                                                          cellHasFocus);
            jLabel.setFont(new Font(jLabel.getFont().getFontName(), Font.PLAIN,
                                    jLabel.getFont().getSize()));
            ResultTreatmentGui resultTreatmentGui = (ResultTreatmentGui)value;
            resultTreatmentGui.customizeTitle(jLabel);
            return jLabel;
        }


        private static Object getRendererValue(Object value) {
            return ((ResultTreatmentGui)value).getTitle();
        }
    }
}
