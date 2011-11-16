package net.codjo.dataprocess.common.report;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class OperationReport {
    static private final XStream XSTREAM = new XStream(new DomDriver());
    private TreatmentReports treatmentReports = new TreatmentReports();


    static {
        XSTREAM.alias("treatmentReport", TreatmentReport.class);
        XSTREAM.alias("operationReport", OperationReport.class);
        XSTREAM.alias("TreatmentReports", TreatmentReports.class);
        XSTREAM.addImplicitCollection(TreatmentReports.class, "treatmentReportList");
    }


    public OperationReport() {
    }


    public void addTreatmentReport(TreatmentReport treatmentReport) {
        treatmentReports.addTreatmentReport(treatmentReport);
    }


    public List<TreatmentReport> getTreatmentReports() {
        return treatmentReports.getTreatmentReports();
    }


    public boolean isError() {
        for (TreatmentReport treatmentReport : treatmentReports.getTreatmentReports()) {
            if (treatmentReport.isError()) {
                return true;
            }
        }
        return false;
    }


    public static synchronized String encode(OperationReport report) {
        return XSTREAM.toXML(report);
    }


    public static synchronized OperationReport decode(String xml) {
        return (OperationReport)XSTREAM.fromXML(xml);
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Traitement(s) OK : \n");
        for (TreatmentReport treatmentReport : treatmentReports.getTreatmentReports()) {
            if (!treatmentReport.isError()) {
                stringBuilder.append('\t').append(treatmentReport.toString()).append('\n');
            }
        }

        stringBuilder.append("Traitement(s) en erreur : \n");
        for (TreatmentReport treatmentReport : treatmentReports.getTreatmentReports()) {
            if (treatmentReport.isError()) {
                stringBuilder.append('\t').append(treatmentReport.toString()).append('\n');
            }
        }
        return stringBuilder.toString();
    }


    static class TreatmentReports {
        private List<TreatmentReport> treatmentReportList = new ArrayList<TreatmentReport>();


        void addTreatmentReport(TreatmentReport treatmentReport) {
            treatmentReportList.add(treatmentReport);
        }


        public List<TreatmentReport> getTreatmentReports() {
            return treatmentReportList;
        }
    }
}
