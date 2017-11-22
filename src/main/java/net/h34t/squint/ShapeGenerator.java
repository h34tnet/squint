package net.h34t.squint;

import net.h34t.squint.shape.*;

class ShapeGenerator {

    static Shape.Generator from(String shape) {
        switch (shape) {

            case "triangle":
                return new Triangle.Generator();
            case "trianglesingle":
                return new TriangleSingle.Generator();
            case "gausstriangle":
                return new GaussTriangle.Generator();
            case "ellipse":
                return new Ellipse.Generator();
            case "trianglebw":
                return new TriangleBW.Generator();
            case "poly4":
                return new Poly4.Generator();
            default:
                throw new IllegalArgumentException("Unknown generator " + shape);
        }
    }
}
