package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ControlRod {
    public int x; 
    public int y;
    public int velocity;
    public int currentHeight;
    public int targetHeight; 
    
    Random rand = new Random();
    public ControlRod(int x, int y) {
        this.x = x;
        this.y = y;
        this.velocity = 2;
        this.currentHeight = 0;
    }

    public void setTargetHeight(int height) {
        this.targetHeight = height;
    }
    
    public void update() {
        if (currentHeight < targetHeight) {
            currentHeight = Math.min(currentHeight + velocity, targetHeight);
        }
        if (currentHeight > targetHeight) {
            currentHeight = Math.max(currentHeight - velocity, targetHeight);
        }
        
    } 
    
    public void absorbNearbyNeutrons(List<Neutron> neutrons) {
        Iterator<Neutron> it = neutrons.iterator();
        while (it.hasNext()) {
            Neutron n = it.next();
            
            if (currentHeight != 0) { 
                boolean withinXRange = n.x - x <= 7 && x - n.x <= 3;
                boolean withinYRange = y == 0 && n.y <= currentHeight;
                
                if (withinXRange && withinYRange) {
                    it.remove();  // absorb the neutron
                }       
            }
        }
    }

    
    public void draw(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);       
    	g.fillRect(x, 0, 4, currentHeight);
        
    }
}
