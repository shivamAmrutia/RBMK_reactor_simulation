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
    		slowDown(n);
    	}      

        //TODO  absorbNeutrons();
        repaint();
    }
    
    ////////  Neutron Methods  ////////
    
    
    // 1) Adding Neutrons Randomly
    
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
    
    // 2) Moderators Slow Down Neutrons
    
    public void slowDown(Neutron n) {
        for (Moderator m : moderators) {
            if (n.x - m.x <= 10 && m.x - n.x <= 6) {
                Random rand = new Random();                
                boolean moveRight = true;
                if (n.dx > 0) moveRight = false; 
                
                n.dx = -n.dx;
                
                if (Math.sqrt(Math.pow(n.dx, 2) + Math.pow(n.dy, 2)) > 5) {
	                int dx, dy;
	                do {
	                    dy = rand.nextInt(8) - 4;
	                    dx = rand.nextInt(8) - 4;
	                }while(dx == 0 && dy == 0);
	                
	                if (moveRight) n.dx = Math.abs(dx);
	                else n.dx = -Math.abs(dx);
	          
	                n.dy = dy;	
	                if(n.dx > 0) {
	                	n.x = Math.max(m.x + 10, n.x + n.dx);
	                }
	                else {
	                	n.x = Math.min(m.x - 10, n.x + n.dx);
	                }
                }
            }
        }
    }
    
    // 3) Control Rods Absorb Neutrons
    
    public void absorbNeutrons() {
    	for (ControlRod c : controlRods) {
    		for (Neutron n: neutrons) {
        		if (c.currentHeight != c.y) {
        			if (n.x - c.x <= 10 && c.x - n.x <= 6) {
        				if((c.y == 0 && n.y <= c.currentHeight) || (c.y == panelHeight && n.y >= (c.currentHeight))) {
        					clearNeutron(n);
        				}
        			}
        		}
        	}
    	}    	
    }
    
    // 4) Clearing All Neutrons 
    
    public void clearAllNeutrons() {
        neutrons.clear();
    }
    
    // 5) Clearing Given Neutron
    
    public void clearNeutron(Neutron n) {
    	neutrons.remove(n);
    }
    
    // 6) Get Neutron Count
    
    public int getNeutronCount() {
        return neutrons.size();
    }

    ////////  Control Rods Methods  ////////
    
    // 1) Seting Up Control Rods

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
    
    // 2) Seting Control Rod Depth
    
    public void setControlRodDepth(int depth) {
        for (ControlRod rod : controlRods) {
            rod.setTargetHeight(depth);
        }
    }
    
    
    
    /////////  Moderators Methods  //////////
    
    
    // 1) Seting Up Moderators
    
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
        	n.move(getWidth(), getHeight());
        	n.draw(g2d);
        }
        
        for (ControlRod rod : controlRods) {
    	    rod.update();
    	    rod.draw(g2d);
    	} 
    }
	
}

