/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.configuration;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.TreatmentModelCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ArgModelHelper;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.dataprocess.common.util.CommonUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.LocalGuiContext;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
/**
 *
 */
public class ConfigurationDialog extends JDialog {
    private JScrollPane trtScrollPane = new JScrollPane();
    private JScrollPane trtConfigScrollPane = new JScrollPane();
    private JList guiTreatmentList = new JList();
    private JPanel mainPanel = new JPanel();
    private JPanel topPanel = new JPanel();
    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();
    private BorderLayout borderLayout = new BorderLayout();
    private BorderLayout mainBorderLayout = new BorderLayout();
    private JLabel execListLabel = new JLabel("           Liste de traitements: ");
    private JLabel repositoryLabel = new JLabel("Référentiel: ");
    private JLabel execListTitleLabel = new JLabel();
    private JLabel repositoryNameLabel = new JLabel();
    private ExecutionListModel execList;
    private DefaultListModel guiTreatmentModel = new DefaultListModel();
    private ConfigurationTable trtConfigurationTable = new ConfigurationTable(0, 0, true);
    private int repositoryId;
    private LocalGuiContext ctxt;
    private JButton quitButton = new JButton();
    private Map<String, String> localContext = new HashMap<String, String>();
    private boolean modified = false;
    private List<String> exclude;
    private Map<String, TreatmentModel> treatmentModelMap = new HashMap<String, TreatmentModel>();
    private DataProcessContext dataProcessContext;


    public ConfigurationDialog(LocalGuiContext ctxt,
                               int repositoryId,
                               DataProcessContext dataProcessContext,
                               ExecutionListModel execList,
                               UserTreatment currUsrTrt,
                               List<String> exclude) {
        super(ctxt.getMainFrame(), "Configuration des paramètres", true);
        this.execList = execList;
        this.repositoryId = repositoryId;
        this.ctxt = ctxt;
        this.exclude = exclude;
        this.dataProcessContext = dataProcessContext;

        dataProcessContext.putAllInMap(localContext);
        initGui();
        selectUserTreatment(currUsrTrt);
    }


