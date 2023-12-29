package dev.ztereohype.nicerskies.core;

import java.util.Map;
import java.util.TreeMap;

public class Gradient {
    private final TreeMap<Double, int[]> gradient;

    public Gradient() {
        this.gradient = new TreeMap<>();
    }

    public void add(double index, int red, int green, int blue) {
        if (index < 0 || index > 1) {
            throw new IllegalArgumentException("Index must be between 0 and 1");
        }

        int[] color = {red, green, blue};
        gradient.put(index, color);
    }

    public void remove(double index) {
        gradient.remove(index);
    }

    public void clear() {
        gradient.clear();
    }

    public int[] getAt(double value) {
        if (value < 0D || value > 1D) {
            throw new IllegalArgumentException("Value must be between 0 and 1");
        }

        Map.Entry<Double, int[]> floorEntry, ceilingEntry;

        floorEntry = gradient.floorEntry(value);
        if (floorEntry == null) { // we're under the lowest, return the lowest
            return gradient.firstEntry().getValue();
        }

        ceilingEntry = gradient.ceilingEntry(value);
        if (ceilingEntry == null) { // we're over the highest, return the highest
            return gradient.lastEntry().getValue();
        }

        double ratio = (value - floorEntry.getKey()) / (ceilingEntry.getKey() - floorEntry.getKey());
        double invRatio = 1 - ratio;

        int[] firstColor = floorEntry.getValue();
        int[] secondColor = ceilingEntry.getValue();

        long red = Math.round(secondColor[0] * ratio + firstColor[0] * invRatio);
        long green = Math.round(secondColor[1] * ratio + firstColor[1] * invRatio);
        long blue = Math.round(secondColor[2] * ratio + firstColor[2] * invRatio);

        return new int[]{(int) red, (int) green, (int) blue};
    }
}