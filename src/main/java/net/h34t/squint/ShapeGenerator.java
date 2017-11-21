package net.h34t.squint;

import net.h34t.squint.shape.Ellipse;
import net.h34t.squint.shape.GaussTriangle;
import net.h34t.squint.shape.Shape;
import net.h34t.squint.shape.Triangle;

public class ShapeGenerator {

    public static Shape.Generator from(String shape) {
        switch (shape) {

            case "triangle":
                return new Triangle.Generator();
            case "gausstriangle":
                return new GaussTriangle.Generator();
            case "ellipse":
                return new Ellipse.Generator();
            default:
                throw new IllegalArgumentException("Unknown generator " + shape);
        }
    }
}
