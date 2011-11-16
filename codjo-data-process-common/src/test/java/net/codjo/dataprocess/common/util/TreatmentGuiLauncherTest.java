/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.util;
import static net.codjo.test.common.matcher.JUnitMatchers.assertThat;
import static net.codjo.test.common.matcher.JUnitMatchers.equalTo;
import org.junit.Test;
/**
 *
 */
public class TreatmentGuiLauncherTest {
    @Test
    public void launchTreatmentGui() throws Exception {
        TreatmentGuiLauncher launcher = new TreatmentGuiLauncher();
        String result =
              (String)launcher.launchTreatmentGui("net.codjo.dataprocess.common.util.MockTreatmentGui",
                                                  "'1','un'");
        assertThat("1 un", equalTo(result));
    }
}
