package net.codjo.dataprocess.gui.repository;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class XmlTreatmentWindow {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea xmlTreatmentsTextArea;


    public XmlTreatmentWindow(JInternalFrame frame) {
        frame.setContentPane(contentPane);
        frame.getRootPane().setDefaultButton(buttonOK);
    }


    public JPanel getMainPanel() {
        return contentPane;
    }


    public JButton getButtonOK() {
        return buttonOK;
    }


    public JButton getButtonCancel() {
        return buttonCancel;
    }


    public JTextArea getXmlTreatmentsTextArea() {
        return xmlTreatmentsTextArea;
    }
}
