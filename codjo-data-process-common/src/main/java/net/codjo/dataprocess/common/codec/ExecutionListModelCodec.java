/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.ExecutionListModelConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.List;
/**
 *
 */
public class ExecutionListModelCodec {
    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";


    public String encode(ExecutionListModel executionListModel) {
        XStream xStream = new XStream(new DomDriver("ISO-8859-1"));
        xStream.registerConverter(new ExecutionListModelConverter());
        xStream.alias("executionList", ExecutionListModel.class);
        return HEADER + xStream.toXML(executionListModel);
    }


    public String encodeList(List<ExecutionListModel> trtExecutionModelList) {
        XStream xStream = new XStream(new DomDriver("ISO-8859-1"));
        xStream.registerConverter(new ExecutionListModelConverter());
        xStream.alias("root", List.class);
        xStream.alias("executionList", ExecutionListModel.class);
        return HEADER + xStream.toXML(trtExecutionModelList);
    }


    public ExecutionListModel decode(String xml) {
        XStream xStream = new XStream(new DomDriver("ISO-8859-1"));
        xStream.registerConverter(new ExecutionListModelConverter());
        xStream.alias("executionList", ExecutionListModel.class);
        return (ExecutionListModel)xStream.fromXML(xml);
    }


    @SuppressWarnings("unchecked")
    public List<ExecutionListModel> decodeList(String xml) {
        XStream xStream = new XStream(new DomDriver("ISO-8859-1"));
        xStream.registerConverter(new ExecutionListModelConverter());
        xStream.alias("root", List.class);
        xStream.alias("executionList", ExecutionListModel.class);
        return (List<ExecutionListModel>)xStream.fromXML(xml);
    }
}
