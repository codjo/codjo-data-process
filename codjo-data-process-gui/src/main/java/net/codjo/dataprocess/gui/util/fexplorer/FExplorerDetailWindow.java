/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.fexplorer;
import net.codjo.dataprocess.client.HandlerCommandSender;
import net.codjo.dataprocess.common.eventsbinder.EventBinderException;
import net.codjo.dataprocess.common.eventsbinder.EventsBinder;
import net.codjo.dataprocess.common.eventsbinder.annotations.OnError;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnAction;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnMouse;
import net.codjo.dataprocess.gui.util.ErrorDialog;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.gui.toolkit.fileChooser.FileChooserManager;
import net.codjo.gui.toolkit.waiting.WaitingPanel;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.framework.GuiContext;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import sun.misc.BASE64Decoder;
/**
 *
 */
public class FExplorerDetailWindow extends JInternalFrame {
    private WaitingPanel waitingPanel = new WaitingPanel("Requête en cours...");
    private DefaultTableModel dirModel;
    private DefaultTableModel fileModel;
    private GuiContext ctxt;


    public FExplorerDetailWindow(GuiContext ctxt) throws RequestException {
        super("Explorateur de fichiers", true, true, true, true);
        this.ctxt = ctxt;
        FExplorerGui gui;
        try {
            gui = buildGui();
        }
        catch (EventBinderException e) {
            throw new RequestException("Error while trying to build gui : " + e.getMessage());
        }
        setSize(800, 600);
        gui.getBlockSizeCombo().setSelectedItem("4096");
        dir(".", gui);
    }


    @OnMouse(value = "dirTable", eventType = OnMouse.EventType.CLICKED, clickCount = 2)
    public void goInDirectorty(MouseEvent event, FExplorerGui gui) {
        if (event.getClickCount() >= 2) {
            int row = gui.getDirTable().getSelectedRow();
            String line = gui.getDirTable().getModel().getValueAt(row, 0).toString();
            try {
                dir(gui.getRelativePath().getText() + "/" + line, gui);
            }
            catch (RequestException ex) {
                GuiUtils.showErrorDialog(this, getClass(), "Erreur lors de l'appel serveur", ex);
            }
        }
    }


    @OnMouse(value = "fileTable", eventType = OnMouse.EventType.CLICKED, clickCount = 2)
    public void downloadFile(MouseEvent event, FExplorerGui gui) {
        int row = gui.getFileTable().getSelectedRow();
        if (row != -1) {
            String line = gui.getFileTable().getModel().getValueAt(row, 0).toString();
            try {
                download(gui.getRelativePath().getText() + "/" + line, gui);
            }
            catch (Exception ex) {
                GuiUtils.showErrorDialog(this, getClass(), "Erreur lors de l'appel serveur", ex);
            }
        }
    }


    @OnAction(propertiesBound = "QuitButton")
    public void quitCommand() {
        dispose();
    }


    @OnAction(propertiesBound = "DownloadButton")
    public void downloadCommand(FExplorerGui gui) {
        downloadFile(null, gui);
    }


    private byte[] readRemoteFile(String fileToDl, int block, int size)
          throws RequestException, IOException {
        StringBuilder buffer = executeDLRequest(fileToDl, block, size);
        BASE64Decoder base64Decoder = new BASE64Decoder();
        extractLine(buffer);
        int realSize = Integer.parseInt(extractLine(buffer));
        byte[] dataTmp = base64Decoder.decodeBuffer(buffer.toString());
        byte[] realData = new byte[realSize];
        System.arraycopy(dataTmp, 0, realData, 0, realData.length);
        return realData;
    }


    private void download(final String fileToDownload, final FExplorerGui gui)
          throws RequestException, IOException {
        String filename = fileToDownload.substring(fileToDownload.lastIndexOf('/') + 1);
        int idx = filename.lastIndexOf('.');
        String fileExt = "";
        if (idx >= 0) {
            fileExt = filename.substring(idx + 1);
            filename = filename.substring(0, idx);
        }
        final File dest =
              FileChooserManager.showChooserForSave(new File(filename + "." + fileExt),
                                                    "Sauvegarde du fichier", filename, fileExt);
        if (dest == null) {
            return;
        }

        waitingPanel.exec(new Downloader(gui, fileToDownload, dest));
    }


