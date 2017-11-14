package net.h34t.squint.shape;

import java.awt.*;
import java.util.Random;

public interface Shape {

    void draw(Graphics2D canvas, int w, int h);

    Shape mutate(Random r);

}
