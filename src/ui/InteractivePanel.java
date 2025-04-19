package ui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import core.ControlRod;
import core.Neutron;
import core.Moderator;

public class InteractivePanel extends JPanel {
    private ArrayList<Neutron> neutrons = new ArrayList<>();
    private final ArrayList<ControlRod> controlRods = new ArrayList<>();
    private final ArrayList<Moderator> moderators = new ArrayList<>();
    private final ArrayList<Integer> moderatorSpace = new ArrayList<>();
    private int panelWidth, panelHeight, cellSize;

    public InteractivePanel(int panelWidth, int panelHeight, int cellSize) {
        setOpaque(false);
        this.panelHeight = panelHeight;
        this.panelWidth = panelWidth;
        this.cellSize = cellSize;
    }  
    
    ////////  Timer Update method  ////////
    
    public void timerUpdate() {
    	for (Neutron n : neutrons) {  
    		n.move(getWidth(), getHeight());
    		for (Moderator m : moderators) {
    	        m.trySlowDown(n);
    	    }
    	}      
    	
    	for (ControlRod rod : controlRods) {
    		rod.update();
     	} 

    	
    	for (ControlRod rod : controlRods) {
    	    rod.absorbNearbyNeutrons(neutrons, panelHeight);
    	}

    }
    
    ////////  Methods  ////////
    
    // 1) Setup by Adding Neutrons Randomly
    
    public void setupNeutrons(int count) {
    	neutrons.clear();
    	Random rand = new Random();
        for (int i = 0; i < count; i++) {
        	int x, y;
        	do {
		           x = rand.nextInt(panelWidth);
		           y = rand.nextInt(panelHeight); 
        	}while(moderatorSpace.contains(x));
	        neutrons.add(new Neutron(x, y));
        }
		
	}
    
   
    
    // 2) Get Neutron Count
    
    public int getNeutronCount() {
        return neutrons.size();
    }
    
    // 3) Seting Up Control Rods

    public void setupControlRods() {
        controlRods.clear();
        
        for (int i = 1; i < panelWidth / cellSize; i += 3) {
        	// Rods from top
        	if(i % 6 == 1) {
	            int x = (i - 1) * cellSize + (cellSize - 2);
	            controlRods.add(new ControlRod(x, 0));
        	}
        	// Rods from bottom
        	else if(i % 6 == 4) {
        		int x = (i - 1) * cellSize + (cellSize - 2);
                controlRods.add(new ControlRod(x, panelHeight));
        	}        	
        }
    }
    
    // 4) Seting Control Rod Depth
    
    public void setControlRodDepth(int depth) {
        for (ControlRod rod : controlRods) {
            rod.setTargetHeight(depth);
        }
    }
        
    
    // 5) Seting Up Moderators
    
    public void setupModerators() {
		moderators.clear();
		
		for (int i = 0; i < panelWidth / cellSize; i += 3) {
			int x = (i - 1) * cellSize + (cellSize - 2);
	        moderators.add(new Moderator(x, panelHeight));
	        for(int j = -6; j <= 10; j += 1) {
	        	moderatorSpace.add(x + j);
	        }
        }
		
	}
    
    
    ////////  Paint Component  ////////

    protected void paintComponent(Graphics g) {    	
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        for(Moderator m: moderators) {
        	m.draw(g2d);
        }
        
        for (Neutron n : neutrons) {
        	n.draw(g2d);
        }
        
        for (ControlRod rod : controlRods) {
    	    rod.draw(g2d);
    	} 
    }

	
	
}

