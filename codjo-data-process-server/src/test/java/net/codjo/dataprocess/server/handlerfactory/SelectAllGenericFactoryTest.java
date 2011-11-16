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
/**
 *
 */
public class SelectAllGenericFactoryTest {
    private Connection con;
    private SelectAllGenericFactory selectAllGenericFactory;
    private JdbcFixture fixture;
    private static final String TABLE_TEST = "#TABLE_TEST";


    @Before
    public void before() throws Exception {
        fixture = new DatabaseFactory().createJdbcFixture();
        fixture.doSetUp();
        selectAllGenericFactory = new SelectAllGenericFactory();
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
    public void proceedWithWhereClause() throws Exception {
        Map<String, String> args = new HashMap<String, String>();
        args.put(DataProcessConstants.TABLE_NAME_KEY, TABLE_TEST);
        args.put(DataProcessConstants.WHERE_CLAUSE_KEY, "VALEUR1 is null");

        String sqlInsert = selectAllGenericFactory.proceed(con, args);
        assertThat("Select TABLE_ID1, TABLE_ID2, VALEUR1, VALEUR2 from #TABLE_TEST where VALEUR1 is null",
                   equalTo(sqlInsert));
    }


    @Test
    public void proceedWithoutWhereClause() throws Exception {
        Map<String, String> args = new HashMap<String, String>();
        args.put(DataProcessConstants.TABLE_NAME_KEY, TABLE_TEST);

        String sqlInsert = selectAllGenericFactory.proceed(con, args);
        assertThat("Select TABLE_ID1, TABLE_ID2, VALEUR1, VALEUR2 from #TABLE_TEST", equalTo(sqlInsert));
    }
}
