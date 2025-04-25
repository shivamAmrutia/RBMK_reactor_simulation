package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

public class GraphiteConnector{
	private int x, y, height;
	
	public GraphiteConnector(int x, int y, int height) {
		this.x = x;
		this.y = y;
		this.height = height;
	}
	
	public void draw(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);       
    	g.fillRect(x, y, 2, height);
        
    }
}