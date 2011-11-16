package net.codjo.dataprocess.gui.util;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 *
 */
public abstract class DocumentListenerAdapter implements DocumentListener {

    public void insertUpdate(DocumentEvent evt) {
        actionPerformed(evt);
    }


    public void removeUpdate(DocumentEvent evt) {
        actionPerformed(evt);
    }


    public void changedUpdate(DocumentEvent evt) {
        actionPerformed(evt);
    }


    abstract protected void actionPerformed(DocumentEvent evt);
}
