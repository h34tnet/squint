package net.h34t.squint;

import net.h34t.squint.shape.Shape;

import java.awt.*;
import java.awt.image.BufferedImage;

class Painter {

    private final int width;
    private final int height;
    private final Graphics2D g2d;
    private final BufferedImage image;

    Painter(int width, int height) {
        this.width = width;
        this.height = height;

        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.g2d = (Graphics2D) image.getGraphics();
        // this.g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    void paint(ImageDNA dna) {
        // clear the canvas
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, width, height);

        for (Shape shape : dna.getShapes())
            shape.draw(g2d, width, height);
    }

    BufferedImage getImage() {
        return image;
    }
}
