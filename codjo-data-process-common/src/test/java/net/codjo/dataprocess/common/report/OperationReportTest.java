package net.codjo.dataprocess.common.report;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class OperationReportTest {

    @Test
    public void testEncodeDecode() throws Exception {
        OperationReport report = new OperationReport();
        report.addTreatmentReport(new TreatmentReport("trt1", "res1", "err1"));
        report.addTreatmentReport(new TreatmentReport("trt2", "res2", "err2"));
        report.addTreatmentReport(new TreatmentReport("trt3", "res3", null));
        String result = OperationReport.encode(report);
        assertThat(result,
                   equalTo(
                         "<operationReport>\n"
                         + "  <treatmentReports>\n"
                         + "    <treatmentReport>\n"
                         + "      <treatmentId>trt1</treatmentId>\n"
                         + "      <result>res1</result>\n"
                         + "      <errorMessage>err1</errorMessage>\n"
                         + "    </treatmentReport>\n"
                         + "    <treatmentReport>\n"
                         + "      <treatmentId>trt2</treatmentId>\n"
                         + "      <result>res2</result>\n"
                         + "      <errorMessage>err2</errorMessage>\n"
                         + "    </treatmentReport>\n"
                         + "    <treatmentReport>\n"
                         + "      <treatmentId>trt3</treatmentId>\n"
                         + "      <result>res3</result>\n"
                         + "    </treatmentReport>\n"
                         + "  </treatmentReports>\n"
                         + "</operationReport>"));

        OperationReport report1 = OperationReport.decode(result);
        assertThat(report1.getTreatmentReports().size(), equalTo(3));
        assertThat(report1.toString(),
                   equalTo("Traitement(s) OK : \n"
                           + "\ttrt3: ok\n"
                           + "Traitement(s) en erreur : \n"
                           + "\ttrt1: err1\n"
                           + "\ttrt2: err2\n"));

        OperationReport report2 = new OperationReport();
        report2.addTreatmentReport(new TreatmentReport("trt1", "res1", null));
        report2.addTreatmentReport(new TreatmentReport("trt2", "res2", null));
        assertThat(report2.toString(), equalTo("Traitement(s) OK : \n"
                                               + "\ttrt1: ok\n"
                                               + "\ttrt2: ok\n"
                                               + "Traitement(s) en erreur : \n"));

        report2 = new OperationReport();
        report2.addTreatmentReport(new TreatmentReport("trt1", "res1", "err1"));
        report2.addTreatmentReport(new TreatmentReport("trt2", "res2", "err2"));
        assertThat(report2.toString(), equalTo("Traitement(s) OK : \n"
                                               + "Traitement(s) en erreur : \n"
                                               + "\ttrt1: err1\n"
                                               + "\ttrt2: err2\n"));
    }


    @Test
    public void addTreatmentReport() throws Exception {
        OperationReport report = new OperationReport();
        assertThat(report.isError(), equalTo(false));

        report.addTreatmentReport(new TreatmentReport("trt1", "res1", "err1"));
        report.addTreatmentReport(new TreatmentReport("trt1", "res1", null));
        assertThat(report.isError(), equalTo(true));

        report = new OperationReport();
        report.addTreatmentReport(new TreatmentReport("trt1", "res1", null));
        assertThat(report.isError(), equalTo(false));
    }
}
