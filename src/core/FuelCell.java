package core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class FuelCell {
	private boolean isFissile;
    private int x, y;
    
    public FuelCell(int x, int y, boolean isFissile) {
    	this.isFissile = isFissile; 
        this.x = x;
        this.y = y;
    }
    
    public void updateFissile() {
    	this.isFissile = !isFissile;
    }

    public void draw(Graphics g) {
        g.setColor(isFissile ? Color.BLUE : Color.GRAY);
        g.fillOval(x, y, 20, 20);       
    }
}

