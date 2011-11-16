/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.tables;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.userparam.DefaultUserCodec;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.common.userparam.User.Repository;
import net.codjo.dataprocess.common.userparam.UserCodec;
import net.codjo.dataprocess.gui.selector.RepositoryComboBox;
import net.codjo.dataprocess.gui.util.AbstractDetailWindow;
import net.codjo.dataprocess.gui.util.DataProcessGuiEvent;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import com.thoughtworks.xstream.core.BaseException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
/**
 *
 */
public class UserDetailWindow extends AbstractDetailWindow {
    private JTextArea textAreaXml;
    private GuiContext ctxt;


    public UserDetailWindow(final DetailDataSource dataSource) throws RequestException {
        super(dataSource, "Droits des utilisateurs sur les référentiels de traitement");
        ctxt = dataSource.getGuiContext();
        dataSource.addDataSourceListener(new DataSourceAdapter() {
            @Override
            public void beforeSaveEvent(DataSourceEvent event) {
                UserCodec userCodec = new DefaultUserCodec();
                User user = userCodec.fromXml(textAreaXml.getText());
                String userName = dataSource.getFieldValue("userName");
                user.setUserName(userName);
                user.setDefaultRepository();
                textAreaXml.setText(userCodec.toXml(user));
                textAreaXml.setCaretPosition(0);
            }


            @Override
            public void saveEvent(DataSourceEvent event) {
                User user = new DefaultUserCodec().fromXml(textAreaXml.getText());
                String userName = dataSource.getFieldValue("userName");
                user.setUserName(userName);
                user.setDefaultRepository();

                if (System.getProperty("user.name").equals(userName)) {
                    dataSource.getGuiContext().sendEvent(new DataProcessGuiEvent(
                          DataProcessGuiEvent.UPDATE_USER_EVENT, user));
                }
            }
        });
    }


    @Override
    protected void postInitGui() {
        super.postInitGui();
        UserCodec userCodec = new DefaultUserCodec();
        if (textAreaXml.getText().trim().length() == 0) {
            textAreaXml.setText(userCodec.toXml(new User()));
            textAreaXml.setCaretPosition(0);
        }
        else {
            try {
                userCodec.fromXml(textAreaXml.getText());
            }
            catch (BaseException ex) {
                Log.error(getClass(), "Erreur de paramétrage", ex);
                JOptionPane.showMessageDialog(getGuiContext().getMainFrame(),
                                              "Le paramétrage de cet utilisateur ne semble pas correcte.\n"
                                              + "Un paramétrage par défaut va vous être proposé.",
                                              "Information importante",
                                              JOptionPane.WARNING_MESSAGE);
                User user = new User();
                textAreaXml.setText(userCodec.toXml(user));
                textAreaXml.setCaretPosition(0);
            }
        }
    }


    @Override
    protected void declareFields() throws RequestException {
        addFieldToPage("Paramétrage", "userName", "Identifiant de l'utilisateur", new JTextField());
        textAreaXml = new JTextArea();
        textAreaXml.setRows(14);
        addField("userParam", "Paramètres de l'utilisateur", textAreaXml);
        buildGui();
    }


