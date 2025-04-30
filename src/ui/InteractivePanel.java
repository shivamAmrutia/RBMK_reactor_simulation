package ui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import core.ControlRod;
import core.GraphiteConnector;
import core.Neutron;
import core.Moderator;

public class InteractivePanel extends JPanel {
    private ArrayList<Neutron> neutrons = new ArrayList<>();
    private final ArrayList<ControlRod> controlRods = new ArrayList<>();
    private final ArrayList<Moderator> moderators = new ArrayList<>();
    private final ArrayList<GraphiteConnector> connectors = new ArrayList<>();
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
    	Iterator<Neutron> iter = neutrons.iterator();
    	
    	
    	while(iter.hasNext()) {
    		Neutron n = iter.next();
    		if(!n.move(getWidth(), getHeight())) {
    			iter.remove();
    			continue;
    		};
    	}    
    	
    	for (Moderator m: moderators) {
    		m.update();
    		m.trySlowDown(neutrons);
    	}
    	
    	for (GraphiteConnector gc: connectors) {
    		gc.update();
    	}
    	
    	for (ControlRod rod : controlRods) {
    		rod.update();
    		rod.absorbNearbyNeutrons(neutrons);
     	}
    	

    }
    

    ////////  Methods  ////////
    
    public ArrayList<Neutron> getNeutronsList(){
    	return neutrons;
    }
    
    public ArrayList<ControlRod> getControlRodsList(){
    	return controlRods;
    }
    
    ////////  Neutron Methods  ////////
    
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
    
    
    // 2) Adding Random Neutrons
    
    public void addRandomNeutrons(int count) {
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
        moderators.clear();
        for (int i = 2; i < panelWidth / cellSize; i += 4) {
        	// Rods from top
            int x = (i - 1) * cellSize + (cellSize - 2);
            controlRods.add(new ControlRod(x, 0));    
            connectors.add(new GraphiteConnector(x + 1, 0, panelHeight / 10));
            moderators.add(new Moderator(x, panelHeight / 10, panelHeight / 2));
            for(int j = -6; j <= 10; j += 1) {
	        	moderatorSpace.add(x + j);
	        }
        }
    }
    
    // 4) Seting Control Rod Depth
    
    public void setControlRodHeight(int height) {
        for (ControlRod rod : controlRods) {
            rod.setTargetHeight(height);
        }
    }
        
    
    // 5) Seting Up Moderators
    
    public void setupModerators() {
    	
		for (int i = 0; i < panelWidth / cellSize; i += 4) {
			int x = (i - 1) * cellSize + (cellSize - 2);
	        moderators.add(new Moderator(x, 0, panelHeight));
	        for(int j = -6; j <= 10; j += 1) {
	        	moderatorSpace.add(x + j);
	        }
        }
		
	}
    
    public void setModeratorsYPosition(int y) {
    	// lower position of moderators connected below the control rod
    	for (Moderator m :moderators) {
    		if (m.height != panelHeight) {
    			m.setYPos(y);
    		}
    	}
    }
    
    public void setConnectorsYPosition(int y) {
    	for (GraphiteConnector gc: connectors) {
    		gc.setYPos(y);
    	}
    }
    
    ////////  Paint Component  ////////

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        ArrayList<Object> drawableObjects = new ArrayList<>();
        drawableObjects.addAll(moderators);
        drawableObjects.addAll(connectors);
        drawableObjects.addAll(neutrons);
        drawableObjects.addAll(controlRods);

        for (Object obj : drawableObjects) {
            if (obj instanceof Moderator) {
                ((Moderator) obj).draw(g2d);
            } else if (obj instanceof GraphiteConnector) {
                ((GraphiteConnector) obj).draw(g2d);
            } else if (obj instanceof Neutron) {
                ((Neutron) obj).draw(g2d);
            } else if (obj instanceof ControlRod) {
                ((ControlRod) obj).draw(g2d);
            }
        }
    }


	
	
}

