import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMedoids;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfCentroidSimilarities;
import net.sf.javaml.core.*;
import net.sf.javaml.distance.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ClusterCreate {

    BTree2[] btreeAr;
    String[] siteNames,r,delim,siteNames2;
    double[] scores,difference;
    CosineSimilarity_1 cos;
    ArrayList<Integer> a, b;
    ArrayList<String> termBank;
    ArrayList<String>[] uniqueTerms;
    double score;
    Dataset data;
    DistanceMeasure eu,cs,md,neu;
    Clusterer km;
    ClusterEvaluation ce;
    Dataset[] result;
    BufferedWriter writer;
    BufferedReader reader;
    String simScores;
    File file;
    BloomFilter filter;
    HashMap<Double,String> siteMap;

    public ClusterCreate(String[] sn, BTree2[] t, ArrayList<String>[]aral ) throws IOException {
        siteNames = sn;
        btreeAr = t;
        cos = new CosineSimilarity_1();
        a = new ArrayList<>();
        b = new ArrayList<>();
        termBank = new ArrayList<>(); //al;
        uniqueTerms = aral;
        scores = new double[(siteNames.length * (siteNames.length - 1) / 2)];
        data = new DefaultDataset();
        eu = new EuclideanDistance();
        cs = new CosineDistance();
        md = new ManhattanDistance();

        result = new Dataset[10];
        km = new KMedoids(result.length,100,eu);
        ce = new SumOfCentroidSimilarities();
        score = 0.0;
        siteMap = new HashMap<>();
        difference = new double[result.length];
        simScores = "";
        filter = new BloomFilter(1000);
    }

    public void compare() throws IOException {
        int count = 0;
        int count2 = 0;
        ArrayList<String>[] userCompareList = new ArrayList[scores.length];
        file = new File("Similarity_scores.txt");

        if(!file.exists()) {
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < btreeAr.length - 1; i++) {
                count = i + 1;

                for(String s:uniqueTerms[i]){
                    if(!termBank.contains(s)){
                        termBank.add(s);
                    }
                }
                for (String s:uniqueTerms[count]) {
                    if (!termBank.contains(s)){
                        termBank.add(s);
                    }
                }

                for (int j = 0; j < termBank.size(); j++) {
                    a.add(btreeAr[i].search(filter.orHashBits(termBank.get(j))));
                }

                while (count < btreeAr.length) {
                    for (int j = 0; j < termBank.size(); j++) {
                        b.add(btreeAr[count].search(filter.orHashBits(termBank.get(j))));
                    }

                   // System.out.println("comparing btrees " + i + " and " + count);
                    scores[count2] = cos.cosineSimilarity(a, b);
                    writer.write(Double.toString(scores[count2]) + ",");
                    siteMap.put(scores[count2], siteNames[count]);
                    count2++;
                    count++;
                    b = new ArrayList<>();
                }
                a = new ArrayList<>();
                termBank = new ArrayList<>();
               // System.out.println(count2 + " comparisons out of " + scores.length + " complete." );
            }
            writer.close();
        }
        else {
            int counter = 0;
            reader = new BufferedReader(new FileReader("Similarity_scores.txt"));
            String[] delim;

            while ((simScores = reader.readLine()) != null) {
                delim = simScores.split(",");
                for (int j = 0; j < delim.length; j++) {
                    scores[j] = Double.parseDouble(delim[j]);
                    siteMap.put(scores[j], siteNames[counter]);
                    counter++;
                    if(counter == btreeAr.length)
                        counter = 0;
                }
            }


        }
    }

    public void setScore(double sc){
        score = sc;
        System.out.println(score);
    }
    public String[] getClusters() {

        double[][] arrays = new double[difference.length][];
        for (int i = 0; i < scores.length; i++) {
            Instance a = new SparseInstance(1);
            a.put(0, scores[i]);
            data.add(a);
        }
        result = km.cluster(data);
        r = new String[result.length];
        double sc = ce.score(result);
        System.out.println(sc);
        for (int i = 0; i < r.length; i++) {
            r[i] = result[i].toString();
            r[i] = r[i].replace("[", "");
            r[i] = r[i].replace("]", "");
            r[i] = r[i].replace("}", "");
            r[i] = r[i].replace("{", "");
            r[i] = r[i].replace("=", "");
            r[i] = r[i].replace(";", "");
            r[i] = r[i].replace("null", "");
            r[i] = r[i].replace(" ", "");
            r[i] = r[i].replace("00.", "0.");
        }

        double[] temp;
        double [] highestValue = new double[r.length];
        for (int i = 0; i < r.length ; i++) {
            delim = r[i].split(",");
            temp = new double[delim.length];
            for (int j = 0; j < temp.length; j++) {
                temp[j] = Double.parseDouble(delim[j]);
            }

            arrays[i] = temp;
            double highest = 0;
            for (int j = 0; j < arrays[i].length ; j++) {
                if(arrays[i][j] > highest){
                    highest = arrays[i][j];
                }
            }
            if(highest > score)
                difference[i] = highest - score;
            else
                difference[i] = score - highest;
            highestValue[i] = highest;
            //System.out.println("difference of cluster " + i + ": "+ difference[i]);
        }
        for (int i = 0; i < highestValue.length ; i++) {
           //System.out.println("high values: " + highestValue[i] + " site value " + siteMap.get(highestValue[i]));
        }
        //Arrays.sort(difference);
        int select = 0;
        for(int i = 0; i < difference.length ; i++) {
            System.out.println(difference[i]);
            System.out.println(highestValue[i]);
            if(difference[i] == highestValue[i] - score) {
                select = i;
            }
        }

       // System.out.println("value of select: "+ highestValue[select]);
        ArrayList<Double> reducedCluster = new ArrayList<>();
        Arrays.sort(arrays[select]);

        for (double d:arrays[select]) {
           //System.out.println("cluster values: " + d);
            if(d >= .7)
                reducedCluster.add(d);
        }
        //siteNames2 = new String[arrays[select].length];
        siteNames2 = new String[reducedCluster.size()];

        for (int i = 0; i < siteNames2.length ; i++) {
            //siteNames2[i] = siteMap.get(arrays[select][i]);
            siteNames2[i] = siteMap.get(reducedCluster.get(i));
        }

        ArrayList<String> uniqueSites = new ArrayList<>();
        for (String site: siteNames2 ) {
            if(!uniqueSites.contains(site))
                uniqueSites.add(site);
        }

        String[] uSites = new String[uniqueSites.size()];
        for (int i = 0; i < uSites.length ; i++) {
            uSites[i] = uniqueSites.get(i);
        }
        return uSites;
    }
    public String[] reducedClusters(double[] cosScores, double maxScore,String[] sites){
        HashMap<Double,String> map = new HashMap<>();
        Dataset dat = new DefaultDataset();
        int select = 0;
        for (int i = 0; i < sites.length ; i++) {
            map.put(cosScores[i],sites[i]);
        }

        for (int i = 0; i < cosScores.length; i++) {
            Instance a = new SparseInstance(1);
            a.put(0, cosScores[i]);
            dat.add(a);
        }
        Clusterer kmd = new KMedoids(5,1000,eu);
        Dataset[] rst = kmd.cluster(dat);
        String[] res = new String[rst.length];

        for (int i = 0; i < res.length; i++) {
            res[i] = rst[i].toString();
            res[i] = res[i].replace("[", "");
            res[i] = res[i].replace("]", "");
            res[i] = res[i].replace("}", "");
            res[i] = res[i].replace("{", "");
            res[i] = res[i].replace("=", "");
            res[i] = res[i].replace(";", "");
            res[i] = res[i].replace("null", "");
            res[i] = res[i].replace(" ", "");
            res[i] = res[i].replace("00.", "0.");
        }
        double[] temp;
        double[][] arrays = new double[res.length][];
        double [] highestValue = new double[res.length];
        double[] difference = new double[res.length];
        int median = 0;
        String[]del;
        for (int i = 0; i < res.length ; i++) {
            del = res[i].split(",");
            temp = new double[del.length];
            for (int j = 0; j < temp.length; j++) {
                temp[j] = Double.parseDouble(del[j]);
            }
            Arrays.sort(temp);
            arrays[i] = temp;
            double highest = 0;
            median = (arrays[i].length + 1) / 2;
            for (int j = 0; j < arrays[i].length ; j++) {
                if(arrays[i][j] > highest){
                    highest = arrays[i][j];
                }
            }

            for (int j = 0; j < arrays[i].length ; j++) {
                if(arrays[i][j] == maxScore)
                    select = i;
            }
            if(highest < maxScore )
                difference[i] = highest - maxScore;
            else
                difference[i] = maxScore - highest;
            highestValue[i] = highest;
        }
        double[] finalResult = arrays[select];
        String[] finalSiteResult = new String[finalResult.length];
        for (int i = 0; i < finalSiteResult.length; i++) {
            finalSiteResult[i] = map.get(finalResult[i]);
        }
        return finalSiteResult;
    }

}
