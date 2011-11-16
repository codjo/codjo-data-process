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
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.XmlUtil;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class TreatmentModelCodecTest {

    @Test
    public void encode() throws Exception {
        ArgList argList = new ArgList();
        List<ArgModel> args = new ArrayList<ArgModel>();
        TreatmentModel trtModel = new TreatmentModel();

        trtModel.setId("monTraitement1");
        trtModel.setTitle("titre du traitement");
        trtModel.setTarget("net.codjo.Maclasse");
        trtModel.setGuiTarget("net.codjo.palanoob");
        trtModel.setComment("pioupiou n'aime plus les pommes et les éclairs aux chocolats");
        trtModel.setResultTable(new ResultTable("MA_TABLE", "selectAllPeriod"));
        trtModel.setType("le troisième type");

        args.add(new ArgModel("periode", "200611", 1, 12));
        args.add(new ArgModel("typestock", "HB2", 2, 12));
        args.add(new ArgModel("codePortefeuille", "HB3", 3, 12));
        argList.setArgs(args);
        trtModel.setArguments(argList);

        String expected = FileUtil.loadContent(toFile("TreatmentModelTest.xml"));
        String str = TreatmentModelCodec.encode(trtModel);
        XmlUtil.assertEquals(expected, str);
    }


    @Test
    public void decode() throws Exception {
        String content = FileUtil.loadContent(toFile("TreatmentModelTest.xml"));
        TreatmentModel treatmentModel = TreatmentModelCodec.decode(content);
        assertTreatmentModel(treatmentModel);
    }


    @Test
    public void decodeFromResources() {
        TreatmentModel treatmentModel = TreatmentModelCodec.decodeFromResources("TreatmentModelTest.xml");
        assertTreatmentModel(treatmentModel);
    }


    private static void assertTreatmentModel(TreatmentModel treatmentModel) {
        assertThat("monTraitement1", equalTo(treatmentModel.getId()));
        assertThat("pioupiou n'aime plus les pommes et les éclairs aux chocolats",
                   equalTo(treatmentModel.getComment()));
        assertThat("titre du traitement", equalTo(treatmentModel.getTitle()));
        assertThat("le troisième type", equalTo(treatmentModel.getType()));
        assertThat("net.codjo.Maclasse", equalTo(treatmentModel.getTarget()));
        assertThat("net.codjo.palanoob", equalTo(treatmentModel.getGuiTarget()));
    }


    private static File toFile(String resourceName) {
        return new File(PathUtil.findResourcesFileDirectory(TreatmentModelCodecTest.class), resourceName);
    }
}
