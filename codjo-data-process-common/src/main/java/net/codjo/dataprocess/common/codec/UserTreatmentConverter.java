package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.model.ResultTable;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
/**
 *
 */
public class UserTreatmentConverter implements Converter {
    private boolean light;


    public UserTreatmentConverter(boolean light) {
        this.light = light;
    }


    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        UserTreatment userTreatment = (UserTreatment)source;
        write("id", userTreatment.getId(), writer);
        write("comment", userTreatment.getComment(), writer);
        write("title", userTreatment.getTitle(), writer);
        if (!light) {
            write("priority", userTreatment.getPriority(), writer);
            writer.startNode("resultTable");
            write("table", userTreatment.getResultTable().getTable(), writer);
            write("selectAllHandler", userTreatment.getResultTable().getSelectAllHandler(), writer);
            writer.endNode();
        }
    }


    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        TreatmentModel treatmentModel = new TreatmentModel();
        UserTreatment userTreatment = new UserTreatment(treatmentModel);
        treatmentModel.setId(reader.getAttribute("id"));
        treatmentModel.setComment(reader.getAttribute("comment"));
        treatmentModel.setTitle(reader.getAttribute("title"));
        if (!light) {
            userTreatment.setPriority(getIntValue("priority", reader));
            reader.moveDown();
            treatmentModel.setResultTable(new ResultTable(reader.getAttribute("table"),
                                                          reader.getAttribute("selectAllHandler")));
            reader.moveUp();
        }
        return userTreatment;
    }


    public boolean canConvert(Class type) {
        return type.equals(UserTreatment.class);
    }


    private static void write(String fieldName, Object value, HierarchicalStreamWriter writer) {
        if (value == null || "".equals(value)) {
            return;
        }
        writer.addAttribute(fieldName, String.valueOf(value));
    }


    private static int getIntValue(String name, HierarchicalStreamReader reader) {
        String value = reader.getAttribute(name);
        if (value != null) {
            return Integer.parseInt(value);
        }
        else {
            return 0;
        }
    }
}
