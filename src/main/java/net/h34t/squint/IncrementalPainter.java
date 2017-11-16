package net.h34t.squint;

import net.h34t.squint.shape.Shape;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class IncrementalPainter {

    private final Graphics2D g2d;
    private final BufferedImage image;

    public IncrementalPainter(BufferedImage source) {
        this.image = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        this.g2d = (Graphics2D) image.getGraphics();
        this.g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void paint(BufferedImage base, Shape candidate) {
        // reset the canvas to the original state
        g2d.drawImage(base, 0, 0, null);

        // draw the new candidate on top
        candidate.draw(g2d, image.getWidth(), image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }
}
