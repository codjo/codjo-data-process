/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.userparam;
import net.codjo.dataprocess.common.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/**
 *
 */
public class User {
    private String userName;
    private String currentRepository;
    private List<Repository> repositoryList = new ArrayList<Repository>();


    public User() {
    }


    public User(String userName) {
        this.userName = userName;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String name) {
        userName = name;
    }


    public List<Repository> getRepositoryList() {
        return repositoryList;
    }


    public List<Repository> getValidRepositoryList() {
        List<Repository> list = new ArrayList<Repository>();
        for (Repository repository : repositoryList) {
            if (repository.isValid()) {
                list.add(repository);
            }
        }
        return list;
    }


    public List<Repository> getNotValidRepositoryList() {
        List<Repository> list = new ArrayList<Repository>();
        for (Repository repository : repositoryList) {
            if (!repository.isValid()) {
                list.add(repository);
            }
        }
        return list;
    }


    public void addRepository(Repository repository) {
        if (getRepository(repository.getName()) == null) {
            repositoryList.add(repository);
        }
    }


    public Repository getRepository(String repositoryName) {
        for (Repository repository : repositoryList) {
            if (repository.name.equals(repositoryName)) {
                return repository;
            }
        }
        return null;
    }


    public void removeRepository(Repository repository) {
        repositoryList.remove(repository);
    }


    public void removeRepository(String repositoryName) {
        for (Repository repository : repositoryList) {
            if (repository.name.equals(repositoryName)) {
                removeRepository(repository);
                break;
            }
        }
    }


    public boolean setDefaultRepository() {
        String currentRepositoryName = getCurrentRepository();
        if (currentRepositoryName == null || getRepository(currentRepositoryName) == null ||
            !getRepository(currentRepositoryName).isValid()) {
            for (Repository repository : repositoryList) {
                if (repository.isValid()) {
                    setCurrentRepository(repository.getName());
                    if (Log.isInfoEnabled()) {
                        Log.info(getClass(), getUserName()
                                             + " n'a pas de référentiel de traitement courant ou bien il n'est plus valide."
                                             + " Il a donc été fixé par défaut à " + repository.getName());
                    }
                    return true;
                }
            }
            setCurrentRepository(null);
            return true;
        }
        return false;
    }


    public void removeAllRepository() {
        repositoryList.clear();
    }


    public String getCurrentRepository() {
        return currentRepository;
    }


    public void setCurrentRepository(String currentRepository) {
        this.currentRepository = currentRepository;
    }


    @Override
    public String toString() {
        return "userName = " + userName + ", currentRepository = " + currentRepository + ", repositoryList = "
               + repositoryList.toString();
    }


    public static class Repository {
        private String name;
        private String expirydate;
        private String expiryday;


        public Repository(String name) {
            this.name = name;
            expirydate = "";
            expiryday = "";
        }


        public Repository(String name, String expirydate) {
            this.name = name;
            this.expirydate = expirydate;
            this.expiryday = "";
        }


        public Repository(String name, String expirydate, String expiryday) {
            this.name = name;
            this.expirydate = expirydate;
            this.expiryday = expiryday;
        }


        public void setName(String name) {
            this.name = name;
        }


        public String getName() {
            return name;
        }


        public void setExpirydate(String expirydate) {
            this.expirydate = expirydate;
        }


        public void setExpiryday(String expiryday) {
            this.expiryday = expiryday;
        }


        public String getExpirydate() {
            return expirydate;
        }


        public String getExpiryday() {
            return expiryday;
        }


        public void updateExpirydate() {
            if (expiryday == null || expiryday.trim().length() == 0) {
                return;
            }
            int nbDay = Integer.parseInt(expiryday);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, nbDay);
            setExpirydate(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
        }


        public boolean isValid() {
            Date now = Calendar.getInstance().getTime();

            if (getExpirydate() == null || getExpirydate().trim().length() == 0) {
                return true;
            }
            Date expiryDate;
            try {
                expiryDate = new SimpleDateFormat("dd-MM-yyyy").parse(getExpirydate().trim());
            }
            catch (ParseException ex) {
                try {
                    expiryDate = new SimpleDateFormat("dd/MM/yyyy").parse(getExpirydate().trim());
                }
                catch (ParseException e1) {
                    Log.error(getClass(),
                              String.format("Le format de la date d'expiration '%s' est erroné.\n"
                                            + "Le format doit être dd-MM-yyyy ou dd/MM/yyyy", expirydate),
                              e1);
                    return false;
                }
            }
            return now.compareTo(expiryDate) < 0;
        }


        @Override
        public String toString() {
            return "[name = " + name + ", expirydate = " + expirydate + ", expiryday = " + expiryday + "]";
        }
    }
}
