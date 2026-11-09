package drawing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 * Passes mouse input on the canvas to the model, and tells the view where
 * the cursor is. The model tells the view to repaint when it changes.
 */
public class Controller extends MouseAdapter {
    private final Model model;
    private final View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            model.press(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            model.drag(e.getX(), e.getY());
        }
        view.showCursor(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            model.release(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        view.showCursor(e.getX(), e.getY());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        view.hideCursor();
    }
}
