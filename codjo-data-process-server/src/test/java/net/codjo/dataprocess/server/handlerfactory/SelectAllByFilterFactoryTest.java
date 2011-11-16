package net.codjo.dataprocess.server.handlerfactory;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.dataprocess.common.DataProcessConstants;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class SelectAllByFilterFactoryTest {
    private Connection con;
    private SelectAllByFilterFactory selectAllByFilterFactory;
    private JdbcFixture fixture;
    private static final String TABLE_TEST = "#TABLE_TEST";


    @Before
    public void before() throws Exception {
        fixture = new DatabaseFactory().createJdbcFixture();
        fixture.doSetUp();
        selectAllByFilterFactory = new SelectAllByFilterFactory();
        con = fixture.getConnection();
        fixture.create(SqlTable.table(TABLE_TEST), "TABLE_ID1 int not null, "
                                                   + "TABLE_ID2 int not null, "
                                                   + "CHAMP1 varchar(50) null,"
                                                   + "CHAMP2 varchar(75) null,"
                                                   + "CHAMP3 varchar(25) null");
    }


    @After
    public void after() {
        fixture.doTearDown();
    }


    @Test
    public void proceedWithWhereClause() throws Exception {
        Map<String, String> args = new HashMap<String, String>();
        args.put(DataProcessConstants.TABLE_NAME_KEY, TABLE_TEST);
        args.put(DataProcessConstants.WHERE_CLAUSE_KEY, "CHAMP1 = '1'");
        args.put("CHAMP1", "VALEUR1");
        args.put("CHAMP2", "VALEUR2");

        String sqlInsert = selectAllByFilterFactory.proceed(con, args);
        assertThat("Select TABLE_ID1, TABLE_ID2, CHAMP1, CHAMP2, CHAMP3 from #TABLE_TEST where CHAMP1 = '1'",
                   equalTo(sqlInsert));
    }


    @Test
    public void proceedWithoutWhereClause() throws Exception {
        Map<String, String> args = new HashMap<String, String>();
        args.put(DataProcessConstants.TABLE_NAME_KEY, TABLE_TEST);
        args.put("champ1", "VALEUR1");
        args.put("champ2", "VALEUR2");

        String sql = selectAllByFilterFactory.proceed(con, args);
        assertThat("Select TABLE_ID1, TABLE_ID2, CHAMP1, CHAMP2, CHAMP3 from #TABLE_TEST"
                   + " where CHAMP1 = ? and CHAMP2 = ?", equalTo(sql));

        args.clear();
        args.put(DataProcessConstants.TABLE_NAME_KEY, TABLE_TEST);
        sql = selectAllByFilterFactory.proceed(con, args);
        assertThat("Select TABLE_ID1, TABLE_ID2, CHAMP1, CHAMP2, CHAMP3 from #TABLE_TEST", equalTo(sql));
    }
}
