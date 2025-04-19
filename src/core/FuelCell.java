package core;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class FuelCell {
    private boolean isFissile;
    private int x, y;

    private static final Random rand = new Random();

    public FuelCell(int x, int y, boolean isFissile) {
        this.isFissile = isFissile;
        this.x = x;
        this.y = y;
    }

    public void updateFissile() {
        this.isFissile = !isFissile;
    }

    // Determines if this fuel cell absorbs the neutron
    public boolean absorbs(Neutron n) {
        int dx = Math.abs(n.x - x);
        int dy = Math.abs(n.y - y);
        if (dx < 10 && dy < 10 && isFissile) {
            return Math.random() < 0.6; // 60% chance of fission
        }
        return false;
    }

    // Emits a new neutron as a result of fission
    public Neutron emitNeutron() {
        return new Neutron(x, y);
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void draw(Graphics g) {
        g.setColor(isFissile ? Color.BLUE : Color.GRAY);
        g.fillOval(x, y, 20, 20);
    }
}
