package drawing;

import java.awt.Color;
import java.util.List;

/**
 * The colours shown as swatches in the toolbar, each with a name for the
 * status bar.
 */
public final class Palette {

    /** One colour in the palette. */
    public record Swatch(String name, Color color) {
    }

    public static final List<Swatch> SWATCHES = List.of(
            new Swatch("Black", Color.BLACK),
            new Swatch("Grey", new Color(0x98A2B3)),
            new Swatch("White", Color.WHITE),
            new Swatch("Red", new Color(0xE5484D)),
            new Swatch("Orange", new Color(0xF76B15)),
            new Swatch("Yellow", new Color(0xFFC53D)),
            new Swatch("Green", new Color(0x30A46C)),
            new Swatch("Teal", new Color(0x12A594)),
            new Swatch("Blue", new Color(0x0090FF)),
            new Swatch("Indigo", new Color(0x3E63DD)),
            new Swatch("Purple", new Color(0x8E4EC6)),
            new Swatch("Pink", new Color(0xD6409F)),
            new Swatch("Brown", new Color(0xA07553)));

    private Palette() {
    }

    /** The palette name of a colour, or its hex code if it is not in the palette. */
    public static String nameOf(Color color) {
        for (Swatch swatch : SWATCHES) {
            if (swatch.color().equals(color)) {
                return swatch.name();
            }
        }
        return String.format("#%06X", color.getRGB() & 0xFFFFFF);
    }
}
