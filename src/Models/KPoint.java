package Models;

import java.util.ArrayList;

public class KPoint {
    private Cluster cluster;
    private ArrayList<Double> dataList;

    public KPoint(ArrayList<Double> dataList) {
        this.dataList = dataList;
    }

    public void setCluster(Cluster c) {
        this.cluster = c;
    }

    public double getColumn(int i) {
        return dataList.get(i);
    }

    public Cluster getCluster() {
        return cluster;
    }

    public int size() {
        return dataList.size();
    }

    public ArrayList<Double> getDataList() {
        return dataList;
    }
}
