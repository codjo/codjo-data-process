package net.codjo.dataprocess.gui.repository;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ArgModelHelper;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.util.CommonUtils;
import net.codjo.dataprocess.gui.launcher.configuration.ConfigurationTable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class ArgumentDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private ConfigurationTable configurationTable;
    private JTextField timeoutTextField;
    private JButton annulerButton;
    private JLabel treatmentIdLabel;
    private JFrame frame;
    private TreatmentModel treatmentModel;
    private DataProcessContext dataProcessContext;
    private boolean cancel;
    private Map<String, String> prop = new HashMap<String, String>();


    public ArgumentDialog(JFrame frame,
                          DataProcessContext dataProcessContext,
                          TreatmentModel treatmentModel) {
        super(frame, true);
        this.frame = frame;
        this.treatmentModel = treatmentModel;
        this.dataProcessContext = dataProcessContext;

        dataProcessContext.putAllInMap(prop);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                onCancel();
            }
        });
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                onOK();
            }
        });
        annulerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                onCancel();
            }
        });
        treatmentIdLabel.setText(treatmentIdLabel.getText() + treatmentModel.getId());
        updateArgumentTable();

        InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        contentPane.getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                onCancel();
            }
        });
        pack();
    }


    public int getTimeout() {
        return Integer.parseInt(timeoutTextField.getText());
    }


    private void onCancel() {
        cancel = true;
        dispose();
    }


    private void onOK() {
        updateLocalContext();
        dataProcessContext.putAll(prop);
        dispose();
    }


    private void createUIComponents() {
        configurationTable = new ConfigurationTable(0, 0, true);
        configurationTable.initConfigurationTableModel();
    }


    private void updateArgumentTable() {
        int rowNumber = treatmentModel.getArguments().getArgs().size();
        DefaultTableModel model = (DefaultTableModel)configurationTable.getModel();
        configurationTable.removeAllRows();
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
        configurationTable.setModel(model);
    }


    private void add(DefaultTableModel model, List<String> variables, String value) {
        if (ArgModelHelper.isGlobalValue(value)) {
            String data = ArgModelHelper.getGlobalValue(value);
            addToModel(data, data, variables, model);
        }
        else if (ArgModelHelper.isLocalValue(value)) {
            value = ArgModelHelper.getLocalValue(value);
            addToModel(localify(value),
                       DataProcessConstants.LOCAL_VISIBILITY + value, variables, model);
        }
    }


    private static String localify(String value) {
        return CommonUtils.localify(1000, "XXXXXXXXXX", value);
    }


    private void addToModel(String data, String view, List<String> variables, DefaultTableModel tableModel) {
        Object[] objectsTable = new Object[]{view, prop.get(data)};
        if (!variables.contains(view)) {
            tableModel.addRow(objectsTable);
            variables.add(view);
        }
    }


    private void updateLocalContext() {
        ArgList argList = configurationTable.getArglist();
        List<ArgModel> arguments = argList.getArgs();
        for (ArgModel argument : arguments) {
            String name = argument.getName();
            String value = argument.getValue();
            if (name.startsWith(DataProcessConstants.LOCAL_VISIBILITY)) {
                name = localify(name.substring(DataProcessConstants.LOCAL_VISIBILITY.length()).trim());
            }
            prop.put(name, (value == null) ? "" : value);
        }
    }


    public boolean isCanceled() {
        return cancel;
    }


    public JTextField getTimeoutTextField() {
        return timeoutTextField;
    }
}
