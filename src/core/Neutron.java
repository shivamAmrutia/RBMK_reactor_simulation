package core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Neutron {
    public int x;
	public int y;
    public int dx, dy;
    Random rand = new Random();

    public Neutron(int x, int y) {
        this.x = x;
        this.y = y;
        do {
            this.dx = rand.nextInt(1, 8);
            this.dy = rand.nextInt(1, 8);
       } while (Math.abs(dx) < 2 && Math.abs(dy) < 2);
       
    }

    public boolean move(int width, int height) {
        x += dx;
        y += dy;
          
        return (x >= 0 && x <= width && y >= 0 && y <= height); 
    }
    
    public void draw(Graphics2D g) {    	
    	if(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) > 5) g.setColor(Color.BLACK);
    	else g.setColor(Color.WHITE);
    	g.fillOval(x, y, 6, 6);
    }    
}
