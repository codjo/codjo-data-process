package net.codjo.dataprocess.gui.treatmenthelper;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class RepositoryPreference {
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private Map<String, List<String>> repositoryMap = new HashMap<String, List<String>>();


    public void addRepository(String repositoryName) {
        repositoryMap.put(repositoryName, new ArrayList<String>());
        propertyChangeSupport.firePropertyChange("repositoryMap", null, null);
    }


    public void removeRepository(String repositoryName) {
        repositoryMap.remove(repositoryName);
        propertyChangeSupport.firePropertyChange("repositoryMap", null, null);
    }


    public List<String> getRepositoryNames() {
        return Arrays.asList(repositoryMap.keySet().toArray(new String[repositoryMap.keySet().size()]));
    }


    public void addRepositoryPath(String repositoryName, String path) {
        List<String> repositoryPath = repositoryMap.get(repositoryName);
        if (repositoryPath == null) {
            throw new IllegalArgumentException("Le repository '" + repositoryName + "' n'existe pas.");
        }
        repositoryPath.add(path);
        propertyChangeSupport.firePropertyChange("repositoryMap", null, null);
    }


    public void removeRepositoryPath(String repositoryName, String path) {
        List<String> repositoryPath = repositoryMap.get(repositoryName);
        if (repositoryPath == null) {
            throw new IllegalArgumentException("Le repository '" + repositoryName + "' n'existe pas.");
        }
        repositoryPath.remove(path);
        propertyChangeSupport.firePropertyChange("repositoryMap", null, null);
    }


    public List<String> getRepositoryPath(String repositoryName) {
        return repositoryMap.get(repositoryName);
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }


    private Object readResolve() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        return this;
    }
}
