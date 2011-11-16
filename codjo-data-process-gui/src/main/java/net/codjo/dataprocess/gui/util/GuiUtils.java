/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.common.userparam.User.Repository;
import net.codjo.gui.toolkit.fileChooser.FileChooserManager;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
/**
 *
 */
public class GuiUtils {
    static final String ESC_ACTION_KEY = "ESC_ACTION_KEY";
    private static final String WALLPAPER = "WALLPAPER";


    private GuiUtils() {
    }


    public static void setSize(JComponent conponent, int width, int height) {
        conponent.setMinimumSize(new Dimension(width, height));
        conponent.setPreferredSize(new Dimension(width, height));
    }


    public static void setMaxSize(Component conponent, int width, int height) {
        conponent.setMaximumSize(new Dimension(width, height));
        conponent.setMinimumSize(new Dimension(width, height));
        conponent.setPreferredSize(new Dimension(width, height));
    }


    public static Icon loadIcon(Class c1, String fileName) {
        URL resource = c1.getResource(fileName);
        return new ImageIcon(resource);
    }


    public static void setInitialFocus(Window w1, Component component) {
        w1.addWindowListener(new FocusWindowAdapter(component));
    }


    public static void setInitialFocus(JInternalFrame w1, Component component) {
        w1.addComponentListener(new FocusComponentListener(component)); // JInternalFrame est un component
    }


