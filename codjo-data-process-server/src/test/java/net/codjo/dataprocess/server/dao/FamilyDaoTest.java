package net.codjo.dataprocess.server.dao;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.datagen.DatagenFixture;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.server.util.TestUtils;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.tokio.TokioFixture;
import java.sql.Connection;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class FamilyDaoTest {
    private static final TokioFixture TOKIO = new TokioFixture(FamilyDaoTest.class);
    private static final DatagenFixture DATAGEN = new DatagenFixture(FamilyDaoTest.class);
    private static final CompositeFixture COMPOSITE_FIXTURE = new CompositeFixture(TOKIO, DATAGEN);


    @BeforeClass
    public static void beforeClass() throws Exception {
        COMPOSITE_FIXTURE.doSetUp();
        JdbcFixture jdbcFixture = TOKIO.getJdbcFixture();
        jdbcFixture.advanced().dropAllObjects();

        try {
            DATAGEN.generate();
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_FAMILY.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_REPOSITORY.tab");
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
    public void newFamily() throws Exception {
        TOKIO.insertInputInDb("FAMILY_NEW");
        Connection con = TOKIO.getConnection();
        FamilyDao familyDao = new FamilyDao();
        familyDao.newFamily(con, 1, "FAM2");
        familyDao.newFamily(con, 1, "FAM3");
        familyDao.newFamily(con, 2, "FAM3");
        TOKIO.assertAllOutputs("FAMILY_NEW");

        if (!DataProcessConstants.FAMILY_ALREADY_EXISTS.equals(
              familyDao.newFamily(con, 1, "FAM2"))) {
            fail("La famille doit déjà exister!");
        }
        if (!DataProcessConstants.FAMILY_ALREADY_EXISTS.equals(
              familyDao.newFamily(con, 2, "FAM3"))) {
            fail("La famille doit déjà exister!");
        }
    }


    @Test
    public void getFamilyIdFromName() throws Exception {
        TOKIO.insertInputInDb("FAMILY_NEW");
        Connection con = TOKIO.getConnection();
        FamilyDao familyDao = new FamilyDao();
        int familyId = familyDao.getFamilyIdFromName(con, 1, "FAM1");
        assertThat(familyId, equalTo(2));

        try {
            familyDao.getFamilyIdFromName(con, 1, "FAM2");
            fail("Le test devrait échouer : La famille 'XXX' est inexistante dans le repository id = Y");
        }
        catch (Exception ex) {
            assertThat(ex.getLocalizedMessage(),
                       equalTo("La famille 'FAM2' est inexistante dans le repository id = 1"));
        }
    }


    @Test
    public void getFamilyMap() throws Exception {
        TOKIO.insertInputInDb("FAMILY_GET");

        Connection con = TOKIO.getConnection();
        FamilyDao familyDao = new FamilyDao();
        Map<String, String> familyMap = familyDao.getFamilyMap(con, "REPO1");
        assertThat(familyMap.size(), equalTo(2));
        assertThat(familyMap.get("1"), equalTo("FAM1"));

        familyMap = familyDao.getFamilyMap(con, "REPO2");
        assertThat(familyMap.size(), equalTo(1));
        assertThat(familyMap.get("3"), equalTo("FAM3"));

        familyMap = familyDao.getFamilyMap(con, "REPO3");
        assertThat(familyMap.size(), equalTo(0));
    }
}