    private void buildGui() {
        GuiUtils.setSize(this, 600, 500);
        RepositoryComboBox repositoryComboBox = new RepositoryComboBox(new LocalGuiContext(ctxt));
        repositoryComboBox.loadData();

        JButton addButton = new JButton(UIManager.getIcon("dataprocess.add"));
        addButton.setToolTipText("Ajouter un référentiel de traitement au paramétrage");

        JButton removeButton = new JButton(UIManager.getIcon("dataprocess.remove"));
        removeButton.setToolTipText("Supprimer un référentiel de traitement du paramétrage");

        JButton addAllButton = new JButton("Tous", UIManager.getIcon("dataprocess.add"));
        addAllButton.setToolTipText("Ajouter tous les référentiels de traitement au paramétrage");

        JButton removeAllButton = new JButton("Tous", UIManager.getIcon("dataprocess.remove"));
        removeAllButton.setToolTipText("Supprimer tous les référentiels de traitement du paramétrage");

        addButton.setBorderPainted(false);
        removeButton.setBorderPainted(false);
        GuiUtils.setSize(addButton, 15, 14);
        GuiUtils.setSize(removeButton, 15, 14);

        JPanel actionAllPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JPanel repositoryNameComboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

        repositoryNameComboBoxPanel.add(getRepositoryComboBoxPanel(repositoryComboBox));
        repositoryNameComboBoxPanel.add(addButton);
        repositoryNameComboBoxPanel.add(removeButton);
        repositoryNameComboBoxPanel.add(addAllButton);
        repositoryNameComboBoxPanel.add(removeAllButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new TitledBorder(" Ajout/suppression de référentiel de traitement "));
        mainPanel.add(repositoryNameComboBoxPanel, BorderLayout.NORTH);
        mainPanel.add(actionAllPanel, BorderLayout.CENTER);

        addComponent("", mainPanel);

        addButton.addActionListener(new RepositoryNameComboBoxListener(repositoryComboBox) {
            @Override
            protected void doAction() {
                addRepository();
            }
        });
        removeButton.addActionListener(new RepositoryNameComboBoxListener(repositoryComboBox) {
            @Override
            protected void doAction() {
                removeRepository();
            }
        });
        addAllButton.addActionListener(new RepositoryNameComboBoxListener(repositoryComboBox) {
            @Override
            protected void doAction() {
                addAllRepository();
            }
        });
        removeAllButton.addActionListener(new RepositoryNameComboBoxListener(repositoryComboBox) {
            @Override
            protected void doAction() {
                removeAllRepository();
            }
        });
    }


    private static JPanel getRepositoryComboBoxPanel(RepositoryComboBox repositoryComboBox) {
        JPanel jContentPane = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.ipadx = 0;
        gridBagConstraints1.insets = new Insets(1, 1, 1, 1);
        gridBagConstraints1.gridx = 0;

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.ipadx = 100;
        gridBagConstraints2.fill = GridBagConstraints.NONE;
        gridBagConstraints2.insets = new Insets(0, 10, 0, 0);
        gridBagConstraints2.gridx = 1;

        jContentPane.add(new JLabel("Référentiel:"), gridBagConstraints1);
        jContentPane.add(repositoryComboBox, gridBagConstraints2);

        return jContentPane;
    }


    private abstract class RepositoryNameComboBoxListener implements ActionListener {
        private User user;
        private RepositoryComboBox repositoryComboBox;


        protected RepositoryNameComboBoxListener(RepositoryComboBox repositoryComboBox) {
            this.repositoryComboBox = repositoryComboBox;
        }


        public void actionPerformed(ActionEvent evt) {
            UserCodec userCodec = new DefaultUserCodec();
            if (textAreaXml.getText().trim().length() == 0) {
                user = new User();
                textAreaXml.setText(userCodec.toXml(user));
                textAreaXml.setCaretPosition(0);
            }
            else {
                user = userCodec.fromXml(textAreaXml.getText());
            }
            doAction();
            textAreaXml.setText(userCodec.toXml(user));
            textAreaXml.setCaretPosition(0);
        }


        public void removeAllRepository() {
            user.removeAllRepository();
        }


        public void removeRepository() {
            if (repositoryComboBox.getModel().getSize() > 0) {
                user.removeRepository(repositoryComboBox.getSelectedValueToDisplay("repositoryName"));
            }
        }


        public void addRepository() {
            if (repositoryComboBox.getModel().getSize() > 0) {
                user.addRepository(
                      new Repository(repositoryComboBox.getSelectedValueToDisplay("repositoryName"),
                                     getExpirydate(), Integer.toString(getExpiryday())));
            }
        }


        public void addAllRepository() {
            for (int i = 0; i < repositoryComboBox.getItemCount(); i++) {
                user.addRepository(new Repository(repositoryComboBox.getValueAt(i, "repositoryName"),
                                                  getExpirydate(), Integer.toString(getExpiryday())));
            }
        }


        protected abstract void doAction();
    }


    private static String getExpirydate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, getExpiryday());
        return new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
    }


    private static int getExpiryday() {
        String userEnvironment = System.getProperty("user.environment");
        if (userEnvironment != null && userEnvironment.startsWith("Production")) {
            return 45;
        }
        else if (userEnvironment != null && userEnvironment.startsWith("Recette")) {
            return 180;
        }
        else {
            return 180;
        }
    }
}
