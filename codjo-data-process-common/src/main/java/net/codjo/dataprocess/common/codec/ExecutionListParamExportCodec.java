package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.ExecutionListModelConverter;
import net.codjo.dataprocess.common.model.ExecutionListParamExport;
import net.codjo.dataprocess.common.model.ExecutionListParamExport.Family;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 *
 */
public class ExecutionListParamExportCodec {
    public String encode(ExecutionListParamExport executionListParamExport) {
        XStream xstream = new XStream(new DomDriver("ISO-8859-1"));
        xstream.alias("repository", ExecutionListParamExport.class);
        xstream.alias("executionList", ExecutionListModel.class);
        xstream.alias("family", Family.class);
        xstream.useAttributeFor("name", String.class);

        xstream.registerConverter(new ExecutionListModelConverter(true));
        xstream.addImplicitCollection(ExecutionListParamExport.class, "familyList");
        xstream.addImplicitCollection(Family.class, "executionListModelList");
        return xstream.toXML(executionListParamExport);
    }


    public ExecutionListParamExport decode(String xml) {
        XStream xstream = new XStream(new DomDriver("ISO-8859-1"));
        xstream.alias("repository", ExecutionListParamExport.class);
        xstream.alias("executionList", ExecutionListModel.class);
        xstream.alias("family", Family.class);
        xstream.useAttributeFor("name", String.class);

        xstream.registerConverter(new ExecutionListModelConverter(true));
        xstream.addImplicitCollection(ExecutionListParamExport.class, "familyList");
        xstream.addImplicitCollection(Family.class, "executionListModelList");
        return (ExecutionListParamExport)xstream.fromXML(xml);
    }
}
