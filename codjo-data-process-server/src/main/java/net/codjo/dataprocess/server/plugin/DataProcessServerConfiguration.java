package net.codjo.dataprocess.server.plugin;
import net.codjo.dataprocess.common.util.UserLoginTracker;
import net.codjo.reflect.collect.ClassCollector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class DataProcessServerConfiguration {
    private static final String SERVER_MAP_UTIL = "serverMapUtil";
    private static final String USER_IMPORT_MAP = "userImportMap";
    private MapStore<String, String> mapStore = new MapStore<String, String>();
    private List<UserLoginTracker> userLoginTrackerList = new ArrayList<UserLoginTracker>();
    private List<ClassCollector> classCollectors = new ArrayList<ClassCollector>();


    public Map<String, String> getServerMapUtil() {
        return Collections.synchronizedMap(mapStore.getStore(SERVER_MAP_UTIL));
    }


    public Map<String, String> getUserImportMap() {
        return Collections.synchronizedMap(mapStore.getStore(USER_IMPORT_MAP));
    }


    public List<UserLoginTracker> getUserLoginTrackerList() {
        return Collections.synchronizedList(userLoginTrackerList);
    }


    public void addClassCollector(ClassCollector classCollector) {
        classCollectors.add(classCollector);
    }


    public List<ClassCollector> getClassCollectors() {
        return Collections.unmodifiableList(classCollectors);
    }


    private static class MapStore<T, K> {
        private Map<String, Map<T, K>> store = new HashMap<String, Map<T, K>>();


        Map<T, K> getStore(String storeName) {
            Map<T, K> map = store.get(storeName);
            if (map == null) {
                map = new HashMap<T, K>();
                store.put(storeName, map);
            }
            return map;
        }
    }
}
