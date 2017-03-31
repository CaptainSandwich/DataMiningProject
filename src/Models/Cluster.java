package Models;

import java.util.ArrayList;

public class Cluster {
    private ArrayList<Double> centroid;
    private ArrayList<KPoint> points;

    public Cluster(ArrayList<Double> centroid) {
        this.centroid = new ArrayList<Double>(centroid);
        this.points = new ArrayList<KPoint>();
    }

    public ArrayList<Double> getCentroid() {
        return centroid;
    }

    public void setCentroid(ArrayList<Double> centroid) {
        this.centroid = centroid;
    }

    public ArrayList<KPoint> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        String s = centroid.toString();
        for(KPoint point : points) {
            s += point.getDataList();
        }
        s = s.replace("[", "\"").replace("]", "\",").replace(", ", ",");
        s = s.substring(0, s.length() - 1);
        return s + "\n";
    }

    public void setColumn(int col, double d) {
        centroid.set(col, d);
    }

    public void addKPoint(KPoint point) {
        points.add(point);
    }
}
