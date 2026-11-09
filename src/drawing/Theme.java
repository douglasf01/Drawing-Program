package drawing;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * The colours and fonts of the program, and a light, flat look and feel
 * built on Swing's Metal look and feel.
 */
public final class Theme {
    public static final Color BACKGROUND = new Color(0xF6F7F9);
    public static final Color SURFACE = Color.WHITE;
    public static final Color WORKSPACE = new Color(0xE4E7EC);
    public static final Color BORDER = new Color(0xD0D5DD);
    public static final Color TEXT = new Color(0x1D2939);
    public static final Color TEXT_MUTED = new Color(0x667085);
    public static final Color ICON = new Color(0x344054);
    public static final Color ICON_DISABLED = new Color(0xB0B7C3);
    public static final Color HOVER = new Color(0xE9ECF1);
    public static final Color ACCENT = new Color(0x2E6BE6);
    public static final Color ACCENT_LIGHT = new Color(0xDCE7FC);

    public static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font FONT_SMALL = FONT.deriveFont(12f);

    private Theme() {
    }

    /** Sets the look and feel. Call this before any component is made. */
    public static void install() {
        MetalLookAndFeel.setCurrentTheme(new FlatMetalTheme());
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            return; // Keep the default look and feel.
        }
        UIManager.put("MenuBar.border", BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        UIManager.put("MenuBar.background", SURFACE);
        UIManager.put("Menu.background", SURFACE);
        UIManager.put("MenuItem.background", SURFACE);
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(BORDER));
        UIManager.put("Menu.border", BorderFactory.createEmptyBorder(4, 8, 4, 8));
        UIManager.put("MenuItem.border", BorderFactory.createEmptyBorder(5, 6, 5, 12));
        UIManager.put("MenuItem.acceleratorForeground", TEXT_MUTED);
        UIManager.put("MenuItem.acceleratorFont", FONT_SMALL);
        UIManager.put("ToolTip.background", TEXT);
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(4, 8, 4, 8));
        UIManager.put("ToolBar.border", BorderFactory.createEmptyBorder());
        UIManager.put("Separator.foreground", BORDER);
        UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
        UIManager.put("ToggleButton.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
    }

    /** A Metal theme with plain fonts, soft greys and a blue accent. */
    private static final class FlatMetalTheme extends DefaultMetalTheme {
        private static final FontUIResource PLAIN = new FontUIResource(FONT);
        private static final FontUIResource SMALL = new FontUIResource(FONT_SMALL);

        @Override
        public String getName() {
            return "Flat";
        }

        @Override
        public FontUIResource getControlTextFont() {
            return PLAIN;
        }

        @Override
        public FontUIResource getMenuTextFont() {
            return PLAIN;
        }

        @Override
        public FontUIResource getSystemTextFont() {
            return PLAIN;
        }

        @Override
        public FontUIResource getUserTextFont() {
            return PLAIN;
        }

        @Override
        public FontUIResource getWindowTitleFont() {
            return PLAIN;
        }

        @Override
        public FontUIResource getSubTextFont() {
            return SMALL;
        }

        @Override
        protected ColorUIResource getPrimary1() {
            return new ColorUIResource(ACCENT);
        }

        @Override
        protected ColorUIResource getPrimary2() {
            return new ColorUIResource(ACCENT_LIGHT);
        }

        @Override
        protected ColorUIResource getPrimary3() {
            return new ColorUIResource(ACCENT_LIGHT);
        }

        @Override
        protected ColorUIResource getSecondary1() {
            return new ColorUIResource(BORDER);
        }

        @Override
        protected ColorUIResource getSecondary2() {
            return new ColorUIResource(BORDER);
        }

        @Override
        protected ColorUIResource getSecondary3() {
            return new ColorUIResource(BACKGROUND);
        }

        @Override
        public ColorUIResource getMenuSelectedForeground() {
            return new ColorUIResource(TEXT);
        }

        @Override
        public ColorUIResource getControlTextColor() {
            return new ColorUIResource(TEXT);
        }

        @Override
        public ColorUIResource getMenuForeground() {
            return new ColorUIResource(TEXT);
        }

        @Override
        public ColorUIResource getSystemTextColor() {
            return new ColorUIResource(TEXT);
        }

        @Override
        public ColorUIResource getUserTextColor() {
            return new ColorUIResource(TEXT);
        }
    }
}
