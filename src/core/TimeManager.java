package core;

import java.util.ArrayList;

import ui.FuelCellPanel;
import ui.InteractivePanel;
import java.util.concurrent.ConcurrentLinkedQueue;


public class TimeManager implements Runnable {
	private final InteractivePanel panel;
	private FuelCellPanel[][] fuelCellPanel;
    private Thread thread;
    private boolean running = false;
    private final int TICK_RATE_MS = 40;
    private final ConcurrentLinkedQueue<Runnable> eventQueue = new ConcurrentLinkedQueue<>();

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
    
    public void enqueue(Runnable event) {
        eventQueue.add(event);
    }


    @Override
    public void run() {
    	int ctr = 0;
        while (running) {
            long startTime = System.currentTimeMillis();
            
            if(ctr == 5) {
            	PhysicsEngine.makeXenon();
            	PhysicsEngine.makeFissile(5);
            	ctr = 0;
            }

            // Update simulation
            panel.timerUpdate();
            panel.addRandomNeutrons(1);
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
            
            ctr += 1;
        }
    }
}