    private void dir(String relativePath, FExplorerGui gui)
          throws RequestException {
        dirModel =
              new DefaultTableModel(new String[]{"Nom"}, 0) {
                  @Override
                  public boolean isCellEditable(int row, int column) {
                      return false;
                  }
              };
        fileModel =
              new DefaultTableModel(new String[]{"Nom", "Taille", "Date de modification"}, 0) {
                  @Override
                  public boolean isCellEditable(int row, int column) {
                      return false;
                  }
              };

        StringBuilder buffer = executeDirRequest(relativePath);
        gui.getRelativePath().setText(relativePath);
        gui.getRealPath().setText(extractLine(buffer));
        extractLine(buffer); //--DIRS
        dirModel.addRow(new String[]{".."});
        String currentLine = extractLine(buffer);
        while (!"--FILES".equals(currentLine)) {
            dirModel.addRow(new String[]{currentLine});
            currentLine = extractLine(buffer);
        }
        currentLine = extractLine(buffer);
        while (!"--EOC".equals(currentLine)) {
            String[] part = currentLine.split("\\|");
            String fname = part[0];
            String fsize = part[1];
            String fdate = part[2];
            fileModel.addRow(new String[]{fname, fsize, fdate});
            currentLine = extractLine(buffer);
        }

        gui.getDirTable().setModel(dirModel);
        gui.getFileTable().setModel(fileModel);
    }


    private StringBuilder executeDirRequest(String relativPath) throws RequestException {
        StringBuilder resultString = null;
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("relativePath", relativPath);
        Result result = sender.sendCommand(ctxt, "dpFexplorer", arg);
        if (result.getRowCount() > 0) {
            resultString = new StringBuilder(result.getRow(0).getFieldValue("result"));
        }
        return resultString;
    }


    private StringBuilder executeDLRequest(String path, int block, int blockSize) throws RequestException {
        StringBuilder resultString = null;
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("filePath", path);
        arg.put("block", Integer.toString(block));
        arg.put("blockSize", Integer.toString(blockSize));
        Result result = sender.sendCommand(ctxt, "dpFdownload", arg);
        if (result.getRowCount() > 0) {
            resultString = new StringBuilder(result.getRow(0).getFieldValue("result"));
        }
        return resultString;
    }


    private static String extractLine(StringBuilder buffer) {
        int index = buffer.indexOf("\n");
        if (index >= 0) {
            String extracted = buffer.substring(0, index);
            buffer.replace(0, index + "\n".length(), "");
            return extracted;
        }
        else {
            return null;
        }
    }


    @OnError
    public void onError(Throwable th) {
        ErrorDialog.show(this, "Error", th);
    }


    private FExplorerGui buildGui() throws EventBinderException {
        FExplorerGui fExplorerGui = new FExplorerGui();
        setGlassPane(waitingPanel);
        EventsBinder binder = new EventsBinder();
        binder.bind(this, fExplorerGui);

        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, fExplorerGui.getMainPanel());

        dirModel = new DefaultTableModel(new String[]{"Nom"}, 1);
        fileModel = new DefaultTableModel(new String[]{"Nom", "Taille", "Date de modification"}, 1);

        fExplorerGui.getDirTable().setModel(dirModel);
        fExplorerGui.getFileTable().setModel(fileModel);
        return fExplorerGui;
    }


    private class Downloader implements Runnable {
        private FExplorerGui gui;
        private String fileToDownload;
        private File dest;


        Downloader(FExplorerGui gui, String fileToDownload, File dest) {
            this.gui = gui;
            this.fileToDownload = fileToDownload;
            this.dest = dest;
        }


        public void run() {
            int blockSize = Integer.parseInt(gui.getBlockSizeCombo().getSelectedItem().toString());
            int block = 0;

            try {
                waitingPanel.setText("Download :" + fileToDownload + " block " + block + " (" + blockSize
                                     + ")");
                FileOutputStream fos = new FileOutputStream(dest);

                byte[] data = readRemoteFile(fileToDownload, block, blockSize);
                while (true) {
                    waitingPanel.setText("Download :" + fileToDownload + " block " + block + " (" + blockSize
                                         + ")");
                    if (data.length > 0) {
                        fos.write(data);
                    }
                    if (data.length < blockSize) {
                        break;
                    }
                    block++;
                    data = readRemoteFile(fileToDownload, block, blockSize);
                }
                fos.close();
                waitingPanel.setText("Download :" + fileToDownload + " block " + block + " (" + blockSize
                                     + ")");
                Runtime.getRuntime().exec("explorer /e,/root," + dest.getParentFile().getAbsolutePath());
            }
            catch (Exception ex) {
                GuiUtils.showErrorDialog(FExplorerDetailWindow.this, getClass(), "Erreur interne", ex);
            }
        }
    }
}
