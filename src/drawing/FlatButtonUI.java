package drawing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * A flat toolbar button: no border, a soft grey background on hover and a
 * light blue background when selected (for toggle buttons).
 */
public final class FlatButtonUI extends BasicButtonUI {
    private static final int ARC = 8;

    /** Gives a button the flat style. Works for plain and toggle buttons. */
    public static <T extends AbstractButton> T style(T button) {
        button.setUI(new FlatButtonUI());
        button.setFocusable(false);
        button.setRolloverEnabled(true);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(Theme.TEXT);
        button.setFont(Theme.FONT);
        button.setIconTextGap(6);
        String text = button.getText();
        int side = text == null || text.isEmpty() ? 6 : 10;
        button.setBorder(BorderFactory.createEmptyBorder(6, side, 6, side));
        return button;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension size = super.getPreferredSize(c);
        // Keep icon-only buttons square.
        size.width = Math.max(size.width, size.height);
        return size;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();
        Color fill = null;
        if (model.isSelected()) {
            fill = Theme.ACCENT_LIGHT;
        } else if (model.isPressed() && model.isArmed()) {
            fill = Theme.BORDER;
        } else if (model.isRollover() && button.isEnabled()) {
            fill = Theme.HOVER;
        }
        if (fill != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), ARC, ARC);
            g2.dispose();
        }
        super.paint(g, c);
    }

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        // The background is already painted in paint().
    }
}
