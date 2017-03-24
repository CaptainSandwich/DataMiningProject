package Models;

import java.util.ArrayList;

public class Cluster {
    private int index;
    private KPoint centroid;
    private ArrayList<KPoint> points;

    public Cluster(int index, KPoint centroid) {
        this.index = index;
        this.centroid = centroid;
        points = new ArrayList<KPoint>();
    }
}
