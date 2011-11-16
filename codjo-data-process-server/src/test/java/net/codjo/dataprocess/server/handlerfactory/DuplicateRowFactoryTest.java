package net.codjo.dataprocess.server.handlerfactory;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.dataprocess.common.DataProcessConstants;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class DuplicateRowFactoryTest {
    private Connection con;
    private DuplicateRowFactory duplicateRowFactory;
    private JdbcFixture fixture;
    private static final String TABLE_TEST = "#TABLE_TEST";


    @Before
    public void before() throws Exception {
        fixture = new DatabaseFactory().createJdbcFixture();
        fixture.doSetUp();
        duplicateRowFactory = new DuplicateRowFactory();
        con = fixture.getConnection();
        fixture.create(SqlTable.table(TABLE_TEST), "TABLE_ID1 int not null, "
                                                   + "TABLE_ID2 int not null, "
                                                   + "VALEUR1 varchar(50)  null,"
                                                   + "VALEUR2 varchar(75)  null");
    }


    @After
    public void after() {
        fixture.doTearDown();
    }


    @Test
    public void proceed() throws Exception {
        Map<String, String> args = new HashMap<String, String>();
        args.put("tableId1", "10");
        args.put("tableId2", "15");
        args.put(DataProcessConstants.TABLE_NAME_KEY, TABLE_TEST);

        String sqlInsert = duplicateRowFactory.proceed(con, args);
        assertThat("insert into #TABLE_TEST (VALEUR1, VALEUR2) "
                   + "select VALEUR1, VALEUR2 from #TABLE_TEST where TABLE_ID1 = ? and TABLE_ID2 = ?",
                   equalTo(sqlInsert));
    }


    @Test
    public void toSqlUpper() {
        assertThat(duplicateRowFactory.sqlUpper("tableId"), equalTo("TABLE_ID"));
        assertThat(duplicateRowFactory.sqlUpper("tTable"), equalTo("T_TABLE"));
    }
}
