package core;


import org.bson.types.ObjectId;

import db.MongoLogger;
import ui.FuelCellPanel;
import ui.InteractivePanel;


public class TimeManager implements Runnable {
	private final InteractivePanel panel;
	private FuelCellPanel[][] fuelCellPanel;
    private Thread thread;
    private boolean running = false;
    private final int TICK_RATE_MS = 40;
    
    private javax.swing.JSlider controlRodSlider;
    private javax.swing.JCheckBox autoControlCheck;
    private javax.swing.JLabel temperatureLabel;
    private javax.swing.JLabel powerLabel;
    private int targetNeutrons;
    
    //db
    private db.MongoLogger mongoLogger;
    private org.bson.types.ObjectId simulationId;
    



    public TimeManager(InteractivePanel panel, FuelCellPanel[][] fuelCellPanel,javax.swing.JSlider controlRodSlider,
            javax.swing.JCheckBox autoControlCheck,
            javax.swing.JLabel temperatureLabel,
            javax.swing.JLabel powerLabel,
            int targetNeutrons,
            MongoLogger mongoLogger, ObjectId simulationId) {
        this.panel = panel;
        this.fuelCellPanel = fuelCellPanel;
        this.controlRodSlider = controlRodSlider;
        this.autoControlCheck = autoControlCheck;
        this.temperatureLabel = temperatureLabel;
        this.powerLabel = powerLabel;
        this.targetNeutrons = targetNeutrons;
        this.mongoLogger = mongoLogger;
        this.simulationId = simulationId;
    }
    
    public void setTargetNeutrons(int targetNeutrons) {
    	this.targetNeutrons = targetNeutrons;
    	return;
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
    	int ctr = 1;
    	long simulationTick = 0;
    	
        while (running) {
            long startTime = System.currentTimeMillis();
            
            if(ctr % 5 == 0){
            	PhysicsEngine.makeXenon();
            	PhysicsEngine.makeFissile(10);
            	panel.addRandomNeutrons(1);
            }
            
            if (ctr % 25 == 0 && simulationId != null) {
                float avgTemp = Water.getAverageTemperature(fuelCellPanel);
                float power = Water.calculatePowerOutput(fuelCellPanel);
                int neutrons = panel.getNeutronCount();

                mongoLogger.logSnapshot(simulationId, simulationTick, neutrons, avgTemp, power);
                simulationTick++;
            }
            
            //reset to avoid overflowing
            if (ctr >= 1000) ctr = 0;
            else ctr++;


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
            
            //save temperature
            float avgTemp = Water.getAverageTemperature(fuelCellPanel);
            
            //save power
            float power = Water.calculatePowerOutput(fuelCellPanel);
            
            // Schedule UI repaint on EDT
            javax.swing.SwingUtilities.invokeLater(panel::repaint);
            
            // Update label safely from background thread
            javax.swing.SwingUtilities.invokeLater(() ->
                temperatureLabel.setText(String.format("%.1f", avgTemp))
            );
            
            javax.swing.SwingUtilities.invokeLater(() ->
            	powerLabel.setText(String.format("%.0f", power))
            );

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
