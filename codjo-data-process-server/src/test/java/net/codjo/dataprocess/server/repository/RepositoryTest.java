/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.repository;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.datagen.DatagenFixture;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.server.treatmenthelper.RepositoryDescriptor;
import net.codjo.dataprocess.server.treatmenthelper.TreatmentHelper;
import net.codjo.dataprocess.server.util.TestUtils;
import net.codjo.test.common.fixture.CompositeFixture;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;

public class RepositoryTest {
    private static final DatagenFixture DATAGEN = new DatagenFixture(RepositoryTest.class);
    private static final JdbcFixture FIXTURE = createJdbcFixture();
    private static final CompositeFixture COMPOSITE_FIXTURE = new CompositeFixture(FIXTURE, DATAGEN);


    @BeforeClass
    public static void beforeClass() throws Exception {
        COMPOSITE_FIXTURE.doSetUp();
        FIXTURE.advanced().dropAllObjects();
        try {
            DATAGEN.generate();
            TestUtils.initScript(FIXTURE, DATAGEN, "PM_REPOSITORY.tab");
            TestUtils.initScript(FIXTURE, DATAGEN, "PM_REPOSITORY_CONTENT.tab");
        }
        catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
    }


    static JdbcFixture createJdbcFixture() {
        try {
            return new DatabaseFactory().createJdbcFixture();
        }
        catch (SQLException e) {
            fail(e.getLocalizedMessage());
        }
        return null;
    }


    @AfterClass
    public static void afterClass() throws Exception {
        COMPOSITE_FIXTURE.doTearDown();
    }


    @Test
    public void loadAllTreatmentsFromXmlRepository() throws Exception {
        int repositoryId = 1;
        Connection con = FIXTURE.getConnection();
        TreatmentHelper.deleteRepository(con, repositoryId);
        RepositoryDescriptor repositoryDescriptor = new RepositoryDescriptor(repositoryId, "REP1",
                                                                             new String[]{
                                                                                   "/net/codjo/dataprocess/server/repository/RepositoryTest3.xml"});
        TreatmentHelper.initRepository(con, Arrays.asList(repositoryDescriptor));
        Repository.loadAllTreatmentsFromRepository(con, repositoryId);

        TreatmentModel trt1 = Repository.getTreatmentById(con, repositoryId, "trt1");
        TreatmentModel trt2 = Repository.getTreatmentById(con, repositoryId, "trt2");

        assertThat("trt1", equalTo(trt1.getId()));
        assertThat("trt2", equalTo(trt2.getId()));
        assertThat("java", equalTo(trt1.getType()));
        assertThat("stored_procedure", equalTo(trt2.getType()));
        assertThat("com.patati.patata.Patatouf", equalTo(trt1.getTarget()));
        assertThat("sp_SIF_Aggregate_Transactions", equalTo(trt2.getTarget()));
        assertThat("com.patati.patata.mage.imba", equalTo(trt1.getGuiTarget()));
        assertThat("com.patati.patata.druide.feral", equalTo(trt2.getGuiTarget()));

        Map<String, TreatmentModel> result = Repository.getTreatments(con, repositoryId);
        assertThat(result.size(), equalTo(2));
        TreatmentModel trt1Test = result.get("trt1");
        TreatmentModel trt2Test = result.get("trt2");
        assertThat(trt1Test, equalTo(trt1));
        assertThat(trt2Test, equalTo(trt2));

        try {
            Repository.loadAllTreatmentsFromRepository(con, 666);
            fail("Une exception était attendue !");
        }
        catch (Exception ex) {
            assertThat(ex.getLocalizedMessage(), equalTo("Le repository [id = 666] n'existe pas !"));
        }

        try {
            Repository.getTreatmentById(con, repositoryId, "XXX");
        }
        catch (TreatmentException ex) {
            assertThat(ex.getLocalizedMessage(),
                       equalTo("Le traitement 'XXX' est inexistant dans le repository [id = 1]"));
        }
    }


