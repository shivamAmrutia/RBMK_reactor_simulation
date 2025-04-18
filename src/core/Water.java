
package core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Water {
	private int temperature;
    private int x, y;
    
    public Water(int temperature) {
    	this.temperature = temperature; 
    }
    
    public void updateTemperature(int newTemperature) {
    	this.temperature = newTemperature;
    }
    
    public void draw(Graphics g) {
        // Interpolate between #00CCFF (cold) and #FF5050 (hot)
        float ratio = Math.min(temperature / 100f, 1.0f);

        int r = (int)((1 - ratio) * 0 + ratio * 255);
        int gColor = (int)((1 - ratio) * 204 + ratio * 80);
        int b = (int)((1 - ratio) * 255 + ratio * 80);

        Color waterColor = new Color(r, gColor, b);
        g.setColor(waterColor);
        g.fillRect(0, 0, 30, 30);
       
    }
}

