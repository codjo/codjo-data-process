package net.codjo.dataprocess.common.model;
import static net.codjo.test.common.matcher.JUnitMatchers.assertThat;
import static net.codjo.test.common.matcher.JUnitMatchers.equalTo;
import static net.codjo.test.common.matcher.JUnitMatchers.is;
import static net.codjo.test.common.matcher.JUnitMatchers.nullValue;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class ResultTableTest {
    private ResultTable resultTable;


    @Before
    public void before() {
        resultTable = new ResultTable();
    }


    @Test
    public void content() {
        assertThat(resultTable.getTable(), is(nullValue()));
        assertThat(resultTable.getSelectAllHandler(), is(nullValue()));

        resultTable.setTable("MA_TABLE");
        resultTable.setSelectAllHandler("selectAllPeriod");

        assertThat(resultTable.getTable(), equalTo("MA_TABLE"));
        assertThat(resultTable.getSelectAllHandler(), equalTo("selectAllPeriod"));
    }
}
