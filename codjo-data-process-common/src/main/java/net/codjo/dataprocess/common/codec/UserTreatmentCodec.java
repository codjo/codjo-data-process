package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.model.UserTreatment;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 *
 */
public class UserTreatmentCodec {

    private UserTreatmentCodec() {
    }


    public static synchronized String encode(UserTreatment userTreatment, boolean light) {
        return createXStream(light).toXML(userTreatment);
    }


    public static synchronized UserTreatment decode(String data, boolean light) {
        return (UserTreatment)createXStream(light).fromXML(data);
    }


    private static XStream createXStream(boolean light) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("userTrt", UserTreatment.class);
        xstream.registerConverter(new UserTreatmentConverter(light));
        return xstream;
    }
}
