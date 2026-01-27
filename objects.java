import java.awt.Color;

/**
 * Klass som hanterar egenskaper hos rit objekt. 
 */
public class objects
{
    private int x;
    private int y;
    private Color color;
    private String type; 
    private int width;
    private int height;
    
    public objects(int x, int y, int width, int height, Color color, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.type = type;
    }
    

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Color getColor() {
        return this.color;
    }

    public String getType() {
        return this.type;
    }
    
    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
    
}