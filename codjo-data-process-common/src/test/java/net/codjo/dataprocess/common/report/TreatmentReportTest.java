package net.codjo.dataprocess.common.report;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class TreatmentReportTest {

    @Test
    public void testData() throws Exception {
        TreatmentReport treatmentReport = new TreatmentReport("treatment1");
        assertThat(treatmentReport.getTreatmentId(), equalTo("treatment1"));
        assertThat(treatmentReport.toString(), equalTo("treatment1: ok"));

        treatmentReport.setErrorMessage("error1");
        assertThat(treatmentReport.getErrorMessage(), equalTo("error1"));

        treatmentReport.setResult("result1");
        assertThat(treatmentReport.getResult(), equalTo("result1"));
        assertThat(treatmentReport.toString(), equalTo("treatment1: error1"));

        assertThat(treatmentReport.isError(), equalTo(true));
        treatmentReport.setErrorMessage(null);
        assertThat(treatmentReport.isError(), equalTo(false));
    }
}
