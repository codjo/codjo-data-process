/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.context;
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
public class DataProcessContextCodec {
    static private final XStream XSTREAM = new XStream(new DomDriver());


    static {
        XSTREAM.registerConverter(new HashMapConverter());
        XSTREAM.alias("DataProcessContext", Map.class);
    }


    private DataProcessContextCodec() {
    }


    public static synchronized String encode(DataProcessContext context) {
        Map<String, String> map = new HashMap<String, String>();
        context.putAllInMap(map);
        return XSTREAM.toXML(map);
    }


    @SuppressWarnings("unchecked")
    public static synchronized DataProcessContext decode(String xml) {
        DataProcessContext context = new DataProcessContext();
        Map<String, String> map = (Map<String, String>)XSTREAM.fromXML(xml);
        context.putAll(map);
        return context;
    }


    private static class HashMapConverter implements Converter {
        public boolean canConvert(Class type) {
            return type.equals(HashMap.class);
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
