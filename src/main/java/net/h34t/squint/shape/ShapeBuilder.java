package net.h34t.squint.shape;

public interface ShapeBuilder <T extends Shape> {

    T generateRandom(int w, int h);

}
