package net.codjo.dataprocess.gui.util;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.model.TreatmentModel;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class TreatmentModelGuiAdapterTest {

    @Test
    public void typeFromGui() {
        TreatmentModel treatmentModel = new TreatmentModel();
        TreatmentModelGuiAdapter modelGuiAdapter = new TreatmentModelGuiAdapter(treatmentModel);

        modelGuiAdapter.setType(TreatmentModelGuiAdapter.SQL, true);
        assertThat(modelGuiAdapter.getType(), equalTo(TreatmentModelGuiAdapter.SQL));
        assertThat(treatmentModel.getType(), equalTo(DataProcessConstants.SQL_QUERY_TYPE_WITH_RESULT));
        modelGuiAdapter.setType(TreatmentModelGuiAdapter.SQL, false);
        assertThat(modelGuiAdapter.getType(), equalTo(TreatmentModelGuiAdapter.SQL));
        assertThat(treatmentModel.getType(), equalTo(DataProcessConstants.SQL_QUERY_TYPE));

        modelGuiAdapter.setType(TreatmentModelGuiAdapter.STORED_PROCEDURE, true);
        assertThat(modelGuiAdapter.getType(), equalTo(TreatmentModelGuiAdapter.STORED_PROCEDURE));
        assertThat(treatmentModel.getType(), equalTo(DataProcessConstants.STORED_PROC_TYPE_WITH_RESULT));
        modelGuiAdapter.setType(TreatmentModelGuiAdapter.STORED_PROCEDURE, false);
        assertThat(modelGuiAdapter.getType(), equalTo(TreatmentModelGuiAdapter.STORED_PROCEDURE));
        assertThat(treatmentModel.getType(), equalTo(DataProcessConstants.STORED_PROC_TYPE));

        modelGuiAdapter.setType(TreatmentModelGuiAdapter.JAVA_CODE, true);
        assertThat(modelGuiAdapter.getType(), equalTo(TreatmentModelGuiAdapter.JAVA_CODE));
        assertThat(treatmentModel.getType(), equalTo(DataProcessConstants.JAVA_TYPE_WITH_RESULT));
        modelGuiAdapter.setType(TreatmentModelGuiAdapter.JAVA_CODE, false);
        assertThat(modelGuiAdapter.getType(), equalTo(TreatmentModelGuiAdapter.JAVA_CODE));
        assertThat(treatmentModel.getType(), equalTo(DataProcessConstants.JAVA_TYPE));

        modelGuiAdapter.setType(TreatmentModelGuiAdapter.BEAN_SHELL, true);
        assertThat(modelGuiAdapter.getType(), equalTo(TreatmentModelGuiAdapter.BEAN_SHELL));
        assertThat(treatmentModel.getType(), equalTo(DataProcessConstants.BSH_TYPE_WITH_RESULT));
        modelGuiAdapter.setType(TreatmentModelGuiAdapter.BEAN_SHELL, false);
        assertThat(modelGuiAdapter.getType(), equalTo(TreatmentModelGuiAdapter.BEAN_SHELL));
        assertThat(treatmentModel.getType(), equalTo(DataProcessConstants.BSH_TYPE));
    }
}
