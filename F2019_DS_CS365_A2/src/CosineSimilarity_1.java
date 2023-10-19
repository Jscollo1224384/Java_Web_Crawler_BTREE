import java.util.*;
class CosineSimilarity_1{


static double cosineSimilarity(ArrayList<Integer> vectorA, ArrayList<Integer> vectorB) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (int i = 0; i < vectorA.size(); i++) {
        //System.out.println("***************************************");
        dotProduct += vectorA.get(i) * vectorB.get(i);
        normA += Math.pow(vectorA.get(i), 2);
        normB += Math.pow(vectorB.get(i), 2);
   }   
    
    //System.out.print(dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
}
}
 