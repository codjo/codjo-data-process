/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util;
import net.codjo.mad.gui.request.Position;
import net.codjo.mad.gui.request.RequestToolBar;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
/**
 *
 */
public abstract class ClosePanel extends JPanel {
    private JButton closeButton;


    protected ClosePanel(String text) {
        closeButton = new JButton(text);
        closeButton.setName("closeButton");
        initPanel();
    }


    protected ClosePanel(String text, Icon icon, String toolTipText) {
        this(text);
        closeButton.setIcon(icon);
        if (toolTipText != null) {
            closeButton.setToolTipText(toolTipText);
        }
    }


    public JButton getCloseButton() {
        return closeButton;
    }


    private void initPanel() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });
        add(closeButton);
    }


    protected abstract void dispose();


    public void addToRequestToolbar(RequestToolBar toolBar) {
        toolBar.addComponent(closeButton, Position.last());
        RequestToolBar.doEffect(toolBar);
    }


    public void removeFromRequestToolbar(RequestToolBar toolBar) {
        toolBar.remove(closeButton);
        RequestToolBar.doEffect(toolBar);
    }
}
