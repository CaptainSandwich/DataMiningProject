package Controllers;

import Models.BayesianTuple;
import Models.Cluster;
import Models.KPoint;
import java.lang.Math;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.sum;

public class Algorithms {

    private static ArrayList<Cluster> initRandomCenters(ArrayList<KPoint> points, int k) {
        ArrayList<Cluster> clusters = new ArrayList<Cluster>();
        for(int i = 0; i < k; i++) {
            clusters.add(new Cluster(randomCenter(points)));
        }

        return clusters;
    }

    private static double distance(ArrayList<Double> list1, ArrayList<Double> list2) {
        double sum = 0;
        for(int i = 0; i < list1.size(); i++) {
            sum += Math.pow(list1.get(i) - list2.get(i), 2);
        }

        return Math.sqrt(sum);
    }

    private static ArrayList<Double> randomCenter(ArrayList<KPoint> points) {
        Random rn = new Random();
        int randomNum =  rn.nextInt(points.size());
        return points.get(randomNum).getDataList();
    }

    public static ArrayList<Cluster> KMeans(ArrayList<KPoint> points,
                                            int k,
                                            int iters)
    {
        double nearest_dist;
        double dist;
        int j;
        int l;
        int i;
        int points_count;
        ArrayList<Cluster> clusters;

        clusters = initRandomCenters(points, k);


        for(l = 0; l < iters; l++) {
            for(KPoint point : points) {
                nearest_dist = POSITIVE_INFINITY;
                for(j = 0; j < k; j++) {
                    Cluster center = clusters.get(j);
                    dist = distance(point.getDataList(), center.getCentroid());

                    if(dist < nearest_dist) {
                        nearest_dist = dist;
                        point.setCluster(center);
                    }
                }
            }


            for(Cluster cluster : clusters) {
                points_count = 0;
                ArrayList<Double> sum = new ArrayList<Double>();
                for(int count = 0; count < points.get(0).size(); count++) {
                    sum.add(0.0);
                }

                for(KPoint point : points) {
                    if(point.getCluster() == cluster) {
                        for(int col =  0; col < point.size(); col++) {
                            sum.set(col, sum.get(col) + point.getColumn(col));
                        }
                        points_count++;

                    }
                }

                switch(points_count) {
                    case 0 :
                        cluster.setCentroid(new ArrayList<Double>(randomCenter(points)));
                        break;

                    default:
                        for(i = 0; i < sum.size(); i++) {
                            cluster.setColumn(i, sum.get(i)/points_count);
                        }
                        break;
                }
            }
            if(l == iters - 1) {
                for(Cluster cluster : clusters) {
                    for(KPoint point : points) {
                        if(point.getCluster() == cluster) {
                            cluster.addKPoint(point);
                        }
                    }
                }
            }
        }
        return clusters;
    }


