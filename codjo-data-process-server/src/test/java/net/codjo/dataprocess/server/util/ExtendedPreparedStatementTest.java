package net.codjo.dataprocess.server.util;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ExtendedPreparedStatementTest {
    private ExtendedPreparedStatement extendedPreparedStatement;


    @Before
    public void before() {
        extendedPreparedStatement = new ExtendedPreparedStatement();
    }


    @Test
    public void compileSql() {
        Map<String, List<Integer>> positions = new HashMap<String, List<Integer>>();

        String sql = "select * from T1 t where (t.name=${name} or t.surName=${name}) and age=${age}";
        String compiledSql = extendedPreparedStatement.compileSql(sql, positions);

        String exepectedCompiledsql = "select * from T1 t where (t.name=? or t.surName=?) and age=?";

        assertThat(exepectedCompiledsql, equalTo(compiledSql));
        assertThat(positions.get("surname"), nullValue());
        assertThat(1, equalTo(positions.get("name").get(0)));
        assertThat(2, equalTo(positions.get("name").get(1)));
        assertThat(3, equalTo(positions.get("age").get(0)));
    }
}