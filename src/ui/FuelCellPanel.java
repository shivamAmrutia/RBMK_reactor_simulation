package ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import core.FuelCell;
import core.Water;


//// Creates a 30 X 30 size panel for an element (Fuelcell + Water) ////

public class FuelCellPanel extends JPanel {
    private boolean isFissile;
    private int temperature;
    private Water water;
    private FuelCell fuelcell;
    int x, y;
    int posX, posY;
    
    public Water getWater() {
    	return this.water;
    }
    
    public FuelCell getFuelCell() {
    	return this.fuelcell;
    }
    
    public FuelCellPanel(int posX, int posY) {
        setPreferredSize(new Dimension(30, 30));
        int size = Math.min(getWidth(), getHeight()) - 10;
        this.x = (getWidth() - size) / 2; 
        this.y = (getHeight() - size) / 2;
        this.posX = posX;
        this.posY = posY;
    }

    public void setState(boolean isFissile, int temperature) {
        this.isFissile = isFissile; 
        this.temperature = temperature;
        addWater();
        addFuelCell();
        repaint();
    }
    
	public void addWater() {
		 this.water = new Water(0, 0, posX, posY, temperature);
	}
	
	public void addFuelCell() {
		 this.fuelcell = new FuelCell(x, y, posX, posY, isFissile);
	}
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        water.draw(g);
        fuelcell.draw(g);
    }
}