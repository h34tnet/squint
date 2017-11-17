package net.h34t.squint;

class PainterThread extends Thread {

    public final IncrementalPainter painter;
    public final Rater rater;

    public PainterThread(IncrementalPainter painter, Rater rater, Runnable r) {
        super(r);
        this.painter = painter;
        this.rater = rater;
    }
}
