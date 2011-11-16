package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.model.TreatmentRoot;
import com.thoughtworks.xstream.XStream;
/**
 *
 */
public class TreatmentRootCodec {
    private TreatmentRootCodec() {
    }


    public static synchronized String encode(TreatmentRoot treatmentRoot) {
        return createTreatmentRootXstream().toXML(treatmentRoot);
    }


    public static synchronized TreatmentRoot decode(String xmlContent) {
        return (TreatmentRoot)createTreatmentRootXstream().fromXML(xmlContent);
    }


    public static synchronized TreatmentRoot decodeFromResources(String uri) {
        return (TreatmentRoot)createTreatmentRootXstream().fromXML(TreatmentModelCodec.class.getResourceAsStream(
              uri));
    }


    private static XStream createTreatmentRootXstream() {
        XStream xstream = TreatmentModelCodec.createTreatmentModelXstream();
        xstream.alias("root", TreatmentRoot.class);
        xstream.addImplicitCollection(TreatmentRoot.class, "treatmentModelList");
        return xstream;
    }
}
