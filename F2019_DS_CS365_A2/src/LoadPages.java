import java.util.*;

import java.io.*;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfCentroidSimilarities;
import net.sf.javaml.core.*;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.featureselection.scoring.GainRatio;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import net.sf.javaml.clustering.KMedoids;

class LoadPages{
    private Document doc;
    private String[] siteNames, delim,clusteredSites;;
    private double[] scores;
    private double max;
    private String names;
    private String terms;
    private String header;
    private BufferedReader reader;
    private ArrayList<String> termBank,user,userCompareList;
    private ArrayList<Integer>vectorA,vectorB;
    private BTree2[] btreeAr;
    private BTree2 btree;
    private FrequencyTable hashIn;
    private int initVal,siteNumber;
    private CosineSimilarity_1 cos;
    private HashMap<String,Double> map;
    private  BloomFilter filter;
    private ClusterCreate cc;
    private ArrayList<String>[] uniqueTerms;
    private String[] clustRe;
    private boolean writeTrigger;


    CosineSimilarity cosineSim;

    LoadPages()throws IOException,ClassNotFoundException {
        String fileName = "pages.txt";
        termBank = new ArrayList<String>();
        vectorA = new ArrayList<Integer>();
        user = new ArrayList<>();
        userCompareList = new ArrayList<>();
        vectorB = new ArrayList<Integer>();
        reader = new BufferedReader(new FileReader(fileName));
        names = "";
        terms = "";
        cos = new CosineSimilarity_1();
        filter = new BloomFilter(1000);
        writeTrigger = false;
        hashIn = new FrequencyTable();
        initVal = 1;
        max = 0;
        cosineSim = new CosineSimilarity();
        map = new HashMap<String, Double>();

    }
    void pageLoader()throws IOException, ClassNotFoundException{

        while((names = reader.readLine()) != null) {
            siteNames = names.split(",");
        }
        reader.close();
        btreeAr = new BTree2[siteNames.length];
        scores = new double[siteNames.length];
        uniqueTerms = new ArrayList[siteNames.length];

        writeTrigger = false;


        for (int i = 0; i < siteNames.length ; i++) {
            String urlKey = siteNames[i].replaceAll("[^a-zA-Z ]", "");
            String cachePathLastModified = "C:/Users/JScol/IdeaProjects/F2019_DS_CS365_A2/cache/last_modified/" + urlKey + ".txt";
            String cachePathContent = "C:/Users/JScol/IdeaProjects/F2019_DS_CS365_A2/cache/content/" + urlKey + ".txt";
            File file = new File(cachePathLastModified);
            BufferedWriter writer;
            if (!file.exists()) {

                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                writer = new BufferedWriter(fw);
                Connection.Response resp = Jsoup.connect(siteNames[i]).method(Connection.Method.POST).followRedirects(false).execute();
                header = resp.header("last-modified");
                writer.write(header);
                writer.close();
            }

            if (file.exists()) {
                reader = new BufferedReader(new FileReader(cachePathLastModified));
                header = reader.readLine();
                reader.close();
            }

            file = new File(cachePathContent);
            Connection.Response resp = Jsoup.connect(siteNames[i]).method(Connection.Method.POST).followRedirects(false).execute();
            String header2 = resp.header("last-modified");
            if (!file.exists() || header.compareTo(header2) != 0) {

                if (header.compareTo(header2) != 0) {

                    file = new File(cachePathLastModified);
                    file.createNewFile();
                    FileWriter fw = new FileWriter(file);
                    writer = new BufferedWriter(fw);
                    writer.write(header2);
                    writer.close();
                    file = new File(cachePathContent);
                }
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                writer = new BufferedWriter(fw);
                doc = Jsoup.connect(siteNames[i]).get();
                System.out.println("connected to: " + doc.title());
                terms = doc.text().replaceAll("[^a-zA-Z ]", " ");
                terms = terms.replaceAll("\\bthe\\b", "");
                terms = terms.replaceAll("\\ba\\b", "");
                terms = terms.replaceAll("\\bto\\b", "");
                terms = terms.replaceAll("\\btoo\\b", "");
                terms = terms.replaceAll("\\band\\b", "");
                terms = terms.replaceAll("\\bof\\b", "");
                terms = terms.replaceAll("\\bis\\b", "");
                terms = terms.replaceAll("\\bhow\\b", "");
                terms = terms.replaceAll("\\bwho\\b", "");
                terms = terms.replaceAll("\\bwhat\\b", "");
                terms = terms.replaceAll("\\bwhere\\b", "");
                terms = terms.replaceAll("\\bwhen\\b", "");
                terms = terms.replaceAll("\\bwhy\\b", "");
                terms = terms.replaceAll("\\bbecause\\b", "");
                terms = terms.replaceAll("\\bor\\b", "");
                terms = terms.replaceAll("\\bdont\\b", "");
                terms = terms.replaceAll("\\bcant\\b", "");
                terms = terms.replaceAll("\\bwont\\b", "");
                terms = terms.replaceAll("\\bfor\\b", "");
                terms = terms.replaceAll("\\bthere\\b", "");
                terms = terms.replaceAll("\\byou\\b", "");
                terms = terms.replaceAll("\\btheir\\b", "");
                terms = terms.replaceAll("\\byour\\b", "");
                terms = terms.replaceAll("\\bThe\\b", "");
                terms = terms.replaceAll("\\bA\\b", "");
                terms = terms.replaceAll("\\bTo\\b", "");
                terms = terms.replaceAll("\\bToo\\b", "");
                terms = terms.replaceAll("\\bAnd\\b", "");
                terms = terms.replaceAll("\\bOf\\b", "");
                terms = terms.replaceAll("\\bIs\\b", "");
                terms = terms.replaceAll("\\bHow\\b", "");
                terms = terms.replaceAll("\\bWho\\b", "");
                terms = terms.replaceAll("\\bWhat\\b", "");
                terms = terms.replaceAll("\\bWhere\\b", "");
                terms = terms.replaceAll("\\bWhen\\b", "");
                terms = terms.replaceAll("\\bWhy\\b", "");
                terms = terms.replaceAll("\\bBecause\\b", "");
                terms = terms.replaceAll("\\bOr\\b", "");
                terms = terms.replaceAll("\\bDont\\b", "");
                terms = terms.replaceAll("\\bCant\\b", "");
                terms = terms.replaceAll("\\bWont\\b", "");
                terms = terms.replaceAll("\\bFor\\b", "");
                terms = terms.replaceAll("\\bThere\\b", "");
                terms = terms.replaceAll("\\bYou\\b", "");
                terms = terms.replaceAll("\\bTheir\\b", "");
                terms = terms.replaceAll("\\bYour\\b", "");
                terms = terms.replaceAll("\\bI\\b", "");
                terms = terms.replaceAll("\\n{2,}", "");
                terms = terms.replaceAll("\\r{2,}", "");
                terms = terms.replaceAll("\\s{2,}", " ");



                writer.write(terms);
                writer.close();
            } else {
                reader = new BufferedReader(new FileReader(cachePathContent));
                terms = reader.readLine();
                reader.close();
                terms = terms.replaceAll("\\bthe\\b", "");
                terms = terms.replaceAll("\\ba\\b", "");
                terms = terms.replaceAll("\\bto\\b", "");
                terms = terms.replaceAll("\\btoo\\b", "");
                terms = terms.replaceAll("\\band\\b", "");
                terms = terms.replaceAll("\\bof\\b", "");
                terms = terms.replaceAll("\\bis\\b", "");
                terms = terms.replaceAll("\\bhow\\b", "");
                terms = terms.replaceAll("\\bwho\\b", "");
                terms = terms.replaceAll("\\bwhat\\b", "");
                terms = terms.replaceAll("\\bwhere\\b", "");
                terms = terms.replaceAll("\\bwhen\\b", "");
                terms = terms.replaceAll("\\bwhy\\b", "");
                terms = terms.replaceAll("\\bbecause\\b", "");
                terms = terms.replaceAll("\\bor\\b", "");
                terms = terms.replaceAll("\\bcant\\b", "");
                terms = terms.replaceAll("\\bwont\\b", "");
                terms = terms.replaceAll("\\bfor\\b", "");
                terms = terms.replaceAll("\\bthere\\b", "");
                terms = terms.replaceAll("\\byou\\b", "");
                terms = terms.replaceAll("\\btheir\\b", "");
                terms = terms.replaceAll("\\byour\\b", "");
                terms = terms.replaceAll("\\bfrom\\b", "");
                terms = terms.replaceAll("\\bThe\\b", "");
                terms = terms.replaceAll("\\bA\\b", "");
                terms = terms.replaceAll("\\bTo\\b", "");
                terms = terms.replaceAll("\\bToo\\b", "");
                terms = terms.replaceAll("\\bAnd\\b", "");
                terms = terms.replaceAll("\\bOf\\b", "");
                terms = terms.replaceAll("\\bIs\\b", "");
                terms = terms.replaceAll("\\bHow\\b", "");
                terms = terms.replaceAll("\\bWho\\b", "");
                terms = terms.replaceAll("\\bWhat\\b", "");
                terms = terms.replaceAll("\\bWhere\\b", "");
                terms = terms.replaceAll("\\bWhen\\b", "");
                terms = terms.replaceAll("\\bWhy\\b", "");
                terms = terms.replaceAll("\\bBecause\\b", "");
                terms = terms.replaceAll("\\bOr\\b", "");
                terms = terms.replaceAll("\\bDont\\b", "");
                terms = terms.replaceAll("\\bCant\\b", "");
                terms = terms.replaceAll("\\bWont\\b", "");
                terms = terms.replaceAll("\\bFor\\b", "");
                terms = terms.replaceAll("\\bThere\\b", "");
                terms = terms.replaceAll("\\bYou\\b", "");
                terms = terms.replaceAll("\\bTheir\\b", "");
                terms = terms.replaceAll("\\bYour\\b", "");
                terms = terms.replaceAll("\\bFrom\\b", "");
                terms = terms.replaceAll("\\bI\\b", "");
                terms = terms.replaceAll("\\n{2,}", "");
                terms = terms.replaceAll("\\r{2,}", "");
                terms = terms.replaceAll("\\s{2,}", " ");

            }


            delim = terms.split(" ");

            btree = new BTree2(26, Integer.toString(i));

            if(btree.file.length() <= btree.NODESIZE)
                writeTrigger = true;
            for (String s : delim) {
                termBank.add(s);
                if(writeTrigger) {
                    System.out.println("Entered writeTriger condition");
                    btree.insert(filter.orHashBits(s));
                }
            }
            btreeAr[i] = btree;
            uniqueTerms[i] = termBank;
            termBank = new ArrayList<>();
        }
        cc = new ClusterCreate(siteNames,btreeAr,uniqueTerms/*termBank*/);
        cc.compare();
    }
    void loadUserSite(String site)throws Exception{
        termBank = new ArrayList<>();
        doc = Jsoup.connect(site).get();
        System.out.println("connected to: " + doc.title());
        terms = doc.text().replaceAll("[^a-zA-Z ]"," ");
        terms = terms.replaceAll("\\bthe\\b", "");
        terms = terms.replaceAll("\\ba\\b", "");
        terms = terms.replaceAll("\\bto\\b", "");
        terms = terms.replaceAll("\\btoo\\b", "");
        terms = terms.replaceAll("\\band\\b", "");
        terms = terms.replaceAll("\\bof\\b", "");
        terms = terms.replaceAll("\\bis\\b", "");
        terms = terms.replaceAll("\\bhow\\b", "");
        terms = terms.replaceAll("\\bwho\\b", "");
        terms = terms.replaceAll("\\bwhat\\b", "");
        terms = terms.replaceAll("\\bwhere\\b", "");
        terms = terms.replaceAll("\\bwhen\\b", "");
        terms = terms.replaceAll("\\bwhy\\b", "");
        terms = terms.replaceAll("\\bbecause\\b", "");
        terms = terms.replaceAll("\\bor\\b", "");
        terms = terms.replaceAll("\\bcant\\b", "");
        terms = terms.replaceAll("\\bwont\\b", "");
        terms = terms.replaceAll("\\bfor\\b", "");
        terms = terms.replaceAll("\\bthere\\b", "");
        terms = terms.replaceAll("\\byou\\b", "");
        terms = terms.replaceAll("\\btheir\\b", "");
        terms = terms.replaceAll("\\byour\\b", "");
        terms = terms.replaceAll("\\bfrom\\b", "");
        terms = terms.replaceAll("\\bThe\\b", "");
        terms = terms.replaceAll("\\bA\\b", "");
        terms = terms.replaceAll("\\bTo\\b", "");
        terms = terms.replaceAll("\\bToo\\b", "");
        terms = terms.replaceAll("\\bAnd\\b", "");
        terms = terms.replaceAll("\\bOf\\b", "");
        terms = terms.replaceAll("\\bIs\\b", "");
        terms = terms.replaceAll("\\bHow\\b", "");
        terms = terms.replaceAll("\\bWho\\b", "");
        terms = terms.replaceAll("\\bWhat\\b", "");
        terms = terms.replaceAll("\\bWhere\\b", "");
        terms = terms.replaceAll("\\bWhen\\b", "");
        terms = terms.replaceAll("\\bWhy\\b", "");
        terms = terms.replaceAll("\\bBecause\\b", "");
        terms = terms.replaceAll("\\bOr\\b", "");
        terms = terms.replaceAll("\\bDont\\b", "");
        terms = terms.replaceAll("\\bCant\\b", "");
        terms = terms.replaceAll("\\bWont\\b", "");
        terms = terms.replaceAll("\\bFor\\b", "");
        terms = terms.replaceAll("\\bThere\\b", "");
        terms = terms.replaceAll("\\bYou\\b", "");
        terms = terms.replaceAll("\\bTheir\\b", "");
        terms = terms.replaceAll("\\bYour\\b", "");
        terms = terms.replaceAll("\\bFrom\\b", "");
        terms = terms.replaceAll("\\bI\\b", "");
        terms = terms.replaceAll("\\n{2,}", "");
        terms = terms.replaceAll("\\r{2,}", "");
        terms = terms.replaceAll("\\s{2,}", " ");
        delim = terms.split(" ");

        for (String s : delim) {
            if (!user.contains(s)){
                user.add(s);
            }
            hashIn.put(s, initVal);
        }
        calcCosineSimScore();

    }
    private void calcCosineSimScore()throws IOException{
        for (int i = 0; i < btreeAr.length ; i++) {
            System.out.println("Reading from BTree: " + i);
            for (String s : uniqueTerms[i]) {
                if (!user.contains(s)) {
                    user.add(s);
                }
            }
            for (String value : user/*termBank*/) {
                vectorB.add(hashIn.get(value));
                vectorA.add(btreeAr[i].search(filter.orHashBits(value)));
            }
           // System.out.println("term List size: "+ user.size());
            scores[i] = CosineSimilarity_1.cosineSimilarity(vectorA, vectorB);
            vectorA = new ArrayList<>();
            user = new ArrayList<>();
        }
       for(int i = 0; i < scores.length ; i++) {
            if(scores[i] > max) {
                max = scores[i];
                siteNumber = i;
            }
       }
       cc.setScore(max);
       clustRe = cc.getClusters();

       clusteredSites = cc.reducedClusters(scores,max,siteNames);

    }
    String getCosineSimScore(){
        return  siteNames[siteNumber] + "\n it has cosine similarity score of: " + Double.toString(max);
    }
    String[] getAllCosScores(){
        String[] scoreList = new String[scores.length];
            for (int i = 0; i < scores.length; i++) {
                scoreList[i] = Double.toString(scores[i]);
            }
            return scoreList;
    }
    public String[] getClust(){
        return clustRe;
    }
    public String[] getClusteredSites(){
        return clusteredSites;

    }

}
