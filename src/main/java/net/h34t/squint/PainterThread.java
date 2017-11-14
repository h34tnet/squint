package net.h34t.squint;

class PainterThread extends Thread {

    public final Painter painter;
    public final Rater rater;

    public PainterThread(Painter painter, Rater rater, Runnable r) {
        super(r);
        this.painter = painter;
        this.rater = rater;
    }
}
