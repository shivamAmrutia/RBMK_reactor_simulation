package core;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Water {
    private float temperature;
    private int x, y;
    private int posX, posY;
    private static final float EVAPORATION_TEMP = 100;

    public Water(int x, int y, int posX, int posY, int temperature) {
        this.x = x;
        this.y = y;
        this.temperature = temperature; // default room temperature
        this.posX = posX;
        this.posY = posY;
    } 

    public void updateTemperature(int updatedTemperature) {
        this.temperature = updatedTemperature;
    }
    
    public boolean isInRange(Neutron n) {
        if (isEvaporated()) return false;        
        if (n.x > posX && n.y > posY && n.x < 30 + posX && n.y < 30 + posY) {
        	return true;
        }
        
        return false;
    }

    public boolean absorbs(Neutron n) {
    	Random random = new Random();
        int prob = random.nextInt(100);
        if(prob < 2) {
        	return true;
        }
        return false;
           	
    }
    
    public boolean isEvaporated() {
        return temperature >= EVAPORATION_TEMP;
    }

    public void coolDown() {
        this.temperature = (float) Math.max(this.temperature - 0.01, 0);
    }
    
    public void heatUp() {
        if (!isEvaporated()) {
            this.temperature +=  3 + (int)(Math.random() * 5); // increase by 1–3 degrees
        }
    }

    public float getTemperature() {
        return temperature;
    }


    public void draw(Graphics g) {
    	
        float ratio = Math.min(this.temperature / 100f, 1.0f);

        int r = (int)((1 - ratio) * 0 + ratio * 255);
        int gColor = (int)((1 - ratio) * 204 + ratio * 80);
        int b = (int)((1 - ratio) * 255 + ratio * 80);
        if(r == 255) {
        	r = 192;
        	gColor = 192;
        	b = 192;
        }
        Color waterColor = new Color(r, gColor, b);
        
        g.setColor(waterColor);
        g.fillRect(x, y, 30, 30);
    }
}
