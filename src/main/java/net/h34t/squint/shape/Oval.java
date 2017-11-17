package net.h34t.squint.shape;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
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

    public static int mv(Random r, int c) {
        return c + r.nextInt(2) * 2 - 1;
    }

    public static int rclamp(Random r, int c, int min, int max) {
        int offs = r.nextInt(2) * 2 - 1;
        return Math.min(Math.max(min, c + offs), max);
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

    @Override
    public List<Shape> mutateAll(Random rd) {
        int r = this.color.getRed();
        int g = this.color.getGreen();
        int b = this.color.getBlue();
        int a = this.color.getAlpha();

        return Arrays.asList(
                new Oval(x, y, w, h, new Color(r, g, b, a)),
                new Oval(x - 1, y, w, h, new Color(r, g, b, a)),
                new Oval(x + 1, y, w, h, new Color(r, g, b, a)),
                new Oval(x, y - 1, w, h, new Color(r, g, b, a)),
                new Oval(x, y + 1, w, h, new Color(r, g, b, a)),
                new Oval(x, y, Math.max(w - 1, 1), h, new Color(r, g, b, a)),
                new Oval(x, y, w + 1, h, new Color(r, g, b, a)),
                new Oval(x, y, w, Math.max(h - 1, 1), new Color(r, g, b, a)),
                new Oval(x, y, w, h + 1, new Color(r, g, b, a)),
                new Oval(x, y, w, h, new Color(clamp(r - 1, 0, 255), g, b, a)),
                new Oval(x, y, w, h, new Color(clamp(r + 1, 0, 255), g, b, a)),
                new Oval(x, y, w, h, new Color(r, clamp(g - 1, 0, 255), b, a)),
                new Oval(x, y, w, h, new Color(r, clamp(g + 1, 0, 255), b, a)),
                new Oval(x, y, w, h, new Color(r, g, clamp(b - 1, 0, 255), a)),
                new Oval(x, y, w, h, new Color(r, g, clamp(b + 1, 0, 255), a)),
                new Oval(x, y, w, h, new Color(r, g, b, clamp(a - 1, 1, 255))),
                new Oval(x, y, w, h, new Color(r, g, b, clamp(a + 1, 0, 255)))
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Oval oval = (Oval) o;

        if (x != oval.x) return false;
        if (y != oval.y) return false;
        if (w != oval.w) return false;
        if (h != oval.h) return false;
        return color.equals(oval.color);
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + w;
        result = 31 * result + h;
        result = 31 * result + color.hashCode();
        return result;
    }
}
