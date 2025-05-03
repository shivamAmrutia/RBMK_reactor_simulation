package ui;
import core.Neutron;
import core.TimeManager;
import db.MongoLogger;
import db.UserManager;

//import core.TimerClock;
import javax.swing.*;

import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class ReactorSimulatorUI {
	
    private JFrame frame;
    private JPanel reactorPanel;
    private JPanel controlsPanel;
    private JPanel fuelConfigPanel;
    private JPanel countPanel;
    private JPanel simulationPanel;
    private JButton generateButton;
    private JButton startButton;
    private JButton stopButton;
    private JTextField fissileInput;
    private JSlider controlRodSlider;
    private JPanel tempRow;
    private JLabel temperatureLabelText;
    private JLabel temperatureLabel;
    private JPanel powerRow;
    private JLabel powerLabelText;
    private JLabel powerLabel;
    private JLabel neutronCountLabel;
    private FuelCellPanel[][] fuelcells;
    private final static int COLS = 30;
    private final static int ROWS = 15;
    private final static int cellSize = 34;
    static int panelWidth = COLS * cellSize;
    static int panelHeight = ROWS * cellSize;
    private JCheckBox autoControlCheck;
    private JTextField targetNeutronInput;
    private int targetNeutrons = 30; // default
    private JCheckBox disablePumpCheck;

    private static InteractivePanel interactiveLayer = new InteractivePanel(panelWidth, panelHeight, cellSize); 
    private TimeManager timeManager;

    
    public ReactorSimulatorUI(MongoLogger mongoLogger, ObjectId userId, ObjectId simulationId) {
        frame = new JFrame("Chernobyl Reactor Simulator");
        frame.setSize(1000, 800);
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
                fuelcells[i][j].setState(false, 50);
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
        
        fuelConfigPanel = new JPanel();
        fissileInput = new JTextField("30", 5);
        generateButton = new JButton("Generate Grid");
        fuelConfigPanel.add(new JLabel("Fissile Fuel %: "));
        fuelConfigPanel.add(fissileInput);
        fuelConfigPanel.add(generateButton);

        	/////////// Simulation control panel (bottom right) ////////////////
        
        simulationPanel = new JPanel();
        simulationPanel.setLayout(new BoxLayout(simulationPanel, BoxLayout.Y_AXIS));
        simulationPanel.add(Box.createVerticalStrut(5));
        simulationPanel.add(startButton = new JButton("Start Simulation"));
        simulationPanel.add(Box.createVerticalStrut(5));
        simulationPanel.add(stopButton = new JButton("Stop Simulation"));
        simulationPanel.add(Box.createVerticalStrut(5));
        simulationPanel.add(new JLabel("Control Rod Position: "));
        simulationPanel.add(controlRodSlider = new JSlider(0, 100, 0));
        simulationPanel.add(Box.createVerticalStrut(5));
        
        tempRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        temperatureLabelText = new JLabel("Avg. Temperature (°C): ");
        temperatureLabel = new JLabel("0");
        tempRow.add(temperatureLabelText);
        tempRow.add(temperatureLabel);
        
        simulationPanel.add(tempRow);
        
        powerRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        powerLabelText = new JLabel("Power Output (MW): ");
        powerLabel = new JLabel("0");
        powerRow.add(powerLabelText);
        powerRow.add(powerLabel);
        simulationPanel.add(powerRow);

        
        simulationPanel.add(Box.createVerticalStrut(5));
        simulationPanel.add(new JLabel("Target Neutrons: "));
        targetNeutronInput = new JTextField("30", 5);
        simulationPanel.add(targetNeutronInput);
        autoControlCheck = new JCheckBox("Enable Auto-Control");
        simulationPanel.add(autoControlCheck);
        
        disablePumpCheck = new JCheckBox("Disable Water Pump");
        simulationPanel.add(disablePumpCheck);

        
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
        
   
        
        //initializing timeManager to ensure fuellCells are populated before passing reference
        timeManager = new TimeManager(interactiveLayer, fuelcells, controlRodSlider, autoControlCheck, temperatureLabel, powerLabel, targetNeutrons, mongoLogger, simulationId);
        	
        
        
        	///////////  Combining Bottom Panels  //////////////
        controlsPanel.add(fuelConfigPanel, BorderLayout.WEST);
        controlsPanel.add(simulationPanel, BorderLayout.CENTER);

        frame.add(controlsPanel, BorderLayout.SOUTH);
        
        /////////////// Neutron Count panel(TOP) ////////////////////
        
        countPanel = new JPanel();
	    neutronCountLabel = new JLabel("Neutron Count: 0");
	    countPanel.add(neutronCountLabel);
	    new Timer(500, e -> {
	        neutronCountLabel.setText("Neutron Count: " + interactiveLayer.getNeutronCount());
	    }).start();
        
	    frame.add(countPanel, BorderLayout.NORTH);
	    
        //////////////// Action listeners  ///////////////////
       
          //// Adjusting Control Rods //// 
	    controlRodSlider.addChangeListener(e -> adjustControlRods());
        
	      //// Generate Simulation ////
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateFuelGrid();
                generateinteractiveLayer();                
            }
        });
        
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
	        percentage = Integer.parseInt(fissileInput.getText());
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
    	
}