    @Test
    public void loadRepoWithTreatmentIdTooLongError() throws Exception {
        int repositoryId = 1;
        Connection con = FIXTURE.getConnection();
        TreatmentHelper.deleteRepository(con, repositoryId);
        RepositoryDescriptor repositoryDescriptor = new RepositoryDescriptor(repositoryId, "REP",
                                                                             new String[]{
                                                                                   "/net/codjo/dataprocess/server/repository/RepoTreatmentIdTooLongTest.xml"});
        try {
            TreatmentHelper.initRepository(con, Arrays.asList(repositoryDescriptor));
            fail("Une exception était attendue !");
        }
        catch (Exception ex) {
            assertThat(ex.getLocalizedMessage(), equalTo(
                  "La taille de l'identifiant d'un traitement ('trt1aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab') dépasse 50 caractères."));
        }
    }


    @Test
    public void loadRepoWithTreatmentContentTooLongError() throws Exception {
        int repositoryId = 1;
        Connection con = FIXTURE.getConnection();
        TreatmentHelper.deleteRepository(con, repositoryId);
        RepositoryDescriptor repositoryDescriptor = new RepositoryDescriptor(repositoryId, "REP",
                                                                             new String[]{
                                                                                   "/net/codjo/dataprocess/server/repository/RepoTreatmentContentTooLongTest.xml"});
        try {
            TreatmentHelper.initRepository(con, Arrays.asList(repositoryDescriptor));
        }
        catch (Exception ex) {
            assertThat(ex.getLocalizedMessage(), equalTo(
                  "\n#####################################################################################################################################\n"
                  + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"
                  + "trt2 est trop long ! : UILLE=#PTF1.CODE_PORTEFEUILLE \"              +\"and PM_OPCVM_PERIMETRE.TYPE_STOCK=#PTF1.TYPE_STOCK \"   \n"
                  + "trt3 est trop long ! : PORTEFEUILLE \"              +\"and PM_OPCVM_PERIMETRE.TYPE_STOCK=#PTF1.TYPE_STOCK \"              +\"wher\n"
                  + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"
                  + "#####################################################################################################################################"));
        }
    }


    @Test
    public void getRepositoryContent() throws Exception {
        int repositoryId = 1;

        Connection con = FIXTURE.getConnection();
        TreatmentHelper.deleteRepository(con, repositoryId);
        RepositoryDescriptor repositoryDescriptor = new RepositoryDescriptor(repositoryId, "REP1",
                                                                             new String[]{
                                                                                   "/net/codjo/dataprocess/server/repository/RepositoryTest3.xml"});
        TreatmentHelper.initRepository(con, Arrays.asList(repositoryDescriptor));
        Repository.loadAllTreatmentsFromRepository(con, repositoryId);

        String result = Repository.getRepositoryContent(con, repositoryId);
        assertThat(result, equalTo("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
                                   + "<root>\n"
                                   + "<treatment id=\"trt1\" scope=\"TREATMENT\" type=\"java\">\r\n"
                                   + "        <title>Maj des ptf miroités</title>\r\n"
                                   + "        <comment>Traitement des Maj des ptf miroités</comment>\r\n"
                                   + "        <target>com.patati.patata.Patatouf</target>\r\n"
                                   + "        <gui-target>com.patati.patata.mage.imba</gui-target>\r\n"
                                   + "        <arguments/>\r\n"
                                   + "    </treatment>\n"
                                   + "<treatment id=\"trt2\" scope=\"TREATMENT\" type=\"stored_procedure\">\r\n"
                                   + "        <title>Conso des mouvements</title>\r\n"
                                   + "        <comment>Traitement des Conso des mouvements</comment>\r\n"
                                   + "        <target>sp_SIF_Aggregate_Transactions</target>\r\n"
                                   + "        <gui-target>com.patati.patata.druide.feral</gui-target>\r\n"
                                   + "        <arguments/>\r\n"
                                   + "    </treatment>\n"
                                   + "</root>"));
    }
}
