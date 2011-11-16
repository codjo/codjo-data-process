/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.editor;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
/**
 *
 */
public class SyntaxDocument extends DefaultStyledDocument {
    public static final String STYLE_NORMAL = "@" + System.currentTimeMillis() + "_STYLE_NORMAL";
    public static final String STYLE_COMMENT = "@" + System.currentTimeMillis() + "_STYLE_COMMENT";
    public static final String STYLE_QUOTE = "@" + System.currentTimeMillis() + "_STYLE_QUOTE";
    public static final String STYLE_KEYWORD = "@" + System.currentTimeMillis() + "_STYLE_KEYWORD";
    private DefaultStyledDocument doc;
    private Element rootElement;
    private boolean multiLineComment;
    private Map<String, SimpleAttributeSet> styles;
    private Map<String, SimpleAttributeSet> keywordsToStyles;


    public SyntaxDocument() {
        doc = this;
        rootElement = doc.getDefaultRootElement();
        putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
        keywordsToStyles = new HashMap<String, SimpleAttributeSet>();
        styles = new HashMap<String, SimpleAttributeSet>();

        addNewStyle(STYLE_NORMAL, Color.black, null, false, false, false);
        addNewStyle(STYLE_COMMENT, new Color(0, 120, 0), null, false, true, false);
        addNewStyle(STYLE_QUOTE, new Color(140, 0, 0), null, false, false, false);
        addNewStyle(STYLE_KEYWORD, new Color(0, 0, 140), null, true, false, false);
    }


    public void addKeyWord(String keyWord) {
        addKeyWord(keyWord, STYLE_NORMAL);
    }


    public void addKeyWord(String keyWord, String styleName) {
        if (!styles.keySet().contains(styleName)) {
            throw new IllegalArgumentException("Style '" + styleName + "' unkown !");
        }
        keywordsToStyles.put(keyWord, styles.get(styleName));
    }


    public void addNewStyle(String name, SimpleAttributeSet attributeSet) {
        styles.put(name, attributeSet);
    }


