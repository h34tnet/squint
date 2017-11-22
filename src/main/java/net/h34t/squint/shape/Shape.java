package net.h34t.squint.shape;

import java.awt.Graphics2D;
import java.util.Random;

public interface Shape {

    void draw(Graphics2D canvas, int w, int h);

    Shape mutate(Random r, int w, int h);

    String exportSVG(int w, int h);

    interface Generator {
        Shape generate(Random r, int w, int h);
    }

}
