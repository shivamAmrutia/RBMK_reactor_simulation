package core;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Water {
    private float temperature;
    private int x, y;
    private int posX, posY;
    private static final float EVAPORATION_TEMP = 100;
    private float coolingRate = 0.1f; // default value


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
//        if (isEvaporated()) return false;        
        if (n.x > posX && n.y > posY && n.x < 30 + posX && n.y < 30 + posY) {
        	return true;
        }
        
        return false;
    }

    public boolean absorbs(Neutron n) {
    	if(isEvaporated()) return false;
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
        this.temperature = (float) Math.max(this.temperature - coolingRate, 0);
    }
    
    public void heatUp() {
//        if (!isEvaporated()) {
            this.temperature = (float) Math.min(this.temperature + Math.random() * 3, 110); // increase by 1–3 degrees
//        }
    }
    
    public void setCoolingRate(float rate) {
        this.coolingRate = rate;
    }


    public float getTemperature() {
        return temperature;
    }
    
    public static float getAverageTemperature(ui.FuelCellPanel[][] grid) {
        float total = 0;
        int count = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Water w = grid[i][j].getWater();
                if (w != null) {
                    total += w.getTemperature();
                    count++;
                }
            }
        }

        return count > 0 ? total / count : 0;
    }
    
    public static float calculatePowerOutput(ui.FuelCellPanel[][] grid) {
        int steamCells = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Water w = grid[i][j].getWater();
                if (w != null && w.isEvaporated()) {
                    steamCells++;
                }
            }
        }

        // Calibrate: 50 steam cells → ~1500 MW → 30 MW per steam cell
        float powerPerSteamCell = 30f;

        return steamCells * powerPerSteamCell;
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
