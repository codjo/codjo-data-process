package net.codjo.dataprocess.gui.treatmenthelper;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.ListCodec;
import net.codjo.dataprocess.common.exception.RepositoryException;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.util.XMLUtils;
import net.codjo.dataprocess.gui.util.ErrorDialog;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.util.file.FileUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static net.codjo.dataprocess.common.DataProcessConstants.KEY_DATE_LAST_IMPORT_REPOSITORY;
/**
 *
 */
class TreatmentHelperGui implements PropertyChangeListener {
    private static final String XML_FILE_TYPE = ".xml";
    private static final String EXTENSION = ".repository.param";

    private JPanel mainPanel;
    private JList pathList;
    private JProgressBar progressBar;
    private JButton importAllButton;
    private JButton addPathButton;
    private JButton removePathButton;
    private JList guiRepositoryList;
    private JButton quitButton;
    private JButton addNewParamButton;
    private JButton removeParamButton;
    private JButton saveParamButton;
    private JLabel statusLabel;
    private String title;
    private MutableGuiContext ctxt;
    private RepositoryPreference repositoryPreference;
    private JInternalFrame frame;
    private static final int TIMEOUT = 60000 * 10;
    private boolean hasChanged;


    TreatmentHelperGui(MutableGuiContext ctxt, JInternalFrame frame) {
        this.frame = frame;
        this.ctxt = ctxt;
        this.title = frame.getTitle();
        repositoryPreference = loadRepositoryParam();
        repositoryPreference.addPropertyChangeListener(this);

        try {
            String dateLastImport =
                  TreatmentClientHelper.getConfigProperty(ctxt, KEY_DATE_LAST_IMPORT_REPOSITORY);
            if (dateLastImport != null) {
                frame.setTitle(title + " - Dernière import effectué le " + dateLastImport);
            }
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
        defineButtonActionListener();
        initGuiRepositoryList();

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                quitAction();
            }
        });

        InputMap inputMap = frame.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        frame.getActionMap().put("cancel", new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                quitAction();
            }
        });
    }


    private void refreshGuiRepositoryList() {
        DefaultListModel repositoryModel = new DefaultListModel();
        for (String repositoryName : repositoryPreference.getRepositoryNames()) {
            repositoryModel.addElement(repositoryName);
        }
        guiRepositoryList.setModel(repositoryModel);
        if (!repositoryModel.isEmpty()) {
            guiRepositoryList.setSelectedIndex(0);
        }
    }


    private void initGuiRepositoryList() {
        try {
            Map<String, String> repositoryNameMap = RepositoryClientHelper.getAllRepositoryNames(ctxt);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : repositoryNameMap.entrySet()) {
                if (!repositoryPreference.getRepositoryNames().contains(entry.getValue())) {
                    repositoryPreference.addRepository(entry.getValue());
                    sb.append(entry.getValue()).append(", ");
                }
            }

            DefaultListModel repositoryModel = new DefaultListModel();
            for (String repositoryName : repositoryPreference.getRepositoryNames()) {
                repositoryModel.addElement(repositoryName);
            }

            guiRepositoryList.setModel(repositoryModel);
            if (!repositoryModel.isEmpty()) {
                guiRepositoryList.setSelectedIndex(0);
            }
            if (sb.length() != 0) {
                sb.delete(sb.length() - ", ".length(), sb.length());
                String message =
                      "Ajout au paramétrage des référentiels de traitement non paramétrés suivants : " + sb;
                statusLabel.setText(message);
                statusLabel.setToolTipText(message);
                Log.info(getClass(), message);
            }
        }
        catch (RequestException ex) {
            Log.error(getClass(), "Erreur interne", ex);
        }
    }


    private void defineButtonActionListener() {
        addPathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doAddRepositoryPath();
            }
        });
        removePathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doRemoveRepositoryPath();
            }
        });
        guiRepositoryList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    onChangeRepository();
                }
            }
        });
        importAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doImportRepositories();
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quitAction();
            }
        });
        addNewParamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doAddNewParamRepository();
            }
        });
        removeParamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doRemoveParamRepository();
            }
        });
        saveParamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                int result =
                      JOptionPane.showInternalConfirmDialog(frame,
                                                            "Sauvegarder le paramétrage ?",
                                                            "Demande de confirmation",
                                                            JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    doSaveParam();
                }
            }
        });
    }


    private void quitAction() {
        if (hasChanged) {
            int result = JOptionPane.showInternalConfirmDialog(frame,
                                                               "Voulez-vous sauvegarder le paramétrage ?",
                                                               "Demande de confirmation",
                                                               JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                doSaveParam();
            }
            else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        frame.dispose();
    }


    private void doAddNewParamRepository() {
        String repositoryName = JOptionPane.showInputDialog(frame,
                                                            "Entrez un nom de référentiel de traitement svp",
                                                            "Demande d'information",
                                                            JOptionPane.QUESTION_MESSAGE).trim();
        if (repositoryName != null && repositoryName.length() != 0) {
            repositoryPreference.addRepository(repositoryName);
            refreshGuiRepositoryList();
        }
    }


    private void doRemoveParamRepository() {
        repositoryPreference.removeRepository((String)guiRepositoryList.getSelectedValue());
        refreshGuiRepositoryList();
        refreshPathList();
    }


    public void doImportRepository() {
        List<String> repositoryPathList = repositoryPreference.getRepositoryPath(getSelectedRepositoryName());

        if (repositoryPathList != null) {
            int nb = repositoryPathList.size();
            progressBar.setMinimum(0);
            progressBar.setMaximum(nb);
            progressBar.setValue(0);
            if (Log.isInfoEnabled()) {
                Log.info(getClass(), nb + " fichiers seront importés.");
            }
            new Thread(new Runnable() {
                public void run() {
                    enableGui(false);
                    try {
                        importRepository(getSelectedRepositoryName(), 0);
                    }
                    catch (Exception ex) {
                        GuiUtils.showErrorDialog(frame, getClass(), "Echec de l'import", ex);
                    }
                    finally {
                        enableGui(true);
                        try {
                            RepositoryClientHelper.reinitializeRepositoryCache(ctxt);
                            updateDateImportRepository(getSelectedRepositoryName());
                        }
                        catch (RequestException ex) {
                            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                        }
                    }
                }
            }).start();
        }
    }


    private void updateDateImportRepository(String comment) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date dateDebut = Calendar.getInstance().getTime();
        String dateLastImport = dateFormat.format(dateDebut) + "  [" + comment + "]";
        try {
            RepositoryClientHelper.importRepository(ctxt,
                                                    DataProcessConstants.ImportRepoCommand.UPDATE_IMPORT_DATE,
                                                    0, "", dateLastImport, TIMEOUT);
            frame.setTitle(title + " - Dernière import effectué le " + dateLastImport);
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
    }


    private void doImportRepositories() {
        int maxNbFile = 0;
        String comment = "";

        Object[] selectedValues = guiRepositoryList.getSelectedValues();
        if (selectedValues.length <= 0) {
            return;
        }
        final List<String> repositoryNames = new ArrayList<String>();
        for (Object selectedValue : selectedValues) {
            repositoryNames.add((String)selectedValue);
        }
        if (!repositoryNames.isEmpty()) {
            comment = new ListCodec().encode(repositoryNames, "", ",");
        }

        for (String repositoryName : repositoryNames) {
            List<String> repositoryPathList = repositoryPreference.getRepositoryPath(repositoryName);
            if (repositoryPathList != null) {
                maxNbFile += repositoryPathList.size();
            }
        }

        progressBar.setMinimum(0);
        progressBar.setMaximum(maxNbFile);
        progressBar.setValue(0);
        if (Log.isInfoEnabled()) {
            Log.info(getClass(), maxNbFile + " fichiers seront importés.");
        }
        final String comment1 = comment;
        new Thread(new Runnable() {
            public void run() {
                int count = 0;
                enableGui(false);
                for (String repositoryName : repositoryNames) {
                    try {
                        count = importRepository(repositoryName, count);
                    }
                    catch (Exception ex) {
                        count += repositoryPreference.getRepositoryPath(repositoryName).size();
                        GuiUtils.showErrorDialog(frame, getClass(),
                                                 "Echec de l'import de '" + repositoryName + "'", ex);
                        progressBar.setValue(count);

                        final int count1 = count;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                progressBar.setValue(count1);
                            }
                        });
                    }
                }
                try {
                    RepositoryClientHelper.reinitializeRepositoryCache(ctxt);
                    updateDateImportRepository(comment1);
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                }
                enableGui(true);
            }
        }).start();
    }


    private int importRepository(String repositoryName, int count) throws Exception {
        int repositoryId;
        try {
            repositoryId = RepositoryClientHelper.getRepositoryIdFromName(ctxt, repositoryName);
        }
        catch (RepositoryException e) {
            repositoryId = -1;
        }
        List<String> repositoryPathList = repositoryPreference.getRepositoryPath(repositoryName);
        if (repositoryPathList != null && !repositoryPathList.isEmpty()) {
            for (String repositoryPath : repositoryPathList) {
                String content = FileUtil.loadContent(new File(repositoryPath));
                checkRepositoryContent(repositoryName, repositoryPath, content);
            }
            RepositoryClientHelper
                  .importRepository(ctxt, DataProcessConstants.ImportRepoCommand.BEGIN_INSERT,
                                    repositoryId, repositoryName, "", TIMEOUT);
            for (String repositoryPath : repositoryPathList) {
                String content = FileUtil.loadContent(new File(repositoryPath));
                if (Log.isInfoEnabled()) {
                    Log.info(getClass(), "Début d'import de " + repositoryPath);
                }
                RepositoryClientHelper.importRepository(ctxt,
                                                        DataProcessConstants.ImportRepoCommand.INSERT_PART,
                                                        repositoryId, repositoryName, content, TIMEOUT);
                count++;
                final int count1 = count;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        progressBar.setValue(count1);
                    }
                });
                if (Log.isInfoEnabled()) {
                    Log.info(getClass(), "Fin d'import de " + repositoryPath);
                }
            }
            RepositoryClientHelper.importRepository(ctxt, DataProcessConstants.ImportRepoCommand.END_INSERT,
                                                    repositoryId, repositoryName, "", TIMEOUT);
            return count;
        }
        return 0;
    }


    public static void checkRepositoryContent(String repositoryName, String repositoryPath, String content)
          throws Exception {
        Document doc = XMLUtils.parse(content);
        NodeList nodes = doc.getElementsByTagName(DataProcessConstants.TREATMENT_ENTITY_XML);
        int nbNodes = nodes.getLength();
        for (int i = 0; i < nbNodes; i++) {
            Node node = nodes.item(i);
            String treatmentId = node.getAttributes().getNamedItem("id").getNodeValue();
            if (treatmentId.length() > 50) {
                throw new TreatmentException(
                      "La taille de l'identifiant d'un traitement ('" + treatmentId
                      + "') dépasse 50 caractères dans le repository '" + repositoryName
                      + "' !\n(Il est dans le fichier : " + repositoryPath + ")");
            }
        }
    }


    private void doRemoveRepositoryPath() {
        String selectedRepositoryName = getSelectedRepositoryName();
        if (selectedRepositoryName != null) {
            String repositoryPath = (String)pathList.getSelectedValue();
            repositoryPreference.removeRepositoryPath(selectedRepositoryName, repositoryPath);
            refreshPathList();
        }
    }


    private void doAddRepositoryPath() {
        String selectedRepositoryName = getSelectedRepositoryName();
        if (selectedRepositoryName != null) {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.addChoosableFileFilter(new XmlFileFilter());

            int result = jFileChooser.showOpenDialog(ctxt.getDesktopPane());
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = jFileChooser.getSelectedFile().getAbsolutePath();
                if (repositoryPreference.getRepositoryPath(selectedRepositoryName) == null) {
                    repositoryPreference.addRepository(selectedRepositoryName);
                }
                repositoryPreference.addRepositoryPath(selectedRepositoryName, filePath);
                refreshPathList();
            }
        }
        else {
            JOptionPane.showInternalMessageDialog(frame,
                                                  "Veuillez tout d'abord ajouter un nouveau paramétrage svp",
                                                  "Info",
                                                  JOptionPane.WARNING_MESSAGE);
        }
    }


    private void refreshPathList() {
        String selectedRepositoryName = getSelectedRepositoryName();
        List<String> repositoryPathList = repositoryPreference.getRepositoryPath(selectedRepositoryName);
        if (repositoryPathList != null && !repositoryPathList.isEmpty()) {
            DefaultListModel pathListModel = new DefaultListModel();
            for (String path : repositoryPathList) {
                pathListModel.addElement(path);
            }
            pathList.setModel(pathListModel);
        }
        else {
            pathList.setModel(new DefaultListModel());
        }
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }


    private void onChangeRepository() {
        String selectedRepositoryName = getSelectedRepositoryName();
        if (selectedRepositoryName != null) {
            refreshPathList();
        }
    }


    private void doSaveParam() {
        String filePath = System.getProperty("user.home") + System.getProperty("file.separator")
                          + ctxt.getProperty("application.name") + EXTENSION;
        try {
            FileWriter fileWriter = new FileWriter(new File(filePath));
            try {
                fileWriter.write(RepositoryPreferenceCodec.toXml(repositoryPreference));
            }
            finally {
                fileWriter.close();
            }
            hasChanged = false;
            saveParamButton.setEnabled(false);
            if (Log.isInfoEnabled()) {
                Log.info(getClass(), "Sauvegarde du paramétrage effectué dans " + filePath);
            }
        }
        catch (IOException ex) {
            String message = "Impossible de sauvegarder le paramétrage dans " + filePath;
            Log.error(getClass(), message, ex);
            ErrorDialog.show(frame, message, ex);
        }
    }


    private RepositoryPreference loadRepositoryParam() {
        String filePath = System.getProperty("user.home") + System.getProperty("file.separator")
                          + ctxt.getProperty("application.name") + EXTENSION;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new RepositoryPreference();
            }
            FileReader reader = null;
            try {
                reader = new FileReader(file);
                return RepositoryPreferenceCodec.fromXml(reader);
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        catch (IOException e) {
            Log.error(getClass(), "Impossible de charger le paramétrage " + filePath);
            return null;
        }
    }


    private String getSelectedRepositoryName() {
        return (String)guiRepositoryList.getSelectedValue();
    }


    private static void enableButton(Container container, boolean enable) {
        if (container instanceof JButton) {
            container.setEnabled(enable);
        }
        else {
            for (Component component : container.getComponents()) {
                if (component instanceof Container) {
                    enableButton((Container)component, enable);
                }
            }
        }
    }


    private void enableGui(boolean enable) {
        enableButton(getMainPanel(), enable);
        if (hasChanged) {
            saveParamButton.setEnabled(true);
        }
        else {
            saveParamButton.setEnabled(false);
        }
        guiRepositoryList.setEnabled(enable);
        pathList.setEnabled(enable);

        if (!enable) {
            getMainPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        else {
            getMainPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }


    public void propertyChange(PropertyChangeEvent evt) {
        hasChanged = true;
        saveParamButton.setEnabled(true);
    }


    private static class XmlFileFilter extends FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return file.isDirectory() || filename.endsWith(XML_FILE_TYPE);
        }


        @Override
        public String getDescription() {
            return "*.xml";
        }
    }
}
