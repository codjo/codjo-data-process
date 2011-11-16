package net.codjo.dataprocess.server.util;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlTable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DatabaseToolsTest {
    private JdbcFixture jdbc;
    private static final String TABLE_TEST_1 = "TABLE_TEST_1";
    private static final String TABLE_TEST_2 = "TABLE_TEST_2";


    @Before
    public void before() throws Exception {
        jdbc = new DatabaseFactory().createJdbcFixture();
        jdbc.doSetUp();
        jdbc.advanced().dropAllObjects();
    }


    @After
    public void after() throws Exception {
        jdbc.doTearDown();
    }


    @Test
    public void getAllFieldNamesByTable() throws Exception {
        createTestTables(jdbc);

        String result = DatabaseTools.getAllFieldNamesByTable(jdbc.getConnection());
        List<String> list = Arrays.asList(result.split(","));
        assertThat(true, equalTo(list.contains("TABLE_TEST_1.id")));
        assertThat(true, equalTo(list.contains("TABLE_TEST_1.field1")));
        assertThat(true, equalTo(list.contains("TABLE_TEST_1.field2")));
        assertThat(true, equalTo(list.contains("TABLE_TEST_2.id")));
        assertThat(true, equalTo(list.contains("TABLE_TEST_2.field1")));
        assertThat(true, equalTo(list.contains("TABLE_TEST_2.field2")));

        dropTestTables(jdbc);
    }


    @Test
    public void executeQuery() throws Exception {
        jdbc.create(SqlTable.table("T_DIRECTSQL_LOG"),
                    "T_DIRECT_SQL_LOG_ID  numeric(23)  identity   not null, "
                    + " INITIATOR         varchar(30)  not null, "
                    + " FLAG              varchar(30)  null, "
                    + " REQUEST_DATE      datetime     not null, "
                    + " SQL_REQUEST       text         not null, "
                    + " RESULT            text         not null");

        jdbc.create(SqlTable.table(TABLE_TEST_1),
                    "id integer, field1 varchar(50) not null, field2 varchar(50) not null");
        jdbc.executeUpdate(
              "insert into " + TABLE_TEST_1 + " (id, field1, field2) values (1, 'poire', 'confiture')");
        jdbc.executeUpdate(
              "insert into " + TABLE_TEST_1 + " (id, field1, field2) values (2, 'banane', 'marmalade')");
        jdbc.executeUpdate(
              "insert into " + TABLE_TEST_1 + " (id, field1, field2) values (3, 'fraise', 'jus')");

        String result = DatabaseTools.executeQuery("michel",
                                                   jdbc.getConnection(),
                                                   "SELECT id, field1, field2 from " + TABLE_TEST_1
                                                   + " order by id, field1, field2", 1, 10);
        assertThat(result, equalTo(
              "RS\n3\n3\nid\tfield1\tfield2\t\n1\tpoire\tconfiture\t\n2\tbanane\tmarmalade\t\n3\tfraise\tjus\t\n"));

        result = DatabaseTools.executeQuery("michel",
                                            jdbc.getConnection(),
                                            "SELETC * from " + TABLE_TEST_1, 1, 10);
        assertThat(result.startsWith(DatabaseTools.EXCEPTION_THROWN + "\n"), equalTo(true));
        jdbc.drop(SqlTable.table("T_DIRECTSQL_LOG"));
    }


    private static void createTestTables(JdbcFixture fixture) throws SQLException {
        fixture.create(SqlTable.table(TABLE_TEST_1),
                       "id integer, field1 varchar(50) not null, field2 varchar(100) not null");
        fixture.create(SqlTable.table(TABLE_TEST_2),
                       "id integer, field1 varchar(100) not null, field2 varchar(100) not null");
    }


    private static void dropTestTables(JdbcFixture fixture) throws SQLException {
        fixture.drop(SqlTable.table(TABLE_TEST_1));
        fixture.drop(SqlTable.table(TABLE_TEST_2));
    }
}
