package net.h34t.squint;

import net.h34t.squint.shape.Shape;

import java.awt.*;
import java.awt.image.BufferedImage;

public class IncrementalPainter {

    private final Graphics2D g2d;
    private final BufferedImage image;

    public IncrementalPainter(BufferedImage source) {
        this.image = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        this.image.getGraphics().drawImage(image, 0, 0, null);
        this.g2d = (Graphics2D) image.getGraphics();
    }

    public void paint(ImageDNA dna) {
        // clear the canvas
        for (Shape shape : dna.getShapes())
            shape.draw(g2d, image.getWidth(), image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }
}