    /**
     * Utilisé pour déclencher un évènement sur les JInternalFrame lors de l'appuie sur la touche 'ESCAPE'
     */
    public static void escapeKeyAction(JComponent component, javax.swing.AbstractAction abstractAction) {
        component.getRootPane().getActionMap().put(ESC_ACTION_KEY, abstractAction);
        component.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
              .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESC_ACTION_KEY);
    }


    /**
     * Force l'ajout d'une scrollbar horizontale sur une JTable.
     */
    public static void addHorizontalScrollBar(JTable table, JScrollPane pane) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        pane.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }


    public static JFrame getParentFrame(Component component) {
        Container container = component.getParent();
        if (container instanceof JFrame || container == null) {
            return (JFrame)container;
        }
        else {
            return getParentFrame(container);
        }
    }


    private static class FocusWindowAdapter extends WindowAdapter {
        Component component;


        FocusWindowAdapter(Component component) {
            this.component = component;
        }


        @Override
        public void windowOpened(WindowEvent evt) {
            component.requestFocus();

            // On supprime le listener car après ouverture de la fenêtre il ne sert plus à rien
            evt.getWindow().removeWindowListener(this);
        }
    }

    private static class FocusComponentListener implements ComponentListener {
        Component component;


        FocusComponentListener(Component c1) {
            component = c1;
        }


        public void componentResized(ComponentEvent event) {
        }


        public void componentMoved(ComponentEvent event) {
            component.requestFocus();

            // On supprime le listener car après ouverture de la fenêtre il ne sert plus à rien
            event.getComponent().removeComponentListener(this);
        }


        public void componentShown(ComponentEvent event) {
            component.requestFocus();

            // On supprime le listener car après ouverture de la fenêtre il ne sert plus à rien
            event.getComponent().removeComponentListener(this);
        }


        public void componentHidden(ComponentEvent event) {
        }
    }


    public static void setWallPaper(GuiContext ctxt, Icon icon, Rectangle rect,
                                    Color bgDev, Color bgRecette, Color bgProd) {
        removeWallPaper(ctxt);
        JLabel label = new JLabel(icon, JLabel.CENTER);
        label.setBounds(rect);
        label.setName(WALLPAPER);
        ctxt.getDesktopPane().add(label);

        String userEnvironment = System.getProperty("user.environment");
        if (userEnvironment != null && userEnvironment.startsWith("Production")) {
            ctxt.getDesktopPane().setBackground(bgProd);
        }
        else if (userEnvironment != null && userEnvironment.startsWith("Recette")) {
            ctxt.getDesktopPane().setBackground(bgRecette);
        }
        else {
            ctxt.getDesktopPane().setBackground(bgDev);
        }
    }


    public static void removeWallPaper(GuiContext ctxt) {
        JDesktopPane desktopPane = ctxt.getDesktopPane();
        Component[] components = desktopPane.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                if (WALLPAPER.equalsIgnoreCase(component.getName())) {
                    desktopPane.remove(component);
                }
            }
        }
    }


    public static void showErrorDialog(Component component, Class logger, String message, Throwable error) {
        Log.error(logger, message, error);
        ErrorDialog.show(component, message, error);
    }


    public static void setToLeftSide(Component cp) {
        if (cp.getParent() == null) {
            throw new IllegalStateException("L'appel à la méthode 'setToLeftSide'"
                                            + " doit s'effectuer après l'ajout au desktop");
        }
        Dimension containerSize = cp.getParent().getSize();
        Dimension frameSize = cp.getSize();
        frameSize.height = containerSize.height;
        frameSize.width = containerSize.width / 4;
        cp.setSize(frameSize);
        cp.setLocation(0, 0);
    }


    public static void showRepositoryMessage(MutableGuiContext ctxt, User user) {
        List<Repository> notValidRepositoryList = user.getNotValidRepositoryList();
        if (!notValidRepositoryList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Repository repository : notValidRepositoryList) {
                sb.append(repository.getName()).append(" n'est plus accessible à partir du ")
                      .append(repository.getExpirydate().trim()).append('\n');
            }
            JOptionPane.showMessageDialog(ctxt.getMainFrame(),
                                          "Il y a des repositories dont vous n'avez maintenant plus accès :\n"
                                          + sb, "Information importante", JOptionPane.WARNING_MESSAGE);
        }
        if (user.getCurrentRepository() == null) {
            JOptionPane.showMessageDialog(ctxt.getMainFrame(),
                                          "Vous n'avez actuellement accès à aucun repository.\n"
                                          + "Merci de contacter un responsable de l'application svp.",
                                          "Information importante",
                                          JOptionPane.WARNING_MESSAGE);
        }
    }


    public static String showChooserForExport(String fileName,
                                              String chooserTitle,
                                              String fileTypeLabel,
                                              String extension,
                                              Component parent) {
        File file = FileChooserManager.showChooserForSave(((fileName != null) ? new File(fileName) : null),
                                                          chooserTitle, fileTypeLabel, extension, parent);
        if (file != null) {
            return file.getAbsolutePath();
        }
        return null;
    }


    public static void setBackgroundColor(MutableGuiContext ctxt, boolean showEnvironment,
                                          Color bgDev, Color bgRecette, Color bgProd, int size) {
        GuiUtils.removeWallPaper(ctxt);
        String userEnvironment = System.getProperty("user.environment");
        JLabel label = new JLabel("Développement", JLabel.CENTER);
        label.setBounds(new Rectangle(365, 219, 550, 412));
        label.setName("WALLPAPER");
        label.setFont(label.getFont().deriveFont(Font.BOLD, size));
        if (userEnvironment != null && userEnvironment.startsWith("Production")) {
            label.setText("Production");
            ctxt.getDesktopPane().setBackground(bgProd);
        }
        else if (userEnvironment != null && userEnvironment.startsWith("Recette")) {
            label.setText("Recette");
            ctxt.getDesktopPane().setBackground(bgRecette);
        }
        else {
            ctxt.getDesktopPane().setBackground(bgDev);
        }
        if (showEnvironment) {
            ctxt.getDesktopPane().add(label);
        }
    }


    public static void setBackgroundColor(MutableGuiContext ctxt, boolean showEnvironment,
                                          Color bgDev, Color bgRecette, Color bgProd) {
        setBackgroundColor(ctxt, showEnvironment, bgDev, bgRecette, bgProd, 40);
    }
}
