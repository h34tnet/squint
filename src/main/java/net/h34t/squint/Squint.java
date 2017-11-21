package net.h34t.squint;

import net.h34t.squint.shape.Shape;
import net.h34t.squint.shape.AdvTriangle;
import net.h34t.squint.shape.Triangle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class Squint {

    private static final int THREADS = 8;
    private static final int SHAPES = 128;
    private static final int OPT_CANDIDATE = 1024;

    private static final int OPT_MUTATIONS = 64;
    private static final int OPT_HC_CUTOFF = 32;


    public static void main(String... args) throws IOException, ExecutionException, InterruptedException {

        Random r = new Random();

        File input = new File(args[0]);

        BufferedImage source = ImageIO.read(input);

        final int w = source.getWidth(), h = source.getHeight();

        String date = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date())
                .replaceAll("[^0-9]", "_");

        String filename = String.format("%s-%s-%d-%d-%d-%d",
                input.getName(),
                date,
                SHAPES,
                OPT_CANDIDATE,
                OPT_HC_CUTOFF,
                OPT_MUTATIONS);

        File outputPng = new File(new File("output"), filename + ".png");
        File outputSvg = new File(new File("output"), filename + ".svg");
        File outputCsv = new File(new File("output"), filename + ".csv");
        System.out.println("storing result to " + outputPng.getName());

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS, new PainterThreadFactory(source));

        long st = System.nanoTime();
        List<Timer> timers = new ArrayList<>(SHAPES);

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
                        Shape candidate = new Triangle(r);

                                /*new Oval(
                                r.nextInt(w),
                                r.nextInt(h),
                                r.nextInt((int) (w * percLeft) + 1) + 5,
                                r.nextInt((int) (h * percLeft) + 1) + 5,
//                            r.nextInt(w - 1) + 1,
//                            r.nextInt(h - 1) + 1,
                                getRandomAlphaColor(r));
                                */

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

                        while (mutations.size() < OPT_MUTATIONS)
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

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                long scoreImprovement = bestCandidateScore - bestCandidate.score;
                System.out.printf("%,16d - improvement%n", scoreImprovement);
                System.out.printf("%,16d - best candidate after %d opt rounds%n", bestCandidate.score, optRounds);

                /*
                failedOptRounds = 0;
                optRounds = 0;
                bestCandidateScore = bestCandidate.score;

                while (failedOptRounds < OPT_HC_CUTOFF) {
                    try {
                        optRounds += 1;
                        candidates.clear();

                        List<Shape> mutations = new ArrayList<>();

                        for (int cs = 0; cs < OPT_MUTATIONS; cs++)
                            mutations.add(bestCandidate.shape.mutateMin(r));

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

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                scoreImprovement = bestCandidateScore - bestCandidate.score;
                System.out.printf("%,16d - improvement%n", scoreImprovement);
                System.out.printf("%,16d - best candidate after %d opt rounds%n", bestCandidate.score, optRounds);
                */

                bestDna = new RatedDNA(bestDna.dna.append(bestCandidate.shape), bestCandidate.score);

                long et = System.nanoTime();
                long dt = (et - st) / 1_000_000;

                timers.add(new Timer(dt, bestDna.score));

                saveLeader(painter, bestDna.dna, outputPng);
                exportSVG(bestDna.dna, w * 3, h * 3, outputSvg, bestDna.score);
                exportCSV(timers, outputCsv, THREADS, SHAPES, OPT_CANDIDATE, OPT_MUTATIONS, OPT_HC_CUTOFF);
                System.out.println();
            }

        } finally {
            executorService.shutdown();
        }
    }

    private static void saveLeader(Painter painter, ImageDNA dna, File output) throws IOException {
        painter.paint(dna);
        ImageIO.write(painter.getImage(), "png", output);
    }

    private static void exportSVG(ImageDNA dna, int w, int h, File output, long score) throws IOException {
        try (FileWriter fw = new FileWriter(output)) {
            fw.write(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                            "<svg width=\"" + w + "\" height=\"" + h + "\" " +
                            "\n" + // "viewBox=\"0 0 " + w + " " + h + "\"\n" +
                            "    xmlns=\"http://www.w3.org/2000/svg\">\n" +
//                            "    <defs><clipPath id="cutoff"><rect x=\"0\" y=\"0\" width=\"" + w + "\" height=\"" + h + "\" /></clipPath></defs>" +
                            //                          "    <g clip-path=\"url(#cutoff)\">" +
                            "    <rect x=\"0\" y=\"0\" width=\"" + w + "\" height=\"" + h + "\" fill=\"white\" />\n");

            for (Shape s : dna.getShapes())
                fw.write(s.exportSVG(w, h));

            // fw.write("    </g>\n");
            fw.write("</svg>\n");
            fw.write("<!-- score: " + String.format(Locale.ENGLISH, "%,16d", score) + " -->\n");
        }
    }

    // THREADS, SHAPES, OPT_CANDIDATE, OPT_MUTATIONS, OPT_HC_CUTOFF
    private static void exportCSV(List<Timer> timers, File output, int threads, int shapes, int candidates, int mutations, int cutoff) throws IOException {
        try (FileWriter fw = new FileWriter(output)) {
            fw.write(String.format(Locale.ENGLISH, "Shapes: %d%n" +
                    "Threads:\t%d%n" +
                    "Candidates:\t%d%n" +
                    "Mutations:\t%d%n" +
                    "Cutoff:\t%d%n", shapes, threads, candidates, mutations, cutoff));

            for (Timer t : timers)
                fw.write(t.time + "\t" + t.score + "\n");
        }
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

    private static class Timer {

        final long time;
        final long score;

        public Timer(long time, long score) {
            this.time = time;
            this.score = score;
        }
    }
}