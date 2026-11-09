package drawing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Starts the drawing program in a window.
 */
public class App {
    public static void main(String[] args) {
        // Smooth text on Linux, where it is not always on by default.
        System.setProperty("awt.useSystemAAFontSettings", "on");
        SwingUtilities.invokeLater(() -> {
            Theme.install();
            View view = new View(new Model());
            JFrame frame = new JFrame("Drawing Program");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setJMenuBar(view.getMenuBar());
            frame.setContentPane(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
