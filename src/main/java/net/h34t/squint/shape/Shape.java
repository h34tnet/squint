package net.h34t.squint.shape;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;

public interface Shape {

    void draw(Graphics2D canvas, int w, int h);

    Shape mutate(Random r);

    List<Shape> mutateAll(Random r);

    String toSVG(int w, int h);

}
