package net.h34t.squint.shape;

import java.awt.Color;
import java.awt.Graphics2D;
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

    public static int clamp(int c, int min, int max) {
        return Math.min(Math.max(min, c), max);
    }

    @Override
    public void draw(Graphics2D canvas, int w, int h) {
        canvas.setColor(this.color);
        canvas.fillOval(x, y, this.w, this.h);
    }

    @Override
    public Shape mutate(Random r) {
        Color ncol = new Color(
                clamp(this.color.getRed() + r.nextInt(3) - 1, 0, 255),
                clamp(this.color.getGreen() + r.nextInt(3) - 1, 0, 255),
                clamp(this.color.getBlue() + r.nextInt(3) - 1, 0, 255),
                clamp(this.color.getAlpha() + r.nextInt(3) - 1, 1, 255)
        );

        return new Oval(
                this.x + r.nextInt(3) - 1,
                this.y + r.nextInt(3) - 1,
                Math.max(4, this.w + r.nextInt(3) - 1),
                Math.max(4, this.h + r.nextInt(3) - 1),
                ncol
        );
    }

}
