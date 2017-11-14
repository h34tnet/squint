package net.h34t.squint;

import net.h34t.squint.shape.Shape;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Painter {

    private final int width;
    private final int height;
    private final Graphics2D g2d;
    private final BufferedImage image;

    public Painter(int width, int height) {
        this.width = width;
        this.height = height;

        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.g2d = (Graphics2D) image.getGraphics();
    }

    public void paint(ImageDNA dna) {
        // clear the canvas
        g2d.setBackground(dna.background);
        g2d.clearRect(0, 0, width, height);

        for (Shape shape : dna.getShapes())
            shape.draw(g2d, width, height);
    }

    public BufferedImage getImage() {
        return image;
    }
}
