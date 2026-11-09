package drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Plain tests without JUnit. Run with: ./build.sh test
 */
public class Tests {
    public static void main(String[] args) {
        dragCreatesRectangle();
        dragUpAndLeftCreatesOval();
        tinyDragAddsNothing();
        dotIsCentredAndSizedByStroke();
        lineKeepsDirection();
        freehandKeepsEveryPoint();
        strokeAndFillAreStoredPerShape();
        onlyRectanglesAndOvalsCanBeFilled();
        undoAndRedo();
        newShapeClearsRedo();
        clearCanBeUndone();
        newDrawingForgetsHistory();
        listenersHearChanges();
        paletteNames();
        canvasPaintsWithoutErrors();
        exportGivesImageOfTheDrawing();
        viewBuildsWithoutAWindow();
        System.out.println("All tests passed.");
    }

    static Model model(Shape.Kind tool, Color color, int stroke, boolean filled) {
        Model m = new Model();
        m.setTool(tool);
        m.setColor(color);
        m.setStroke(stroke);
        m.setFilled(filled);
        return m;
    }

    static void dragCreatesRectangle() {
        Model m = model(Shape.Kind.RECTANGLE, Color.RED, 2, true);
        m.press(10, 20);
        m.drag(30, 40);
        m.release(50, 70);
        check(m.getShapes().size() == 1, "one shape added");
        check(m.getShapes().get(0).equals(Shape.between(Shape.Kind.RECTANGLE, 10, 20, 50, 70, Color.RED, 2, true)),
                "rectangle uses the press and release points");
        check(m.getShapes().get(0).bounds().equals(new Rectangle(10, 20, 40, 50)), "rectangle bounds");
        check(m.getPreview() == null, "preview cleared after release");
    }

    static void dragUpAndLeftCreatesOval() {
        Model m = model(Shape.Kind.OVAL, Color.BLACK, 2, false);
        m.press(100, 100);
        m.drag(60, 70);
        check(m.getPreview().bounds().equals(new Rectangle(60, 70, 40, 30)), "preview follows the drag");
        check(m.getShapes().isEmpty(), "preview is not a shape yet");
        m.release(60, 70);
        check(m.getShapes().get(0).bounds().equals(new Rectangle(60, 70, 40, 30)),
                "drag up and left gives a positive size");
    }

    static void tinyDragAddsNothing() {
        Model m = model(Shape.Kind.RECTANGLE, Color.BLACK, 2, false);
        m.press(5, 5);
        m.release(5, 5);
        m.press(5, 5);
        m.release(40, 6);
        check(m.getShapes().isEmpty(), "a click or a flat rectangle adds nothing");
        m.release(300, 300);
        check(m.getShapes().isEmpty(), "release without press does nothing");
        check(!m.canUndo(), "nothing to undo after rejected shapes");

        m.setTool(Shape.Kind.LINE);
        m.press(5, 5);
        m.release(40, 5);
        check(m.getShapes().size() == 1, "a flat line is allowed");
    }

    static void dotIsCentredAndSizedByStroke() {
        Model m = model(Shape.Kind.DOT, Color.BLUE, 4, false);
        m.press(50, 60);
        Shape dot = m.getShapes().get(0);
        Rectangle b = dot.bounds();
        check(dot.kind() == Shape.Kind.DOT, "dot tool adds a dot on press");
        check(b.width == Shape.dotSize(4) && b.height == b.width, "dot size follows the stroke");
        check(b.x + b.width / 2 == 50 && b.y + b.height / 2 == 60, "dot is centred on the click");
        check(m.getPreview() == null, "dot has no preview");
        m.release(50, 60);
        check(m.getShapes().size() == 1, "release after a dot adds nothing more");
    }

    static void lineKeepsDirection() {
        Model m = model(Shape.Kind.LINE, Color.BLACK, 3, false);
        m.press(100, 10);
        m.drag(50, 50);
        m.release(20, 90);
        Shape line = m.getShapes().get(0);
        check(line.kind() == Shape.Kind.LINE, "line tool adds a line");
        check(line.start().equals(new Shape.Point(100, 10)) && line.end().equals(new Shape.Point(20, 90)),
                "line goes from the press point to the release point");
    }

    static void freehandKeepsEveryPoint() {
        Model m = model(Shape.Kind.PEN, Color.BLACK, 2, false);
        m.press(0, 0);
        m.drag(5, 2);
        m.drag(5, 2);
        m.drag(10, 8);
        check(m.getPreview().points().size() == 3, "preview shows the path so far, without repeated points");
        m.release(12, 20);
        Shape path = m.getShapes().get(0);
        check(path.kind() == Shape.Kind.PEN, "pen tool adds a freehand path");
        check(path.points().equals(List.of(new Shape.Point(0, 0), new Shape.Point(5, 2),
                new Shape.Point(10, 8), new Shape.Point(12, 20))), "path keeps every point in order");
        check(path.bounds().equals(new Rectangle(0, 0, 12, 20)), "path bounds cover all points");

        m.press(30, 30);
        m.release(30, 30);
        check(m.getShapes().size() == 1, "a pen click without movement adds nothing");
    }

    static void strokeAndFillAreStoredPerShape() {
        Model m = model(Shape.Kind.RECTANGLE, Color.RED, 8, true);
        m.press(0, 0);
        m.release(20, 20);
        m.setColor(Color.BLUE);
        m.setStroke(1);
        m.setFilled(false);
        m.press(30, 30);
        m.release(60, 60);
        Shape first = m.getShapes().get(0);
        Shape second = m.getShapes().get(1);
        check(first.stroke() == 8 && first.filled() && first.color().equals(Color.RED),
                "first shape keeps its own stroke, fill and colour");
        check(second.stroke() == 1 && !second.filled() && second.color().equals(Color.BLUE),
                "second shape uses the new settings");
    }

