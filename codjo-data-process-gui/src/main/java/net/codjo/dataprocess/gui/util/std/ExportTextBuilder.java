package net.codjo.dataprocess.gui.util.std;
import net.codjo.gui.toolkit.table.GroupColumn;
import net.codjo.gui.toolkit.table.GroupableTableHeader;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.common.WindowsHelper;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;
/**
 *
 */
public class ExportTextBuilder {
    private static final Logger LOG = Logger.getLogger(ExportTextBuilder.class);
    private static final int MAX_NB_FILES_TO_CREATE = 10;
    private static final String NUMERIC_SORTER = "Numeric";
    private static final String DEFAULT_DECIMAL_SEPARATOR = ".";
    private static final String DEFAULT_FILE_BASE_NAME = "export_txt";
    private static final String DEFAULT_FILE_EXT = ".txt";
    private static final String DEFAULT_FILE_SEPARATOR = "\t";
    private static final String TEMPORARY_DIRECTORY = System.getProperty("java.io.tmpdir");
    private List<RequestTable> tableList = new ArrayList<RequestTable>();
    private Map<RequestTable, List<Object[][]>> headerTable = new HashMap<RequestTable, List<Object[][]>>();
    private GuiContext context;
    private RenderValueFunctor renderValueFunctor = new MyRenderValueFunctor();


    public ExportTextBuilder() {
    }


    public ExportTextBuilder add(RequestTable jTable) {
        return add(jTable, true);
    }


    public ExportTextBuilder add(RequestTable jTable, boolean withHeader) {
        this.tableList.add(jTable);
        return this;
    }


    public ExportTextBuilder setHeaderData(RequestTable jTable, Object[][]... headers) {
        List<Object[][]> headerList = new ArrayList<Object[][]>();
        headerList.addAll(Arrays.asList(headers));
        headerTable.put(jTable, headerList);
        return this;
    }


    public void buildTextDatas(boolean exportAllPages, ExportTextWriter writer)
          throws RequestException, IOException {
        int maxColumnCount = findMaxColumnCount();
        for (RequestTable table : tableList) {
            int numcols = table.getColumnCount();
            processHeader(table, maxColumnCount, writer);
            processDataRows(table, maxColumnCount, numcols, writer);
            if (exportAllPages) {
                while (table.hasNextPage()) {
                    table.loadNextPage();
                    processDataRows(table, maxColumnCount, numcols, writer);
                }
            }
        }
    }