    // todo bayesian tuple will have classCount, columnCount, data, and other params?
    public static double[] NaiveBayesClassifyNewTuple(int[][] data, int[] newTuple, int columnCount,
                                                                String[][] classes, int columnSolveIndex) /*, BayesianTuple tupleToAdd) */
    {


        // Create true class #s, since ours fills it with nulls - todo change this in BayesController.java
        // todo so we don't have to do it here
        int classCount[] = new int[columnCount];
        for(int i = 0; i < classes.length; i++) {

            int countForCurrentClass = 0;
            for(int j = 0; j < classes[i].length; j++) {
                if(classes[i][j] == null || classes[i][j].isEmpty()){
                    break;
                }
                countForCurrentClass++;
            }
            classCount[i] = countForCurrentClass;
        }

     //   System.out.println("True class #s");
      //  for(int a : classCount){
      //      System.out.println(a + " ");
       // }

        // Determine P(C)
        double[] pc = new double[classCount[columnSolveIndex]];

        for(int i = 0; i < pc.length; i++){
            // Count of the current columnn's current class
            int count = 0;

            for(int j = 0; j < data.length; j++){
                if(data[j][columnSolveIndex] == i) {
                    count++;
                }

                if(j == data.length - 1){
                    pc[i] = (double) count / data.length;
                    //System.out.println(count + " " + data.length);
                    //System.out.println("prob index " + columnSolveIndex + " is of class " + i + ": " + pc[i]);
                }
            }
        }

        // Determine (P Ak = x | C)
        ArrayList<Double[]> pa = new ArrayList<Double[]>();

        for(int i = 0; i < columnCount; i++){
            //if(i == columnSolveIndex)
            //    pa.add(new Double[0]);
            pa.add(new Double[classCount[i]]);
        }


        //int currentPAIndex = 0;
        //
        double[][] paxC = new double[classCount[columnSolveIndex]][columnCount];
        for(int currentPAIndex = 0; currentPAIndex < pa.size(); currentPAIndex++){
            // skip what we've already done
            if(currentPAIndex == columnSolveIndex)
                continue;


            // i is the # of classes contained in the remaining columns
            for(int i = 0; i < pa.get(currentPAIndex).length; i++){

                // discount values of i that doesn't match our new tuple
                // (we only care about relevant information to our new tuple)
                if(i != newTuple[currentPAIndex])
                    continue;

                // k represents all possible classes we can get
                for(int k = 0; k < classCount[columnSolveIndex]; k++) {

                    int count = 0;
                    int total = 0;
                    // j iterates over all rows in the set
                    for (int j = 0; j < data.length; j++) {
                        if (data[j][currentPAIndex] == i && data[j][columnSolveIndex] == k) {
                            count++;
                        }

                        if(data[j][columnSolveIndex] == k)
                            total++;

                        //System.out.println("count: " + count);
                        //System.out.println("total: " + total);

                        if (j == data.length - 1) {
                            //  pa.get(currentPAIndex)[i] = (double) count / data.length;
                            paxC[k][currentPAIndex] = (double) count / total;
                            System.out.println(count + " " + data.length);
                        //    System.out.println("prob index " + currentPAIndex +
                         //           " is of class " + i + ": " + paxC[k][currentPAIndex] + " given k " + k);
                        }
                    }
                }
            }
        }


        // classify new tuple
        double[] predictions = new double[classCount[columnSolveIndex]];

        for(int i = 0; i < predictions.length; i++){
            for(int j = 0; j < columnCount; j++){
            //    System.out.println(paxC[i][j] + " ");
            }
            //System.out.println();
        }

        for(int i = 0; i < predictions.length; i++){
            double something = 1;
            for(int j = 0; j < columnCount; j++){
                if(j == columnSolveIndex) {
                    continue;
                }

            //    System.out.println("j = " + j);
                //predictions[i] = pc[i] * something
                something *= paxC[i][j];
            //    System.out.print(paxC[i][j] + " ");
            }
            predictions[i] = pc[i] * something;
        }

        /*
        int prediction = 0;
        double max = Double.MIN_VALUE;
        for(int i = 0; i < predictions.length; i++){
            if(predictions[i] > max){
                max = predictions[i];
                prediction = i;
            }

           // System.out.println("prob[" + i +"]: " + predictions[i]);
        }

        System.out.println("New tuple predicted to be of class " + prediction); */
        //double[][] pa = new double[cla]

        return predictions;
        // create classDetermined var in tupleToAdd, set it to whatever our classifier guesses then return this under output as a label
        //return /* new tuple with determined class */ null;
    }



    // simple transposeMatrix function found on stack overflow
    public static double[][] transposeMatrix(double [][] m){
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    }

    public static double[] vectorMultiply(double[][] m, double[] v){
       int mRows = m.length;
       int mCols = m[0].length;
       // vRows should be equal to mCols
       int vRows = v.length;

       double[] c = new double[vRows];

       double rowSum;
       for(int i = 0; i < mRows; i++){
           rowSum = 0.0;
           for(int j = 0; j < mCols; j++){
               rowSum += v[j] * m[i][j];
           }
               c[i] = rowSum;
       }


       return c;
    }

    // d is our dampening factor
    public static double[] pageRank(int[][] data, double d, int iterations){

        // although rows should equal cols
        int rows = data.length;
        int cols = data[0].length;


        double[] pagerank = new double[rows];
        double[][]probabilityMatrix = new double[rows][cols];
        double[][] dataMatrix = new double[rows][cols];
        double[][] dataMatrixTransposed = new double[rows][cols];


        int sumOfLinksInRow = 0;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                sumOfLinksInRow = 0;

                // iterate once through each row, count sum of total, set each 1 to 1 / rowSum, seen in slides here
                // https://www.slideshare.net/maimustafa566/page-rank-algorithm-33212250
                for(int k = 0; k < cols; k++){

                    if(data[i][k] == 1){
                        sumOfLinksInRow++;
                    }
                }

                if(data[i][j] == 1) {
                    dataMatrix[i][j] = 1.0 / (double)sumOfLinksInRow;
                } else {
                    dataMatrix[i][j] = 0;
                }
            }
        }

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
            }
        }

        // transpose data matrix

        dataMatrixTransposed = transposeMatrix(dataMatrix);



        // initialize pagerank of each page to 1/rows (number of pages) * (1-d)
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++){
                probabilityMatrix[i][j] = (1.0 / (double) rows) * (1-d);
            }
        }

        // multiply matrixT by d
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                dataMatrixTransposed[i][j] = dataMatrixTransposed[i][j] * d;
            }
        }

        // add them (pagerank and matrixT) together
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
               probabilityMatrix[i][j] = probabilityMatrix[i][j] + dataMatrixTransposed[i][j];
            }
        }


        // initial pagerank matrix
        for(int i = 0; i < rows; i++){
            pagerank[i] = 1;
        }

        // loop: multiply pagerank matrix and probabilityMatrix, store result in pageRank until done with steps
        // Generally we'd do this until it converges, but we let the user decide that
        int iterator = 0;

        // in case user enters one
        do {

            pagerank = vectorMultiply(probabilityMatrix, pagerank);
            iterator++;
        } while(iterator < iterations);

        return pagerank;
    }

}
