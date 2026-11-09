package drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * One shape on the canvas. It is immutable: the model replaces shapes
 * instead of changing them, which keeps undo and redo simple.
 *
 * Rectangles, ovals and lines are stored as two points (start and end of the
 * drag), a dot as one point and a freehand stroke as all the points the
 * mouse passed through.
 */
public record Shape(Kind kind, List<Point> points, Color color, int stroke, boolean filled) {

    /** The tools the user can draw with, in toolbar order. */
    public enum Kind {
        PEN("Pen"), LINE("Line"), RECTANGLE("Rectangle"), OVAL("Oval"), DOT("Dot");

        private final String label;

        Kind(String label) {
            this.label = label;
        }

        /** True for shapes that can be filled instead of outlined. */
        public boolean canFill() {
            return this == RECTANGLE || this == OVAL;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /** A point on the canvas, in pixels. */
    public record Point(int x, int y) {
    }

    public Shape {
        if (points.isEmpty()) {
            throw new IllegalArgumentException("a shape needs at least one point");
        }
        if (stroke < 1) {
            throw new IllegalArgumentException("stroke must be at least 1");
        }
        points = List.copyOf(points);
        // Only rectangles and ovals have an inside to fill.
        filled = filled && kind.canFill();
    }

    /** Makes a rectangle, oval or line between two points. */
    public static Shape between(Kind kind, int x1, int y1, int x2, int y2,
                                Color color, int stroke, boolean filled) {
        return new Shape(kind, List.of(new Point(x1, y1), new Point(x2, y2)), color, stroke, filled);
    }

    /** Makes a dot centred on the point (x, y). */
    public static Shape dot(int x, int y, Color color, int stroke) {
        return new Shape(Kind.DOT, List.of(new Point(x, y)), color, stroke, true);
    }

    /** The diameter of a dot drawn with the given stroke width. */
    public static int dotSize(int stroke) {
        return stroke * 2 + 4;
    }

    public Point start() {
        return points.get(0);
    }

    public Point end() {
        return points.get(points.size() - 1);
    }

    /** The smallest rectangle around the points (for a dot: around the dot). */
    public Rectangle bounds() {
        if (kind == Kind.DOT) {
            int size = dotSize(stroke);
            return new Rectangle(start().x() - size / 2, start().y() - size / 2, size, size);
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Point p : points) {
            minX = Math.min(minX, p.x());
            minY = Math.min(minY, p.y());
            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Rectangle r = bounds();
        switch (kind) {
            case DOT -> g.fill(new Ellipse2D.Double(r.x, r.y, r.width, r.height));
            case LINE -> g.draw(new Line2D.Double(start().x(), start().y(), end().x(), end().y()));
            case RECTANGLE -> fillOrDraw(g, new Rectangle2D.Double(r.x, r.y, r.width, r.height));
            case OVAL -> fillOrDraw(g, new Ellipse2D.Double(r.x, r.y, r.width, r.height));
            case PEN -> g.draw(path());
        }
    }

    private void fillOrDraw(Graphics2D g, java.awt.Shape outline) {
        if (filled) {
            g.fill(outline);
        } else {
            g.draw(outline);
        }
    }

    private Path2D path() {
        Path2D path = new Path2D.Double();
        path.moveTo(start().x(), start().y());
        for (Point p : points.subList(1, points.size())) {
            path.lineTo(p.x(), p.y());
        }
        return path;
    }
}
