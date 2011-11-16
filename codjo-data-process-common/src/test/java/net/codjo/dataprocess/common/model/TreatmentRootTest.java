package net.codjo.dataprocess.common.model;
import net.codjo.dataprocess.common.exception.TreatmentException;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class TreatmentRootTest {
    @Test
    public void testContent() throws Exception {
        TreatmentRoot root = new TreatmentRoot();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement1");
        treatmentModel.setTitle("titre 1");
        root.getTreatmentModelList().add(treatmentModel);

        TreatmentModel treatmentMode2 = new TreatmentModel();
        treatmentMode2.setId("traitement2");
        treatmentMode2.setTitle("titre 2");
        root.getTreatmentModelList().add(treatmentMode2);

        TreatmentModel treatmentModelTest = root.getTreatmentModel("traitement1");
        assertThat(treatmentModelTest, is(sameInstance(treatmentModel)));
        assertThat(treatmentModelTest.getTitle(), equalTo("titre 1"));

        assertThat(root.toString(), equalTo("traitement1;traitement2;"));

        try {
            root.getTreatmentModel("traitement_inconnue");
            fail("le test aurait dû échoué.");
        }
        catch (TreatmentException ex) {
            assertThat(ex.getMessage(), equalTo("Le traitement id = 'traitement_inconnue' est inexistant."));
        }

        root = new TreatmentRoot();
        assertThat(root.toString(), equalTo("treatmentModelList == null"));
    }
}
