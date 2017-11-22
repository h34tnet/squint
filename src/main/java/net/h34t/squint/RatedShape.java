package net.h34t.squint;

import net.h34t.squint.shape.Shape;

class RatedShape {

    final Shape shape;
    final long score;

    RatedShape(Shape shape, long score) {
        this.shape = shape;
        this.score = score;
    }
}
