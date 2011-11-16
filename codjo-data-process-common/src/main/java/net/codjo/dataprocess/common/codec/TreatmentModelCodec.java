/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ResultTable;
import net.codjo.dataprocess.common.model.TreatmentModel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 *
 */
public class TreatmentModelCodec {

    private TreatmentModelCodec() {
    }


    public static synchronized String encode(TreatmentModel treatmentModel) {
        return createTreatmentModelXstream().toXML(treatmentModel);
    }


    public static synchronized TreatmentModel decode(String xmlContent) {
        return (TreatmentModel)createTreatmentModelXstream().fromXML(xmlContent);
    }


    public static synchronized TreatmentModel decodeFromResources(String uri) {
        return (TreatmentModel)createTreatmentModelXstream()
              .fromXML(TreatmentModelCodec.class.getResourceAsStream(uri));
    }


    static XStream createTreatmentModelXstream() {
        XStream xstream = new XStream(new DomDriver("ISO-8859-1"));
        xstream.addImplicitCollection(ArgList.class, "args");

        xstream.alias("treatment", TreatmentModel.class);
        xstream.useAttributeFor("id", String.class);
        xstream.useAttributeFor("type", String.class);
        xstream.aliasField("gui-target", TreatmentModel.class, "guiTarget");
        xstream.aliasField("result-table", TreatmentModel.class, "resultTable");

        xstream.alias("arg", ArgModel.class);
        xstream.registerConverter(new ArgModelConverter());

        xstream.registerConverter(new ResultTableConverter());
        return xstream;
    }


    private static class ArgModelConverter implements Converter {
        public boolean canConvert(Class type) {
            return type == ArgModel.class;
        }


        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            ArgModel argument = (ArgModel)source;
            write("name", argument.getName(), writer);
            write("position", argument.getPosition(), writer);
            write("type", argument.getType(), writer);
            write("value", argument.getValue(), writer);
        }


        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

            return new ArgModel(reader.getAttribute("name"),
                                reader.getAttribute("value"),
                                getIntValue(reader, "position"),
                                getIntValue(reader, "type"));
        }


        private static int getIntValue(HierarchicalStreamReader reader, String name) {
            return Integer.parseInt(reader.getAttribute(name));
        }


        private static void write(String fieldName, Object value, HierarchicalStreamWriter writer) {
            if (value == null) {
                return;
            }
            writer.addAttribute(fieldName, String.valueOf(value));
        }
    }

    private static class ResultTableConverter implements Converter {
        public boolean canConvert(Class type) {
            return type == ResultTable.class;
        }


        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            ResultTable resultTable = (ResultTable)source;
            write("selectAllHandler", resultTable.getSelectAllHandler(), writer);
            writer.setValue(resultTable.getTable());
        }


        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return new ResultTable(reader.getValue(), reader.getAttribute("selectAllHandler"));
        }


        private static void write(String fieldName, Object value, HierarchicalStreamWriter writer) {
            if (value == null) {
                return;
            }
            writer.addAttribute(fieldName, String.valueOf(value));
        }
    }
}
