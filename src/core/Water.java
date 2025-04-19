package core;

import java.awt.Color;
import java.awt.Graphics;

public class Water {
    private int temperature;
    private int x, y;
    private static final int EVAPORATION_TEMP = 100;

    public Water(int x, int y, int temperature) {
        this.x = x;
        this.y = y;
        this.temperature = temperature; // default room temperature
    }

    public void updateTemperature(int updatedTemperature) {
        this.temperature = updatedTemperature;
    }
    
    public boolean absorbs(Neutron n) {
        if (isEvaporated()) return false;

        int dx = Math.abs(n.x - x);
        int dy = Math.abs(n.y - y);
        if (dx < 10 && dy < 10 && Math.random() < 0.4) { // 40% absorption chance
            this.updateTemperature(this.temperature + 5); // heats up on absorption
            return true;
        }
        return false;
    }

    
    public boolean isEvaporated() {
        return temperature >= EVAPORATION_TEMP;
    }

    public void coolDown() {
        if (this.temperature > 20) this.updateTemperature(this.temperature - 1);;
    }
    
    public void heatUp() {
        if (!isEvaporated() && Math.random() < 0.3) { // 30% chance to heat up slightly
            int updatedTemperature = this.temperature +  1 + (int)(Math.random() * 3); // increase by 1–3 degrees
            this.updateTemperature(updatedTemperature);
        }
    }

    public int getTemperature() {
        return temperature;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void draw(Graphics g) {
        float ratio = Math.min(temperature / 100f, 1.0f);

        int r = (int)((1 - ratio) * 0 + ratio * 255);
        int gColor = (int)((1 - ratio) * 204 + ratio * 80);
        int b = (int)((1 - ratio) * 255 + ratio * 80);

        Color waterColor = new Color(r, gColor, b);
        g.setColor(waterColor);
        g.fillRect(x, y, 30, 30);
    }
}
