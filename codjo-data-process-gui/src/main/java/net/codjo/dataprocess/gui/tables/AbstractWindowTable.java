/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.tables;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
/**
 *
 */
public abstract class AbstractWindowTable extends JInternalFrame {
    private JScrollPane scrollPane = new JScrollPane();
    private RequestTable requestTable = new RequestTable();
    private RequestToolBar toolBar = new RequestToolBar();


    protected AbstractWindowTable(GuiContext ctxt, String title, boolean editable) throws RequestException {
        super(title, true, true, true, true);
        jbInit();
        setPreferredSize(new Dimension(1100, 700));
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestTable.setEditable(editable);
        requestTable.setPreference(getRequestTablePreference());

        doInitStuff();

        if (editable) {
            toolBar.setHasUndoRedoButtons(true);
        }
        toolBar.setHasValidationButton(true);
        toolBar.setHasRecordCountField(true);
        toolBar.setHasExcelButton(true);
        toolBar.setHasNavigatorButton(true);
        toolBar.init(ctxt, requestTable);
        requestTable.load();
    }


    RequestTable getRequestTable() {
        return requestTable;
    }


    protected void doInitStuff() {
    }


    protected abstract Preference getRequestTablePreference();


    private void jbInit() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(toolBar, BorderLayout.SOUTH);
        scrollPane.getViewport().add(requestTable, null);

        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
    }
}
