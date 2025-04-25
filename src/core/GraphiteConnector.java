package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

public class GraphiteConnector{
	private int x, y, y_new, height, velocity;
	
	public GraphiteConnector(int x, int y, int height) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.y_new = y;
		this.velocity = 2;
	}
	
	public void setYPos(int y_new) {
		this.y_new = y_new;
	}

	public void update() {
		 if (y < y_new) {
	            y = Math.min(y + velocity, y_new);
	        }
	        if (y > y_new) {
	            y = Math.max(y - velocity, y_new);
	        }		
	}
	
	public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);       
    	g.fillRect(x, y, 2, height);
        
    }
	
}