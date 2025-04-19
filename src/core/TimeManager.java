package core;

import java.util.ArrayList;

import ui.FuelCellPanel;
import ui.InteractivePanel;

public class TimeManager implements Runnable {
	private final InteractivePanel panel;
	private FuelCellPanel[][] fuelCellPanel;
    private Thread thread;
    private boolean running = false;
    private final int TICK_RATE_MS = 40;

    public TimeManager(InteractivePanel panel, FuelCellPanel[][] fuelCellPanel) {
        this.panel = panel;
        this.fuelCellPanel = fuelCellPanel;
    }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "SimulationThread");
        thread.start();
    }

    public void stop() {
        running = false;
        try {
            if (thread != null) thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            long startTime = System.currentTimeMillis();

            // Update simulation
            panel.timerUpdate();
            PhysicsEngine.updatePhysics(panel.getNeutronsList(), fuelCellPanel, panel.getControlRodsList());
            
            // Schedule UI repaint on EDT
            javax.swing.SwingUtilities.invokeLater(panel::repaint);

            // Wait to maintain tick rate
            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = TICK_RATE_MS - elapsed;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
