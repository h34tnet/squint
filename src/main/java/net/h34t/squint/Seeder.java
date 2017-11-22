package net.h34t.squint;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;

public interface Seeder {

    RatedShape findNew(ExecutorService executorService, BufferedImage base, long minScore);
}
