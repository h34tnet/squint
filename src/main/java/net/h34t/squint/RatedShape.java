package net.h34t.squint;

import net.h34t.squint.shape.Shape;

public class RatedShape {

    public final Shape shape;
    public final long score;

    public RatedShape(Shape shape, long score) {
        this.shape = shape;
        this.score = score;
    }
}
