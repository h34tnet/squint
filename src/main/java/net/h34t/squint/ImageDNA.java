package net.h34t.squint;


import net.h34t.squint.shape.Shape;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ImageDNA {

    public final Color background;
    public List<Shape> shapes;

    public ImageDNA(Color background) {
        this.background = background;
        this.shapes = new ArrayList<>();
    }

    public ImageDNA(Color background, List<Shape> shapes) {
        this.background = background;
        this.shapes = new ArrayList<>();
        this.shapes.addAll(shapes);
    }

    public List<Shape> getShapes() {
        return this.shapes;
    }

    public ImageDNA copy() {
        return new ImageDNA(this.background, this.shapes);
    }


    /**
     * Clones this object and appends a new shape
     *
     * @param shape the shape to append
     * @return
     */
    public ImageDNA append(Shape shape) {
        List<Shape> shapes = new ArrayList<>(this.shapes.size() + 1);
        shapes.addAll(this.shapes);
        shapes.add(shape);
        return new ImageDNA(new Color(this.background.getRGB()), shapes);
    }
}
