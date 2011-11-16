package net.codjo.dataprocess.server.dao;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.datagen.DatagenFixture;
import net.codjo.dataprocess.server.util.TestUtils;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.tokio.TokioFixture;
import java.sql.Connection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class UtilDaoTest {
    private static final TokioFixture TOKIO = new TokioFixture(UtilDaoTest.class);
    private static final DatagenFixture DATAGEN = new DatagenFixture(UtilDaoTest.class);
    private static final CompositeFixture COMPOSITE_FIXTURE = new CompositeFixture(TOKIO, DATAGEN);


    @BeforeClass
    public static void beforeClass() throws Exception {
        COMPOSITE_FIXTURE.doSetUp();
        JdbcFixture jdbcFixture = TOKIO.getJdbcFixture();
        jdbcFixture.advanced().dropAllObjects();

        try {
            DATAGEN.generate();
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_TREATMENT.tab");
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
    public void exportSqlQueryToStringFormat() throws Exception {
        TOKIO.insertInputInDb("EXPORT");

        String sql = " select * from PM_TREATMENT";
        Connection con = TOKIO.getConnection();
        UtilDao utilDao = new UtilDao();
        String result = utilDao.exportSqlQueryToStringFormat(con, sql, ", ", "\"",
                                                             Boolean.TRUE);
        assertThat(result, equalTo("\"TREATMENT_ID\", \"PRIORITY\", \"EXECUTION_LIST_ID\"\n"
                                   + "\"AAAA\", \"1\", \"10\"\n" + "\"BBBB\", \"2\", \"20\"\n"
                                   + "\"CCCC\", \"3\", \"30\"\n" + "\"DDDD\", \"4\", \"40\"\n"));

        // test avec une table temporaire ...
        sql = "create table #TEMP (CODE_PORTEFEUILLE varchar(10)  null, "
              + " QUANTITE numeric(25,5) default 0  null) "
              + " insert into #TEMP (CODE_PORTEFEUILLE, QUANTITE) values ('CP1', 1) "
              + " insert into #TEMP (CODE_PORTEFEUILLE, QUANTITE) values ('CP2', 10) "
              + " select * from #TEMP ";
        result = utilDao.exportSqlQueryToStringFormat(con, sql, ", ", "\"", Boolean.TRUE);
        assertThat(result, equalTo("\"CODE_PORTEFEUILLE\", \"QUANTITE\"\n" + "\"CP1\", \"1.00000\"\n"
                                   + "\"CP2\", \"10.00000\"\n"));
    }
}
