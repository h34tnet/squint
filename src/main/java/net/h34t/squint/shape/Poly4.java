package net.h34t.squint.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class Poly4 implements Shape {

    private float[] dna;

    Poly4(Random r) {
        this.dna = new float[]{
                r.nextFloat(),
                r.nextFloat(),
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

    private Poly4(float[] dna) {
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
                        (int) (dna[7] * w * 2) - w / 2,
                },
                new int[]{
                        (int) (dna[8] * h * 2) - h / 2,
                        (int) (dna[9] * h * 2) - h / 2,
                        (int) (dna[10] * h * 2) - h / 2,
                        (int) (dna[11] * h * 2) - h / 2,
                },
                4);
    }

    @Override
    public Shape mutate(Random r, int w, int h) {
        float[] mutDna = Arrays.copyOf(dna, dna.length);

        mutDna[r.nextInt(mutDna.length)] += (r.nextFloat() - .5f) / 10f;

        return new Poly4(mutDna);
    }

    @Override
    public String exportSVG(int w, int h) {
        String pairs = (dna[4] * w + "," + dna[8] * h + " ") +
                dna[5] * w + "," + dna[9] * h + " " +
                dna[6] * w + "," + dna[10] * h + " " +
                dna[7] * w + "," + dna[11] * h;

        String col = String.format(Locale.ENGLISH, "rgb(%d,%d,%d)",
                (int) Math.max(0, Math.min(255, dna[0] * 255)),
                (int) Math.max(0, Math.min(255, dna[1] * 255)),
                (int) Math.max(0, Math.min(255, dna[2] * 255)));

        return "<polygon points=\"" + pairs + "\"  fill=\"" + col + "\" fill-opacity=\"" + dna[3] + "\" />\n";
    }

    public static class Generator implements Shape.Generator {

        @Override
        public Shape generate(Random r, int w, int h) {
            return new Poly4(r);
        }
    }
}
