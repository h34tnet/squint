package net.h34t.squint;

class PainterThread extends Thread {

    final IncrementalPainter painter;
    final Rater rater;

    PainterThread(IncrementalPainter painter, Rater rater, Runnable r) {
        super(r);
        this.painter = painter;
        this.rater = rater;
    }
}
