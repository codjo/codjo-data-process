/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.fexplorer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
/**
 *
 */
public class FExplorerGui {
    private JPanel mainPanel;
    private JTextField relativePath;
    private JTextField realPath;
    private JTable dirTable;
    private JTable fileTable;
    private JComboBox blockSizeCombo;
    private JButton quitButton;
    private JButton downloadButton;


    public JComboBox getBlockSizeCombo() {
        return blockSizeCombo;
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }


    public JTextField getRelativePath() {
        return relativePath;
    }


    public JTextField getRealPath() {
        return realPath;
    }


    public JTable getDirTable() {
        return dirTable;
    }


    public JTable getFileTable() {
        return fileTable;
    }


    public JButton getQuitButton() {
        return quitButton;
    }


    public JButton getDownloadButton() {
        return downloadButton;
    }
}
