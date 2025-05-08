package ui;
import core.TimeManager;
import db.UserManager;
import db.MongoLogger;


//import core.TimerClock;
import javax.swing.*;

import org.bson.types.ObjectId;


import java.awt.*;
import java.util.Random;

public class ReactorSimulatorUI {
	
    private JFrame frame;
    private JPanel reactorPanel;
    private JPanel controlsPanel;
    private JPanel fuelConfigPanelParent;
    private JPanel managePanel;
    private JPanel fuelConfigPanel;
    private JPanel countPanel;
    private JPanel simulationPanel;
    private JButton resetButton;
    private JButton startButton;
    private JButton stopButton;
    private JSlider controlRodSlider;
    private JPanel tempRow;
    private JLabel temperatureLabelText;
    private JLabel temperatureLabel;
    private JPanel powerRow;
    private JLabel powerLabelText;
    private JLabel powerLabel;
    private JLabel neutronCountLabel;
    private FuelCellPanel[][] fuelcells;
    private int fissileInput;
    private final static int COLS = 32;
    private final static int ROWS = 15;
    private final static int cellSize = 34;
    static int panelWidth = COLS * cellSize;
    static int panelHeight = ROWS * cellSize;
    private JCheckBox autoControlCheck;
    private JTextField targetNeutronInput;
    private int targetNeutrons = 30; // default
    private JCheckBox disablePumpCheck;
    
    private JButton openSimManagerBtn = new JButton("Manage Simulations");
    
    private db.UserManager userManager;
    private db.MongoLogger mongoLogger;


    private static InteractivePanel interactiveLayer = new InteractivePanel(panelWidth, panelHeight, cellSize); 
    private TimeManager timeManager;

    
    public ReactorSimulatorUI(UserManager userManager, MongoLogger mongoLogger, ObjectId simulationId, LaunchPrompt.LaunchConfig config) {
    	
    	this.userManager = userManager;
    	this.mongoLogger = mongoLogger;
    	this.targetNeutrons = config.targetNeutrons;
    	
    	
        frame = new JFrame("Chernobyl Reactor Simulator");
        frame.setSize(1100, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //////////////////  Reactor core grid (Elements: Fuel cell + Water) ////////////////////
        
        reactorPanel = new JPanel();
        reactorPanel.setLayout(new GridLayout(ROWS, COLS, 4, 4)); 
        fuelcells = new FuelCellPanel[ROWS][COLS]; 

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                FuelCellPanel cell = new FuelCellPanel(j * cellSize, i * cellSize);
                reactorPanel.add(cell);
                fuelcells[i][j] = cell;
                fuelcells[i][j].setState(false, 30);
            }            
        }
        
        

        
        /////////////////  Interactive Layer (Neutrons, Moderators, and Control Rods) ///////////////////////////
        
        interactiveLayer.setBounds(0, 0, panelWidth, panelHeight);
        
