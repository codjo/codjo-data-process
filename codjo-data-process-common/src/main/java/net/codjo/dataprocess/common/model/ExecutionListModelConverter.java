package net.codjo.dataprocess.common.model;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class ExecutionListModelConverter implements Converter {
    private boolean encodeForExport;


    public ExecutionListModelConverter() {
        this(false);
    }


    public ExecutionListModelConverter(boolean encodeForExport) {
        this.encodeForExport = encodeForExport;
    }


    public boolean canConvert(Class type) {
        return type.equals(ExecutionListModel.class);
    }


    @SuppressWarnings("unchecked")
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        ExecutionListModel executionListModel = (ExecutionListModel)source;
        if (encodeForExport) {
            write("name", executionListModel.getName(), writer);
            write("priority", String.valueOf(executionListModel.getPriority()), writer);
        }
        else {
            write("id", String.valueOf(executionListModel.getId()), writer);
            write("name", executionListModel.getName(), writer);
            write("status", String.valueOf(executionListModel.getStatus()), writer);
            write("priority", String.valueOf(executionListModel.getPriority()), writer);
            write("familyId", String.valueOf(executionListModel.getFamilyId()), writer);
        }

        List<UserTreatment> list = executionListModel.getSortedTreatmentList();
        for (UserTreatment userTrt : list) {
            writer.startNode("treatment");
            if (encodeForExport) {
                write("id", userTrt.getId(), writer);
                write("priority", String.valueOf(userTrt.getPriority()), writer);
            }
            else {
                write("id", userTrt.getId(), writer);
                write("comment", userTrt.getComment(), writer);
                write("priority", String.valueOf(userTrt.getPriority()), writer);
                write("title", userTrt.getTitle(), writer);
                if (userTrt.getResultTable() != null) {
                    write("resultTable", userTrt.getResultTable().getTable(), writer);
                    write("selectAllHandler", userTrt.getResultTable().getSelectAllHandler(), writer);
                }
            }
            writer.endNode();
        }
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


    private static void updatePriorityMap(ExecutionListModel executionListModel) {
        Map<UserTreatment, Integer> priorityMap = executionListModel.getPriorityMap();
        if (priorityMap == null) {
            return;
        }
        for (UserTreatment usrTrt : priorityMap.keySet()) {
            priorityMap.put(usrTrt, usrTrt.getPriority());
        }
    }


    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ExecutionListModel execListModel = new ExecutionListModel();

        execListModel.setId(getIntValue("id", reader));
        execListModel.setName(reader.getAttribute("name"));
        execListModel.setStatus(getIntValue("status", reader));
        execListModel.setPriority(getIntValue("priority", reader));
        execListModel.setFamilyId(getIntValue("familyId", reader));

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            TreatmentModel treatmentModel = new TreatmentModel();
            treatmentModel.setId(reader.getAttribute("id"));
            treatmentModel.setComment(reader.getAttribute("comment"));
            treatmentModel.setTitle(reader.getAttribute("title"));
            ResultTable resultTable = new ResultTable(reader.getAttribute("resultTable"),
                                                      reader.getAttribute("selectAllHandler"));
            treatmentModel.setResultTable(resultTable);
            UserTreatment usrTrt = new UserTreatment(treatmentModel);
            usrTrt.setPriority(getIntValue("priority", reader));
            execListModel.addUserTreatment(usrTrt);
            reader.moveUp();
        }
        updatePriorityMap(execListModel);
        return execListModel;
    }
}
