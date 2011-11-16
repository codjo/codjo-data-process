package net.codjo.dataprocess.common.userparam;
import net.codjo.dataprocess.common.userparam.User.Repository;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class UserTest {
    @Test
    public void user() {
        User user = new User("michel");
        assertThat(user.getUserName(), equalTo("michel"));
        user.setUserName("mimi");
        assertThat(user.getUserName(), equalTo("mimi"));

        user.setCurrentRepository("REPO");
        assertThat(user.getCurrentRepository(), equalTo("REPO"));

        user.addRepository(new Repository("REPO1", "04-06-2009"));
        Repository repository2 = new Repository("REPO2");
        user.addRepository(repository2);
        user.addRepository(new Repository("REPO3"));
        user.addRepository(new Repository("REPO1"));
        user.addRepository(new Repository("REPO2"));
        assertThat(user.getRepositoryList().toString(),
                   equalTo("[[name = REPO1, expirydate = 04-06-2009, expiryday = ], "
                           + "[name = REPO2, expirydate = , expiryday = ], "
                           + "[name = REPO3, expirydate = , expiryday = ]]"));

        user.removeRepository(repository2);
        assertThat(user.getRepositoryList().toString(),
                   equalTo(
                         "[[name = REPO1, expirydate = 04-06-2009, expiryday = ], "
                         + "[name = REPO3, expirydate = , expiryday = ]]"));

        assertThat(user.getRepository("REPO3").toString(),
                   equalTo("[name = REPO3, expirydate = , expiryday = ]"));
        assertThat(user.getRepository("XXX"), nullValue());
        user.removeRepository("REPO1");
        assertThat(user.getRepositoryList().toString(),
                   equalTo("[[name = REPO3, expirydate = , expiryday = ]]"));

        user.removeAllRepository();
        assertThat(user.getRepositoryList().size(), equalTo(0));
    }


    @Test
    public void setDefaultRepository() {
        User user = new User("michel");
        user.setCurrentRepository("HB2");
        boolean result = user.setDefaultRepository();
        assertThat(result, equalTo(true));
        assertThat(user.getCurrentRepository(), nullValue());

        user.setCurrentRepository(null);
        user.addRepository(new Repository("HB2", "01-01-2000"));
        user.addRepository(new Repository("HB3"));
        user.addRepository(new Repository("HB4"));
        result = user.setDefaultRepository();
        assertThat(result, equalTo(true));
        assertThat(user.getCurrentRepository(), equalTo("HB3"));

        user.setCurrentRepository("HB3");
        result = user.setDefaultRepository();
        assertThat(result, equalTo(false));
        assertThat(user.getCurrentRepository(), equalTo("HB3"));

        user.setCurrentRepository("HB2");
        result = user.setDefaultRepository();
        assertThat(result, equalTo(true));
        assertThat(user.getCurrentRepository(), equalTo("HB3"));
    }


    @Test
    public void repository() {
        Repository repository = new Repository("HB2", "01-01-9999");
        assertThat(repository.getName(), equalTo("HB2"));
        assertThat(repository.getExpirydate(), equalTo("01-01-9999"));

        repository.setExpirydate("01-01-2001");
        assertThat(repository.getExpirydate(), equalTo("01-01-2001"));

        repository.setName("HB3");
        assertThat(repository.getName(), equalTo("HB3"));

        repository = new Repository("HB2", "01-01-9999");
        assertThat(repository.isValid(), equalTo(true));

        repository = new Repository("HB2", "01/01/9999");
        assertThat(repository.isValid(), equalTo(true));

        repository = new Repository("HB2", "01/01//9999");
        assertThat(repository.isValid(), equalTo(false));

        repository = new Repository("HB2", "01-01-1000");
        assertThat(repository.isValid(), equalTo(false));

        repository = new Repository("HB2", "2001");
        assertThat(repository.isValid(), equalTo(false));
    }
}
