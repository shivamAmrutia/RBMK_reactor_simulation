package ui;
import core.Neutron;
import core.TimerClock;
import javax.swing.*;
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
    private JLabel temperatureLabel;
    private JLabel neutronCountLabel;
    private FuelCellPanel[][] fuelcells;
    private final static int COLS = 30;
    private final static int ROWS = 15;
    private final static int cellSize = 34;
    static int panelWidth = COLS * cellSize;
    static int panelHeight = ROWS * cellSize;
    private static InteractivePanel interactiveLayer = new InteractivePanel(panelWidth, panelHeight, cellSize);    
    

    public ReactorSimulatorUI() {
    	
        frame = new JFrame("Chernobyl Reactor Simulator");
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //////////////////  Reactor core grid (Elements: Fuelcell + Water) ////////////////////
        
        reactorPanel = new JPanel();
        reactorPanel.setLayout(new GridLayout(ROWS, COLS, 4, 4)); 
        fuelcells = new FuelCellPanel[ROWS][COLS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                FuelCellPanel cell = new FuelCellPanel();
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
        simulationPanel.add(controlRodSlider = new JSlider(0, 75, 0));
        simulationPanel.add(Box.createVerticalStrut(5));
        simulationPanel.add(temperatureLabel = new JLabel("Temperature: 0 °C"));

        
        
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
        frame.setVisible(true);
    }
	    
    //////////////////////////////////////////////// Helper Methods //////////////////////////////////////////////////////
    
    // Generate Interactive Layer
    
	private void generateinteractiveLayer() {		
		interactiveLayer.clearAllNeutrons();
		interactiveLayer.setupControlRods();
		interactiveLayer.setupModerators();
		interactiveLayer.addRandomNeutrons(30);
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
	            int temp = rand.nextInt(100); // simulate water temp
	            fuelcells[i][j].setState(isFissile, temp);
	        }
	    }	   
	}	
	
	// Adjust Control Rods
	
    private void adjustControlRods() {
	int sliderValue = controlRodSlider.getValue();
	    int depth = (int) ((sliderValue / 100.0) * (ROWS * cellSize));
	    interactiveLayer.setControlRodDepth(depth);
    }
    	
}



