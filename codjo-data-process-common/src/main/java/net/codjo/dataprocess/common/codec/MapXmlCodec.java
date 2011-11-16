package net.codjo.dataprocess.common.codec;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
/**
 *
 */
public class MapXmlCodec {
    private static final XStream XSTREAM = new XStream(new DomDriver());


    static {
        XSTREAM.aliasType("map", Map.class);
        XSTREAM.registerConverter(new HashMapConverter());
    }


    private MapXmlCodec() {
    }


    public static synchronized String encode(Map<String, String> map) {
        return XSTREAM.toXML(map);
    }


    @SuppressWarnings("unchecked")
    public static synchronized Map<String, String> decode(String data) {
        return (Map<String, String>)XSTREAM.fromXML(data);
    }


    private static class HashMapConverter implements Converter {
        public boolean canConvert(Class type) {
            return Map.class.isAssignableFrom(type);
        }


        @SuppressWarnings("unchecked")
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            Map<String, String> map = (Map<String, String>)source;
            for (Entry<String, String> entry : map.entrySet()) {
                writer.startNode("data");
                writer.addAttribute("key", entry.getKey());
                writer.addAttribute("value", entry.getValue());
                writer.endNode();
            }
        }


        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            Map<String, String> map = new HashMap<String, String>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String key = reader.getAttribute("key");
                String value = reader.getAttribute("value");
                map.put(key, value);
                reader.moveUp();
            }
            return map;
        }
    }
}
