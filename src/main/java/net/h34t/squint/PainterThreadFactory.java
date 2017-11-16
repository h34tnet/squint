package net.h34t.squint;

import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadFactory;

class PainterThreadFactory implements ThreadFactory {

    private BufferedImage bi;

    PainterThreadFactory(BufferedImage bi) {
        this.bi = bi;
    }

    @Override
    public Thread newThread(Runnable r) {
        IncrementalPainter painter = new IncrementalPainter(bi);
        Rater rater = new Rater(bi);
        return new PainterThread(painter, rater, r);
    }
}
