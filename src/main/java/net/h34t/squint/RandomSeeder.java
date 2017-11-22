package net.h34t.squint;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

public class RandomSeeder implements Seeder {

    private final int candidates;
    private final ShapeGenerator builder;
    private final Random r;
    private final int w, h;

    public RandomSeeder(Random r, int w, int h, int candidates, ShapeGenerator builder) {
        this.r = r;
        this.candidates = candidates;
        this.builder = builder;
        this.w = w;
        this.h = h;
    }

    @Override
    public RatedShape findNew(ExecutorService executorService, BufferedImage base, long minScore) {

        List<Squint.RatingTask> candidates = new ArrayList<>();
        RatedShape bestCandidate = null;
//
//        do {
//            for (int i = 0; i < this.candidates; i++) {
//                Shape candidate = builder.get(r, w, h);
//
//                candidates.add(new Squint.RatingTask(base, candidate));
//            }
//
//            List<Future<RatedShape>> results = null;
//            try {
//                results = executorService.invokeAll(candidates);
//            } catch (InterruptedException ignored) {
//            }
//
//            RatedShape runnerUp = getBestCandidate(results);
//            if (runnerUp.score < minScore)
//                bestCandidate = runnerUp;
//
//        } while (bestCandidate == null);

        return bestCandidate;
    }
}
