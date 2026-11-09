package drawing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;

/**
 * The white canvas. Paints the shapes in the model and the shape being
 * dragged, and can export the drawing as an image.
 */
public final class DrawingPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    /** The colour behind the shapes. */
    public static final Color PAPER = Color.WHITE;

    private final transient Model model;

    public DrawingPanel(Model model) {
        this.model = model;
        setBackground(PAPER);
        setPreferredSize(new Dimension(800, 520));
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            paintShapes(g2, model.getShapes());
            Shape preview = model.getPreview();
            if (preview != null) {
                preview.draw(g2);
            }
        } finally {
            g2.dispose();
        }
    }

    /** Paints shapes with antialiasing, oldest first. */
    public static void paintShapes(Graphics2D g, List<Shape> shapes) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        for (Shape shape : shapes) {
            shape.draw(g);
        }
    }

    /**
     * The finished drawing as an image the size of the canvas, on a white
     * background. The shape being dragged is not included.
     */
    public BufferedImage toImage() {
        return render(model.getShapes(), Math.max(1, getWidth()), Math.max(1, getHeight()));
    }

    /** Paints shapes onto a new white image of the given size. */
    public static BufferedImage render(List<Shape> shapes, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setColor(PAPER);
            g.fillRect(0, 0, width, height);
            paintShapes(g, shapes);
        } finally {
            g.dispose();
        }
        return image;
    }
}
