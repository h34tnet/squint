package net.h34t.squint.shape;

import java.awt.*;
import java.util.List;
import java.util.Random;

public interface Shape {

    void draw(Graphics2D canvas, int w, int h);

    Shape mutate(Random r, int w, int h);

//    List<Shape> mutateAll(Random r, int w, int h);
//    Shape mutateMin(Random r, int w, int h);

    String exportSVG(int w, int h);

    interface Generator {
        Shape generate(Random r);
    }

}
