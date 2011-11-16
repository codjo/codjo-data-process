package net.codjo.dataprocess.server.util;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.datagen.DatagenFixture;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.tokio.TokioFixture;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class SQLUtilCopyTableTest {
    private static final String TABLE_TEST1 = "TABLE_TEST1";
    private static final String TABLE_TEST2 = "TABLE_TEST2";
    private static final DatagenFixture DATAGEN = new DatagenFixture(SQLUtilTest.class);
    private static final TokioFixture TOKIO = new TokioFixture(SQLUtilTest.class);
    private static final CompositeFixture COMPOSITE_FIXTURE = new CompositeFixture(TOKIO, DATAGEN);


    @BeforeClass
    public static void beforeClass() throws Exception {
        COMPOSITE_FIXTURE.doSetUp();
        JdbcFixture jdbcFixture = TOKIO.getJdbcFixture();
        jdbcFixture.advanced().dropAllObjects();
        try {
            DATAGEN.generate();
            TestUtils.initScript(jdbcFixture, DATAGEN, "T_TRANSFER.tab");
        }
        catch (Exception ex) {
            COMPOSITE_FIXTURE.doTearDown();
            fail(ex.getLocalizedMessage());
        }
    }


    @AfterClass
    public static void afterClass() throws Exception {
        COMPOSITE_FIXTURE.doTearDown();
    }


    @Before
    public void before() throws Exception {
        dropTestTables(TOKIO.getJdbcFixture());
        createTestTables(TOKIO.getJdbcFixture());
    }


    @After
    public void after() {
        dropTestTables(TOKIO.getJdbcFixture());
    }


    @Test
    public void listColumnNamesOfATable() throws Exception {
        Connection con = TOKIO.getConnection();

        List<String> result = SQLUtilCopyTable.listColumnNamesOfATable(con, TABLE_TEST2);
        assertThat("[id, field1, field2, field3]", equalTo(result.toString()));

        result = SQLUtilCopyTable.listColumnNamesOfATable(con, TABLE_TEST1);
        assertThat("[id, field1, field2]", equalTo(result.toString()));
    }


    @Test
    public void determineCommonFields() throws Exception {
        Connection con = TOKIO.getConnection();

        List<String> result = SQLUtilCopyTable.determineCommonFields(TABLE_TEST1, TABLE_TEST2, con);
        assertThat("[id, field1, field2]", equalTo(result.toString()));
    }


    @Test
    public void copyTable() throws Exception {
        JdbcFixture fixture = TOKIO.getJdbcFixture();
        fixture.executeUpdate(
              "insert into " + TABLE_TEST1 + " (id, field1, field2) values (1, 'AAA', 'BBB')");
        fixture.executeUpdate(
              "insert into " + TABLE_TEST1 + " (id, field1, field2) values (2, 'CCC', 'DDD')");
        fixture.executeUpdate(
              "insert into " + TABLE_TEST1 + " (id, field1, field2) values (3, 'EEE', 'FFF')");
        fixture.executeUpdate(
              "insert into " + TABLE_TEST1 + " (id, field1, field2) values (4, 'AAA', 'HHH')");

        assertThat(4L, equalTo(countRow(fixture, TABLE_TEST1)));
        assertThat(0L, equalTo(countRow(fixture, TABLE_TEST2)));

        Map<String, String> treatmentOnColumn = new HashMap<String, String>();
        treatmentOnColumn.put("field1", "convert(varchar(100), field1)");

        SQLUtilCopyTable.ResultCopyTable resultCopyTable = new SQLUtilCopyTable.ResultCopyTable();
        SQLUtilCopyTable.copyTable(fixture.getConnection(), TABLE_TEST1, "id", TABLE_TEST2, null, 5,
                                   treatmentOnColumn, true, resultCopyTable);
        assertThat(4L, equalTo(countRow(fixture, TABLE_TEST1)));
        assertThat(4L, equalTo(countRow(fixture, TABLE_TEST2)));
        assertThat(resultCopyTable.getException(), nullValue());

        fixture.executeUpdate("delete from " + TABLE_TEST2);

        treatmentOnColumn = new HashMap<String, String>();
        treatmentOnColumn.put("field_inconnue", "convert(varchar(100), field1)");

        resultCopyTable = new SQLUtilCopyTable.ResultCopyTable();
        SQLUtilCopyTable.copyTable(fixture.getConnection(), TABLE_TEST1, "id", TABLE_TEST2, null, 5,
                                   treatmentOnColumn, true, resultCopyTable);
        assertThat(4L, equalTo(countRow(fixture, TABLE_TEST1)));
        assertThat(0L, equalTo(countRow(fixture, TABLE_TEST2)));
        assertThat(resultCopyTable.getException(), notNullValue());

        fixture.executeUpdate("delete from " + TABLE_TEST2);

        treatmentOnColumn = new HashMap<String, String>();
        resultCopyTable = new SQLUtilCopyTable.ResultCopyTable();
        SQLUtilCopyTable.copyTable(fixture.getConnection(), TABLE_TEST1, "id", TABLE_TEST2, "field1 = 'AAA'",
                                   5, treatmentOnColumn, true, resultCopyTable);
        assertThat(4L, equalTo(countRow(fixture, TABLE_TEST1)));
        assertThat(2L, equalTo(countRow(fixture, TABLE_TEST2)));
        assertThat(resultCopyTable.getException(), nullValue());
    }


    @Test
    public void testResultCopyTable() throws Exception {
        SQLUtilCopyTable.ResultCopyTable resultCopyTable = new SQLUtilCopyTable.ResultCopyTable();
        SQLException sqlException = new SQLException("mon message d'erreur");
        resultCopyTable.setException(sqlException);
        resultCopyTable.setKey(10);

        assertThat(resultCopyTable.getKey(), equalTo(10));
        assertThat((SQLException)resultCopyTable.getException(), is(sameInstance(sqlException)));
    }


    private static long countRow(JdbcFixture fixture, String tableName) throws SQLException {
        ResultSet rs = fixture.executeQuery("select count(*) from " + tableName);
        try {
            rs.next();
            return rs.getLong(1);
        }
        finally {
            rs.close();
        }
    }


    private static void createTestTables(JdbcFixture fixture) throws SQLException {
        fixture.create(SqlTable.table(TABLE_TEST1),
                       "id integer, field1 varchar(50) not null, field2 varchar(50) not null");
        fixture.create(SqlTable.table(TABLE_TEST2),
                       "id integer, field1 varchar(50) not null, field2 varchar(50) not null, field3 varchar(50) null");
    }


    private static void dropTestTables(JdbcFixture fixture) {
        fixture.drop(SqlTable.table(TABLE_TEST1));
        fixture.drop(SqlTable.table(TABLE_TEST2));
    }
}
