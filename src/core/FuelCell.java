package core;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class FuelCell {
    private boolean isFissile;
    private boolean isXenon = false;
    private int x, y;
    private int posX, posY;
    private static final Random rand = new Random();

    public FuelCell(int x, int y, int posX, int posY, boolean isFissile) {
        this.isFissile = isFissile;
        this.x = x;
        this.y = y;
        this.posX = posX;
        this.posY = posY;
    }

    public void updateFissile() { 
        this.isFissile = !isFissile;
    }
    
    // Determines if neutron is in range of fuelcell

    public boolean isInRange(Neutron n) {
    	int abs_x = this.posX + this.x;
        int abs_y = this.posY + this.y;
        if (n.x > abs_x && n.y > abs_y && n.x < 20 + abs_x && n.y < 20 + abs_y) {
        	return true;
        }        
        return false;    	
    }
    
    // Determines if this fuel cell absorbs the neutron
    public boolean absorbs(Neutron n) {
        if (isInRange(n) && this.isFissile) {        	
            int prob = rand.nextInt(9);
            if(prob < 6) {// 60% fission chance
            	return true;
            }
            else {
            	return false;
            }
        }
        return false;
    }
        
    public boolean getXenon() {
		return isXenon;
	}
    
    public boolean getFissile() {
		return isFissile;
	}
    
    public void updateXenon() {
		this.isXenon = false;
	}
    
    public void makeXenon() {
    	if(!this.isFissile) {
    		isXenon = true;
    		isFissile = true;
    	} 	
    }

    // Emits a new neutron as a result of fission
    public Neutron emitNeutron() {
        return new Neutron(this.posX + this.x, this.posY + this.y);
    }


    public void draw(Graphics g) {
    	if(this.isXenon) {
    		g.setColor(Color.BLACK);
    	}
    	else {
    		g.setColor(isFissile ? Color.BLUE : Color.GRAY);
    	}        
        g.fillOval(x, y, 20, 20);
    }
	

}
