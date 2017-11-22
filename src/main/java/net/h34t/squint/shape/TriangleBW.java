package net.h34t.squint.shape;

import java.awt.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class TriangleBW implements Shape {

    private float[] dna;

    TriangleBW(Random r) {
        dna = new float[]{
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
        };
    }

    private TriangleBW(float[] dna) {
        this.dna = dna;
    }

    @Override
    public void draw(Graphics2D canvas, int w, int h) {
        Color c = new Color(
                Math.min(1f, Math.max(0f, dna[0])),
                Math.min(1f, Math.max(0f, dna[0])),
                Math.min(1f, Math.max(0f, dna[0])),
                Math.min(1f, Math.max(0f, dna[1])));

        canvas.setColor(c);
        // canvas.setBackground(c);

        canvas.fillPolygon(
                new int[]{
                        (int) (dna[2] * w * 2) - w / 2,
                        (int) (dna[3] * w * 2) - w / 2,
                        (int) (dna[4] * w * 2) - w / 2,
                },
                new int[]{
                        (int) (dna[5] * h * 2) - h / 2,
                        (int) (dna[6] * h * 2) - h / 2,
                        (int) (dna[7] * h * 2) - h / 2,
                },
                3);
    }

    @Override
    public Shape mutate(Random r, int w, int h) {
        float[] mutDna = Arrays.copyOf(dna, dna.length);

//        int count = r.nextInt(mutDna.length - 1) + 1;
//
//        for (int i = 0; i < count; i++)
        mutDna[r.nextInt(mutDna.length)] += r.nextFloat() - .5;

        return new TriangleBW(mutDna);
    }

    @Override
    public String exportSVG(int w, int h) {
        StringBuilder pairs = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            pairs.append(dna[i + 2] * w).append(",").append(dna[i + 5] * h).append(" ");
        }

        String col = String.format(Locale.ENGLISH, "rgb(%d,%d,%d)",
                (int) Math.min(255, dna[0] * 255),
                (int) Math.min(255, dna[0] * 255),
                (int) Math.min(255, dna[0] * 255));

        return "<polygon points=\"" + pairs.toString() + "\"  fill=\"" + col + "\" fill-opacity=\"" + dna[1] + "\" />\n";
    }

    public static class Generator implements Shape.Generator {

        @Override
        public Shape generate(Random r, int w, int h) {
            return new TriangleBW(r);
        }
    }
}
