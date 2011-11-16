package net.codjo.dataprocess.common.codec;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class ListXmlCodec {
    private static final XStream XSTREAM = new XStream(new DomDriver());


    static {
        XSTREAM.aliasType("list", List.class);
        XSTREAM.registerConverter(new ArrayListConverter());
    }


    private ListXmlCodec() {
    }


    public static synchronized String encode(List<String> list) {
        return XSTREAM.toXML(list);
    }


    @SuppressWarnings("unchecked")
    public static synchronized List<String> decode(String data) {
        return (List<String>)XSTREAM.fromXML(data);
    }


    private static class ArrayListConverter implements Converter {
        public boolean canConvert(Class type) {
            return List.class.isAssignableFrom(type);
        }


        @SuppressWarnings("unchecked")
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            List<String> list = (List<String>)source;
            for (String data : list) {
                writer.startNode("data");
                writer.addAttribute("value", data);
                writer.endNode();
            }
        }


        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            List<String> list = new ArrayList<String>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String data = reader.getAttribute("value");
                list.add(data);
                reader.moveUp();
            }
            return list;
        }
    }
}