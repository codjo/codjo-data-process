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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class SQLUtilTest {
    private static final long NUMBER_OF_ROW = 100;
    private static final String TABLE_NAME = "TEST1";
    private static final String TABLE_NAME2 = "TEST2";
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
            dropAndCreateTestTables();
        }
        catch (Exception ex) {
            COMPOSITE_FIXTURE.doTearDown();
            fail(ex.getLocalizedMessage());
        }
    }


    @AfterClass
    public static void afterClass() throws Exception {
        dropTestTables(TOKIO.getJdbcFixture());
        COMPOSITE_FIXTURE.doTearDown();
    }


    @Test
    public void truncateTable() throws Exception {
        fillTestTables();
        assertThat(NUMBER_OF_ROW, equalTo(countRow(TABLE_NAME)));
        SQLUtil.truncateTable(TABLE_NAME, 5, TOKIO.getJdbcFixture().getConnection());
        assertThat(0L, equalTo(countRow(TABLE_NAME)));
    }


    @Test
    public void transfertTable() throws Exception {
        fillTestTables();
        assertThat(NUMBER_OF_ROW, equalTo(countRow(TABLE_NAME)));
        assertThat(0L, equalTo(countRow(TABLE_NAME2)));

        Map<String, String> treatmentOnColumn = new HashMap<String, String>();
        treatmentOnColumn.put("dataTest", "convert(varchar(100),dataTest)");
        SQLUtilCopyTable
              .copyTable(TOKIO.getJdbcFixture().getConnection(), TABLE_NAME, "id", TABLE_NAME2, null,
                         5, treatmentOnColumn, true, new SQLUtilCopyTable.ResultCopyTable());
        assertThat(NUMBER_OF_ROW, equalTo(countRow(TABLE_NAME)));
        assertThat(NUMBER_OF_ROW, equalTo(countRow(TABLE_NAME2)));
    }


    @Test
    public void executeUpdateSql() throws Exception {
        JdbcFixture fixture = TOKIO.getJdbcFixture();
        fixture.executeUpdate("delete from " + TABLE_NAME);
        fixture.executeUpdate("insert into " + TABLE_NAME + " (id, dataTest) values (1, 'poire')");
        fixture.executeUpdate("insert into " + TABLE_NAME + " (id, dataTest) values (2, 'pomme')");
        fixture.executeUpdate("insert into " + TABLE_NAME + " (id, dataTest) values (3, 'fraise')");
        fixture.executeUpdate("insert into " + TABLE_NAME + " (id, dataTest) values (4, 'fraise')");
        fixture.executeUpdate("update " + TABLE_NAME + " set dataTest = 'pomme frite' where id = 2");
        ResultSet rs = fixture.executeQuery(
              "select id from " + TABLE_NAME + " where dataTest = 'pomme frite'");
        assertThat(rs.next(), equalTo(true));
        assertThat(2, equalTo(rs.getInt(1)));
        rs.close();
    }


    @Test
    public void countRowsTable() throws Exception {
        JdbcFixture fixture = TOKIO.getJdbcFixture();
        fixture.executeUpdate("delete from " + TABLE_NAME);
        fixture.executeUpdate("insert into " + TABLE_NAME + " (id, dataTest) values (1, 'poire')");
        fixture.executeUpdate("insert into " + TABLE_NAME + " (id, dataTest) values (2, 'pomme')");
        fixture.executeUpdate("insert into " + TABLE_NAME + " (id, dataTest) values (3, 'fraise')");
        fixture.executeUpdate("insert into " + TABLE_NAME + " (id, dataTest) values (4, 'fraise')");

        Connection con = fixture.getConnection();
        int result = SQLUtil.countRowsTable(TABLE_NAME, "where dataTest = 'fraise'", con);
        assertThat(2, equalTo(result));

        result = SQLUtil.countRowsTable(TABLE_NAME, " dataTest = 'fraise'", con);
        assertThat(2, equalTo(result));

        result = SQLUtil.countRowsTable(TABLE_NAME, " dataTest = 'pomme'", con);
        assertThat(1, equalTo(result));

        result = SQLUtil.countRowsTable(TABLE_NAME, null, con);
        assertThat(4, equalTo(result));
    }


    private static void createTestTables(JdbcFixture fixture) throws SQLException {
        fixture.create(SqlTable.table(TABLE_NAME), "id integer, dataTest varchar(50) not null");
        fixture.create(SqlTable.table(TABLE_NAME2),
                       "id integer,dataTest varchar(100) not null, notFilled varchar(10) null");
    }


    private static void dropTestTables(JdbcFixture fixture) throws SQLException {
        fixture.drop(SqlTable.table(TABLE_NAME));
        fixture.drop(SqlTable.table(TABLE_NAME2));
    }


    private static void dropAndCreateTestTables() throws SQLException {
        try {
            dropTestTables(TOKIO.getJdbcFixture());
        }
        catch (Exception ex) {
            ;
        }
        createTestTables(TOKIO.getJdbcFixture());
    }


    private static long countRow(String tableName) throws SQLException {
        String sql = "select count(*) from " + tableName;
        ResultSet rs = TOKIO.getJdbcFixture().executeQuery(sql);
        try {
            rs.next();
            return rs.getLong(1);
        }
        finally {
            rs.close();
        }
    }


    private static void fillTestTables() throws SQLException {
        JdbcFixture fixture = TOKIO.getJdbcFixture();
        fixture.executeUpdate("delete from " + TABLE_NAME);
        fixture.executeUpdate("delete from " + TABLE_NAME2);

        ExtendedPreparedStatement exStmt = new ExtendedPreparedStatement(
              "insert into " + TABLE_NAME + "(id, dataTest) values (${id}, ${data})");
        for (int count = 0; count < NUMBER_OF_ROW; count++) {
            exStmt.setParameterValue("id", count);
            exStmt.setParameterValue("data", Long.toHexString(System.currentTimeMillis()) + "-" + count);
            exStmt.createAndSetPreparedStatement(fixture.getConnection()).executeUpdate();
        }
    }


    @Test
    public void getTableColumns() throws SQLException {
        Connection con = TOKIO.getJdbcFixture().getConnection();
        List<String> result = SQLUtil.getTableFields(con, TABLE_NAME);
        assertThat(result.toString(), equalTo("[TEST1.id, TEST1.dataTest]"));

        result = SQLUtil.getTableFields(con, TABLE_NAME2);
        assertThat(result.toString(), equalTo("[TEST2.id, TEST2.dataTest, TEST2.notFilled]"));
    }
}
