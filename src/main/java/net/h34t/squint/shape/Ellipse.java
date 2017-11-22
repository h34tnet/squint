package net.h34t.squint.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class Ellipse implements Shape {

    private final float[] dna;

    Ellipse(Random r) {
        this.dna = new float[]{
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat(),
                r.nextFloat()
        };
    }

    private Ellipse(float[] dna) {
        this.dna = dna;
    }

    @Override
    public void draw(Graphics2D canvas, int w, int h) {
        int rw = (int) (w * dna[6]);
        int rh = (int) (h * dna[7]);

        Color c = new Color(
                Math.min(1f, Math.max(0f, dna[0])),
                Math.min(1f, Math.max(0f, dna[1])),
                Math.min(1f, Math.max(0f, dna[2])),
                Math.min(1f, Math.max(0f, dna[3])));

        canvas.setColor(c);
        canvas.fillOval((int) (dna[4] * w - rw / 2), (int) (dna[5] * h - rh / 2), rw, rh);
    }

    @Override
    public Shape mutate(Random r, int w, int h) {
        float[] dna = Arrays.copyOf(this.dna, this.dna.length);
//        dna[r.nextInt(dna.length)] = r.nextFloat();

        int count = r.nextInt(dna.length - 1) + 1;

        float d = Math.max(1f / w, 1f / h) * 2f;

        for (int i = 0; i < count; i++)
            dna[r.nextInt(dna.length)] += r.nextFloat() * d - d / 2f;

        return new Ellipse(dna);
    }

    @Override
    public String exportSVG(int w, int h) {
        String col = String.format(Locale.ENGLISH, "rgb(%d,%d,%d)",
                (int) Math.min(255, dna[0] * 255),
                (int) Math.min(255, dna[1] * 255),
                (int) Math.min(255, dna[2] * 255));

        return "<ellipse cx=\"" + (dna[4] * w) + "\" cy=\"" + (dna[5] * h) + "\" rx=\"" + (dna[6] / 2 * w) + "\" ry=\"" + (dna[7] / 2 * h)
                + "\" fill=\"" + col + "\" fill-opacity=\"" + dna[3] + "\" />\n";

    }

    public static class Generator implements Shape.Generator {

        @Override
        public Shape generate(Random r, int w, int h) {
            return new Ellipse(r);
        }
    }
}
