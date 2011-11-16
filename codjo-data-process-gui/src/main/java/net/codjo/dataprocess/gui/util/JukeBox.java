/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util;
import net.codjo.dataprocess.common.Log;
import java.applet.Applet;
import java.applet.AudioClip;
/**
 *
 */
public class JukeBox {
    private JukeBox() {
    }


    /**
     * Joue le son de la victoire et le lib�re sinon le p�riph�rique sonore reste occup� pour les autres
     * applications.
     */
    public static void playSuccessSound() {
        String sound = "/sons/Bravos.wav";
        try {
            AudioClip successClip = loadSound(sound);
            successClip.play();
            try {
                Thread.sleep(1500);
                successClip = null;
                flushMemory();
            }
            catch (InterruptedException ex) {
                Log.error(JukeBox.class, ex);
            }
        }
        catch (Exception e) {
            Log.error(JukeBox.class, "Impossible de jouer le son de r�ussite (" + sound + ")");
            throw new IllegalArgumentException("Impossible de jouer le son de r�ussite (" + sound + ")");
        }
    }


    /**
     * Joue le son de la d�faite et le lib�re sinon le p�riph�rique sonore reste occup� pour les autres
     * applications.
     */
    public static void playFailureSound() {
        String sound = "/sons/Verre.wav";
        try {
            AudioClip failureClip = loadSound(sound);
            failureClip.play();
            try {
                Thread.sleep(1500);
                failureClip = null;
                flushMemory();
            }
            catch (InterruptedException ex) {
                Log.error(JukeBox.class, ex);
            }
        }
        catch (Exception e) {
            Log.error(JukeBox.class, "Impossible de jouer le son d'�chec (" + sound + ")");
            throw new IllegalArgumentException("Impossible de jouer le son d'�chec (" + sound + ")");
        }
    }


    /**
     * Chargement d'un son
     *
     * @return Le son
     */
    private static AudioClip loadSound(String path) {
        return Applet.newAudioClip(JukeBox.class.getResource(path));
    }


    /**
     * Force le garbage collector
     */
    private static void flushMemory() {
        System.gc();
        System.runFinalization();
    }
}
