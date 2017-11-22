package net.h34t.squint.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class IntPoly4 implements Shape {

    private int[] dna;

    IntPoly4(Random r, int w, int h) {
        this.dna = new int[]{
                r.nextInt(255),
                r.nextInt(255),
                r.nextInt(255),
                r.nextInt(255),

                r.nextInt(w),
                r.nextInt(w),
                r.nextInt(w),
                r.nextInt(w),

                r.nextInt(h),
                r.nextInt(h),
                r.nextInt(h),
                r.nextInt(h),
        };
    }

    private IntPoly4(int[] dna) {
        this.dna = dna;
    }

    @Override
    public void draw(Graphics2D canvas, int w, int h) {
        Color c = new Color(
                Math.min(255, Math.max(0, dna[0])),
                Math.min(255, Math.max(0, dna[1])),
                Math.min(255, Math.max(0, dna[2])),
                Math.min(255, Math.max(0, dna[3])));

        canvas.setColor(c);

        canvas.fillPolygon(
                new int[]{
                        dna[4],
                        dna[5],
                        dna[6],
                        dna[7],
                },
                new int[]{
                        dna[8],
                        dna[9],
                        dna[10],
                        dna[11],
                },
                4);
    }

    @Override
    public Shape mutate(Random r, int w, int h) {
        int[] mutDna = Arrays.copyOf(dna, dna.length);

        int rounds = r.nextInt(dna.length);


        for (int i = 0; i < rounds; i++) {
            int offs = r.nextInt(mutDna.length);

            if (r.nextBoolean())
                mutDna[offs] += 1;
            else
                mutDna[offs] -= 1;
        }

        return new IntPoly4(mutDna);
    }

    @Override
    public String exportSVG(int w, int h) {
        String col = String.format(Locale.ENGLISH, "rgb(%d,%d,%d)",
                Math.max(0, Math.min(255, dna[0])),
                Math.max(0, Math.min(255, dna[1])),
                Math.max(0, Math.min(255, dna[2])));

        String pairs =
                dna[4] + "," + dna[8] + " " +
                        dna[5] + "," + dna[9] + " " +
                        dna[6] + "," + dna[10] + " " +
                        dna[7] + "," + dna[11];

        return "<polygon points=\"" + pairs + "\"  fill=\"" + col + "\" fill-opacity=\"" + (dna[3] / 256f) + "\" />\n";
    }

    public static class Generator implements Shape.Generator {

        @Override
        public Shape generate(Random r, int w, int h) {
            return new IntPoly4(r, w, h);
        }
    }
}
