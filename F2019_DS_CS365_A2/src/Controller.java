import java.io.IOException;
import java.util.*;

public class Controller{
    private LoadPages l;
    private String[] errMess;
    private boolean error;

    public Controller(){
        errMess = new String[20];
        error = false;
        Arrays.fill(errMess, "Invalid URL!");
    }
    public String[] run(String url) throws IOException,ClassNotFoundException {
        l = new LoadPages();
        l.pageLoader();

        try {
            l.loadUserSite(url);
        } catch (Exception e) {
            System.out.println("Invalid Entry! Enter a valid url");
            error = true;
        }
        if (error)
            return errMess;
        else {
            return l.getAllCosScores();
        }
    }
    String mostSimilar(){
        if(error)
            return "You have entered an invalid url. Please click the clear button and re-enter a valid url.";
        else{
            String mostSim = l.getCosineSimScore();
            error = false;
            return mostSim;
        }
    }
    public String[] runClust(){
        String[] clust = l.getClust();
        return clust;
    }
    public String[] getSiteCluster(){
        String[] clust = l.getClusteredSites();
        System.out.println("SIZE: " + clust.length);
        return clust;
    }

}
