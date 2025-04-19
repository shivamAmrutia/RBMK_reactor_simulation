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
    
    public void trySlowDown(Neutron n) {
        if (Math.abs(n.x - x) <= 6) {
            Random rand = new Random();
            boolean moveRight = n.dx <= 0;
            
            n.dx = -n.dx;

            if (Math.sqrt(n.dx * n.dx + n.dy * n.dy) > 5) {
                int dx, dy;
                do {
                    dx = rand.nextInt(8) - 4;
                    dy = rand.nextInt(8) - 4;
                } while (dx == 0 && dy == 0);

                n.dx = moveRight ? Math.abs(dx) : -Math.abs(dx);
                n.dy = dy;

                if (n.dx > 0) {
                    n.x = Math.max(x + 10, n.x + n.dx);
                } else {
                    n.x = Math.min(x - 10, n.x + n.dx);
                }
            }
        }
    }

    
    public void draw(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.setStroke(new BasicStroke(2));
        g.drawRect(x, 0, 4, height);       
    }
}