        /////// Creating Layered Panel : so a reactor panel and a neutron panel on top of it //////////
       
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(panelWidth, panelHeight));
        reactorPanel.setBounds(0, 0, panelWidth, panelHeight);
        layeredPane.add(reactorPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(interactiveLayer, JLayeredPane.PALETTE_LAYER);

        frame.add(layeredPane, BorderLayout.CENTER);

        ////////////  Controls panel (BOTTOM)  ////////////////////
        
        controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(1, 2));

        	/////////// Fuel config panel (bottom left) ///////////////
        
        fuelConfigPanelParent = new JPanel();
        fuelConfigPanelParent.setLayout(new BoxLayout(fuelConfigPanelParent, BoxLayout.Y_AXIS));
        fuelConfigPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        fissileInput = config.fuelPercentage;
        resetButton = new JButton("Reset Simulation");
        fuelConfigPanel.add(resetButton);
        fuelConfigPanel.add(startButton = new JButton("Start Simulation"));
        fuelConfigPanel.add(stopButton = new JButton("Stop Simulation"));
        
        fuelConfigPanelParent.add(fuelConfigPanel, BorderLayout.CENTER);
        managePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        managePanel.add(openSimManagerBtn);
        
        fuelConfigPanelParent.add(fuelConfigPanel);
        fuelConfigPanelParent.add(managePanel);

        	/////////// Simulation control panel (bottom right) ////////////////
        
        simulationPanel = new JPanel();
        simulationPanel.setLayout(new BoxLayout(simulationPanel, BoxLayout.Y_AXIS));
        simulationPanel.add(Box.createVerticalStrut(5));
        simulationPanel.add(Box.createVerticalStrut(5));
        simulationPanel.add(Box.createVerticalStrut(5));
        
        
        JLabel rodLabel = new JLabel("Control Rod Position:");
        rodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        simulationPanel.add(rodLabel);
        simulationPanel.add(controlRodSlider = new JSlider(0, 100, 0));
        simulationPanel.add(Box.createVerticalStrut(5));
        
        
        simulationPanel.add(Box.createVerticalStrut(20));
        
        JPanel targetRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        targetRow.add(new JLabel("Target Neutrons:"));
        targetNeutronInput = new JTextField(String.valueOf(config.targetNeutrons), 5);
        targetRow.add(targetNeutronInput);
        simulationPanel.add(targetRow);

        
        
        JPanel checkboxRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); 
        checkboxRow.setAlignmentX(Component.CENTER_ALIGNMENT); 
        checkboxRow.add(autoControlCheck = new JCheckBox("Enable Auto-Control"));
        checkboxRow.add(disablePumpCheck = new JCheckBox("Disable Water Pump"));        
        simulationPanel.add(checkboxRow);
        simulationPanel.add(Box.createVerticalStrut(20)); 
        
        

        
        //adding event listener to control water pumps
        disablePumpCheck.addActionListener(e -> {
            float rate = disablePumpCheck.isSelected() ? 0.05f : 0.1f;

            for (int i = 0; i < fuelcells.length; i++) {
                for (int j = 0; j < fuelcells[i].length; j++) {
                    if (fuelcells[i][j].getWater() != null) {
                        fuelcells[i][j].getWater().setCoolingRate(rate);
                    }
                }
            }
        });
        
        //adding simulation Manger to browse and delete
        openSimManagerBtn.addActionListener(e -> {
            new SimulationManagerUI(mongoLogger, config.userId); // create and show manager
        });

        	
        
        
        	///////////  Combining Bottom Panels  //////////////
        controlsPanel.add(fuelConfigPanelParent, BorderLayout.WEST);
        controlsPanel.add(simulationPanel, BorderLayout.EAST);

        frame.add(controlsPanel, BorderLayout.SOUTH);
        
        /////////////// Neutron Count panel(TOP) ////////////////////
        
        countPanel = new JPanel();
	    neutronCountLabel = new JLabel("Neutron Count: 0");
	    countPanel.add(neutronCountLabel);
	    new Timer(500, e -> {
	        neutronCountLabel.setText("Neutron Count: " + interactiveLayer.getNeutronCount());
	    }).start();
        
	    frame.add(countPanel, BorderLayout.NORTH);
	    
	    tempRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        temperatureLabelText = new JLabel("Avg. Temperature (°C): ");
        temperatureLabel = new JLabel("0");
        tempRow.add(temperatureLabelText);
        tempRow.add(temperatureLabel);
        countPanel.add(Box.createHorizontalStrut(20)); 
        countPanel.add(tempRow);
        
        powerRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        powerLabelText = new JLabel("Power Output (MW): ");
        powerLabel = new JLabel("0");
        powerRow.add(powerLabelText);
        powerRow.add(powerLabel);
        countPanel.add(Box.createHorizontalStrut(20)); 
        countPanel.add(powerRow);
	    
        //////////////// Action listeners  ///////////////////
       
          //// Adjusting Control Rods //// 
	    controlRodSlider.addChangeListener(e -> adjustControlRods());
        
	    
	    

	    //initializing timeManager to ensure fuellCells are populated before passing reference
	    timeManager = new TimeManager(interactiveLayer, fuelcells, config.fuelPercentage, controlRodSlider, autoControlCheck, temperatureLabel, powerLabel, targetNeutrons, mongoLogger, simulationId);
	    
        resetButton.addActionListener(e -> resetSimulation());
        
        startButton.addActionListener(e -> {
	        try {
	            timeManager.setTargetNeutrons(Integer.parseInt(targetNeutronInput.getText()));
	        } catch (NumberFormatException ex) {
	            targetNeutrons = 30; // fallback
	        }
	        timeManager.start();
        }
        );
        stopButton.addActionListener(e -> timeManager.stop());

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                timeManager.stop(); // stop thread on close
            }
        });
        
      //call generate methods
        generateFuelGrid();
        generateinteractiveLayer();        

        frame.setVisible(true);
    }
	    
    //////////////////////////////////////////////// Helper Methods //////////////////////////////////////////////////////
    
    // Generate Interactive Layer
    
	private void generateinteractiveLayer() {		
		interactiveLayer.setupNeutrons(30);
		interactiveLayer.setupControlRods();
		interactiveLayer.setupModerators();
	}
	
	// Returns Interactive Layer

    public static InteractivePanel getInteractivePanel() {
		return interactiveLayer;
	}
	
    // Generate Fuel Grid
    
	private void generateFuelGrid() {
	    int percentage;
	    try {
	        percentage = fissileInput;
	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(frame, "Enter a valid percentage (0-100)");
	        return;
	    }
	
	    Random rand = new Random();
	    for (int i = 0; i < ROWS; i++) { 
	        for (int j = 0; j < COLS; j++) {
	            boolean isFissile = rand.nextInt(100) < percentage;
	            int temp = 0; // simulate water temp
	            fuelcells[i][j].setState(isFissile, temp);
	        }
	    }	   
	}	
	
	// Adjust Control Rods
	
    private void adjustControlRods() {
		int sliderValue = controlRodSlider.getValue();
		    int height = (int) ((sliderValue / 100.0) * panelHeight);
		    // 3 parts adjusted: control rod of height (max 35% panelheight), connectors positioned after controlrods (of height =  panelheight/10), moderators positioned after both
		    interactiveLayer.setControlRodHeight(height);
		    interactiveLayer.setConnectorsYPosition(height);
		    interactiveLayer.setModeratorsYPosition(height + panelHeight / 10 );
	    }
   
    private void resetSimulation() {
        // Step 1: Stop current simulation
        timeManager.stop();

        // Step 2: Show prompt again
        LaunchPrompt.LaunchConfig config = LaunchPrompt.promptUser(userManager);

        // Step 3: Reset fuel grid and control Rod Slider
        this.fissileInput = config.fuelPercentage;
        this.targetNeutrons = config.targetNeutrons;
        targetNeutronInput.setText(String.valueOf(config.targetNeutrons));
        controlRodSlider.setValue(0);


        // Step 4: Regenerate grid & interactive layer
        generateFuelGrid();
        generateinteractiveLayer();

        // Step 5: Create new simulationId and TimeManager
        ObjectId newSimulationId = mongoLogger.startSimulation(config.userId, config.targetNeutrons, config.fuelPercentage);
        this.timeManager = new TimeManager(interactiveLayer, fuelcells, config.fuelPercentage,controlRodSlider, autoControlCheck,
                temperatureLabel, powerLabel, config.targetNeutrons, mongoLogger, newSimulationId);
    }
    	
}



