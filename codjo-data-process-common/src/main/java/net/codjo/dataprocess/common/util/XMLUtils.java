/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.util;
import net.codjo.dataprocess.common.DataProcessConstants;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 *
 */
public class XMLUtils {
    private XMLUtils() {
    }


    public static String nodeToString(Node node) throws TransformerException {
        return nodeToString(node, true, false);
    }


    public static String nodeToString(Node node, boolean replaceCRLF, boolean removeSpaceChar)
          throws TransformerException {
        String result;
        StringWriter strWriter = new StringWriter();

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.transform(new DOMSource(node), new StreamResult(strWriter));

        String str = getRidOfHeader(strWriter.toString());
        if (replaceCRLF) {
            result = flattenAndReplaceCRLF(str, removeSpaceChar);
        }
        else {
            result = flatten(str, removeSpaceChar);
        }
        return result;
    }


    public static String getRidOfHeader(String node) {
        int pos = node.indexOf("?>");
        pos = node.indexOf('<', pos);
        return node.substring(pos);
    }


    public static Document parse(String str) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(str)));
    }


    public static String flatten(String str, boolean removeSpaceChar) {
        StringBuilder buffer = new StringBuilder();
        boolean previousWhite = true;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\r' || ch == '\n') {
            }
            else if (Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
                if (removeSpaceChar) {
                    if (!previousWhite) {
                        buffer.append(' ');
                    }
                    previousWhite = true;
                }
                else {
                    buffer.append(ch);
                }
            }
            else {
                buffer.append(ch);
                previousWhite = false;
            }
        }
        return buffer.toString();
    }


    public static String flattenAndReplaceCRLF(String str, boolean removeSpaceChar) {
        return flattenAndReplaceCRLF(str, DataProcessConstants.SPECIAL_CHAR_REPLACER_N,
                                     DataProcessConstants.SPECIAL_CHAR_REPLACER_R, removeSpaceChar);
    }


    public static String flattenAndReplaceCRLF(String str, String replaceN,
                                               String replaceR, boolean removeSpaceChar) {
        StringBuilder buffer = new StringBuilder();
        boolean previousWhite = true;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\n') {
                buffer.append(replaceN);
            }
            else if (ch == '\r') {
                buffer.append(replaceR);
            }
            else if (Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
                if (removeSpaceChar) {
                    if (!previousWhite) {
                        buffer.append(' ');
                    }
                    previousWhite = true;
                }
                else {
                    buffer.append(ch);
                }
            }
            else {
                buffer.append(ch);
                previousWhite = false;
            }
        }
        return buffer.toString();
    }
}