    public void addNewStyle(String name, Color foreGroundColor, Color backGroundColor, boolean bold,
                            boolean italic, boolean underline) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        if (backGroundColor != null) {
            StyleConstants.setBackground(attributeSet, backGroundColor);
        }
        StyleConstants.setForeground(attributeSet, foreGroundColor);
        StyleConstants.setBold(attributeSet, bold);
        StyleConstants.setItalic(attributeSet, italic);
        StyleConstants.setUnderline(attributeSet, underline);
        styles.put(name, attributeSet);
    }


    /*
    * Override to apply syntax highlighting after the document has been updated
    */
    @Override
    public void insertString(int offset, String str, AttributeSet attributeSet)
          throws BadLocationException {
        super.insertString(offset, str, attributeSet);
        processChangedLines(offset, str.length());
    }


    /*
    * Override to apply syntax highlighting after the document has been updated
    */
    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        processChangedLines(offset, 0);
    }


    public void replaceWord(WordElement currendWordElement, String replacedBy)
          throws BadLocationException {
        remove(currendWordElement.getStartOffset(), currendWordElement.getFullWord().length());
        insertString(currendWordElement.getStartOffset(), replacedBy, new SimpleAttributeSet());
    }


    public WordElement getCurrentWordElement(int carretPosition)
          throws BadLocationException {
        //change le carret position en index de caracter
        int startWordOffset = carretPosition > 0 ? carretPosition - 1 : carretPosition;
        int endWordOffset = startWordOffset;

        //recupère les données de la ligne
        int element = rootElement.getElementIndex(startWordOffset);
        int startLineOffset = rootElement.getElement(element).getStartOffset();
        int endLineOffset = rootElement.getElement(element).getEndOffset();
        if (endLineOffset > getLength()) {
            endLineOffset = getLength();
        }

        //remonte le mot
        while (startWordOffset > startLineOffset && !isDelimiter(getText(startWordOffset - 1, 1))) {
            startWordOffset--;
        }

        //avance dans le mot
        while (endWordOffset <= endLineOffset && !isDelimiter(getText(endWordOffset + 1, 1))) {
            endWordOffset++;
        }
        return new WordElement(getText(startWordOffset, endWordOffset - startWordOffset + 1),
                               startWordOffset, carretPosition - startWordOffset);
    }


    /*
    * Determine how many lines have been changed,
    * then apply highlighting to each line
    */
    private void processChangedLines(int offset, int length)
          throws BadLocationException {
        String content = doc.getText(0, doc.getLength());

// The lines affected by the latest document update
        int startLine = rootElement.getElementIndex(offset);
        int endLine = rootElement.getElementIndex(offset + length);
// Make sure all comment lines prior to the startOffset line are commented
// and determine if the startOffset line is still in a multi line comment
        setMultiLineComment(commentLinesBefore(content, startLine));
// Do the actual highlighting
        for (int i = startLine; i <= endLine; i++) {
            applyHighlighting(content, i);
        }

        // Resolve highlighting to the next end multi line delimiter
        if (isMultiLineComment()) {
            commentLinesAfter(content, endLine);
        }
        else {
            highlightLinesAfter(content, endLine);
        }
    }


    /*
    * Highlight lines when a multi line comment is still 'open'
    * (ie. matching end delimiter has not yet been encountered)
    */
    private boolean commentLinesBefore(String content, int line) {
        int offset = rootElement.getElement(line).getStartOffset();

// Start of comment not found, nothing to do
        int startDelimiter = lastIndexOf(content, getStartDelimiter(), offset - 2);
        if (startDelimiter < 0) {
            return false;
        }

        // Matching startOffset/end of comment found, nothing to do
        int endDelimiter = indexOf(content, getEndDelimiter(), startDelimiter);
        if (endDelimiter < offset & endDelimiter != -1) {
            return false;
        }
        // End of comment not found, highlight the lines
        doc.setCharacterAttributes(startDelimiter, offset - startDelimiter + 1, styles.get(STYLE_COMMENT),
                                   false);
        return true;
    }


    /*
    * Highlight comment lines to matching end delimiter
    */
    private void commentLinesAfter(String content, int line) {
        int offset = rootElement.getElement(line).getEndOffset();

// End of comment not found, nothing to do
        int endDelimiter = indexOf(content, getEndDelimiter(), offset);
        if (endDelimiter < 0) {
            return;
        }

        // Matching startOffset/end of comment found, comment the lines
        int startDelimiter = lastIndexOf(content, getStartDelimiter(), endDelimiter);
        if (startDelimiter < 0 || startDelimiter <= offset) {
            doc.setCharacterAttributes(offset, endDelimiter - offset + 1, styles.get(STYLE_COMMENT), false);
        }
    }


    /*
    * Highlight lines to startOffset or end delimiter
    */
    private void highlightLinesAfter(String content, int line)
          throws BadLocationException {
        int offset = rootElement.getElement(line).getEndOffset();

// Start/End delimiter not found, nothing to do
        int startDelimiter = indexOf(content, getStartDelimiter(), offset);
        int endDelimiter = indexOf(content, getEndDelimiter(), offset);
        if (startDelimiter < 0) {
            startDelimiter = content.length();
        }
        if (endDelimiter < 0) {
            endDelimiter = content.length();
        }
        int delimiter = Math.min(startDelimiter, endDelimiter);
        if (delimiter < offset) {
            return;
        }

        // Start/End delimiter found, reapply highlighting
        int endLine = rootElement.getElementIndex(delimiter);
        for (int i = line + 1; i < endLine; i++) {
            Element branch = rootElement.getElement(i);
            Element leaf = doc.getCharacterElement(branch.getStartOffset());
            AttributeSet as = leaf.getAttributes();
            if (as.isEqual(styles.get(STYLE_COMMENT))) {
                applyHighlighting(content, i);
            }
        }
    }


    /*
    * Parse the line to determine the appropriate highlighting
    */
    private void applyHighlighting(String content, int line)
          throws BadLocationException {
        int startOffset = rootElement.getElement(line).getStartOffset();
        int endOffset = rootElement.getElement(line).getEndOffset() - 1;
        int lineLength = endOffset - startOffset;
        int contentLength = content.length();
        if (endOffset >= contentLength) {
            endOffset = contentLength - 1;
        }

        // check for multi line comments
        // (always set the comment attribute for the entire line)
        if (endingMultiLineComment(content, startOffset, endOffset)
            || isMultiLineComment()
            || startingMultiLineComment(content, startOffset, endOffset)) {
            doc.setCharacterAttributes(startOffset, endOffset - startOffset + 1, styles.get(STYLE_COMMENT),
                                       false);
            return;
        }
        // set normal attributes for the line
        doc.setCharacterAttributes(startOffset, lineLength, styles.get(STYLE_NORMAL), true);
// check for single line comment
        int index = content.indexOf(getSingleLineDelimiter(), startOffset);
        if ((index > -1) && (index < endOffset)) {
            doc.setCharacterAttributes(index, endOffset - index + 1, styles.get(STYLE_COMMENT), false);
            endOffset = index - 1;
        }
        // check for tokens
        checkForTokens(content, startOffset, endOffset);
    }


    /*
    * Does this line contain the startOffset delimiter
    */
    private boolean startingMultiLineComment(String content, int startOffset, int endOffset)
          throws BadLocationException {
        int index = indexOf(content, getStartDelimiter(), startOffset);
        if ((index < 0) || (index > endOffset)) {
            return false;
        }
        else {
            setMultiLineComment(true);
            return true;
        }
    }


    /*
    * Does this line contain the end delimiter
    */
    private boolean endingMultiLineComment(String content, int startOffset, int endOffset)
          throws BadLocationException {
        int index = indexOf(content, getEndDelimiter(), startOffset);
        if ((index < 0) || (index > endOffset)) {
            return false;
        }
        else {
            setMultiLineComment(false);
            return true;
        }
    }


    /*
    * We have found a startOffset delimiter
    * and are still searching for the end delimiter
    */
    private boolean isMultiLineComment() {
        return multiLineComment;
    }


    private void setMultiLineComment(boolean value) {
        multiLineComment = value;
    }


    /*
    * Parse the line for tokens to highlight
    */
    private void checkForTokens(String content, int startOffset, int endOffset) {
        while (startOffset <= endOffset) {
            // skip the delimiters to find the startOffset of a new token
            while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
                if (startOffset < endOffset) {
                    startOffset++;
                }
                else {
                    return;
                }
            }

            // Extract and process the entire token
            if (isQuoteDelimiter(content.substring(startOffset, startOffset + 1))) {
                startOffset = getQuoteToken(content, startOffset, endOffset);
            }
            else {
                startOffset = getOtherToken(content, startOffset, endOffset);
            }
        }
    }


    /*
    *
    */
    private int getQuoteToken(String content, int startOffset, int endOffset) {
        String quoteDelimiter = content.substring(startOffset, startOffset + 1);
        String escapeString = getEscapeString(quoteDelimiter);
        int index;
        int endOfQuote = startOffset;
// skip over the escape quotes in this quote
        index = content.indexOf(escapeString, endOfQuote + 1);
        while ((index > -1) && (index < endOffset)) {
            endOfQuote = index + 1;
            index = content.indexOf(escapeString, endOfQuote);
        }
        // now find the matching delimiter
        index = content.indexOf(quoteDelimiter, endOfQuote + 1);
        if ((index < 0) || (index > endOffset)) {
            endOfQuote = endOffset;
        }
        else {
            endOfQuote = index;
        }
        doc.setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, styles.get(STYLE_QUOTE), false);
        return endOfQuote + 1;
    }


    private int getOtherToken(String content, int startOffset, int endOffset) {
        int endOfToken = startOffset + 1;
        while (endOfToken <= endOffset) {
            if (isDelimiter(content.substring(endOfToken, endOfToken + 1))) {
                break;
            }
            endOfToken++;
        }
        String token = content.substring(startOffset, endOfToken);
        if (isKeyword(token)) {
            doc.setCharacterAttributes(startOffset, endOfToken - startOffset, keywordsToStyles.get(token),
                                       false);
        }
        return endOfToken + 1;
    }


    /*
    * Assume the needle will the found at the startOffset/end of the line
    */
    private int indexOf(String content, String needle, int offset) {
        int index;
        while ((index = content.indexOf(needle, offset)) != -1) {
            String text = getLine(content, index).trim();
            if (text.startsWith(needle) || text.endsWith(needle)) {
                break;
            }
            else {
                offset = index + 1;
            }
        }
        return index;
    }


    /*
    * Assume the needle will the found at the startOffset/end of the line
    */
    private int lastIndexOf(String content, String needle, int offset) {
        int index;
        while ((index = content.lastIndexOf(needle, offset)) != -1) {
            String text = getLine(content, index).trim();
            if (text.startsWith(needle) || text.endsWith(needle)) {
                break;
            }
            else {
                offset = index - 1;
            }
        }
        return index;
    }


    private String getLine(String content, int offset) {
        int line = rootElement.getElementIndex(offset);
        Element lineElement = rootElement.getElement(line);
        int start = lineElement.getStartOffset();
        int end = lineElement.getEndOffset();
        return content.substring(start, end - 1);
    }


    /*
    * Override for other languages
    */
    protected boolean isDelimiter(String character) {
        String operands = ",;:{}()[]+-/%<=>!&|^~*";
        return Character.isWhitespace(character.charAt(0)) || operands.contains(character);
    }


    /*
    * Override for other languages
    */
    protected boolean isQuoteDelimiter(String character) {
        String quoteDelimiters = "\"'";
        return quoteDelimiters.contains(character);
    }


    /*
    * Override for other languages
    */
    protected boolean isKeyword(String token) {
        return keywordsToStyles.keySet().contains(token);
    }


    /*
    * Override for other languages
    */
    protected String getStartDelimiter() {
        return "/*";
    }


    /*
    * Override for other languages
    */
    protected String getEndDelimiter() {
        return "*/";
    }


    /*
    * Override for other languages
    */
    protected String getSingleLineDelimiter() {
        return "//";
    }


    /*
    * Override for other languages
    */
    protected String getEscapeString(String quoteDelimiter) {
        return "\\" + quoteDelimiter;
    }


    public static class WordElement {
        private String fullWord;
        private int startOffset;
        private final int cutPoint;


        public WordElement(String fullWord, int start, int cutPoint) {
            this.fullWord = fullWord;
            this.startOffset = start;
            this.cutPoint = cutPoint;
        }


        public String getFullWord() {
            return fullWord;
        }


        public String getPrefix() {
            return fullWord.substring(0, cutPoint);
        }


        public String getSuffix() {
            return fullWord.substring(cutPoint);
        }


        public int getStartOffset() {
            return startOffset;
        }
    }
}
