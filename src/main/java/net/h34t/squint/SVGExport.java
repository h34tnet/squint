package net.h34t.squint;

import net.h34t.squint.shape.Shape;

import java.util.List;
import java.util.Locale;

/**
 * TODO add description
 */
public class SVGExport {


    public String export(List<Shape> shapes, int w, int h) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(Locale.ENGLISH, "<svg width=\"%1$d\" height=\"%2$d\" viewBox=\"0 0 %1$d %2$d\" style=\"background-color: white;\" xmlns=\"http://www.w3.org/2000/svg\">%n", w, h));
        builder.append(String.format(Locale.ENGLISH, "<rect x=\"0\" y=\"0\" width=\"%d\" height=\"%d\" style=\"fill: white;\"/>%n%n", w, h));

        for (Shape s : shapes)
            builder.append("    ").append(s.toSVG(w, h)).append("\n");

        builder.append("</svg>\n");

        return builder.toString();
    }
}
