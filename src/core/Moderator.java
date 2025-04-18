package core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Moderator {
    public int x; 
    public int height;
    
    public Moderator(int x, int height) {
        this.x = x;
        this.height = height;
    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.setStroke(new BasicStroke(2));
        g.drawRect(x, 0, 4, height);       
    }
}
