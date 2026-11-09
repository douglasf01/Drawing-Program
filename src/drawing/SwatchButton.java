package drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

/**
 * A small rounded square of colour. A ring is drawn around it when its
 * colour is the current pen colour.
 */
public final class SwatchButton extends JButton {
    private static final long serialVersionUID = 1L;
    private static final int SIZE = 24;

    private final Color color;

    public SwatchButton(Color color, String name) {
        this.color = color;
        setToolTipText(name);
        getAccessibleContext().setAccessibleName(name);
        setFocusable(false);
        setRolloverEnabled(true);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    public Color getColor() {
        return color;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SIZE, SIZE);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = (getWidth() - SIZE) / 2;
            int y = (getHeight() - SIZE) / 2;
            if (isSelected()) {
                g2.setColor(Theme.ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x + 1, y + 1, SIZE - 2, SIZE - 2, 8, 8);
            } else if (getModel().isRollover()) {
                g2.setColor(Theme.BORDER);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x + 1, y + 1, SIZE - 2, SIZE - 2, 8, 8);
            }
            g2.setColor(color);
            g2.fillRoundRect(x + 4, y + 4, SIZE - 8, SIZE - 8, 5, 5);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(0, 0, 0, 40));
            g2.drawRoundRect(x + 4, y + 4, SIZE - 9, SIZE - 9, 5, 5);
        } finally {
            g2.dispose();
        }
    }
}