    private void initGui() {
        setSize(900, 500);
        initTopPanel();
        initLeftPanel();
        initRightPanel();
        initBottomPanel();
        initMainPanel();

        getContentPane().setLayout(borderLayout);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        mainPanel.getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                quitCommand();
            }
        });
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                quitCommand();
            }
        });
        if (guiTreatmentList.getModel().getSize() != 0) {
            guiTreatmentList.setSelectedIndex(0);
        }
        trtConfigurationTable.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent evt) {
                modified = true;
            }
        });
    }


    private void initTopPanel() {
        topPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        execListTitleLabel.setText(execList.getName());
        String repositoryName = "";
        try {
            repositoryName = RepositoryClientHelper.getRepositoryName(ctxt, Integer.toString(repositoryId));
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(this, getClass(), "Erreur interne", ex);
        }
        repositoryNameLabel.setText(repositoryName);
        execListTitleLabel.setFont(execListTitleLabel.getFont().deriveFont(Font.BOLD));
        repositoryNameLabel.setFont(repositoryNameLabel.getFont().deriveFont(Font.BOLD));
        topPanel.add(repositoryLabel);
        topPanel.add(repositoryNameLabel);
        topPanel.add(execListLabel);
        topPanel.add(execListTitleLabel);
        topPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(134, 134, 134)));
    }


    private void initLeftPanel() {
        initGuiTreatmentList();
        leftPanel.setLayout(new BorderLayout());
        TitledBorder leftTitledBorder = new TitledBorder(" Traitements ");
        leftPanel.setBorder(leftTitledBorder);
        leftPanel.add(trtScrollPane, BorderLayout.CENTER);
        guiTreatmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trtScrollPane.getViewport().add(guiTreatmentList, null);
        guiTreatmentList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    guiTreatmentListSelectionned();
                }
            }
        });
    }


    private void initRightPanel() {
        trtConfigurationTable.initConfigurationTableModel();
        rightPanel.setLayout(new BorderLayout());
        TitledBorder rightTitledBorder = new TitledBorder(" Paramètres ");
        rightPanel.setBorder(rightTitledBorder);
        trtConfigScrollPane.getViewport().add(trtConfigurationTable, null);
        rightPanel.add(trtConfigScrollPane, BorderLayout.CENTER);
    }


    private void initBottomPanel() {
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quitCommand();
            }
        });
        quitButton.setIcon(UIManager.getIcon("dataprocess.exit"));
        GuiUtils.setMaxSize(quitButton, 36, 25);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(quitButton);
    }


    private void quitCommand() {
        if (!modified) {
            dispose();
            return;
        }
        updateLocalContext();
        int result = JOptionPane.showConfirmDialog(this,
                                                   "Voulez vous prendre en compte les modifications ?",
                                                   "Demande de confirmation",
                                                   JOptionPane.YES_NO_CANCEL_OPTION,
                                                   JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }
        if (result == JOptionPane.YES_OPTION) {
            actionQuit();
        }
        else {
            dispose();
        }
    }


    private void selectUserTreatment(UserTreatment currUsrTrt) {
        if (currUsrTrt != null) {
            for (int i = 0; i < guiTreatmentList.getModel().getSize(); i++) {
                UserTreatment usrTrt = (UserTreatment)guiTreatmentList.getModel().getElementAt(i);
                if (currUsrTrt.equals(usrTrt)) {
                    guiTreatmentList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }


    private void actionQuit() {
        dataProcessContext.putAll(localContext);
        dispose();
    }


    private void initMainPanel() {
        mainPanel.setLayout(mainBorderLayout);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }


    private void initGuiTreatmentList() {
        guiTreatmentList.setModel(guiTreatmentModel);
        guiTreatmentList.setSelectionModel(new DefaultListSelectionModel());
        guiTreatmentList.setCellRenderer(new ConfigurationDialog.TrtListCellRenderer());

        List<UserTreatment> sortedList = execList.getSortedTreatmentList();
        for (int i = 0; i < sortedList.size(); i++) {
            UserTreatment usrTrt = sortedList.get(i);
            guiTreatmentModel.add(i, usrTrt);
        }
    }


    private void updateArgumentTable(TreatmentModel treatmentModel) {
        int rowNumber = treatmentModel.getArguments().getArgs().size();
        DefaultTableModel model = (DefaultTableModel)trtConfigurationTable.getModel();
        trtConfigurationTable.removeAllRows();
        if (rowNumber != 0) {
            List<ArgModel> arguments = treatmentModel.getArguments().getArgs();

            List<String> variables = new ArrayList<String>();
            for (ArgModel argument : arguments) {
                if (!argument.isFunctionValue()) {
                    add(model, variables, argument.getValue().trim());
                }
                else {
                    List<String> params = argument.getFunctionParams();
                    for (String param : params) {
                        add(model, variables, param);
                    }
                }
            }
        }
        trtConfigurationTable.setModel(model);
    }


    private void add(DefaultTableModel model, List<String> variables, String value) {
        if (ArgModelHelper.isGlobalValue(value)) {
            if (!exclude.contains(value)) {
                String data = ArgModelHelper.getGlobalValue(value);
                addToModel(data, data, variables, model);
            }
        }
        else if (ArgModelHelper.isLocalValue(value)) {
            value = ArgModelHelper.getLocalValue(value);
            addToModel(CommonUtils.localify(repositoryId, execList.getName(), value),
                       DataProcessConstants.LOCAL_VISIBILITY + value, variables, model);
        }
    }


    private void addToModel(String data, String view, List<String> variables, DefaultTableModel tableModel) {
        Object[] objectsTable = new Object[]{view, localContext.get(data)};
        if (!variables.contains(view)) {
            tableModel.addRow(objectsTable);
            variables.add(view);
        }
    }


    private void guiTreatmentListSelectionned() {
        updateLocalContext();
        UserTreatment userTreatment = getCurrentTreatmentModel();
        try {
            updateArgumentTable(getTreatmentModel(userTreatment.getId()));
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(this, getClass(), "Traitement inexistant", ex);
        }
    }


    private TreatmentModel getTreatmentModel(String treatmentId) throws RequestException {
        TreatmentModel treatmentModel = treatmentModelMap.get(treatmentId);
        if (treatmentModel == null) {
            String treatmentModelXml = TreatmentClientHelper.getTreatmentModel(ctxt,
                                                                               String.valueOf(repositoryId),
                                                                               treatmentId);
            treatmentModel = TreatmentModelCodec.decode(treatmentModelXml);
            treatmentModelMap.put(treatmentId, treatmentModel);
        }
        return treatmentModel;
    }


    private UserTreatment getCurrentTreatmentModel() {
        return (UserTreatment)guiTreatmentList.getModel().getElementAt(guiTreatmentList.getSelectedIndex());
    }


    private void updateLocalContext() {
        ArgList argList = trtConfigurationTable.getArglist();
        List<ArgModel> arguments = argList.getArgs();
        for (ArgModel argument : arguments) {
            String name = argument.getName();
            String value = argument.getValue();
            if (name.startsWith(DataProcessConstants.LOCAL_VISIBILITY)) {
                name = CommonUtils.localify(repositoryId, execList.getName(), name.substring(
                      DataProcessConstants.LOCAL_VISIBILITY.length()).trim());
            }
            localContext.put(name, (value == null) ? "" : value);
        }
    }


    private class TrtListCellRenderer implements ListCellRenderer {
        private DefaultListCellRenderer renderer = new DefaultListCellRenderer();


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)renderer.getListCellRendererComponent(list,
                                                                         getRendererValue(value), index,
                                                                         isSelected,
                                                                         cellHasFocus);
            UserTreatment usrTrt = (UserTreatment)value;
            try {
                TreatmentModel trtMod = getTreatmentModel(usrTrt.getId());
                if (trtMod.isConfigurable(exclude)) {
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                }
            }
            catch (RequestException e) {
                ;
            }
            return label;
        }


        private Object getRendererValue(Object value) {
            UserTreatment usrt = (UserTreatment)value;
            return usrt.getId() + "      ";
        }
    }
}
