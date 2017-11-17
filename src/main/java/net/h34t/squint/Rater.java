package net.h34t.squint;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class Rater {

    private final Raster rB;

    public Rater(BufferedImage source) {
        rB = source.getRaster();
    }

    public long getScore(BufferedImage a) {
        Raster rA = a.getData();

        int[] colA = new int[4];
        int[] colB = new int[4];

        long diff = 0;

        for (int y = 0, yy = a.getHeight(); y < yy; y++) {
            for (int x = 0, xx = a.getWidth(); x < xx; x++) {
                rA.getPixel(x, y, colA);
                rB.getPixel(x, y, colB);

                int c0 = colA[0] - colB[0];
                int c1 = colA[1] - colB[1];
                int c2 = colA[2] - colB[2];

                int cc0 = Math.abs(c0 * c0 * c0);
                int cc1 = Math.abs(c1 * c1 * c2);
                int cc2 = Math.abs(c2 * c2 * c2);

                diff += cc0 + cc1 + cc2;
            }
        }

        return diff;
    }
}
