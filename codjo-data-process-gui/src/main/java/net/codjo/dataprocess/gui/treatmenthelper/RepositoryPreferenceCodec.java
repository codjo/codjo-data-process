package net.codjo.dataprocess.gui.treatmenthelper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.Reader;
/**
 *
 */
public class RepositoryPreferenceCodec {
    private RepositoryPreferenceCodec() {
    }


    public static XStream createXstream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("preferences", RepositoryPreference.class);
        xstream.omitField(RepositoryPreference.class, "propertyChangeSupport");
        return xstream;
    }


    public static RepositoryPreference fromXml(String xml) {
        return (RepositoryPreference)createXstream().fromXML(xml);
    }


    public static RepositoryPreference fromXml(Reader xmlReader) {
        return (RepositoryPreference)createXstream().fromXML(xmlReader);
    }


    public static String toXml(RepositoryPreference repositoryPreference) {
        return createXstream().toXML(repositoryPreference);
    }
}
