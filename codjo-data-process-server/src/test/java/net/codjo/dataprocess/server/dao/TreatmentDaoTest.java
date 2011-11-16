package net.codjo.dataprocess.server.dao;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.datagen.DatagenFixture;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.TreatmentModelCodec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.dataprocess.common.util.XMLUtils;
import net.codjo.dataprocess.server.handlerhelper.TreatmentHandlerHelper;
import net.codjo.dataprocess.server.repository.Repository;
import net.codjo.dataprocess.server.util.SQLUtil;
import net.codjo.dataprocess.server.util.TestUtils;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.tokio.TokioFixture;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static net.codjo.dataprocess.common.DataProcessConstants.Command.COPY;
import static net.codjo.dataprocess.common.DataProcessConstants.Command.CREATE;
import static net.codjo.dataprocess.common.DataProcessConstants.Command.DELETE;
import static net.codjo.dataprocess.common.DataProcessConstants.Command.IS_EXIST;
import static net.codjo.dataprocess.common.DataProcessConstants.Command.READ;
import static net.codjo.dataprocess.common.DataProcessConstants.Command.UPDATE;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class TreatmentDaoTest {
    private TreatmentDao treatmentDao = new TreatmentDao();
    private static final TokioFixture TOKIO = new TokioFixture(TreatmentDaoTest.class);
    private static final DatagenFixture DATAGEN = new DatagenFixture(TreatmentDaoTest.class);
    private static final CompositeFixture COMPOSITE_FIXTURE = new CompositeFixture(TOKIO, DATAGEN);


    @BeforeClass
    public static void beforeClass() throws Exception {
        COMPOSITE_FIXTURE.doSetUp();
        try {
            JdbcFixture jdbcFixture = TOKIO.getJdbcFixture();
            jdbcFixture.advanced().dropAllObjects();
            DATAGEN.generate();
            TestUtils.initScript(jdbcFixture, DATAGEN, "T_TRANSFER.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_DP_CONTEXT.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_REPOSITORY.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_REPOSITORY_CONTENT.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_FAMILY.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_EXECUTION_LIST.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_TREATMENT.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_EXECUTION_LIST_STATUS.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_TREATMENT_STATUS.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_DEPENDENCY.tab");
        }
        catch (Exception e) {
            COMPOSITE_FIXTURE.doTearDown();
            fail(e.getLocalizedMessage());
        }
    }


    @AfterClass
    public static void afterClass() throws Exception {
        COMPOSITE_FIXTURE.doTearDown();
    }


    @Test
    public void load() throws Exception {
        // étalon
        List<ExecutionListModel> expectedList = new ArrayList<ExecutionListModel>();
        expectedList.add(buildModel(1, "liste1", 1, 0));
        expectedList.add(buildModel(2, "liste2", 2, 1));

        UserTreatment usrTrt = expectedList.get(1).getPriorityMap().keySet().iterator().next();
        expectedList.get(1).getPriorityMap().remove(usrTrt);

        TOKIO.insertInputInDb("LOAD");
        Repository.reinitializeRepositoryCache();
        initRepository(1, "TreatmentTest.xml");

        List<ExecutionListModel> resList = treatmentDao.getExecutionListModel(TOKIO.getConnection(), 1, 1);
        assertThat(expectedList.size(), equalTo(resList.size()));
        verifyResult(resList, expectedList);
    }


    @Test
    public void getExecutionListModel() throws Exception {
        TOKIO.insertInputInDb("LOAD");
        Repository.reinitializeRepositoryCache();
        initRepository(1, "TreatmentTest.xml");
        ExecutionListModel execListModel = treatmentDao.getExecutionListModel(TOKIO.getConnection(), "liste1",
                                                                              1, 1);
        assertThat(execListModel.getName(), equalTo("liste1"));

        execListModel = treatmentDao.getExecutionListModel(TOKIO.getConnection(), "liste2", 1, 1);
        assertThat(execListModel.getName(), equalTo("liste2"));

        try {
            execListModel = treatmentDao.getExecutionListModel(TOKIO.getConnection(), "liste4", 1, 1);
            assertThat(execListModel.getName(), equalTo("liste4"));
            fail("Le test aurait dû échoué.");
        }
        catch (Exception ex) {
            assertThat(ex.getLocalizedMessage(),
                       equalTo("Liste de traitements 'liste4' non trouvée (repository id = 1, family id = 1)"));
        }
    }


    private static void initRepository(int repositoryId, String fileName) throws Exception {
        String content = loadFileAsString(fileName);

        TOKIO.executeQuery("delete PM_REPOSITORY_CONTENT");
        String sql =
              "insert into PM_REPOSITORY_CONTENT (REPOSITORY_CONTENT_ID, REPOSITORY_ID, TREATMENT_ID, CONTENT)"
              + " values (?, ?, ?, ?)";

        final Document doc = XMLUtils.parse(content);
        NodeList nodes = doc.getElementsByTagName(DataProcessConstants.TREATMENT_ENTITY_XML);

        Connection con = TOKIO.getConnection();
        PreparedStatement pStmt = con.prepareStatement(sql);
        try {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String treatmentId = node.getAttributes().getNamedItem("id").getNodeValue();
                String nodeValue = XMLUtils.nodeToString(node);
                pStmt.setInt(1, SQLUtil.getNextId(con, "PM_REPOSITORY_CONTENT", "REPOSITORY_CONTENT_ID"));
                pStmt.setInt(2, repositoryId);
                pStmt.setString(3, treatmentId);
                pStmt.setString(4, nodeValue);
                pStmt.executeUpdate();
            }
        }
        finally {
            pStmt.close();
        }

        sql = "delete PM_REPOSITORY insert into PM_REPOSITORY (REPOSITORY_ID) values (?)";
        pStmt = con.prepareStatement(sql);
        try {
            pStmt.setInt(1, repositoryId);
            pStmt.executeUpdate();
        }
        finally {
            pStmt.close();
        }
    }


    @Test
    public void save() throws Exception {
        List<ExecutionListModel> trtList = new ArrayList<ExecutionListModel>();
        trtList.add(buildModel(10, "liste1", 1, 45));
        trtList.add(buildModel(20, "liste2", 2, 95));

        Map<UserTreatment, Integer> prioMap = trtList.get(1).getPriorityMap();
        UserTreatment toBeRemove = null;
        for (UserTreatment userTreatment : prioMap.keySet()) {
            toBeRemove = userTreatment;
            if ((toBeRemove.getPriority() == 3) && ("TRAITEMENT_3".equals(toBeRemove.getId()))) {
                break;
            }
        }

        prioMap.remove(toBeRemove);

        TOKIO.insertInputInDb("SAVE");
        treatmentDao.save(TOKIO.getConnection(), trtList, 1, 0);
        TOKIO.assertAllOutputs("SAVE");
    }


    @Test
    public void manageTreatmentModel() throws Exception {
        String treatmentContent1 =
              "<treatment id=\"CexportSharesByCompany\" type=\"sql\" scope=\"CONTROL\">\n"
              + "  <target>[$][@]            delete TC_CSCOP_TITRESGPFO[$][@]            insert into TC_CSCOP_TITRESGPFO[$][@]            (CODE_VALEUR,[$][@]            LIBELLE_VALEUR,[$][@]            CATEGORIE_VALEUR)[$][@]            values (&apos;RIEN&apos;,&apos;1;AG;AG;100;&apos;+left(?,4)+&apos;;&apos;+right(?,2),&apos;RIEN&apos;)[$][@][$][@]            insert into TC_CSCOP_TITRESGPFO[$][@]            (CODE_VALEUR,[$][@]            LIBELLE_VALEUR,[$][@]            CATEGORIE_VALEUR)[$][@]            select &apos;RIEN&apos;,&apos;2;20;&apos;[$][@]            + CODE_ENTITE_SAP_CIE +&apos;;&apos;[$][@]            + CODE_ENTITE_SAP_PTF +&apos;;&apos;[$][@]            + convert(varchar(25), convert(numeric(24),VARIATION_QUANTITE))[$][@]            +&apos;;0;0;0;0;&apos;[$][@]            + case when (VARIATION_QUANTITE &gt; 0) then[$][@]            convert(varchar(25),convert(numeric(24),VARIATION_QUANTITE))[$][@]            else &apos;0&apos; end +&apos;;&apos;[$][@]            + case when (VARIATION_QUANTITE &lt; 0) then[$][@]            convert(varchar(25),convert(numeric(24),VARIATION_QUANTITE))[$][@]            else &apos;0&apos; end +&apos;;0&apos;[$][@]            ,&apos;RIEN&apos;[$][@]            from EXPORT_BO_SPLIT[$][@]            where EXPORT_BO_SPLIT.PERIODE = ?[$][@]            and CODE_ENTITE_SAP_CIE = ?[$][@]            and CODE_PORTEFEUILLE = &apos;SHARE&apos;[$][@]            order by CODE_ENTITE_SAP_CIE,CODE_ENTITE_SAP_PTF DESC[$][@]        </target>\n"
              + "  <arguments>\n"
              + "    <arg name=\"periode\" position=\"1\" type=\"12\" value=\"$periode$\"/>\n"
              + "    <arg name=\"periode\" position=\"2\" type=\"12\" value=\"$periode$\"/>\n"
              + "    <arg name=\"periode\" position=\"3\" type=\"12\" value=\"$periode$\"/>\n"
              + "    <arg name=\"company\" position=\"4\" type=\"12\" value=\"$company$\"/>\n"
              + "  </arguments>\n"
              + "  <comment></comment>\n"
              + "  <title>Export par compagnie</title>\n"
              + "  <result-table>TC_CSCOP_TITRESGPFO</result-table>\n"
              + "</treatment>";
        String treatmentContent2 =
              "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
              + "<treatment id=\"C60TreatmentHistoriseRapprochement\" scope=\"TREATMENT\" type=\"java\">"
              + "<comment>Insertion dans l'historique des rapprochements</comment>"
              + "<title>C60.TreatmentHistoriseRapprochement</title>"
              + "<target>net.codjo.creo.server.dataprocess.treatment.c60.C60TreatmentHistoriseRapprochement</target>"
              + "<result-table/>"
              + "<arguments/>"
              + "</treatment>";
        String treatmentContentUpdate
              = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                + "<treatment id=\"C60TreatmentHistoriseRapprochement\" scope=\"TREATMENT\" type=\"java\">"
                + "<comment>pomme pomme pomme pomme !</comment>"
                + "<title>C60.TreatmentHistoriseRapprochement</title>"
                + "<target>net.codjo.creo.server.dataprocess.treatment.c60.C60TreatmentHistoriseRapprochement</target>"
                + "<result-table/>"
                + "<arguments>"
                + "<arg position=\"1\" type=\"12\" name=\"datecourante\" value=\"$datecourante$\"/>"
                + "<arg position=\"2\" type=\"12\" name=\"datecourante\" value=\"$datecourante$\"/>"
                + "</arguments>"
                + "</treatment>";

        Connection con = TOKIO.getConnection();
        deleteTables(con);

        assertThat(DataProcessConstants.NO_RESULT,
                   equalTo(treatmentDao.manageTreatmentModel(con, CREATE, 1, treatmentContent1)));
        assertThat(DataProcessConstants.NO_RESULT,
                   equalTo(treatmentDao.manageTreatmentModel(con, CREATE, 1, treatmentContent2)));

        try {
            assertThat(DataProcessConstants.NO_RESULT,
                       equalTo(treatmentDao.manageTreatmentModel(con, UPDATE, 2, treatmentContentUpdate)));
            fail("Le test devrait échouer : le traitement est inexistant dans ce repository");
        }
        catch (TreatmentException ex) {
            assertThat(
                  "\n'C60TreatmentHistoriseRapprochement' est inexistant dans le repository (repositoryId = 2)",
                  equalTo(ex.getMessage()));
        }

        assertThat(DataProcessConstants.NO_RESULT,
                   equalTo(treatmentDao.manageTreatmentModel(con, UPDATE, 1,
                                                             treatmentContentUpdate)));

        String xmlContent = treatmentDao.manageTreatmentModel(con, READ, 1, treatmentContentUpdate);
        TreatmentModel treatmentModel = TreatmentModelCodec.decode(xmlContent);
        assertThat("pomme pomme pomme pomme !", equalTo(treatmentModel.getComment()));
        assertThat(2, equalTo(treatmentModel.getArguments().getArgs().size()));

        assertThat(DataProcessConstants.NO_RESULT,
                   equalTo(treatmentDao.manageTreatmentModel(con, DELETE, 1,
                                                             treatmentContentUpdate)));
        try {
            assertThat(DataProcessConstants.NO_RESULT,
                       equalTo(treatmentDao.manageTreatmentModel(con, DELETE, 1,
                                                                 treatmentContentUpdate)));
            fail("Le test devrait échouer : le traitement devrait déjà être supprimé.");
        }
        catch (TreatmentException ex) {
            assertThat(
                  "\n'C60TreatmentHistoriseRapprochement' est inexistant dans le repository (repositoryId = 1)",
                  equalTo(ex.getMessage()));
        }

        xmlContent = treatmentDao.manageTreatmentModel(con, READ, 1, treatmentContent1);
        treatmentModel = TreatmentModelCodec.decode(xmlContent);
        assertThat(treatmentModel.getTarget(), equalTo("delete TC_CSCOP_TITRESGPFO\n"
                                                       + "            insert into TC_CSCOP_TITRESGPFO\n"
                                                       + "            (CODE_VALEUR,\n"
                                                       + "            LIBELLE_VALEUR,\n"
                                                       + "            CATEGORIE_VALEUR)\n"
                                                       + "            values ('RIEN','1;AG;AG;100;'+left(?,4)+';'+right(?,2),'RIEN')\n"
                                                       + "\n"
                                                       + "            insert into TC_CSCOP_TITRESGPFO\n"
                                                       + "            (CODE_VALEUR,\n"
                                                       + "            LIBELLE_VALEUR,\n"
                                                       + "            CATEGORIE_VALEUR)\n"
                                                       + "            select 'RIEN','2;20;'\n"
                                                       + "            + CODE_ENTITE_SAP_CIE +';'\n"
                                                       + "            + CODE_ENTITE_SAP_PTF +';'\n"
                                                       + "            + convert(varchar(25), convert(numeric(24),VARIATION_QUANTITE))\n"
                                                       + "            +';0;0;0;0;'\n"
                                                       + "            + case when (VARIATION_QUANTITE > 0) then\n"
                                                       + "            convert(varchar(25),convert(numeric(24),VARIATION_QUANTITE))\n"
                                                       + "            else '0' end +';'\n"
                                                       + "            + case when (VARIATION_QUANTITE < 0) then\n"
                                                       + "            convert(varchar(25),convert(numeric(24),VARIATION_QUANTITE))\n"
                                                       + "            else '0' end +';0'\n"
                                                       + "            ,'RIEN'\n"
                                                       + "            from EXPORT_BO_SPLIT\n"
                                                       + "            where EXPORT_BO_SPLIT.PERIODE = ?\n"
                                                       + "            and CODE_ENTITE_SAP_CIE = ?\n"
                                                       + "            and CODE_PORTEFEUILLE = 'SHARE'\n"
                                                       + "            order by CODE_ENTITE_SAP_CIE,CODE_ENTITE_SAP_PTF DESC"));
        assertThat(Boolean.valueOf(treatmentDao.manageTreatmentModel(con, IS_EXIST, 1, treatmentContent1)),
                   equalTo(true));
        assertThat(Boolean.valueOf(treatmentDao.manageTreatmentModel(con,
                                                                     IS_EXIST,
                                                                     1,
                                                                     treatmentContentUpdate)),
                   equalTo(false));

        assertThat(DataProcessConstants.NO_RESULT,
                   equalTo(treatmentDao.manageTreatmentModel(con, CREATE, 1, treatmentContent2)));
        treatmentDao.manageTreatmentModel(con, COPY, 1, "3");
        assertThat(Boolean.valueOf(treatmentDao.manageTreatmentModel(con, IS_EXIST, 3, treatmentContent1)),
                   equalTo(true));
        assertThat(Boolean.valueOf(treatmentDao.manageTreatmentModel(con, IS_EXIST, 3, treatmentContent2)),
                   equalTo(true));
    }


    @Test
    public void deleteTreatmentModel() throws Exception {
        String treatmentContentAa
              = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                + "<treatment id=\"traitement_aa\" scope=\"TREATMENT\" type=\"java\">"
                + "<comment>traitement_aa traitement_aa traitement_aa</comment>"
                + "<title>Mon traitement aa</title>"
                + "<target>net.codjo.creo.server.dataprocess.treatment.c60.traitement_aa</target>"
                + "<result-table/>"
                + "<arguments>"
                + "<arg position=\"1\" type=\"12\" name=\"datecourante\" value=\"$datecourante$\"/>"
                + "<arg position=\"2\" type=\"12\" name=\"datecourante\" value=\"$datecourante$\"/>"
                + "</arguments>"
                + "</treatment>";
        String treatmentContentCc
              = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                + "<treatment id=\"traitement_cc\" scope=\"TREATMENT\" type=\"java\">"
                + "<comment>traitement_cc traitement_cc traitement_cc</comment>"
                + "<title>Mon traitement cc</title>"
                + "<target>net.codjo.creo.server.dataprocess.treatment.c60.traitement_cc</target>"
                + "<result-table/>"
                + "<arguments>"
                + "<arg position=\"1\" type=\"12\" name=\"datecourante\" value=\"$datecourante$\"/>"
                + "<arg position=\"2\" type=\"12\" name=\"datecourante\" value=\"$datecourante$\"/>"
                + "</arguments>"
                + "</treatment>";
        String treatmentContentDd
              = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                + "<treatment id=\"traitement_dd\" scope=\"TREATMENT\" type=\"java\">"
                + "<comment>traitement_cc traitement_cc traitement_cc</comment>"
                + "<title>Mon traitement cc</title>"
                + "<target>net.codjo.creo.server.dataprocess.treatment.c60.traitement_cc</target>"
                + "<result-table/>"
                + "<arguments>"
                + "<arg position=\"1\" type=\"12\" name=\"datecourante\" value=\"$datecourante$\"/>"
                + "<arg position=\"2\" type=\"12\" name=\"datecourante\" value=\"$datecourante$\"/>"
                + "</arguments>"
                + "</treatment>";

        TOKIO.insertInputInDb("delete_treatmentModel");
        try {
            TreatmentHandlerHelper.manageTreatmentModel(TOKIO.getConnection(),
                                                        DataProcessConstants.Command.DELETE,
                                                        1, treatmentContentAa);
            fail("Ce test devrait echoué car le traitement 'traitement_aa' est utilisé dans des listes de traitement!");
        }
        catch (TreatmentException ex) {
            assertThat(ex.getLocalizedMessage(), equalTo("\n"
                                                         + "Le traitement 'traitement_aa' est utilisé dans des listes de traitement : 'ma_list_3','ma_list_4'.\n"
                                                         + "Sa suppression est donc impossible."));
        }

        TreatmentHandlerHelper.manageTreatmentModel(TOKIO.getConnection(),
                                                    DataProcessConstants.Command.DELETE,
                                                    1, treatmentContentCc);

        try {
            TreatmentHandlerHelper.manageTreatmentModel(TOKIO.getConnection(),
                                                        DataProcessConstants.Command.DELETE,
                                                        1, treatmentContentDd);
            fail("Ce test devrait échoué car le traitement 'traitement_dd' est inexistant");
        }
        catch (TreatmentException ex) {
            assertThat(ex.getLocalizedMessage(), equalTo("\n"
                                                         + "'traitement_dd' est inexistant dans le repository (repositoryId = 1)"));
        }
        TOKIO.assertAllOutputs("delete_treatmentModel");
    }


    private static void deleteTables(Connection con) throws SQLException {
        con.createStatement().executeUpdate("delete PM_REPOSITORY delete PM_REPOSITORY_CONTENT "
                                            + "delete PM_EXECUTION_LIST delete PM_EXECUTION_LIST_STATUS "
                                            + "delete PM_TREATMENT_STATUS delete PM_TREATMENT");
    }


    private static void verifyResult(List<ExecutionListModel> result, List<ExecutionListModel> expected) {
        for (int i = 0; i < result.size(); i++) {
            ExecutionListModel trtExpected = expected.get(i);
            ExecutionListModel trtResult = result.get(i);
            assertThat(trtExpected.getName(), equalTo(trtResult.getName()));
            Map resMap = trtResult.getPriorityMap();
            Map expectedMap = trtExpected.getPriorityMap();

            assertThat(expectedMap.size(), equalTo(resMap.size()));
        }
    }


    private static ExecutionListModel buildModel(int id, String name, int priority, int status) {
        ExecutionListModel trtMod = new ExecutionListModel();
        trtMod.setId(id);
        trtMod.setName(name);
        trtMod.setPriority(priority);
        trtMod.setStatus(status);
        Map<UserTreatment, Integer> userTreatmentMap = buildTreatmentMap();
        trtMod.setPriorityMap(userTreatmentMap);
        return trtMod;
    }


    private static Map<UserTreatment, Integer> buildTreatmentMap() {
        Map<UserTreatment, Integer> userTreatmentMap = new HashMap<UserTreatment, Integer>();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("TRAITEMENT_1");
        treatmentModel.setTitle("comment1");
        UserTreatment usrTrt = new UserTreatment(treatmentModel);
        usrTrt.setPriority(0);
        userTreatmentMap.put(usrTrt, 0);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("TRAITEMENT_2");
        treatmentModel.setTitle("comment2");
        usrTrt = new UserTreatment(treatmentModel);
        usrTrt.setPriority(2);
        userTreatmentMap.put(usrTrt, 1);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("TRAITEMENT_3");
        treatmentModel.setTitle("comment3");
        usrTrt = new UserTreatment(treatmentModel);
        usrTrt.setPriority(3);
        userTreatmentMap.put(usrTrt, 2);

        return userTreatmentMap;
    }


    private static String loadFileAsString(String fileName) throws IOException {
        String localPath = TreatmentDaoTest.class.getResource(fileName).getPath();
        return FileUtil.loadContent(new File(localPath));
    }
}
