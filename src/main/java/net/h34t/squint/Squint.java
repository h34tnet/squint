package net.h34t.squint;

import net.h34t.squint.shape.Shape;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Squint {

    private static final int THREADS = 8;

    private static final int SHAPES = 128;

    private static final int OPT_CANDIDATE = 8 * 256;

    private static final int OPT_MUTATIONS = 8 * 4;

    private static final int OPT_HC_CUTOFF = 64;


    public static void main(String... args) throws IOException, ExecutionException, InterruptedException {

        Random r = new Random(0);

        String shape = "intpoly4";

        Shape.Generator generator = ShapeGenerator.from(shape);

        File input = new File(args[0]);

        BufferedImage source = ImageIO.read(input);

        final int w = source.getWidth(), h = source.getHeight();

        String filename;

        if (args.length == 2) {
            filename = args[1] + "-" + shape;
        } else {
            String date = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date())
                    .replaceAll("[^0-9]", "_");

            filename = String.format("%s-%s-%d-%d-%d-%d",
                    input.getName(),
                    date,
                    SHAPES,
                    OPT_CANDIDATE,
                    OPT_HC_CUTOFF,
                    OPT_MUTATIONS);
        }

        File outputPng = new File(new File("output"), filename + ".png");
        File outputSvg = new File(new File("output"), filename + ".svg");
        File outputCsv = new File(new File("output"), filename + ".csv");
        System.out.println("storing result to " + outputPng.getName());

        ExecutorService executorService = null;

        long st = System.nanoTime();
        List<Timer> timers = new ArrayList<>(SHAPES);

        try {
            executorService = Executors.newFixedThreadPool(THREADS, new PainterThreadFactory(source));

            Painter painter = new Painter(source.getWidth(), source.getHeight());
            painter.paint(new ImageDNA());
            Rater rater = new Rater(source);

            RatedDNA bestDna = new RatedDNA(new ImageDNA(), rater.getScore(painter.getImage()));

            BufferedImage base;
            List<RatingTask> candidates = new ArrayList<>();

            for (int j = 0; j < SHAPES; j++) {
                double percDone = (double) j / SHAPES;
                double percLeft = 1d - percDone;

                System.out.printf("round %d - %.2f%% done, %.2f%% left%n", j, percDone * 100d, percLeft * 100d);
                System.out.printf("%,16d - best candidate%n", bestDna.score);

                painter.paint(bestDna.dna);
                base = painter.getImage();

                RatedShape bestCandidate = null;

                do {
                    candidates.clear();
                    for (int i = 0; i < OPT_CANDIDATE; i++)
                        candidates.add(new RatingTask(base, generator.generate(r, w, h)));

                    RatedShape runnerUp = getBestCandidate(executorService.invokeAll(candidates));
                    if (runnerUp.score < bestDna.score)
                        bestCandidate = runnerUp;

                    if (bestCandidate == null) {
                        System.out.println("Setting round didn't yield a candidate");
                    }

                } while (bestCandidate == null);

                System.out.printf("%,16d - improvement%n", bestDna.score - bestCandidate.score);
                System.out.printf("%,16d - best candidate after initial rounds%n", bestCandidate.score);

                int failedOptRounds = 0;
                int optRounds = 0;

                long bestCandidateScore = bestCandidate.score;

                while (failedOptRounds < OPT_HC_CUTOFF) {
                    optRounds += 1;
                    candidates.clear();

                    while (candidates.size() < OPT_MUTATIONS)
                        candidates.add(new RatingTask(base, bestCandidate.shape.mutate(r, w, h)));

                    RatedShape runnerUp = getBestCandidate(executorService.invokeAll(candidates));

                    if (runnerUp != null && (runnerUp.score < bestCandidate.score)) {
                        failedOptRounds = 0;
                        bestCandidate = runnerUp;

                    } else {
                        failedOptRounds++;
                    }
                }

                long scoreImprovement = bestCandidateScore - bestCandidate.score;
                System.out.printf("%,16d - improvement%n", scoreImprovement);
                System.out.printf("%,16d - best candidate after %d opt rounds%n", bestCandidate.score, optRounds);

                bestDna = new RatedDNA(bestDna.dna.append(bestCandidate.shape), bestCandidate.score);

                long et = System.nanoTime();
                long dt = (et - st) / 1_000_000;

                timers.add(new Timer(dt, bestDna.score));

                saveLeader(painter, bestDna.dna, outputPng);
                exportSVG(bestDna.dna, w, h, outputSvg, bestDna.score);
                exportCSV(timers, outputCsv, THREADS, SHAPES, OPT_CANDIDATE, OPT_MUTATIONS, OPT_HC_CUTOFF);
                // ImageIO.write(source, "PNG", new File("output/source.png"));
                System.out.println();
            }

        } finally {
            if (executorService != null)
                executorService.shutdown();
        }

        System.out.println("done, result written to " + outputPng.getAbsolutePath());
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
                fw.write("    " + s.exportSVG(w, h));

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

        Timer(long time, long score) {
            this.time = time;
            this.score = score;
        }
    }
}