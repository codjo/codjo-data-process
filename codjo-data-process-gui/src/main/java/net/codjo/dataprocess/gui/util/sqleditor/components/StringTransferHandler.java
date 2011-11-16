package net.codjo.dataprocess.gui.util.sqleditor.components;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
/**
 *
 */
public abstract class StringTransferHandler extends TransferHandler {

    protected abstract String exportString(JComponent jc);


    protected abstract void importString(JComponent jc, String str);


    protected abstract void cleanup(JComponent jc, boolean remove);


    @Override
    protected Transferable createTransferable(JComponent jc) {
        return new StringSelection(exportString(jc));
    }


    @Override
    public int getSourceActions(JComponent jc) {
        return COPY;
    }


    @Override
    public boolean importData(JComponent jc, Transferable tt) {
        if (canImport(jc, tt.getTransferDataFlavors())) {
            try {
                String str = (String)tt.getTransferData(DataFlavor.stringFlavor);
                importString(jc, str);
                return true;
            }
            catch (UnsupportedFlavorException ufe) {
                ;
            }
            catch (IOException ioe) {
                ;
            }
        }

        return false;
    }


    @Override
    protected void exportDone(JComponent jc, Transferable data, int action) {
        cleanup(jc, action == MOVE);
    }


    @Override
    public boolean canImport(JComponent jc, DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (DataFlavor.stringFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
}