    private void processDataRows(RequestTable table,
                                 int maxColumnCount,
                                 int numcols,
                                 ExportTextWriter writer) throws IOException {
        String[] sorters = new String[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            sorters[i] = table.getPreference()
                  .getColumns()
                  .get(table.convertColumnIndexToView(i))
                  .getSorter();
        }
        for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
            Object[] dataRow = new Object[maxColumnCount];
            for (int colIndex = 0; colIndex < numcols; colIndex++) {
                dataRow[colIndex] = renderValueFunctor.getRenderedValue(table, rowIndex, colIndex);
            }
            writeRow(dataRow, writer, sorters);
        }
    }


    private void processHeader(RequestTable table, int maxColumnCount, ExportTextWriter writer)
          throws IOException {
        List<Object[][]> headerDataList = headerTable.get(table);
        if (headerDataList != null) {
            for (Object[][] headerData : headerDataList) {
                for (Object[] aHeaderData : headerData) {
                    Object[] dataRow = new Object[maxColumnCount];
                    System.arraycopy(aHeaderData, 0, dataRow, 0, aHeaderData.length);
                    writeRow(dataRow, writer, null);
                }
            }
        }
    }


    private int findMaxColumnCount() {
        int length = 0;
        for (RequestTable table : tableList) {
            int columnCount = table.getColumnCount();
            if (columnCount > length) {
                length = columnCount;
            }
        }
        return length;
    }


    public static Object[][] createColumnHeader(RequestTable jTable) {
        JTableHeader tableHeader = jTable.getTableHeader();
        if (tableHeader instanceof GroupableTableHeader) {
            return createGroupableColumnHeader(tableHeader, jTable);
        }
        else {
            return createDefaultColumnHeader(jTable);
        }
    }


    private static Object[][] createDefaultColumnHeader(RequestTable jTable) {
        TableColumnModel columnModel = jTable.getTableHeader().getColumnModel();
        int columnCount = columnModel.getColumnCount();
        Object[][] headerArray = new Object[1][columnCount];
        for (int colIndex = 0; colIndex < columnCount; colIndex++) {
            headerArray[0][colIndex] = columnModel.getColumn(colIndex).getHeaderValue();
        }
        return headerArray;
    }


    private static Object[][] createGroupableColumnHeader(JTableHeader tableHeader, RequestTable jTable) {
        GroupableTableHeader header = (GroupableTableHeader)tableHeader;

        int columnCount = jTable.getColumnCount();
        int headerRowCount = header.getRowCount();
        Object[][] headerArray = new Object[headerRowCount][columnCount];
        TableColumnModel columnModel = jTable.getColumnModel();
        for (int col = 0; col < columnCount; col++) {
            TableColumn tableColumn = columnModel.getColumn(col);
            int row = 0;
            for (Iterator<GroupColumn> it = header.getColumnGroups(tableColumn); it.hasNext(); ) {
                GroupColumn groupColumn = it.next();
                headerArray[row][col] = groupColumn.getHeaderValue();
                row++;
            }

            headerArray[headerRowCount - 1][col] = jTable.getColumnName(col);
        }
        return headerArray;
    }


    public List<RequestTable> getTableList() {
        return tableList;
    }


    private static void writeRow(Object[] data, ExportTextWriter writer, String[] sorters)
          throws IOException {
        StringBuilder sb = new StringBuilder();
        int numcols = data.length;
        for (int j = 0; j < numcols; j++) {
            Object val = data[j];

            if (val != null && val instanceof String) {
                if (null != writer.getDecimalSeparator()
                    && null != sorters
                    && NUMERIC_SORTER.equalsIgnoreCase(sorters[j])) {
                    val = ((String)val).replace(DEFAULT_DECIMAL_SEPARATOR, writer.getDecimalSeparator());
                }
            }

            if (val != null) {
                sb.append("\"").append(val).append("\"");
            }
            if (j < numcols - 1) {
                sb.append(writer.getFileSeparator());
            }
        }
        sb.append("\n");
        writer.write(sb.toString());
    }


    private static String generateFilename(String fileBaseName, String fileExt, int nbFile) {
        String fileName = TEMPORARY_DIRECTORY + fileBaseName;
        if (nbFile == 0) {
            fileName += fileExt;
        }
        else {
            fileName += nbFile + fileExt;
        }

        return fileName;
    }


    public void generate(boolean exportAllPages) throws IOException, RequestException {
        String fileBaseName;
        String fileExt;
        if (context != null) {
            if (context.hasProperty("export.text.fileBaseName")) {
                fileBaseName = (String)context.getProperty("export.text.fileBaseName");
            }
            else {
                fileBaseName = DEFAULT_FILE_BASE_NAME;
            }
            if (context.hasProperty("export.text.fileExt")) {
                fileExt = (String)context.getProperty("export.text.fileExt");
            }
            else {
                fileExt = DEFAULT_FILE_EXT;
            }
        }
        else {
            throw new IllegalArgumentException(
                  "Le GuiContext n'a pas été initialisé. Appelez 'ExportTextBuilder.setGuiContext' avant.");
        }

        ExportTextWriter exportTextWriter = null;
        String fileName;
        try {
            if (tableList.isEmpty()) {
                throw new RequestException("No table was added, use ");
            }
            FileWriter file = null;
            String errMsg = "";
            int nbFile = 0;
            fileName = generateFilename(fileBaseName, fileExt, nbFile);
            while ((file == null) && (nbFile <= MAX_NB_FILES_TO_CREATE)) {
                try {
                    file = new FileWriter(fileName);
                }
                catch (Exception ex) {
                    errMsg = ex.getMessage();
                    nbFile++;
                    fileName = generateFilename(fileBaseName, fileExt, nbFile);
                }
            }

            if (file == null) {
                context.displayInfo("Impossible d'ouvrir le fichier : " + errMsg);
                throw new IOException("Impossible d'ouvrir le fichier : " + errMsg);
            }

            exportTextWriter = new ExportTextWriter(context, file);
            context.displayInfo("Export de type text vers " + fileName);
            buildTextDatas(exportAllPages, exportTextWriter);
        }
        catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            context.displayInfo(ex.getLocalizedMessage());
            throw new IOException(ex.getLocalizedMessage());
        }
        catch (RequestException ex) {
            LOG.error(ex.getMessage(), ex);
            context.displayInfo(ex.getLocalizedMessage());
            throw new RequestException(ex.getLocalizedMessage());
        }
        finally {
            if (exportTextWriter != null) {
                exportTextWriter.close();
            }
        }
        WindowsHelper.openWindowsFile(new File(fileName));
    }


    public void setGuiContext(GuiContext context) {
        this.context = context;
    }


    GuiContext getContext() {
        return context;
    }


    public void addAll(RequestTable... tables) {
        for (RequestTable table : tables) {
            add(table);
        }
    }


    public void setRenderValueFunctor(RenderValueFunctor renderValueFunctor) {
        this.renderValueFunctor = renderValueFunctor;
    }


    static class ExportTextWriter extends BufferedWriter {
        private String fileSeparator = DEFAULT_FILE_SEPARATOR;
        private String decimalSeparator = DEFAULT_DECIMAL_SEPARATOR;


        ExportTextWriter(GuiContext context, Writer file) {
            super(file);
            configSeparator(context);
        }


        ExportTextWriter(GuiContext context, Writer file, int sz) {
            super(file, sz);
            configSeparator(context);
        }


        private void configSeparator(GuiContext context) {
            if (context != null) {
                if (context.hasProperty("export.text.fileSeparator")) {
                    fileSeparator = (String)context.getProperty("export.text.fileSeparator");
                }
                if (context.hasProperty("export.text.decimalSeparator")) {
                    decimalSeparator = (String)context.getProperty("export.text.decimalSeparator");
                }
            }
            else {
                throw new IllegalArgumentException(
                      "Le GuiContext n'a pas été initialisé. Appelez 'ExportTextBuilder.setGuiContext' avant.");
            }
        }


        public String getFileSeparator() {
            return fileSeparator;
        }


        public String getDecimalSeparator() {
            return decimalSeparator;
        }
    }

    public static interface RenderValueFunctor {
        Object getRenderedValue(RequestTable jTable, int row, int col);
    }

    private static class MyRenderValueFunctor implements RenderValueFunctor {
        public Object getRenderedValue(RequestTable jTable, int row, int col) {
            int columnIndexToView = jTable.convertColumnIndexToView(col);
            TableCellRenderer renderer = jTable.getCellRenderer(row, columnIndexToView);
            Object value = jTable.getValueAt(row, col);

            if (renderer != null) {
                Component component = renderer
                      .getTableCellRendererComponent(jTable, value, false, false, row, col);
                if (component instanceof JLabel) {
                    value = ((JLabel)component).getText();
                }
            }
            return value;
        }
    }
}
