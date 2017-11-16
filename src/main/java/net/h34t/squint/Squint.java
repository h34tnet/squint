package net.h34t.squint;

import net.h34t.squint.shape.Oval;
import net.h34t.squint.shape.Shape;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Squint {

    public static final int THREADS = 4;

    public static final int SHAPES = 512;
    public static final int OPT_CANDIDATE = 128;
    public static final int OPT_HC_CUTOFF = 16;


    public static void main(String... args) throws IOException, ExecutionException, InterruptedException {

        Random r = new Random(0);

        BufferedImage source = ImageIO.read(new File(args[0]));

        final int w = source.getWidth(), h = source.getHeight();

        File output = File.createTempFile("output-", ".png", new File("output"));
        System.out.println("storing result to " + output.getName());

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS, new PainterThreadFactory(source));

        Painter painter = new Painter(source.getWidth(), source.getHeight());
        Rater tester = new Rater(source);

        RatedDNA bestDna = new RatedDNA(null, Long.MAX_VALUE);

        for (int i = 0; i < OPT_CANDIDATE; i++) {
            ImageDNA idna = new ImageDNA(getRandomColor(r));
            painter.paint(idna);
            long score = tester.getScore(painter.getImage());
            if (score < bestDna.score)
                bestDna = new RatedDNA(idna, score);
        }

        BufferedImage bestImage;

        for (int j = 0; j < SHAPES; j++) {
            System.out.println("round " + j);

            painter.paint(bestDna.dna);
            bestImage = painter.getImage();

            List<RatingTask> candidates = new ArrayList<>();
            RatedShape bestCandidate = null;

            do {
                for (int i = 0; i < OPT_CANDIDATE; i++) {
                    Shape candidate = new Oval(
                            r.nextInt(w),
                            r.nextInt(h),
                            r.nextInt(w - 1) + 1,
                            r.nextInt(h - 1) + 1,
                            getRandomAlphaColor(r));

                    candidates.add(new RatingTask(bestImage, candidate));
                }

                List<Future<RatedShape>> results = null;
                try {
                    results = executorService.invokeAll(candidates);
                } catch (InterruptedException ignored) {
                }

                RatedShape runnerUp = getBestCandidate(results);
                if (runnerUp.score < bestDna.score)
                    bestCandidate = runnerUp;

            } while (bestCandidate == null);

            System.out.printf("%,16d - best candidate after initial rounds%n", bestCandidate.score);

            int failedOptRounds = 0;
            int optRounds = 0;

            while (failedOptRounds < OPT_HC_CUTOFF) {
                try {
                    optRounds += 1;
                    candidates.clear();

                    for (int cs = 0; cs < 4; cs++)
                        candidates.add(new RatingTask(bestImage, bestCandidate.shape.mutate(r)));

                    RatedShape runnerUp = getBestCandidate(executorService.invokeAll(candidates));

                    if (runnerUp.score < bestCandidate.score) {
                        failedOptRounds = 0;
                        bestCandidate = runnerUp;


                    } else {
                        failedOptRounds++;
                    }

                } catch (InterruptedException ignored) {
                }
            }

            System.out.printf("%,16d - best candidate after %d opt rounds%n", bestCandidate.score, optRounds);


            long scoreImprovement = bestCandidate.score - bestDna.score;
            System.out.printf("%,16d - improvement%n%n", scoreImprovement);


            bestDna = new RatedDNA(bestDna.dna.append(bestCandidate.shape), bestCandidate.score);

            saveLeader(painter, bestDna.dna, output);
        }

        executorService.shutdown();

    }

    private static Color getRandomColor(Random r) {
        return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

    private static Color getRandomAlphaColor(Random r) {
        return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

    private static void saveLeader(Painter painter, ImageDNA dna, File output) throws IOException {
        painter.paint(dna);
        ImageIO.write(painter.getImage(), "png", output);

    }

    private static RatedShape getBestCandidate(List<Future<RatedShape>> results) throws ExecutionException, InterruptedException {
        RatedShape best = null;
        for (Future<RatedShape> res : results) {
            RatedShape cand = res.get();

            if (best == null || cand.score < best.score)
                best = cand;
        }

        return best;
    }

    public static class RatingTask implements Callable<RatedShape> {

        private final BufferedImage base;
        private final Shape candidate;

        RatingTask(BufferedImage base, Shape candidate) {
            this.base = base;
            this.candidate = candidate;
        }

        @Override
        public RatedShape call() throws Exception {
            PainterThread thread = (PainterThread) Thread.currentThread();
            thread.painter.paint(base, candidate);
            long score = thread.rater.getScore(thread.painter.getImage());

            return new RatedShape(candidate, score);
        }
    }
}