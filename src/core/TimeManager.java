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
    
    private javax.swing.JSlider controlRodSlider;
    private javax.swing.JCheckBox autoControlCheck;
    private int targetNeutrons;


    public TimeManager(InteractivePanel panel, FuelCellPanel[][] fuelCellPanel,javax.swing.JSlider controlRodSlider,
            javax.swing.JCheckBox autoControlCheck,
            int targetNeutrons) {
        this.panel = panel;
        this.fuelCellPanel = fuelCellPanel;
        this.controlRodSlider = controlRodSlider;
        this.autoControlCheck = autoControlCheck;
        this.targetNeutrons = targetNeutrons;
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
    	int ctr = 0;
        while (running) {
            long startTime = System.currentTimeMillis();
            
            if(ctr == 5){
            	PhysicsEngine.makeXenon();
            	PhysicsEngine.makeFissile(10);
            	panel.addRandomNeutrons(1);
            	ctr = 0;
            }

            // Update simulation
            panel.timerUpdate();
            
            
            //auto-control system
            if (autoControlCheck != null && autoControlCheck.isSelected()) {
        	    int currentCount = panel.getNeutronCount();
        	    int sliderValue = controlRodSlider.getValue();

        	    // Simple proportional control
        	    if (currentCount > targetNeutrons + 5 && sliderValue < 100) {
        	        controlRodSlider.setValue(sliderValue + 1); // insert rods
        	    } else if (currentCount < targetNeutrons - 5 && sliderValue > 0) {
        	        controlRodSlider.setValue(sliderValue - 1); // retract rods
        	    }
        	}
            
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
