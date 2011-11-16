package net.codjo.dataprocess.gui.selector;
import java.util.Comparator;
import java.util.Map;
/**
 *
 */
public class FamilyComparator implements Comparator<String> {
    private Map<String, String> familiesMap;


    public FamilyComparator(Map<String, String> familiesMap) {
        this.familiesMap = familiesMap;
    }


    public int compare(String familyId1, String familyId2) {
        String familyName1 = familiesMap.get(familyId1);
        String familyName2 = familiesMap.get(familyId2);

        if (familyName1.equals(familyName2)) {
            return 0;
        }
        else if (familyName1.compareTo(familyName2) < 0) {
            return -1;
        }
        else {
            return 1;
        }
    }
}
