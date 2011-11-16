package net.codjo.dataprocess.server.treatmenthelper;
import static net.codjo.test.common.matcher.JUnitMatchers.assertThat;
import static net.codjo.test.common.matcher.JUnitMatchers.equalTo;
import org.junit.Test;
/**
 *
 */
public class RepositoryDescriptorTest {
    @Test
    public void content() {
        RepositoryDescriptor descriptor = new RepositoryDescriptor(10, "REPO1",
                                                                   new String[]{"file1", "file2"});
        assertThat(descriptor.getRepositoryId(), equalTo(10));
        assertThat(descriptor.getRepositoryName(), equalTo("REPO1"));
        assertThat(descriptor.getRepositoryPath(), equalTo(new String[]{"file1", "file2"}));
    }
}
