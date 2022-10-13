package codes.ztereohype.example.core;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class Gradient {
    private final TreeMap<Double, Color> gradient;

    public Gradient() {
        this.gradient = new TreeMap<>();
    }

    public void add(double index, int red, int green, int blue) {
        if (index < 0 || index > 1) {
            throw new IllegalArgumentException("Index must be between 0 and 1");
        }

        Color color = new Color(red,green,blue);
        gradient.put(index, color);
    }

    public void remove(double index) {
        gradient.remove(index);
    }

    public void clear() {
        gradient.clear();
    }

    public Color getAt(double value) {
        if (value < 0D || value > 1D) {
            throw new IllegalArgumentException("Value must be between 0 and 1");
        }

        Map.Entry<Double, Color> floorEntry, ceilingEntry;

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

        Color firstColor = floorEntry.getValue();
        Color secondColor = ceilingEntry.getValue();

        long red   = Math.round(secondColor.getRed()   * ratio + firstColor.getRed()   * invRatio);
        long green = Math.round(secondColor.getGreen() * ratio + firstColor.getGreen() * invRatio);
        long blue  = Math.round(secondColor.getBlue()  * ratio + firstColor.getBlue()  * invRatio);

        return new Color((int)red, (int)green, (int)blue);
    }
}