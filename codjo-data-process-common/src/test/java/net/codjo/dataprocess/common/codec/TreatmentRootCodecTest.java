package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ResultTable;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.TreatmentRoot;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.XmlUtil;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class TreatmentRootCodecTest {

    @Test
    public void decode() throws Exception {
        String content = FileUtil.loadContent(toFile("RepositoryTest.xml"));
        TreatmentRoot treatRoot = TreatmentRootCodec.decode(content);
        assertTreatmentRoot(treatRoot);
    }


    @Test
    public void decodeFromResources() {
        TreatmentRoot treatmentRoot = TreatmentRootCodec.decodeFromResources("RepositoryTest.xml");
        assertTreatmentRoot(treatmentRoot);
    }


    @Test
    public void encode() throws Exception {
        TreatmentRoot root = new TreatmentRoot();
        TreatmentModel trtModel =
              createTreatment("TraitementJavaExemple", "java", "TraitementJavaExemple",
                              "TraitementJavaExemple",
                              "net.codjo.creo.dataprocess.treatment.TraitementJavaExemple", null,
                              null);
        List<ArgModel> args = new ArrayList<ArgModel>();
        args.add(new ArgModel("maColonne", "$maColonne$", 1, 12));
        args.add(new ArgModel("monLibelle", "$monLibelle$", 2, 12));
        addArgument(trtModel, args);
        addTreatment(root, trtModel);

        trtModel = createTreatment("TraitementSqlExemple", "sql_with_result", "Test de la mort",
                                   "Test de la mort",
                                   "insert into MA_TABLE (MA_COLONNE, MON_LIBELLE) values (?, ?) select * from MA_TABLE",
                                   new ResultTable("AP_MA_TABLE", "selectAllPeriod"),
                                   "net.codjo.palanoob");
        args = new ArrayList<ArgModel>();
        args.add(new ArgModel("maColonne", "$maColonne$", 1, 12));
        args.add(new ArgModel("monLibelle", "$monLibelle$", 2, 12));
        addArgument(trtModel, args);
        addTreatment(root, trtModel);

        XmlUtil.assertEquals(FileUtil.loadContent(toFile("TreatmentRootTest.xml")),
                             TreatmentRootCodec.encode(root));
    }


    @Test
    public void encodeWithOneTreatment() throws Exception {
        ArgList argList = new ArgList();
        List<ArgModel> args = new ArrayList<ArgModel>();
        TreatmentModel trtModel = new TreatmentModel();

        trtModel.setId("monTraitement1");
        trtModel.setTitle("titre du traitement");
        trtModel.setTarget("net.codjo.Maclasse");
        trtModel.setGuiTarget("net.codjo.palanoob");
        trtModel.setComment("pioupiou aime les pommes et les éclairs aux chocolats");
        trtModel.setResultTable(new ResultTable("MA_TABLE", null));
        trtModel.setType("le troisième type");

        args.add(new ArgModel("periode", "200611", 1, 12));
        args.add(new ArgModel("typestock", "HB2", 2, 12));
        argList.setArgs(args);
        trtModel.setArguments(argList);

        String expected = FileUtil.loadContent(toFile("TreatmentRoot_oneTreatmentTest.xml"));
        TreatmentRoot root = new TreatmentRoot();
        addTreatment(root, trtModel);
        XmlUtil.assertEquals(expected, TreatmentRootCodec.encode(root));
    }


    private static void addTreatment(TreatmentRoot root, TreatmentModel trtModel) {
        root.getTreatmentModelList().add(trtModel);
    }


    private static void addArgument(TreatmentModel trtModel, List<ArgModel> args) {
        ArgList argList = new ArgList();
        argList.setArgs(args);
        trtModel.setArguments(argList);
    }


    private static void assertTreatmentRoot(TreatmentRoot treatRoot) {
        assertThat(treatRoot, notNullValue());
        assertThat(3, equalTo(treatRoot.getTreatmentModelList().size()));
        Iterator i1 = treatRoot.getTreatmentModelList().iterator();

        TreatmentModel item = (TreatmentModel)i1.next();
        assertThat("1", equalTo(item.getId()));
        assertThat("commentaire magnifique et étrange", equalTo(item.getComment()));
        assertThat("titre magnifique", equalTo(item.getTitle()));
        assertThat("stored_procedure", equalTo(item.getType()));
        assertThat("sp_maproc", equalTo(item.getTarget()));
        assertThat("net.codjo.wow.druide", equalTo(item.getGuiTarget()));
        ArgList argList = item.getArguments();
        assertThat(argList.getArgs().size() == 2, equalTo(true));

        item = (TreatmentModel)i1.next();
        assertThat("2", equalTo(item.getId()));
        assertThat("titre 2", equalTo(item.getTitle()));
        assertThat("sql", equalTo(item.getType()));
        assertThat("insert into MEHDI values (?,?)", equalTo(item.getTarget()));
        assertThat("net.codjo.wow.chaman", equalTo(item.getGuiTarget()));
        argList = item.getArguments();
        assertThat(argList.getArgs().size() == 3, equalTo(true));

        item = (TreatmentModel)i1.next();
        assertThat("commentaire pas magnifique", equalTo(item.getComment()));
        assertThat("titre pas magnifique", equalTo(item.getTitle()));
        assertThat("3", equalTo(item.getId()));
        assertThat("java", equalTo(item.getType()));
        assertThat("net.codjo.dataprocess.MonTraitement", equalTo(item.getTarget()));
        assertThat("net.codjo.wow.chaman.nerf.nref", equalTo(item.getGuiTarget()));
    }


    private static File toFile(String resourceName) {
        return new File(PathUtil.findResourcesFileDirectory(TreatmentModelCodecTest.class), resourceName);
    }


    private static TreatmentModel createTreatment(String id,
                                                  String type,
                                                  String comment,
                                                  String title,
                                                  String target,
                                                  ResultTable resultTable,
                                                  String guiTarget) {
        TreatmentModel trtModel = new TreatmentModel();
        trtModel.setId(id);
        trtModel.setTitle(title);
        trtModel.setTarget(target);
        trtModel.setComment(comment);
        trtModel.setResultTable(resultTable);
        trtModel.setType(type);
        trtModel.setGuiTarget(guiTarget);
        return trtModel;
    }
}
