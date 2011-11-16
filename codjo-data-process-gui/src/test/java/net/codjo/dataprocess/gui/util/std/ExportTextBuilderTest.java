package net.codjo.dataprocess.gui.util.std;
import net.codjo.dataprocess.gui.util.std.ExportTextBuilder.ExportTextWriter;
import net.codjo.gui.toolkit.table.GroupableTableHeaderBuilder;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class ExportTextBuilderTest {
    private static final String VALUES_SEPARATOR = ";";
    private RequestTable tableWith3Columns;
    private RequestTable tableWith2Columns;
    private ExportTextBuilder exportTextBuilder;


    @Before
    public void setUp() throws Exception {
        exportTextBuilder = new ExportTextBuilder();
        DefaultGuiContext defaultGuiContext = new DefaultGuiContext();

        LocalGuiContext guiContext = new LocalGuiContext(defaultGuiContext);
        guiContext.putProperty("export.text.fileSeparator", VALUES_SEPARATOR);
        guiContext.putProperty("export.text.decimalSeparator", ",");
        exportTextBuilder.setGuiContext(guiContext);
        tableWith3Columns = createTable(new String[]{"lettre", "chiffre", "lettre_chiffre"},
                                        new String[][]{{"a", "1", "a1"},
                                                       {"b", "2", "b2"},
                                                       {"c", "3", "c3"},
                                                       {"d", "4", "d4"},
                                        });
        tableWith2Columns = createTable(new String[]{"toto", "tata"},
                                        new String[][]{{"x", "9"},
                                                       {"y", "8"},
                                                       {"z", "7"}});
    }


    @Test
    public void test_single_table() throws Exception {
        StringWriter stringWriter = new StringWriter();
        exportTextBuilder.add(tableWith3Columns)
              .setHeaderData(tableWith3Columns, ExportTextBuilder.createColumnHeader(tableWith3Columns))
              .buildTextDatas(true, new ExportTextWriter(exportTextBuilder.getContext(),
                                                         stringWriter, 1));
        assertMatrixEquals(new String[][]{
              {"\"lettre\"", "\"chiffre\"", "\"lettre_chiffre\""},
              {"\"a\"", "\"1\"", "\"a1\""},
              {"\"b\"", "\"2\"", "\"b2\""},
              {"\"c\"", "\"3\"", "\"c3\""},
              {"\"d\"", "\"4\"", "\"d4\""}}, stringWriter);
    }


    @Test
    public void test_multiple_tables() throws Exception {
        StringWriter stringWriter = new StringWriter();
        exportTextBuilder.add(tableWith3Columns)
              .setHeaderData(tableWith3Columns, ExportTextBuilder.createColumnHeader(tableWith3Columns))
              .add(tableWith2Columns)
              .setHeaderData(tableWith2Columns, ExportTextBuilder.createColumnHeader(tableWith2Columns))
              .buildTextDatas(true, new ExportTextWriter(exportTextBuilder.getContext(),
                                                         stringWriter, 1));
        assertMatrixEquals(new String[][]{
              {"\"lettre\"", "\"chiffre\"", "\"lettre_chiffre\""},
              {"\"a\"", "\"1\"", "\"a1\""},
              {"\"b\"", "\"2\"", "\"b2\""},
              {"\"c\"", "\"3\"", "\"c3\""},
              {"\"d\"", "\"4\"", "\"d4\""},
              {"\"toto\"", "\"tata\"", ""},
              {"\"x\"", "\"9\"", ""},
              {"\"y\"", "\"8\"", ""},
              {"\"z\"", "\"7\"", ""},
        }, stringWriter);
    }


    @Test
    public void test_multiple_table_with_customized_header() throws Exception {
        StringWriter stringWriter = new StringWriter();
        exportTextBuilder.add(tableWith3Columns)
              .setHeaderData(tableWith3Columns, ExportTextBuilder.createColumnHeader(tableWith3Columns))
              .add(tableWith2Columns)
              .buildTextDatas(true, new ExportTextWriter(exportTextBuilder.getContext(),
                                                         stringWriter, 1));
        assertMatrixEquals(new String[][]{
              {"\"lettre\"", "\"chiffre\"", "\"lettre_chiffre\""},
              {"\"a\"", "\"1\"", "\"a1\""},
              {"\"b\"", "\"2\"", "\"b2\""},
              {"\"c\"", "\"3\"", "\"c3\""},
              {"\"d\"", "\"4\"", "\"d4\""},
              {"\"x\"", "\"9\"", ""},
              {"\"y\"", "\"8\"", ""},
              {"\"z\"", "\"7\"", ""},
        }, stringWriter);
    }


    @Test
    public void test_multiple_table_with_groupable_header() throws Exception {
        StringWriter stringWriter = new StringWriter();
        GroupableTableHeaderBuilder.install(tableWith3Columns)
              .createGroupColumn("Groupe 1", 0, 1).build();
        exportTextBuilder.add(tableWith3Columns)
              .setHeaderData(tableWith3Columns, ExportTextBuilder.createColumnHeader(tableWith3Columns))
              .add(tableWith2Columns)
              .setHeaderData(tableWith2Columns, ExportTextBuilder.createColumnHeader(tableWith2Columns))
              .buildTextDatas(true, new ExportTextWriter(exportTextBuilder.getContext(),
                                                         stringWriter, 1));
        assertMatrixEquals(new String[][]{
              {"\"Groupe 1\"", "\"Groupe 1\"", ""},
              {"\"lettre\"", "\"chiffre\"", "\"lettre_chiffre\""},
              {"\"a\"", "\"1\"", "\"a1\""},
              {"\"b\"", "\"2\"", "\"b2\""},
              {"\"c\"", "\"3\"", "\"c3\""},
              {"\"d\"", "\"4\"", "\"d4\""},
              {"\"toto\"", "\"tata\"", ""},
              {"\"x\"", "\"9\"", ""},
              {"\"y\"", "\"8\"", ""},
              {"\"z\"", "\"7\"", ""},
        }, stringWriter);
    }


    @Test
    public void test_multiple_header() throws Exception {
        StringWriter stringWriter = new StringWriter();
        GroupableTableHeaderBuilder.install(tableWith3Columns)
              .createGroupColumn("Groupe 1", 0, 1).build();
        Object[][] mainHeader = ExportTextBuilder.createColumnHeader(tableWith3Columns);
        Object[][] upperHeader = new Object[][]{{"Casse", "toi", "pauvre mec!"}};
        Object[][] lowerHeader = new Object[][]{{"pauvre fille!"}, {"t chiante"}};

        exportTextBuilder.add(tableWith3Columns)
              .setHeaderData(tableWith3Columns, upperHeader, mainHeader, lowerHeader)
              .add(tableWith2Columns)
              .setHeaderData(tableWith2Columns, ExportTextBuilder.createColumnHeader(tableWith2Columns))
              .buildTextDatas(true, new ExportTextWriter(exportTextBuilder.getContext(),
                                                         stringWriter, 1));
        assertMatrixEquals(new String[][]{
              {"\"Casse\"", "\"toi\"", "\"pauvre mec!\""},
              {"\"Groupe 1\"", "\"Groupe 1\"", ""},
              {"\"lettre\"", "\"chiffre\"", "\"lettre_chiffre\""},
              {"\"pauvre fille!\"", "", ""},
              {"\"t chiante\"", "", ""},
              {"\"a\"", "\"1\"", "\"a1\""},
              {"\"b\"", "\"2\"", "\"b2\""},
              {"\"c\"", "\"3\"", "\"c3\""},
              {"\"d\"", "\"4\"", "\"d4\""},
              {"\"toto\"", "\"tata\"", ""},
              {"\"x\"", "\"9\"", ""},
              {"\"y\"", "\"8\"", ""},
              {"\"z\"", "\"7\"", ""},
        }, stringWriter);
    }


    @Test
    public void test_multi_pages() throws Exception {
        String[][] datas = new String[][]{
              {"a", "1", "a1"},
              {"b", "2", "b2"},
              {"c", "3", "c3"},
              {"d", "4", "d4"},
        };
        String[] columnNames = new String[]{"lettre", "chiffre", "lettre_chiffre"};

        OnePageOneRowListDataSource dataSource = new OnePageOneRowListDataSource(columnNames, datas);
        tableWith3Columns = createTable(columnNames, datas, dataSource);
        dataSource.load();

        StringWriter stringWriter = new StringWriter();
        exportTextBuilder.add(tableWith3Columns)
              .setHeaderData(tableWith3Columns, ExportTextBuilder.createColumnHeader(tableWith3Columns))
              .buildTextDatas(true, new ExportTextWriter(exportTextBuilder.getContext(),
                                                         stringWriter, 1));
        assertMatrixEquals(new String[][]{
              {"\"lettre\"", "\"chiffre\"", "\"lettre_chiffre\""},
              {"\"a\"", "\"1\"", "\"a1\""},
              {"\"b\"", "\"2\"", "\"b2\""},
              {"\"c\"", "\"3\"", "\"c3\""},
              {"\"d\"", "\"4\"", "\"d4\""}}, stringWriter);
    }


    @Test
    public void test_multi_pages_disabled() throws Exception {
        String[][] datas = new String[][]{
              {"a", "1", "a1"},
              {"b", "2", "b2"},
              {"c", "3", "c3"},
              {"d", "4", "d4"},
        };
        String[] columnNames = new String[]{"lettre", "chiffre", "lettre_chiffre"};

        OnePageOneRowListDataSource dataSource = new OnePageOneRowListDataSource(columnNames, datas);

        tableWith3Columns = createTable(columnNames, datas, dataSource);
        dataSource.load();

        StringWriter stringWriter = new StringWriter();
        exportTextBuilder.add(tableWith3Columns)
              .setHeaderData(tableWith3Columns, ExportTextBuilder.createColumnHeader(tableWith3Columns))
              .buildTextDatas(false, new ExportTextWriter(exportTextBuilder.getContext(),
                                                          stringWriter, 1));
        assertMatrixEquals(new String[][]{
              {"\"lettre\"", "\"chiffre\"", "\"lettre_chiffre\""},
              {"\"a\"", "\"1\"", "\"a1\""},}
              , stringWriter);
    }


    @Test
    public void test_decimalSeparator() throws Exception {
        String decimalSep = (String)exportTextBuilder.getContext()
              .getProperty("export.text.decimalSeparator");
        Assert.assertEquals(",", decimalSep);
        RequestTable table = createTable(new String[]{"chiffre avec sorter", "chiffre sans sorter"},
                                         new String[][]{{"1.5", "2.0"}, {"5.0", "6.2"}});
        table.getPreference().getColumns().get(0).setSorter("Numeric");

        StringWriter stringWriter = new StringWriter();
        exportTextBuilder.add(table)
              .setHeaderData(table, ExportTextBuilder.createColumnHeader(table))
              .buildTextDatas(true, new ExportTextWriter(exportTextBuilder.getContext(),
                                                         stringWriter, 1));
        assertMatrixEquals(new String[][]{
              {"\"chiffre avec sorter\"", "\"chiffre sans sorter\""},
              {"\"1,5\"", "\"2.0\""},
              {"\"5,0\"", "\"6.2\""}}
              , stringWriter);
    }


    @SuppressWarnings({"LocalVariableNamingConvention"})
    private static void assertMatrixEquals(String[][] expected, StringWriter actual) {
        String[] actuals = actual.toString().split("\n");
        Assert.assertEquals("Expected " + expected.length + " rows but was " + actuals.length,
                            expected.length,
                            actuals.length);
        for (int i = 0; i < expected.length; i++) {
            int j = 0;
            Collection<String> tmp = new ArrayList<String>();
            while (j <= actuals[i].length()) {
                int k = actuals[i].indexOf(VALUES_SEPARATOR, j);
                if (-1 == k) {
                    tmp.add(actuals[i].substring(j));
                    break;
                }
                tmp.add(actuals[i].substring(j, k));
                j = k + 1;
            }
            String[] values = tmp.toArray(new String[tmp.size()]);
            Assert.assertArrayEquals(
                  "Expected: " + Arrays.asList(expected[i]) + " but was: " + Arrays.asList(values),
                  expected[i], values);
        }
    }


    private static RequestTable createTable(String[] columnNames, String[][] values) throws Exception {
        return createTable(columnNames, values, new ListDataSource());
    }


    private static RequestTable createTable(String[] columnNames,
                                            String[][] values,
                                            ListDataSource listDataSource)
          throws Exception {
        Preference preference = new Preference();
        for (String columnName : columnNames) {
            preference.getColumns().add(new Column(columnName, columnName));
        }
        RequestTable table = new RequestTable(listDataSource);
        table.setPreference(preference);
        listDataSource.setColumns(columnNames);
        for (String[] row : values) {
            listDataSource.addRow(createRow(columnNames, row));
        }
        return table;
    }


    private static Row createRow(String[] columnNames, String[] values) {
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < columnNames.length; i++) {
            map.put(columnNames[i], values[i]);
        }
        return new Row(map);
    }


    private static class OnePageOneRowListDataSource extends ListDataSource {
        private Row[] rows;


        private OnePageOneRowListDataSource(String[] columnNames, String[][] datas) {
            List<Row> rowList = new ArrayList<Row>();
            for (String[] data : datas) {
                rowList.add(createRow(columnNames, data));
            }
            rows = rowList.toArray(new Row[rowList.size()]);
            setPageSize(1);
        }


        @Override
        public void load() throws RequestException {
            super.load();
            setRow(rows[0]);
        }


        @Override
        public void loadNextPage() throws RequestException {
            super.loadNextPage();
            if (getCurrentPage() <= rows.length) {
                setRow(rows[getCurrentPage() - 1]);
            }
            else {
                setLoadResult(null);
            }
        }


        private void setRow(Row row) {
            Result result = new Result();
            result.addRow(row);
            setLoadResult(result);
        }


        @Override
        public int getTotalRowCount() {
            return rows.length;
        }
    }
}
