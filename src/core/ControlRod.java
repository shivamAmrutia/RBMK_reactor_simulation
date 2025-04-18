package core;

import java.awt.Color;
import java.awt.Graphics2D;
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
        this.currentHeight = y;
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
    
    public void draw(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        if(y > 0) {
        	g.fillRect(x, y - currentHeight, 4, currentHeight);
        }
        else {
        	g.fillRect(x, 0, 4, currentHeight);
        }
    }
}
