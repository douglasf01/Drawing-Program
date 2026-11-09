package drawing;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * The drawing and the pen settings. Holds the shapes, the current tool,
 * colour, stroke width and fill setting, the shape being dragged, and the
 * undo and redo history. It knows nothing about Swing, so it is easy to test.
 */
public class Model {
    /** Rectangles, ovals and lines smaller than this (in pixels) are not added. */
    public static final int MIN_SIZE = 2;

    private List<Shape> shapes = List.of();
    private final Deque<List<Shape>> undoStack = new ArrayDeque<>();
    private final Deque<List<Shape>> redoStack = new ArrayDeque<>();
    private final List<Runnable> listeners = new ArrayList<>();

    private Shape.Kind tool = Shape.Kind.PEN;
    private Color color = Color.BLACK;
    private int stroke = 2;
    private boolean filled;

    private Shape preview;
    private final List<Shape.Point> dragPoints = new ArrayList<>();

    /** Registers code to run after any change to the drawing or the settings. */
    public void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    private void changed() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    // ----- Mouse input -----

    /** Mouse pressed: a dot is added at once, other tools start a preview. */
    public void press(int x, int y) {
        dragPoints.clear();
        if (tool == Shape.Kind.DOT) {
            preview = null;
            commit(Shape.dot(x, y, color, stroke));
            return;
        }
        dragPoints.add(new Shape.Point(x, y));
        preview = build(x, y);
        changed();
    }

    /** Mouse dragged: grow the preview. */
    public void drag(int x, int y) {
        if (preview == null) {
            return;
        }
        if (tool == Shape.Kind.PEN && !new Shape.Point(x, y).equals(preview.end())) {
            dragPoints.add(new Shape.Point(x, y));
        }
        preview = build(x, y);
        changed();
    }

    /** Mouse released: keep the shape if it is big enough. */
    public void release(int x, int y) {
        if (preview == null) {
            return;
        }
        drag(x, y);
        Shape shape = preview;
        preview = null;
        dragPoints.clear();
        if (isBigEnough(shape)) {
            commit(shape);
        } else {
            changed();
        }
    }

    private Shape build(int x, int y) {
        if (tool == Shape.Kind.PEN) {
            return new Shape(tool, dragPoints, color, stroke, false);
        }
        Shape.Point start = dragPoints.get(0);
        return Shape.between(tool, start.x(), start.y(), x, y, color, stroke, filled);
    }

    private static boolean isBigEnough(Shape shape) {
        int w = shape.bounds().width;
        int h = shape.bounds().height;
        return switch (shape.kind()) {
            case RECTANGLE, OVAL -> w >= MIN_SIZE && h >= MIN_SIZE;
            case LINE, PEN -> Math.max(w, h) >= MIN_SIZE;
            case DOT -> true;
        };
    }

    // ----- Editing and history -----

    private void commit(Shape shape) {
        List<Shape> next = new ArrayList<>(shapes);
        next.add(shape);
        replaceShapes(next);
    }

    /** Sets new shapes as one step that can be undone. */
    private void replaceShapes(List<Shape> next) {
        undoStack.push(shapes);
        redoStack.clear();
        shapes = List.copyOf(next);
        changed();
    }

    /** Removes all shapes. This can be undone. */
    public void clear() {
        if (!shapes.isEmpty()) {
            replaceShapes(List.of());
        }
    }

    /** Starts a new, empty drawing and forgets the undo history. */
    public void newDrawing() {
        shapes = List.of();
        undoStack.clear();
        redoStack.clear();
        preview = null;
        dragPoints.clear();
        changed();
    }

    /** Goes back one step, if there is one. */
    public void undo() {
        if (canUndo()) {
            redoStack.push(shapes);
            shapes = undoStack.pop();
            changed();
        }
    }

    /** Repeats the last step that was undone, if there is one. */
    public void redo() {
        if (canRedo()) {
            undoStack.push(shapes);
            shapes = redoStack.pop();
            changed();
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /** The finished shapes, oldest first. The list cannot be changed. */
    public List<Shape> getShapes() {
        return shapes;
    }

    /** The shape being dragged, or null if there is none. */
    public Shape getPreview() {
        return preview;
    }

    // ----- Pen settings -----

    public Shape.Kind getTool() {
        return tool;
    }

    public void setTool(Shape.Kind tool) {
        this.tool = tool;
        changed();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        changed();
    }

    public int getStroke() {
        return stroke;
    }

    public void setStroke(int stroke) {
        if (stroke < 1) {
            throw new IllegalArgumentException("stroke must be at least 1");
        }
        this.stroke = stroke;
        changed();
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
        changed();
    }
}
