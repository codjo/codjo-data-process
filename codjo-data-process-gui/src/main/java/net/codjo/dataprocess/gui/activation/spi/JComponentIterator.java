package net.codjo.dataprocess.gui.activation.spi;
/**
 *
 */
public interface JComponentIterator {

    JComponentPod next();


    boolean hasMoreComponents();


    void resetIndex();
}
