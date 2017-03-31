package Controllers;

import Models.Cluster;
import Models.KPoint;
import java.lang.Math;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Double.POSITIVE_INFINITY;

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
            for(i = 0; i < points.size(); i++) {
                nearest_dist = POSITIVE_INFINITY;
                KPoint point = points.get(i);
                for(j = 0; j < k; j++) {
                    Cluster center = clusters.get(j);
                    dist = distance(point.getDataList(), center.getCentroid());

                    if(dist < nearest_dist) {
                        nearest_dist = dist;
                        point.setCluster(center);
                    }
                }
            }


            for(j = 0; j < k; j++) {
                points_count = 0;
                ArrayList<Double> sum = new ArrayList<Double>();
                Cluster cluster = clusters.get(j);
                for(int count = 0; count < points.get(0).size(); count++) {
                    sum.add(0.0);
                }

                for(i = 0; i < points.size(); i++) {
                    KPoint point = points.get(i);
                    if(point.getCluster() == clusters.get(j)) {
                        for(int col =  0; col < point.size(); col++) {
                            sum.set(col, sum.get(col) + point.getColumn(col));
                        }
                        points_count++;

                        if(l == iters - 1) {
                            cluster.addKPoint(point);
                        }
                    }
                }

                switch(points_count) {
                    case 0 :
                        clusters.get(j).setCentroid(new ArrayList<Double>(randomCenter(points)));
                        break;

                    default:
                        for(i = 0; i < sum.size(); i++) {
                            cluster.setColumn(i, sum.get(i)/points_count);
                        }
                        break;
                }
            }

        }
        return clusters;
    }
}
