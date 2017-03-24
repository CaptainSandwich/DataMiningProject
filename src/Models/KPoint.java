package Models;

import java.util.ArrayList;

public class KPoint {
    int centerIndex;
    private ArrayList<Double> dimensions;

    public KPoint(ArrayList<Double> dimensions) {
        this.dimensions = dimensions;
    }

    public double getDimension(int i) {
        return dimensions.get(i);
    }

    public int size() {
        return dimensions.size();
    }
}
