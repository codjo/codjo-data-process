package net.codjo.dataprocess.gui.util.editor;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
/**
 *
 */
public class ColoredEditorDocument extends DefaultStyledDocument {
    public static final String STYLE_NORMAL = "__style__std__normal";
    public static final String STYLE_COMMENT_SINGLE_LINE = "__style__std__comment_single_line";
    public static final String STYLE_COMMENT_MULTI_LINE = "__style__std__comment_multi_line";
    public static final String STYLE_NUMBER = "__style__std__number";
    private Element rootElement;
    private Map<String, MutableAttributeSet> styles = new HashMap<String, MutableAttributeSet>();
    private Map<String, String> keywordsToStyle = new HashMap<String, String>();
    private Pattern singleLineCommentDelimter = Pattern.compile("//");
    private Pattern patternNumberDelimter =
          Pattern.compile("[\\s|,|=|\\*|/]+[-+]?[0-9]*\\.?[0-9]+[\\s|,|\\n]");
    private Pattern multiLineCommentDelimiterStart = Pattern.compile("/\\*");
    private Pattern multiLineCommentDelimiterEnd = Pattern.compile("\\*/");
    private boolean singleLineCommentActivated = true;
    private boolean multiLineCommentActivated = true;


    public ColoredEditorDocument() {
        putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
        rootElement = getDefaultRootElement();

        recordStyle(STYLE_COMMENT_SINGLE_LINE, Color.gray, false, true, false);
        recordStyle(STYLE_COMMENT_MULTI_LINE, Color.gray, false, true, false);
        recordStyle(STYLE_NORMAL, Color.black, false, false, false);
        recordStyle(STYLE_NUMBER, Color.RED, false, true, false);
    }


    public void disableSingleLineComment() {
        singleLineCommentActivated = false;
    }


    public void disableMultiLineComment() {
        multiLineCommentActivated = false;
    }


    public void enableSingleLineComment(String pattern) {
        singleLineCommentActivated = true;
        singleLineCommentDelimter = Pattern.compile(pattern);
    }


    public void enableMuVoidLineComment(String patternStart, String patternEnd) {
        multiLineCommentActivated = true;
        multiLineCommentDelimiterStart = Pattern.compile(patternStart);
        multiLineCommentDelimiterEnd = Pattern.compile(patternEnd);
    }


    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        super.insertString(offset, str, attr);
        processChangedLines(offset, str.length());
    }


    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        processChangedLines(offset, length);
    }


    public void processChangedLines(int offset, int length) throws BadLocationException {
        String text = getText(0, getLength());
        highlightString(0, getLength(), true, STYLE_NORMAL);

        highlightKeywords(text);

        if (singleLineCommentActivated) {
            highlightMultipleLineComment(text);
        }

        if (multiLineCommentActivated) {
            highlightSingleLineComment(text);
        }

        highlightNumber(text);
    }


    private void highlightKeywords(String text) {
        Set keyw = keywordsToStyle.keySet();

        for (Object aKeyw : keyw) {
            String keyword = (String)aKeyw;
            String styleName = keywordsToStyle.get(keyword);

            Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                highlightString(matcher.start(), keyword.length(), true, styleName);
            }
        }
    }


    private void highlightNumber(String text) {
        Matcher matcher = patternNumberDelimter.matcher(text);

        while (matcher.find()) {
            highlightString(matcher.start(), matcher.end() - matcher.start(), true, STYLE_NUMBER);
        }
    }


    private void highlightMultipleLineComment(String text) {
        Matcher mlcStart = multiLineCommentDelimiterStart.matcher(text);
        Matcher mlcEnd = multiLineCommentDelimiterEnd.matcher(text);

        while (mlcStart.find()) {
            if (mlcEnd.find(mlcStart.end())) {
                highlightString(mlcStart.start(), (mlcEnd.end() - mlcStart.start()), true,
                                STYLE_COMMENT_MULTI_LINE);
            }
            else {
                highlightString(mlcStart.start(), getLength(), true, STYLE_COMMENT_MULTI_LINE);
            }
        }
    }


    private void highlightSingleLineComment(String text) {
        Matcher slc = singleLineCommentDelimter.matcher(text);

        while (slc.find()) {
            int line = rootElement.getElementIndex(slc.start());
            int endOffset = rootElement.getElement(line).getEndOffset() - 1;

            highlightString(slc.start(), (endOffset - slc.start()), true, STYLE_COMMENT_SINGLE_LINE);
        }
    }


    public void highlightString(int begin, int length, boolean replace, String styleName) {
        MutableAttributeSet style = styles.get(styleName);
        setCharacterAttributes(begin, length, style, replace);
    }


    // public String getLine(String content, int line)
    // {
    // Element lineElement = rootElement.getElement( line );
    // return content.substring(lineElement.getStartOffset(), lineElement.getEndOffset() - 1);
    // }
    public void recordStyle(String styleName, Color color, boolean bold, boolean italic, boolean underLine) {
        MutableAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        StyleConstants.setItalic(style, italic);
        StyleConstants.setUnderline(style, underLine);
        if (styles.get(styleName) != null) {
            styles.remove(styleName);
        }
        styles.put(styleName, style);
    }


    public void recordKeyWord(String keyword, String styleName) throws IllegalArgumentException {
        if (styles.get(styleName) == null) {
            throw new IllegalArgumentException("Unknown styleName :'" + styleName + "'");
        }
        keywordsToStyle.put(keyword, styleName);
    }
}
