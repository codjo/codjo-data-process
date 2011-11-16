package net.codjo.dataprocess.gui.activation.spi;
import java.util.Properties;
import javax.swing.JComponent;
/**
 *
 */
public class JComponentPod {
    private JComponent jcomponent;
    private String unikKey;
    private Properties properties;


    public JComponentPod(JComponent jcomponent, String key) {
        this(jcomponent, key, new Properties());
    }


    public JComponentPod(JComponent jcomponent, String key, Properties properties) {
        this.jcomponent = jcomponent;
        this.unikKey = key;
        this.properties = new Properties(properties);
    }


    public JComponent getJcomponent() {
        return jcomponent;
    }


    public String getUnikKey() {
        return unikKey;
    }


    public Properties getProperties() {
        return properties;
    }
}
