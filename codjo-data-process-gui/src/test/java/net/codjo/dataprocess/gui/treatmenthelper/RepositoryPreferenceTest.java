package net.codjo.dataprocess.gui.treatmenthelper;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class RepositoryPreferenceTest {
    private RepositoryPreference preference;


    @Before
    public void before() {
        preference = new RepositoryPreference();
    }


    @Test
    public void repositoryPath() {
        preference.addRepository("HB2");
        preference.addRepositoryPath("HB2", "c:\\dev\\dev\\repoHB2_1.xml");
        preference.addRepositoryPath("HB2", "c:\\dev\\dev\\repoHB2_2.xml");

        preference.addRepository("HB3");
        preference.addRepositoryPath("HB3", "c:\\dev\\dev\\repoHB3_1.xml");
        preference.addRepositoryPath("HB3", "c:\\dev\\dev\\repoHB3_2.xml");

        assertThat(preference.getRepositoryNames().contains("HB2"), equalTo(true));
        assertThat(preference.getRepositoryNames().contains("HB3"), equalTo(true));
        assertThat(preference.getRepositoryPath("HB2").contains("c:\\dev\\dev\\repoHB2_1.xml"),
                   equalTo(true));
        assertThat(preference.getRepositoryPath("HB2").contains("c:\\dev\\dev\\repoHB2_2.xml"),
                   equalTo(true));

        preference.removeRepositoryPath("HB2", "c:\\dev\\dev\\repoHB2_1.xml");
        assertThat(preference.getRepositoryPath("HB2").contains("c:\\dev\\dev\\repoHB2_2.xml"),
                   equalTo(true));
        assertThat(preference.getRepositoryPath("HB2").contains("c:\\dev\\dev\\repoHB2_1.xml"),
                   equalTo(false));

        assertThat(2, equalTo(preference.getRepositoryNames().size()));
        preference.removeRepository("HB3");
        assertThat(1, equalTo(preference.getRepositoryNames().size()));
        assertThat(preference.getRepositoryNames().contains("HB2"), equalTo(true));
    }
}
