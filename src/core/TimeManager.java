package core;


import java.util.Map;
import java.util.TreeMap;
import javax.swing.JOptionPane;

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
    
    private static final float MAX_TEMPERATURE = 100.0f;   // threshold for meltdown
    private boolean alertShown = false;
    
    private javax.swing.JSlider controlRodSlider;
    private javax.swing.JCheckBox autoControlCheck;
    private javax.swing.JLabel temperatureLabel;
    private javax.swing.JLabel powerLabel;
    private int targetNeutrons;
    private int targetFuelPercent;
    private final Map<Long, Integer> targetNeutronLog = new TreeMap<>();

    
    //db
    private db.MongoLogger mongoLogger;
    private org.bson.types.ObjectId simulationId;
    
    //Global tick
    private long simulationTick = 0;



    public TimeManager(InteractivePanel panel, FuelCellPanel[][] fuelCellPanel, int targetFuelPercent,javax.swing.JSlider controlRodSlider,
            javax.swing.JCheckBox autoControlCheck,
            javax.swing.JLabel temperatureLabel,
            javax.swing.JLabel powerLabel,
            int targetNeutrons,
            MongoLogger mongoLogger, ObjectId simulationId) {
        this.panel = panel;
        this.fuelCellPanel = fuelCellPanel;
        this.targetFuelPercent = targetFuelPercent;
        this.controlRodSlider = controlRodSlider;
        this.autoControlCheck = autoControlCheck;
        this.temperatureLabel = temperatureLabel;
        this.powerLabel = powerLabel;
        this.targetNeutrons = targetNeutrons;
        this.mongoLogger = mongoLogger;
        this.simulationId = simulationId;
        
        targetNeutronLog.put(0L, targetNeutrons);
    }
    
    public void setTargetNeutrons(int targetNeutrons) {
        this.targetNeutrons = targetNeutrons;
        if (running) {
        	 mongoLogger.logTargetChange(simulationId, (long) simulationTick, targetNeutrons); // record change
        }
    }
    
    public int getFissilePercent() {
    	int count = 0;
    	for(FuelCellPanel[] fuelCellRow: fuelCellPanel) {
    		for(FuelCellPanel fuelCell: fuelCellRow) {
    			if(fuelCell.checkFissile()){
    				count++;
    			}
    		}
    		
    	}
    	
    	return (count*100)/480;
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
    	
        while (running) {
            long startTime = System.currentTimeMillis();
            
            if(ctr % 5 == 0){
            	PhysicsEngine.makeXenon();
            	int diff = targetFuelPercent - getFissilePercent();
              	if(diff > 0) {
            		PhysicsEngine.makeFissile( (int)(diff*4.8));            		
            	}
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
            
            if (!alertShown && avgTemp > MAX_TEMPERATURE) {
                alertShown = true;
                javax.swing.SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(
                        panel,
                        "Reactor core has blown! Simulation will stop.",
                        "Core Meltdown Alert",
                        JOptionPane.ERROR_MESSAGE
                    )
                );
                stop();
                break;
            }
            
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
