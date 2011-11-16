package net.codjo.dataprocess.server.dao;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.datagen.DatagenFixture;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.util.XMLUtils;
import net.codjo.dataprocess.server.util.SQLUtil;
import net.codjo.dataprocess.server.util.TestUtils;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.tokio.TokioFixture;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class RepositoryDaoTest {
    private RepositoryDao repositoryDao = new RepositoryDao();
    private static final TokioFixture TOKIO = new TokioFixture(RepositoryDaoTest.class);
    private static final DatagenFixture DATAGEN = new DatagenFixture(RepositoryDaoTest.class);
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
    public void deleteNewRepository() throws Exception {
        TOKIO.insertInputInDb("REPOSITORY_DELETE");
        Connection con = TOKIO.getConnection();
        repositoryDao.deleteRepository(con, 2);
        TOKIO.assertAllOutputs("REPOSITORY_DELETE");

        TOKIO.insertInputInDb("REPOSITORY_NEW");
        repositoryDao.newRepository(con, "REP2");
        TOKIO.assertAllOutputs("REPOSITORY_NEW");

        if (!DataProcessConstants.REPOSITORY_ALREADY_EXISTS.equals(
              repositoryDao.newRepository(con, "REP2"))) {
            fail("Le repository doit déjà exister!");
        }
    }


    @Test
    public void getRepositoryIdFromName() throws Exception {
        Connection con = TOKIO.getConnection();
        TOKIO.insertInputInDb("REPOSITORY");
        int repositoryId = repositoryDao.getRepositoryIdFromName(con, "REPO3");
        assertThat(repositoryId, equalTo(3));
        try {
            repositoryDao.getRepositoryIdFromName(con, "XXX");
            fail("Le test devrait échouer : le repository 'XXX' est inexistant.");
        }
        catch (Exception ex) {
            assertThat(ex.getLocalizedMessage(), equalTo("Le repository XXX n'existe pas."));
        }
    }


    @Test
    public void getRepositoryNameFromId() throws Exception {
        Connection con = TOKIO.getConnection();
        TOKIO.insertInputInDb("REPOSITORY");
        String repositoryName = repositoryDao.getRepositoryNameFromId(con, 2);
        assertThat(repositoryName, equalTo("REPO2"));
        try {
            repositoryDao.getRepositoryNameFromId(con, 10);
            fail("Le test devrait échouer : le repository Id = 10 est inexistant.");
        }
        catch (Exception ex) {
            assertThat(ex.getLocalizedMessage(), equalTo("Le repository Id = 10 n'existe pas."));
        }
    }


    @Test
    public void updateRepository() throws Exception {
        String xmlBegin1 = "<treatment id=\"CCscopComptehb2\" scope=\"CONTROL\" type=\"java\">"
                           + "        <comment>Existence dans PM_TRANSCO de tous les NumSAP tronques trouves dans AP_TITRES_GPFI.COMPTE_HB2 </comment>"
                           + "        <title>CCscopComptehb2</title>"
                           + "        <target>net.codjo.scop.control.common.cscop.CCscopComptehb2Control</target>"
                           + "        <result-table>TC_CSCOP_COMPTEHB2</result-table>"
                           + "        <arguments>" + "             <arg position=\"1\">"
                           + "                <name>periode</name>"
                           + "                <value>$periode$</value>" + "             </arg>"
                           + "        </arguments>" + "   </treatment>";
        String xmlBegin2 = "<treatment id=\"CCscopTypevaleur\" scope=\"CONTROL\" type=\"java\">"
                           + "        <comment>Dans AP_TITRES_GPFI et AP_TITRES_GPFO, verifier que CATEGORIE_VALEUR est toujours renseigne </comment>"
                           + "        <title>CCscopTypevaleur </title>"
                           + "        <target>net.codjo.scop.control.common.cscop.CCscopTypevaleurControl</target>"
                           + "        <result-table>TC_CSCOP_CATEGORIEVALEUR</result-table>"
                           + "        <arguments>" + "           <arg position=\"1\">"
                           + "                <name>periode</name>"
                           + "                <value>$periode$</value>" + "           </arg>"
                           + "        </arguments>" + "    </treatment>";
        String xmlEnd1 = "<treatment id=\"CCscopComptehb2\" scope=\"CONTROL\" type=\"java\">"
                         + "        <comment>Existence dans PM_TRANSCO de tous les NumSAP tronques trouves dans AP_TITRES_GPFI.COMPTE_HB2 </comment>"
                         + "        <title>CCscopComptehb2</title>"
                         + "        <target>net.codjo.scop.control.common.cscop.CCscopComptehb2Control</target>"
                         + "        <result-table>TC_CSCOP_COMPTEHB2</result-table>"
                         + "        <arguments>" + "             <arg position=\"1\">"
                         + "                <name>periode</name>"
                         + "                <value>$periode2$</value>" + "             </arg>"
                         + "        </arguments></treatment>";
        String xmlEnd2 = "<treatment id=\"CCscopTypevaleurVmob\" scope=\"CONTROL\" type=\"java\">"
                         + "        <comment>Dans AP_TITRES_GPFI et AP_TITRES_GPFO, verifier que si TYPE_VALEUR = 'VMOB', alors CATEGORIE_TYPE_VALEUR est renseigne </comment>"
                         + "        <title>CCscopTypevaleurVmob </title>"
                         + "        <target>net.codjo.scop.control.common.cscop.CCscopTypevaleurVmobControl</target>"
                         + "        <result-table>TC_CSCOP_TYPEVALEUR_VMOB</result-table>"
                         + "        <arguments>" + "           <arg position=\"1\">"
                         + "                <name>periode</name>"
                         + "                <value>$periode$</value>" + "           </arg>"
                         + "        </arguments></treatment>";
        String xmlEnd = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root>" + xmlEnd1 + xmlEnd2
                        + "</root>";

        Connection con = TOKIO.getConnection();
        con.createStatement().executeUpdate("delete PM_REPOSITORY_CONTENT");

        PreparedStatement insertStatement = con.prepareStatement("insert into PM_REPOSITORY_CONTENT"
                                                                 + " (REPOSITORY_CONTENT_ID, REPOSITORY_ID, TREATMENT_ID, CONTENT) values(?, ?, ?, ?)");
        try {
            int nextRepositoryContentId = SQLUtil.getNextId(con, "PM_REPOSITORY_CONTENT",
                                                            "REPOSITORY_CONTENT_ID");
            insertStatement.setInt(1, nextRepositoryContentId);
            insertStatement.setInt(2, 1);
            insertStatement.setString(3, "CCscopComptehb2");
            insertStatement.setString(4, xmlBegin1);
            insertStatement.executeUpdate();

            insertStatement.setInt(1, ++nextRepositoryContentId);
            insertStatement.setInt(2, 2);
            insertStatement.setString(3, "CCscopTypevaleur");
            insertStatement.setString(4, xmlBegin2);
            insertStatement.executeUpdate();
        }
        finally {
            insertStatement.close();
        }

        String result = repositoryDao.updateRepository(con, 1, xmlEnd);
        assertThat(result, equalTo("OK"));

        Statement stmt = con.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(
                  "select REPOSITORY_ID, CONTENT, TREATMENT_ID from PM_REPOSITORY_CONTENT");
            try {
                rs.next();

                assertThat(rs.getInt("REPOSITORY_ID"), equalTo(2));
                assertThat(rs.getString("CONTENT"), equalTo(xmlBegin2));
                assertThat(rs.getString("TREATMENT_ID"), equalTo("CCscopTypevaleur"));

                rs.next();
                assertThat(rs.getInt("REPOSITORY_ID"), equalTo(1));
                assertThat(rs.getString("CONTENT"), equalTo(XMLUtils.flattenAndReplaceCRLF(xmlEnd1, false)));
                assertThat(rs.getString("TREATMENT_ID"), equalTo("CCscopComptehb2"));

                rs.next();
                assertThat(rs.getInt("REPOSITORY_ID"), equalTo(1));
                assertThat(rs.getString("CONTENT"), equalTo(XMLUtils.flattenAndReplaceCRLF(xmlEnd2, false)));
                assertThat(rs.getString("TREATMENT_ID"), equalTo("CCscopTypevaleurVmob"));
            }
            finally {
                rs.close();
            }
        }
        finally {
            stmt.close();
        }
    }
}