    static void onlyRectanglesAndOvalsCanBeFilled() {
        Model m = model(Shape.Kind.LINE, Color.BLACK, 2, true);
        m.press(0, 0);
        m.release(20, 20);
        check(!m.getShapes().get(0).filled(), "a line is never filled");
        check(Shape.Kind.OVAL.canFill() && !Shape.Kind.PEN.canFill(), "only some kinds can be filled");
        boolean thrown = false;
        try {
            m.setStroke(0);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        check(thrown && m.getStroke() == 2, "a stroke below 1 is refused");
    }

    static Model withDots(int count) {
        Model m = model(Shape.Kind.DOT, Color.BLACK, 2, false);
        for (int i = 0; i < count; i++) {
            m.press(10 * (i + 1), 10);
        }
        return m;
    }

    static void undoAndRedo() {
        Model m = withDots(2);
        m.undo();
        check(m.getShapes().size() == 1 && m.getShapes().get(0).start().x() == 10, "undo removes the last shape");
        check(m.canRedo(), "redo is possible after undo");
        m.redo();
        check(m.getShapes().size() == 2 && m.getShapes().get(1).start().x() == 20, "redo brings the shape back");
        check(!m.canRedo(), "nothing left to redo");
        m.undo();
        m.undo();
        m.undo();
        check(m.getShapes().isEmpty() && !m.canUndo(), "undo on an empty drawing is safe");
        m.redo();
        m.redo();
        m.redo();
        check(m.getShapes().size() == 2, "redo replays every undone step");
    }

    static void newShapeClearsRedo() {
        Model m = withDots(2);
        m.undo();
        m.press(99, 99);
        check(!m.canRedo(), "a new shape clears the redo history");
        check(m.getShapes().size() == 2, "new shape is added after the undo");
    }

    static void clearCanBeUndone() {
        Model m = withDots(3);
        m.clear();
        check(m.getShapes().isEmpty(), "clear removes every shape");
        m.undo();
        check(m.getShapes().size() == 3, "undo after clear brings all shapes back");
        m.redo();
        check(m.getShapes().isEmpty(), "redo clears again");
        int steps = 0;
        while (m.canUndo()) {
            m.undo();
            steps++;
        }
        check(steps == 4, "clear is one undo step");
        Model empty = new Model();
        empty.clear();
        check(!empty.canUndo(), "clearing an empty drawing adds no undo step");
    }

    static void newDrawingForgetsHistory() {
        Model m = withDots(2);
        m.undo();
        m.newDrawing();
        check(m.getShapes().isEmpty() && !m.canUndo() && !m.canRedo(), "new drawing is empty with no history");
    }

    static void listenersHearChanges() {
        Model m = new Model();
        int[] calls = {0};
        m.addChangeListener(() -> calls[0]++);
        m.setTool(Shape.Kind.DOT);
        m.press(5, 5);
        m.undo();
        check(calls[0] == 3, "listeners run after settings and drawing changes");
    }

    static void paletteNames() {
        check(Palette.nameOf(Color.BLACK).equals("Black"), "palette colour has its name");
        check(Palette.nameOf(new Color(0x123456)).equals("#123456"), "other colours show a hex code");
    }

    static void canvasPaintsWithoutErrors() {
        Model m = model(Shape.Kind.DOT, Color.BLACK, 2, false);
        m.press(20, 20);
        m.setTool(Shape.Kind.RECTANGLE);
        m.setColor(Color.BLUE);
        m.setFilled(true);
        m.press(30, 30);
        m.release(80, 90);
        m.setTool(Shape.Kind.OVAL);
        m.setColor(Color.RED);
        m.press(100, 100);
        m.drag(150, 140);

        DrawingPanel canvas = new DrawingPanel(m);
        canvas.setSize(200, 200);
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            canvas.paint(g);
        } finally {
            g.dispose();
        }
        check(image.getRGB(20, 20) == Color.BLACK.getRGB(), "dot is painted");
        check(image.getRGB(50, 50) == Color.BLUE.getRGB(), "filled rectangle is painted");
        check(image.getRGB(125, 120) == Color.RED.getRGB(), "preview oval is painted while dragging");
        check(image.getRGB(190, 10) == Color.WHITE.getRGB(), "background is white");
    }

    static void exportGivesImageOfTheDrawing() {
        Model m = model(Shape.Kind.RECTANGLE, Color.BLUE, 4, false);
        m.press(20, 20);
        m.release(100, 80);
        m.setTool(Shape.Kind.OVAL);
        m.press(120, 120);
        m.drag(180, 180);

        DrawingPanel canvas = new DrawingPanel(m);
        canvas.setSize(300, 200);
        BufferedImage image = canvas.toImage();
        check(image.getWidth() == 300 && image.getHeight() == 200, "export has the canvas size");
        check(image.getRGB(20, 50) == Color.BLUE.getRGB(), "outline is in the export");
        check(image.getRGB(60, 50) == Color.WHITE.getRGB(), "outlined rectangle is empty inside");
        check(image.getRGB(150, 120) == Color.WHITE.getRGB(), "shape being dragged is not exported");
    }

    static void viewBuildsWithoutAWindow() {
        Model m = new Model();
        View view = new View(m);
        check(view.getMenuBar().getMenuCount() == 2, "menu bar has File and Edit");
        m.setTool(Shape.Kind.DOT);
        m.press(10, 10);
        check(view.getCanvas() != null, "view builds and follows the model without a window");
    }

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        System.out.println("ok: " + message);
    }
}
