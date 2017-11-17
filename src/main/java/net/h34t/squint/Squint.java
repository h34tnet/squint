package net.h34t.squint;

import net.h34t.squint.shape.Oval;
import net.h34t.squint.shape.Shape;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class Squint {

    private static final int THREADS = 8;
    private static final int SHAPES = 128;
    private static final int OPT_CANDIDATE = 4096;
    private static final int OPT_HC_CUTOFF = 64;
    private static final int OPT_MUTATIONS = 32;


    public static void main(String... args) throws IOException, ExecutionException, InterruptedException {

        Random r = new Random();

        File input = new File(args[0]);

        BufferedImage source = ImageIO.read(input);

        final int w = source.getWidth(), h = source.getHeight();

        String date = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date())
                .replaceAll("[^0-9]", "_");

        String filename = String.format("%s-%s-%d-%d-%d-%d.png",
                input.getName(),
                date,
                SHAPES,
                OPT_CANDIDATE,
                OPT_HC_CUTOFF,
                OPT_MUTATIONS);

        File output = new File(new File("output"), filename);
        System.out.println("storing result to " + output.getName());

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS, new PainterThreadFactory(source));

        try {
            Painter painter = new Painter(source.getWidth(), source.getHeight());

            // RatedDNA bestDna = new RatedDNA(null, Long.MAX_VALUE);
            RatedDNA bestDna = new RatedDNA(new ImageDNA(Color.WHITE), Long.MAX_VALUE);

//            for (int i = 0; i < OPT_CANDIDATE; i++) {
//                ImageDNA idna = new ImageDNA(getRandomColor(r));
//                painter.paint(idna);
//                long score = tester.getScore(painter.getImage());
//                if (score < bestDna.score)
//                    bestDna = new RatedDNA(idna, score);
//            }

            BufferedImage bestImage;

            Set<Shape> tested = new HashSet<>();
            for (int j = 0; j < SHAPES; j++) {
                double percDone = (double) j / SHAPES;
                double percLeft = 1d - percDone;

                tested.clear();
                System.out.printf("round %d - %.2f%% done%n", j, percDone * 100d);
                System.out.printf("%,16d - best candidate%n", bestDna.score);

                painter.paint(bestDna.dna);
                bestImage = painter.getImage();

                List<RatingTask> candidates = new ArrayList<>();
                RatedShape bestCandidate = null;

                do {
                    for (int i = 0; i < OPT_CANDIDATE; i++) {
                        Shape candidate = new Oval(
                                r.nextInt(w),
                                r.nextInt(h),
                                r.nextInt((int) (w * percLeft) + 1) + 5,
                                r.nextInt((int) (h * percLeft) + 1) + 5,
//                            r.nextInt(w - 1) + 1,
//                            r.nextInt(h - 1) + 1,
                                getRandomAlphaColor(r));

                        if (!tested.contains(candidate)) {
                            candidates.add(new RatingTask(bestImage, candidate));
                            tested.add(candidate);
                        }
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

                System.out.printf("%,16d - improvement%n", bestDna.score - bestCandidate.score);
                System.out.printf("%,16d - best candidate after initial rounds%n", bestCandidate.score);

                int failedOptRounds = 0;
                int optRounds = 0;

                long bestCandidateScore = bestCandidate.score;

                while (failedOptRounds < OPT_HC_CUTOFF) {
                    try {
                        optRounds += 1;
                        candidates.clear();

                        List<Shape> mutations = new ArrayList<>();
                        mutations.addAll(bestCandidate.shape.mutateAll(r));

                        for (int cs = 0; cs < OPT_MUTATIONS; cs++)
                            mutations.add(bestCandidate.shape.mutate(r));

                        for (Shape mutation : mutations)
                            if (!tested.contains(mutation)) {
                                candidates.add(new RatingTask(bestImage, mutation));
                                tested.add(mutation);
                            }

                        RatedShape runnerUp = getBestCandidate(executorService.invokeAll(candidates));

                        if (runnerUp != null && (runnerUp.score < bestCandidate.score)) {
                            failedOptRounds = 0;
                            bestCandidate = runnerUp;

                        } else {
                            failedOptRounds++;
                        }

                    } catch (InterruptedException ignored) {
                    }
                }


                long scoreImprovement = bestCandidateScore - bestCandidate.score;
                System.out.printf("%,16d - improvement%n", scoreImprovement);
                System.out.printf("%,16d - best candidate after %d opt rounds%n", bestCandidate.score, optRounds);

                bestDna = new RatedDNA(bestDna.dna.append(bestCandidate.shape), bestCandidate.score);

                saveLeader(painter, bestDna.dna, output);
                System.out.println();
            }

        } finally {
            executorService.shutdown();
        }
    }

    private static Color getRandomColor(Random r) {
        return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

    private static Color getRandomAlphaColor(Random r) {
        return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255), r.nextInt(254) + 1);
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