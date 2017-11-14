package net.h34t.squint.shape;

import java.awt.*;
import java.util.Random;

public class Oval implements Shape {

    public final int x;
    public final int y;
    public final int w;
    public final int h;

    public final Color color;

    public Oval(int x, int y, int w, int h, Color color) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.color = color;
    }

    @Override
    public void draw(Graphics2D canvas, int w, int h) {
        canvas.setColor(this.color);
        canvas.fillOval(x, y, this.w, this.h);
    }

    @Override
    public Shape mutate(Random r) {

        return null;
    }


}
