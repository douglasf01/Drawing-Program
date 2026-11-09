package drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;

/**
 * Small toolbar icons drawn with Java2D, so the program needs no image files.
 * Every icon is drawn on a 20 by 20 pixel grid.
 */
public final class Icons {
    public static final int SIZE = 20;

    private Icons() {
    }

    /** Draws an icon's lines and shapes in the given colour. */
    @FunctionalInterface
    private interface Painter {
        void paint(Graphics2D g);
    }

    /** An icon that runs a painter with antialiasing and a 1.6 pixel round pen. */
    private record DrawnIcon(Color color, Painter painter) implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.translate(x, y);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(color);
                painter.paint(g2);
            } finally {
                g2.dispose();
            }
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }

    /** The icon for a drawing tool. */
    public static Icon tool(Shape.Kind kind, Color color) {
        return new DrawnIcon(color, switch (kind) {
            case PEN -> g -> {
                Path2D pen = new Path2D.Double();
                pen.moveTo(4, 16);
                pen.lineTo(5, 12.5);
                pen.lineTo(13.5, 4);
                pen.lineTo(16, 6.5);
                pen.lineTo(7.5, 15);
                pen.closePath();
                g.draw(pen);
                g.draw(new Line2D.Double(11.5, 6, 14, 8.5));
            };
            case LINE -> g -> g.draw(new Line2D.Double(4, 16, 16, 4));
            case RECTANGLE -> g -> g.draw(new RoundRectangle2D.Double(3.5, 5.5, 13, 9, 2, 2));
            case OVAL -> g -> g.draw(new Ellipse2D.Double(3, 5, 14, 10));
            case DOT -> g -> g.fill(new Ellipse2D.Double(6.5, 6.5, 7, 7));
        });
    }

    /** A square that is either outlined or filled. */
    public static Icon fill(boolean filled, Color color) {
        return new DrawnIcon(color, g -> {
            RoundRectangle2D square = new RoundRectangle2D.Double(4, 4, 12, 12, 3, 3);
            if (filled) {
                g.fill(square);
            }
            g.draw(square);
        });
    }

    /** A short horizontal line as thick as the stroke width (capped to fit). */
    public static Icon stroke(int width, Color color) {
        return new DrawnIcon(color, g -> {
            float w = Math.min(width, 10);
            g.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Line2D.Double(3 + w / 2, 10, 17 - w / 2, 10));
        });
    }

    /** A curved arrow pointing left (undo) or right (redo). */
    public static Icon history(boolean redo, Color color) {
        return new DrawnIcon(color, g -> {
            if (redo) {
                g.scale(-1, 1);
                g.translate(-SIZE, 0);
            }
            Path2D arc = new Path2D.Double();
            arc.moveTo(5, 9);
            arc.lineTo(12, 9);
            arc.curveTo(17, 9, 17, 16, 12, 16);
            arc.lineTo(8, 16);
            g.draw(arc);
            Path2D head = new Path2D.Double();
            head.moveTo(8, 5.5);
            head.lineTo(4.5, 9);
            head.lineTo(8, 12.5);
            g.draw(head);
        });
    }

    /** A filled circle in a colour, used for the current colour in the status bar. */
    public static Icon colorDot(Color color) {
        return new DrawnIcon(color, g -> {
            Ellipse2D circle = new Ellipse2D.Double(5, 5, 10, 10);
            g.fill(circle);
            g.setStroke(new BasicStroke(1f));
            g.setColor(new Color(0, 0, 0, 60));
            g.draw(circle);
        });
    }
}
