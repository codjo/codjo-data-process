/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.editor;
import net.codjo.dataprocess.gui.util.ErrorDialog;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;
/**
 *
 */
public class ColoredEditor extends JTextPane implements KeyListener {
    private final ICompletion completion;


    public ColoredEditor(ICompletion completion) {
        this.completion = completion;
        setEditorKit(new ColoredEditorKit());
        addKeyListener(this);
    }


    public ICompletion getCompletion() {
        return completion;
    }


    public SyntaxDocument getSyntaxDocument() {
        return ((ColoredEditorKit)getEditorKit()).getDefaultDocument();
    }


    @Override
    public boolean getScrollableTracksViewportWidth() {
        Component parent = getParent();

        return parent == null || (getUI().getPreferredSize(this).width <= parent.getSize().width);
    }


    public void keyTyped(KeyEvent event) {
    }


    public void keyPressed(KeyEvent event) {
        if (event.getKeyChar() == ' ' && event.getModifiers() == KeyEvent.CTRL_MASK) {
            SyntaxDocument.WordElement wordElement = getCurrentWord();
            if (wordElement == null) {
                return;
            }
            List<String> list = completion.getListWithPartial(wordElement.getPrefix());
            if (list.size() > 1) {
                JPopupMenu popupMenu = new JPopupMenu();
                for (String completor : list) {
                    JMenuItem item = new JMenuItem(completor);
                    item.addActionListener(new Putter(wordElement, completor));
                    popupMenu.add(item);
                }
                Point caretPosition = getCaret().getMagicCaretPosition();
                popupMenu.show(this, (int)caretPosition.getX(), (int)caretPosition.getY() + 15);
            }
            else if (list.size() == 1) {
                try {
                    getSyntaxDocument().replaceWord(wordElement, list.get(0));
                }
                catch (BadLocationException ex) {
                    ErrorDialog.show(null, "Editor error", ex);
                }
            }
        }
    }


    private SyntaxDocument.WordElement getCurrentWord() {
        try {
            return getSyntaxDocument().getCurrentWordElement(getCaretPosition());
        }
        catch (BadLocationException ex) {
            ErrorDialog.show(null, "Editor error", ex);
        }
        return null;
    }


    public void keyReleased(KeyEvent event) {
        // Todo
    }


    static class ColoredEditorKit extends StyledEditorKit {
        private SyntaxDocument defaultDocument;


        public SyntaxDocument getDefaultDocument() {
            return defaultDocument;
        }


        @Override
        public Document createDefaultDocument() {
            defaultDocument = new SyntaxDocument();
            return defaultDocument;
        }
    }

    private class Putter implements ActionListener {
        private String replaceBy;
        private final SyntaxDocument.WordElement wordElement;


        Putter(SyntaxDocument.WordElement wordElement, String replaceBy) {
            this.wordElement = wordElement;
            this.replaceBy = replaceBy;
        }


        public void actionPerformed(ActionEvent event) {
            try {
                getSyntaxDocument().replaceWord(wordElement, replaceBy);
            }
            catch (BadLocationException ex) {
                ErrorDialog.show(null, "Editor error", ex);
            }
        }
    }
}
