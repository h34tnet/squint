package net.h34t.squint;

import net.h34t.squint.shape.Oval;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Squint {

    public static void main(String... args) throws IOException, ExecutionException, InterruptedException {

        Random r = new Random(0);

        BufferedImage source = ImageIO.read(new File(args[0]));

        final int w = source.getWidth(), h = source.getHeight();

        File output = File.createTempFile("output-", ".png", new File("output"));
        System.out.println("storing result to " + output.getName());

        ExecutorService executorService = Executors.newFixedThreadPool(16, new PainterThreadFactory(source));

        Painter painter = new Painter(source.getWidth(), source.getHeight());
        Rater tester = new Rater(source);

        RatedDNA best = new RatedDNA(null, Long.MAX_VALUE);

        for (int i = 0; i < 512; i++) {
            ImageDNA idna = new ImageDNA(getRandomColor(r));
            painter.paint(idna);
            long score = tester.getScore(painter.getImage());
            if (score < best.score)
                best = new RatedDNA(idna, score);
        }

        for (int j = 0; j < 512; j++) {
            System.out.println("round " + j);

            List<RatingTask> candidates = new ArrayList<>();

            for (int i = 0; i < 256; i++) {
                ImageDNA idna = best.dna.copy();
                idna.getShapes().add(new Oval(
                        r.nextInt(w),
                        r.nextInt(h),
                        r.nextInt(w - 1) + 1,
                        r.nextInt(h - 1) + 1,
                        getRandomAlphaColor(r)));

                candidates.add(new RatingTask(idna));
            }

            List<Future<RatedDNA>> results = null;
            try {
                results = executorService.invokeAll(candidates);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Future<RatedDNA> res : results) {
                RatedDNA cand = res.get();

                if (cand.score < best.score) {
                    best = cand;
                }
            }

            System.out.println("best candidate: " + best.score);

            saveLeader(painter, best.dna, output);
        }

        executorService.shutdown();

    }

    public static Color getRandomColor(Random r) {
        return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

    public static Color getRandomAlphaColor(Random r) {
        return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

    public static void saveLeader(Painter painter, ImageDNA dna, File output) throws IOException {
        painter.paint(dna);
        ImageIO.write(painter.getImage(), "png", output);

    }

    public static class RatingTask implements Callable<RatedDNA> {

        private final ImageDNA dna;

        public RatingTask(ImageDNA dna) {
            this.dna = dna;
        }

        @Override
        public RatedDNA call() throws Exception {
            PainterThread thread = (PainterThread) Thread.currentThread();
            thread.painter.paint(dna);
            long score = thread.rater.getScore(thread.painter.getImage());

            return new RatedDNA(dna, score);
        }
    }

}
