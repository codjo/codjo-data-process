/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.context;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
/**
 *
 */
public class DataProcessContext implements Cloneable {
    public static final String PACKAGE_ARGUMENT_MODIFIER = "packageArgumentModifier";
    private Map<String, String> context = new HashMap<String, String>();


    public DataProcessContext() {
    }


    public void setContext(DataProcessContext dataProcessContext) {
        this.context = dataProcessContext.context;
    }


    public String getProperty(String key) {
        return context.get(key);
    }


    public void setProperty(String key, String value) {
        if (value != null && !"".equals(value.trim())) {
            context.put(key, value);
        }
        else {
            removeProperty(key);
        }
    }


    public void removeProperty(String key) {
        context.remove(key);
    }


    public Set<String> keySet() {
        return context.keySet();
    }


    public boolean containsKey(String key) {
        return context.containsKey(key);
    }


    public Set<Map.Entry<String, String>> entrySet() {
        return context.entrySet();
    }


    public void putAll(Map<String, String> map) {
        context.putAll(map);
        for (Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            if (value == null || "".equals(value.trim())) {
                context.remove(entry.getKey());
            }
        }
    }


    public void addContext(DataProcessContext aContext) {
        if (aContext != null) {
            putAll(aContext.context);
        }
    }


    public void putAllInMap(Map<String, String> map) {
        map.putAll(context);
    }


    public int size() {
        return context.size();
    }


    public String encode() {
        return DataProcessContextCodec.encode(this);
    }


    public void setPropertyLocalArgument(String key, String value, int repositoryId,
                                         String executionListName) {
        key = new StringBuilder().append(repositoryId).append(".").append(executionListName)
              .append(".").append(key).toString();
        context.put(key, value);
    }


    @Override
    public DataProcessContext clone() throws CloneNotSupportedException {
        super.clone();
        DataProcessContext dataProcessContext = new DataProcessContext();
        dataProcessContext.context.putAll(context);
        return dataProcessContext;
    }


    @Override
    public String toString() {
        return context.toString();
    }
}
