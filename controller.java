
/**
 * Klass som hanterar mus inmatningar.
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

class controller implements ActionListener, MouseListener, MouseMotionListener {
    
    private model model;
    private view view;
    private boolean isMousePressed = false;

    public controller(model mymodel, view myview){
        this.model = mymodel;
        this.view = myview;
    }
    /**
     * Hanterar händelser när knappar trycks. Uppdaterar läget och färgen som är aktiva.
     */
    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand(); {
            if (command.equals("button1")) {
                model.setCurrentColor(Color.BLACK);
                view.updateLabel("Läge: " + model.getCurrentShapeType() + " Färg: " + model.getCurrentColor());
            }
            
            else if (command.equals("button2")) {
                model.setCurrentColor(Color.BLUE);
                view.updateLabel("Läge: " + model.getCurrentShapeType() + " Färg: " + model.getCurrentColor());
            }
            
            else if (command.equals("button3")) {
            model.setCurrentColor(Color.RED);
            view.updateLabel("Läge: " + model.getCurrentShapeType() + " Färg: " + model.getCurrentColor());
            }
            
            else if (command.equals("button4")) {
            model.setCurrentShape("rectangle");
            view.updateLabel("Läge: " + model.getCurrentShapeType() + " Färg: " + model.getCurrentColor());
            }
            
            else if (command.equals("button5")) {
            model.setCurrentShape("oval");
            view.updateLabel("Läge: " + model.getCurrentShapeType() + " Färg: " + model.getCurrentColor());
            }
            
            else if (command.equals("button6")) {
            model.setCurrentShape("dot");
            view.updateLabel("Läge: " + model.getCurrentShapeType() + " Färg: " + model.getCurrentColor());
            }
            
            else if (command.equals("button7")) {
                model.clearLastShape();
            }
            view.repaint();
        }
    }
    
    /**
     * Pricken är den objektet som ska målas direkt vid klick.  
     */
    public void mouseClicked(MouseEvent e) {
        if (model.getCurrentShapeType().equals("dot")) {
            model.setClicked(e.getX(), e.getY());
            model.addShape();
        }
        view.repaint();
    }
    
    /**
     * Vid nedtryckning skapas ett förhandsvisat ritningsobjekt, antingen rektangel eller oval.
     */
    public void mousePressed(MouseEvent e) {
        isMousePressed = true;
        if (model.getCurrentShapeType().equals("rectangle") || model.getCurrentShapeType().equals("oval") ) {
            
            model.setPress(e.getX(),e.getY());
            model.setPreviewShape(e.getX(), e.getY(), 1, 1, model.getCurrentColor(),model.getCurrentShapeType());
            

        }
    }
    /**
     * När musen dras så målas ett förhandsvisade objektet och justeras utifrån musrörelserna.
     */
    public void mouseDragged(MouseEvent e) {
        if (model.getCurrentShapeType().equals("rectangle") || model.getCurrentShapeType().equals("oval")) {

            model.setCurrentDrag(e.getX(), e.getY());
                         
            int x = Math.min(model.getPressX(), e.getX());
            int y = Math.min(model.getPressY(), e.getY());
            int width = Math.abs(e.getX() - model.getPressX());
            int height = Math.abs(e.getY() - model.getPressY());
            
            model.setPreviewShape(x, y, width, height, model.getCurrentColor(), model.getCurrentShapeType());
            
            view.repaint();
            
            
        }
    }
    /**
     * När musen inte är nedtryckt längre, skapar vi ett fast ritobjekt, som är det sista förhandsvisade objektet. 
     */
    public void mouseReleased(MouseEvent e) {
        isMousePressed = false;
        if (model.getCurrentShapeType().equals("rectangle") || model.getCurrentShapeType().equals("oval")) {
            model.setReleased(e.getX(),e.getY());
            model.addShape();
            model.clearPreview();
            view.repaint();

        } 
    }
        
    public boolean getMousePressed() {
        return isMousePressed;
    }
        
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}

