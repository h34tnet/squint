package net.h34t.squint.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class Triangle implements Shape {

    private float[] dna;

    Triangle(Random r) {
        dna = new float[]{
                r.nextFloat(),
                r.nextFloat(),
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

    private Triangle(float[] dna) {
        this.dna = dna;
    }

    @Override
    public void draw(Graphics2D canvas, int w, int h) {
        Color c = new Color(
                Math.min(1f, Math.max(0f, dna[0])),
                Math.min(1f, Math.max(0f, dna[1])),
                Math.min(1f, Math.max(0f, dna[2])),
                Math.min(1f, Math.max(0f, dna[3])));

        canvas.setColor(c);

        canvas.fillPolygon(
                new int[]{
                        (int) (dna[4] * w * 2) - w / 2,
                        (int) (dna[5] * w * 2) - w / 2,
                        (int) (dna[6] * w * 2) - w / 2,
                },
                new int[]{
                        (int) (dna[7] * h * 2) - h / 2,
                        (int) (dna[8] * h * 2) - h / 2,
                        (int) (dna[9] * h * 2) - h / 2,
                },
                3);
    }

    @Override
    public Shape mutate(Random r, int w, int h) {
        float[] mutDna = Arrays.copyOf(dna, dna.length);

        int count = r.nextInt(mutDna.length - 1) + 1;

        float d = Math.max(1f / w, 1f / h) * 2f;

        for (int i = 0; i < count; i++)
            mutDna[r.nextInt(mutDna.length)] += r.nextFloat() * d - d / 2f;

        // mutDna[r.nextInt(mutDna.length)] += r.nextFloat() - .5f;

        return new Triangle(mutDna);
    }

    @Override
    public String exportSVG(int w, int h) {
        StringBuilder pairs = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            pairs.append(dna[i + 4] * w).append(",").append(dna[i + 7] * h).append(" ");
        }

        String col = String.format(Locale.ENGLISH, "rgb(%d,%d,%d)",
                (int) Math.max(0, Math.min(255, dna[0] * 255)),
                (int) Math.max(0, Math.min(255, dna[1] * 255)),
                (int) Math.max(0, Math.min(255, dna[2] * 255)));

        return "<polygon points=\"" + pairs.toString() + "\"  fill=\"" + col + "\" fill-opacity=\"" + dna[3] + "\" />\n";
    }

    public static class Generator implements Shape.Generator {

        @Override
        public Shape generate(Random r, int w, int h) {
            return new Triangle(r);
        }
    }
}
