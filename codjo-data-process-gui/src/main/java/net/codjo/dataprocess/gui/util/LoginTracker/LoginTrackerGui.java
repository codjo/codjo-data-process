package net.codjo.dataprocess.gui.util.LoginTracker;
import net.codjo.dataprocess.client.UserLoginTrackerClientHelper;
import net.codjo.dataprocess.common.util.UserLoginTracker;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
/**
 *
 */
public class LoginTrackerGui {
    private JInternalFrame frame;
    private MutableGuiContext ctxt;
    private int delay;
    private JPanel mainPanel;
    private JButton quitButton;
    private JTable usersJTable;
    private JButton refreshButton;


    LoginTrackerGui(MutableGuiContext ctxt, final JInternalFrame frame, int delay) {
        this.frame = frame;
        this.ctxt = ctxt;
        this.delay = delay;

        usersJTable.setModel(new MyDefaultTableModel());
        InputMap inputMap = frame.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        frame.getActionMap().put("cancel", new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                frame.dispose();
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                frame.dispose();
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                refreshData();
            }
        });
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
                refreshData();
            }
        });
    }


    private void refreshData() {
        DefaultTableModel tableModel = (DefaultTableModel)usersJTable.getModel();
        removeAllRows(tableModel);
        try {
            List<UserLoginTracker> userLoginTrackerList = UserLoginTrackerClientHelper.getUserList(ctxt);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            for (UserLoginTracker userLoginTracker : userLoginTrackerList) {
                Date date = dateFormat.parse(userLoginTracker.getDate());
                Date now = Calendar.getInstance().getTime();
                if (now.getTime() - date.getTime() <= delay) {
                    tableModel.addRow(new String[]{userLoginTracker.getUserName(),
                                                   userLoginTracker.getRepository(),
                                                   userLoginTracker.getHostname(),
                                                   userLoginTracker.getIpaddr()});
                }
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
    }


    private static void removeAllRows(DefaultTableModel tableModel) {
        while (tableModel.getRowCount() != 0) {
            tableModel.removeRow(0);
        }
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }


    private static class MyDefaultTableModel extends DefaultTableModel {
        private MyDefaultTableModel() {
            addColumn("Utilisateur");
            addColumn("Référentiel de traitements");
            addColumn("Hostname");
            addColumn("Adresse IP");
        }


        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
