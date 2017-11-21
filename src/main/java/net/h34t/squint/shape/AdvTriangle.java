package net.h34t.squint.shape;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AdvTriangle implements Shape {

    private float[] dna;

    public AdvTriangle(Random r) {
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

    public AdvTriangle(float[] dna) {
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
        // canvas.setBackground(c);

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
    public Shape mutate(Random r) {
        float[] mutDna = Arrays.copyOf(dna, dna.length);

        // int count = (int) Math.min(dna.length, Math.abs(r.nextGaussian() * mutDna.length) + 1);
        int count = r.nextInt(mutDna.length - 1) + 1;

        for (int i = 0; i < count; i++)
            mutDna[r.nextInt(mutDna.length)] = r.nextFloat();

        return new AdvTriangle(mutDna);
    }

    @Override
    public List<Shape> mutateAll(Random r) {
        int dnaLen = dna.length;
        List<Shape> shapes = new ArrayList<>(dnaLen);
        for (int i = 0; i < dnaLen; i++) {
            float[] mutDna = Arrays.copyOf(dna, dnaLen);
            mutDna[i] = r.nextFloat();
            shapes.add(new AdvTriangle(mutDna));
        }
        return shapes;
    }

    @Override
    public Shape mutateMin(Random r) {
        float[] mutDna = Arrays.copyOf(dna, dna.length);
        mutDna[r.nextInt(mutDna.length)] += r.nextGaussian() / 100d;
        return new AdvTriangle(mutDna);
    }

    @Override
    public String exportSVG(int w, int h) {
        StringBuilder pairs = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            pairs.append(dna[i + 4] * w).append(",").append(dna[i + 7] * h).append(" ");
        }

        String col = String.format(Locale.ENGLISH, "rgb(%d,%d,%d)",
                (int) Math.min(255, dna[0] * 255),
                (int) Math.min(255, dna[1] * 255),
                (int) Math.min(255, dna[2] * 255));

        return "    <polygon points=\"" + pairs.toString() + "\"  fill=\"" + col + "\" fill-opacity=\"" + dna[3] + "\" />\n";
    }
}
