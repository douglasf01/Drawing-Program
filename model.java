
/**
 * Hanterar ritobjekt och dess egenskaper som ska målas. 
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
public class model
{
    private model model;
    private view view;
     
    private int clickedx;
    private int clickedy;
    private int releasedx;
    private int releasedy;
    private int pressx;
    private int pressy;
    private int draggedx;
    private int draggedy;
     
    private boolean isPreview = true;
    private objects previewShape;
    
    private Color currentColor = Color.BLACK;
    private String currentShapeType = "dot";
    
    private List<objects> objectslist  = new ArrayList<>();
    /**
     * Lägger till ett ritobjekt med klasstypen objects.
     */
    public void addShape() {
        if (currentShapeType.equals("dot")) {
            objectslist.add(new objects(clickedx, clickedy, 10, 10, currentColor, currentShapeType)); 
        }
        else{
            int x = Math.min(pressx, draggedx);
            int y = Math.min(pressy, draggedy);
            int width = Math.abs(draggedx - pressx);
            int height = Math.abs(draggedy - pressy);
            
            objectslist.add(new objects(x, y, width, height, currentColor, currentShapeType));
            
        }
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public void setCurrentShape(String type) {
        this.currentShapeType = type;
    }
     
    public void setClicked(int x, int y) {
        this.clickedx = x;
        this.clickedy = y;
    }
    
    public void setPress(int x, int y) {
        this.pressx = x;
        this.pressy = y;
    }
    
    public void setReleased(int x, int y){
        this.releasedx = x;
        this.releasedy = y;
    }

    public void setCurrentDrag(int x, int y) {
        this.draggedx = x;
        this.draggedy = y;
    }
    
    public Color getCurrentColor() {
        return currentColor;
    }

    public int getPressX(){
        return pressx;
    }
    
    public int getPressY(){
        return pressy;
    }
    
    public List<objects> getObjects() {
        return objectslist;
    }
    
    public void clearLastShape() {
        if (objectslist.size() == 0){
            System.out.println("Inga målade objekt");}
        else{
            objectslist.remove(objectslist.size() - 1);
        }
    }
    
    public objects getPreviewShape() {
        return previewShape;
    }
    
    public void setTruePreview(){
        isPreview = true;
    }
    
    public void clearPreview(){
        this.previewShape = null;
    }
    
    public void setPreviewShape(int x, int y, int width, int height, Color color, String type) {
        this.previewShape = new objects(x, y, width, height, color, type);
        this.isPreview = true;
    }

    public String getCurrentShapeType() {
        return currentShapeType;    
    }  
}  

    
    

