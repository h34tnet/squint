package net.h34t.squint;


import net.h34t.squint.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class ImageDNA {

    public List<Shape> shapes;

    public ImageDNA() {
        this.shapes = new ArrayList<>();
    }

    public ImageDNA(List<Shape> shapes) {
        this.shapes = new ArrayList<>();
        this.shapes.addAll(shapes);
    }

    public List<Shape> getShapes() {
        return this.shapes;
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
        return new ImageDNA(shapes);
    }
}
