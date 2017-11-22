package net.h34t.squint;

import net.h34t.squint.shape.Shape;

import java.awt.*;
import java.awt.image.BufferedImage;

class IncrementalPainter {

    private final Graphics2D g2d;
    private final BufferedImage image;

    IncrementalPainter(BufferedImage source) {
        this.image = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        this.g2d = (Graphics2D) image.getGraphics();
        // this.g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    void paint(BufferedImage base, Shape candidate) {
        // reset the canvas to the original state
        g2d.drawImage(base, 0, 0, null);

        // draw the new candidate on top
        candidate.draw(g2d, image.getWidth(), image.getHeight());
    }

    BufferedImage getImage() {
        return image;
    }
}
