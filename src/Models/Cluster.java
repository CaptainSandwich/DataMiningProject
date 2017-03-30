package Models;

import java.util.ArrayList;

public class Cluster {
    private ArrayList<Double> centroid;

    public Cluster(ArrayList<Double> centroid) {
        this.centroid = centroid;
    }

    public ArrayList<Double> getCentroid() {
        return centroid;
    }

    public void setColumn(int col, double d) {
        centroid.add(col, d);
    }
}
