
/**
 * Klass som hanterar ritningen av objekt. 
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class view extends JFrame{    
    
    private model mymodel;
    private controller mycontroller;
    private JLabel text;
    /**
     * Initerar ritytan.
     * 
     * @param mymodel klassobjekt av typen model
     */
    public view(model mymodel) {
        this.mymodel = mymodel;
        this.mycontroller = new controller(mymodel, this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setSize(600,600);
        setLocation(300,300);
        viewpanel panel = new viewpanel();
                
        panel.addMouseListener(mycontroller);
        panel.addMouseMotionListener(mycontroller);
        
        add(panel);
        setTitle("Ritverktyg");
        
        JPanel top_buttons = new JPanel();
        
        JButton button1 = new JButton("Svart"); 
        button1.setActionCommand("button1");
        button1.addActionListener(mycontroller);

        JButton button2 = new JButton("Blå");
        button2.setActionCommand("button2");
        button2.addActionListener(mycontroller);

        JButton button3 = new JButton("Röd");
        button3.setActionCommand("button3");
        button3.addActionListener(mycontroller);

        JButton button4 = new JButton("Rektangel");
        button4.setActionCommand("button4");
        button4.addActionListener(mycontroller);
        
        JButton button5 = new JButton("Oval");
        button5.setActionCommand("button5");
        button5.addActionListener(mycontroller);

        JButton button6 = new JButton("Prick");
        button6.setActionCommand("button6");
        button6.addActionListener(mycontroller);

        JButton button7 = new JButton("Ångra");
        button7.setActionCommand("button7");
        button7.addActionListener(mycontroller);

        top_buttons.add(button1);
        top_buttons.add(button2);
        top_buttons.add(button3);
        top_buttons.add(button4);
        top_buttons.add(button5);
        top_buttons.add(button6);
        top_buttons.add(button7);
        
        add(top_buttons, BorderLayout.NORTH);
        
        JPanel info = new JPanel();
        text =new JLabel("Läge: " + mymodel.getCurrentShapeType() + " Färg: " + mymodel.getCurrentColor());
        info.add(text);
        add(info, BorderLayout.SOUTH);
        
        setVisible(true);
        panel.repaint();
    }
    
    public String mode(){
        return mymodel.getCurrentShapeType();
    }
        
    public Color color(){
        return mymodel.getCurrentColor();
    }
    
    public void updateLabel(String newText) {
        text.setText(newText);
    }
    
    /**
     * Klass som ritar objekten i model och förhandsvisningsobjekten.
     */
    private class viewpanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            List<objects> shapes = mymodel.getObjects();
            
            for (objects shape : shapes) {
                g.setColor(shape.getColor());
                int x = shape.getX();
                int y = shape.getY();
                int width = shape.getWidth();
                int height = shape.getHeight();
                String type = shape.getType();
        
                if (type.equals("dot")) {
                    g.fillOval(x, y, 10, 10);
                } else if (type.equals("rectangle")) {
                    g.fillRect(x, y, width, height);
                } else if (type.equals("oval")) {
                    g.fillOval(x, y, width, height);
                }
            }
            
            if (mycontroller.getMousePressed()) {
                objects pshape = mymodel.getPreviewShape();
                g.setColor(pshape.getColor());
        
                int x = pshape.getX();
                int y = pshape.getY();
                int width = pshape.getWidth();
                int height = pshape.getHeight();
                
                if(pshape.getType().equals("rectangle")){
                    g.fillRect(x,y,width,height);
                }
                if(pshape.getType().equals("oval")){
                    g.fillOval(x,y,width,height);
                }
            }
        }
    }
}




                
            
    


    
