/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.model.UserTreatment;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.List;
/**
 *
 */
public class UserTreatmentListCodec {

    private UserTreatmentListCodec() {
    }


    public static String encode(List<UserTreatment> userTrtList, boolean light) {
        return createXStream(light).toXML(userTrtList);
    }


    @SuppressWarnings("unchecked")
    public static List<UserTreatment> decode(String userTrtListAsString, boolean light) {
        return (List<UserTreatment>)createXStream(light).fromXML(userTrtListAsString);
    }


    private static XStream createXStream(boolean light) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("root", List.class);
        xstream.alias("userTrt", UserTreatment.class);
        xstream.registerConverter(new UserTreatmentConverter(light));
        return xstream;
    }
}
