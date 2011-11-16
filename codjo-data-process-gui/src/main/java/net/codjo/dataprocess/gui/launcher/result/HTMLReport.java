/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.result;
import java.text.MessageFormat;
import javax.swing.JEditorPane;
/**
 *
 */
public class HTMLReport extends JEditorPane {
    private HTMLPart[] parts = {};
    private String headerPattern = null;
    private Object[] headerArguments = {};


    public HTMLReport() {
        setContentType("text/html");
        setEditable(false);
    }


    public void setHTMLParts(HTMLPart[] parts) {
        this.parts = parts;
        for (HTMLPart part : parts) {
            part.setHtmlReport(this);
        }
    }


    public void setHeaderArguments(Object[] headerArguments) {
        this.headerArguments = headerArguments;
    }


    public void setHeaderPattern(String headerPattern) {
        this.headerPattern = headerPattern;
    }


    public Object[] getHeaderArguments() {
        return headerArguments;
    }


    public String getHeaderPattern() {
        return headerPattern;
    }


    private String buildReport() {
        StringBuilder buffer = new StringBuilder("<html><body bgcolor=\"F5F5F5\">");
        buffer.append(getHeaderReport());
        buffer.append("<table border=\"0\" cellspacing=\"2\" width=\"100%\">");

        for (HTMLPart part : parts) {
            buffer.append(part.buildReport());
        }

        buffer.append("</table></body></html>");
        return buffer.toString();
    }


    void updateGui() {
        setText(buildReport());
    }


    private String getHeaderReport() {
        if (getHeaderPattern() != null && getHeaderArguments() != null) {
            return MessageFormat.format(getHeaderPattern(), getHeaderArguments()) + "<p>";
        }
        else {
            return "";
        }
    }
}
