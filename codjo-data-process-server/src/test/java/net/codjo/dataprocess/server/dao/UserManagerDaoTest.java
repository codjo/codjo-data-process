/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.dao;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.datagen.DatagenFixture;
import net.codjo.dataprocess.common.exception.UserManagerException;
import net.codjo.dataprocess.server.util.TestUtils;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.tokio.TokioFixture;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class UserManagerDaoTest {
    private static final TokioFixture TOKIO = new TokioFixture(UserManagerDaoTest.class);
    private static final DatagenFixture DATAGEN = new DatagenFixture(UserManagerDaoTest.class);
    private static final CompositeFixture COMPOSITE_FIXTURE = new CompositeFixture(TOKIO, DATAGEN);


    @BeforeClass
    public static void beforeClass() throws Exception {
        COMPOSITE_FIXTURE.doSetUp();
        try {
            JdbcFixture jdbcFixture = TOKIO.getJdbcFixture();
            jdbcFixture.advanced().dropAllObjects();
            DATAGEN.generate();
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_DP_USER.tab");
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
    public void createUser() throws Exception {
        TOKIO.insertInputInDb("CREATE");
        new UserManagerDao(TOKIO.getConnection()).createUser("michel", "michel param");
        TOKIO.assertAllOutputs("CREATE");
    }


    @Test
    public void isExist() throws Exception {
        TOKIO.insertInputInDb("LOAD");
        assertThat(new UserManagerDao(TOKIO.getConnection()).isExist("michel"), equalTo(true));
        assertThat(new UserManagerDao(TOKIO.getConnection()).isExist("michel2"), equalTo(false));
    }


    @Test
    public void loadUser() throws Exception {
        TOKIO.insertInputInDb("LOAD");
        String result = new UserManagerDao(TOKIO.getConnection()).loadUser("michel");
        assertThat(result, equalTo("michel param"));
    }


    @Test
    public void updateUser() throws Exception {
        TOKIO.insertInputInDb("MODIF");
        new UserManagerDao(TOKIO.getConnection()).updateUser("michel", "michel param modif");
        TOKIO.assertAllOutputs("MODIF");
    }


    @Test
    public void manageUser() throws Exception {
        TOKIO.getJdbcFixture().delete(SqlTable.table("PM_DP_USER"));

        UserManagerDao userManager = new UserManagerDao(TOKIO.getConnection());
        userManager.createUser("michel", "michel param");
        assertThat(userManager.isExist("michel"), equalTo(true));
        assertThat(userManager.loadUser("michel"), equalTo("michel param"));
        userManager.updateUser("michel", "michel param modif");
        assertThat(userManager.loadUser("michel"), equalTo("michel param modif"));
        try {
            userManager.updateUser("michel2", "michou2 param modif");
            fail("Ce test aurait dû échouer !");
        }
        catch (UserManagerException ex) {
            assertThat(ex.getLocalizedMessage(),
                       equalTo("L'utilisateur 'michel2' est introuvable dans PM_DP_USER."));
        }
    }
}
