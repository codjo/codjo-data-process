package net.codjo.dataprocess.gui.launcher;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
/**
 *
 */
public class ExecutionListWaitingWindow implements PropertyChangeListener {
    private static final String EXECUTING = " (en cours ...)";
    private static final String STOP_PENDING = " (arrêt en cours ...)";
    private JPanel mainPanel;
    private JLabel execListNameLabel;
    private JTextArea infoTextArea;
    private JButton stopButton;
    private JScrollPane infoScrollPane;
    private JButton workingButton;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JInternalFrame frame;
    private PropertyChangeSupport propertyChangeSupport;
    private String currentExecListTreatmentName;
    private StopActionListener stopActionListener = new StopActionListener();


    public ExecutionListWaitingWindow(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
        buidGui();
        infoScrollPane.setVisible(false);
    }


    public JInternalFrame getFrame() {
        return frame;
    }


    private void buidGui() {
        frame = new JInternalFrame("Veuillez patienter - Exécution en cours ...", true, false, false, false);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        stopButton.addActionListener(stopActionListener);
        frame.setContentPane(mainPanel);
        frame.setBackground(mainPanel.getBackground());
        frame.pack();
    }


    public void start() {
        frame.setVisible(true);
    }


    public void stop(boolean error) {
        if (error) {
            String message = "Exécution en échec.";
            Color color = new Color(204, 0, 0);
            execListNameLabel.setForeground(color);
            execListNameLabel.setText(message);
            topPanel.setBackground(color);
            bottomPanel.setBackground(color);
            workingButton.setVisible(false);
        }
        else {
            String message = "Succès.";
            execListNameLabel.setForeground(new Color(0, 204, 0));
            execListNameLabel.setText(message);
            frame.dispose();
        }
        frame.setTitle("Exécution terminée.");
        stopButton.setEnabled(true);
        stopButton.setText("Fermer");
        stopButton.setIcon(UIManager.getIcon("dataprocess.exit"));
        stopButton.setPreferredSize(new Dimension(85, 25));
        stopButton.removeActionListener(stopActionListener);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                frame.dispose();
            }
        });
    }


    public void propertyChange(final PropertyChangeEvent evt) {
        String type = evt.getPropertyName();
        if (ExecutionListProgress.INFO_EVENT.equals(type)) {
            infoScrollPane.setVisible(true);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    infoTextArea.setText(infoTextArea.getText() + "\n" + evt.getNewValue());
                }
            });
        }
        else if (ExecutionListProgress.EXECUTIONLIST_EVENT.equals(type)) {
            ExecutionListModel executionListModel = (ExecutionListModel)evt.getNewValue();
            currentExecListTreatmentName = executionListModel.getName();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    infoTextArea.setText("");
                    infoScrollPane.setVisible(false);
                    execListNameLabel.setText(currentExecListTreatmentName + EXECUTING);
                }
            });
        }
    }


    private void stopCommand() {
        frame.setTitle("Veuillez patienter - Arrêt en cours ...");
        stopButton.setText("Arrêt en cours");
        stopButton.setPreferredSize(new Dimension(130, 25));
        stopButton.setEnabled(false);
        execListNameLabel.setText(currentExecListTreatmentName + STOP_PENDING);
        propertyChangeSupport.firePropertyChange(ExecutionListProgress.STOP_EVENT, null, null);
    }


    private class StopActionListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            stopCommand();
        }
    }
}
