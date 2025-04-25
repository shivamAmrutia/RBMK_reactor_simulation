package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import ui.FuelCellPanel;
import ui.InteractivePanel;

public class PhysicsEngine {

    private static final Random rand = new Random();
    private static ArrayList<FuelCell> convertToXenon = new ArrayList<>();
    
    public static void updatePhysics(ArrayList<Neutron> neutrons,
                                     FuelCellPanel[][] fuelCellPanel,
                                     ArrayList<ControlRod> controlRods) 
    {
        handleAbsorption(neutrons, fuelCellPanel, controlRods);
        handleFission(neutrons, fuelCellPanel);
    }

    public static void handleAbsorption(ArrayList<Neutron> neutrons,
    									FuelCellPanel[][] fuelCellPanel,
                                        ArrayList<ControlRod> controlRods) 
    {
        Iterator<Neutron> iter = neutrons.iterator();
        while (iter.hasNext()) {
            Neutron n = iter.next();
                        
            //should we handle control rod absorption here as well
            
         // Water Absorption
            for (FuelCellPanel[] fuelCellPanelList : fuelCellPanel) {
            	boolean isabsorbed = false;
            	for(FuelCellPanel fuelPanel : fuelCellPanelList) {
            		Water water = fuelPanel.getWater();
            		if (water.isInRange(n)) {
            			water.heatUp();
            			if (water.absorbs(n)){
            				iter.remove();  
            				isabsorbed = true;
            			}    
            		} 
            		else {
            			water.coolDown();          	            			
            		}
            	}
            	if (isabsorbed) break;
            }
        }
    }

    public static void handleFission(ArrayList<Neutron> neutrons, FuelCellPanel[][] fuelCellPanel) {
        ArrayList<Neutron> newNeutrons = new ArrayList<>();
        
        for (FuelCellPanel[] fuelCellPanelList : fuelCellPanel) {
        	for(FuelCellPanel fuelPanel: fuelCellPanelList) {
        		Iterator<Neutron> it = neutrons.iterator();
        		FuelCell fuelcell = fuelPanel.getFuelCell();
        		while(it.hasNext()){
        			Neutron n = it.next();
        			if (!fuelcell.getXenon() && fuelcell.absorbs(n)) {
        				it.remove();
        				fuelcell.updateFissile();
    					int emitted = rand.nextInt(3) + 2; // emit 2-4 neutrons
        				for (int i = 0; i < emitted; i++) {
        					newNeutrons.add(fuelcell.emitNeutron());
        				}
        				int prob = rand.nextInt(9);
        		        if(prob < 3) {
        		        	convertToXenon.add(fuelcell);
        		        } 
        				break;
        			}
        			else if(fuelcell.getXenon() && fuelcell.isInRange(n)) {
        				it.remove();
        				fuelcell.updateFissile();
        				fuelcell.updateXenon(); 
        				convertToXenon.remove(fuelcell);
        				
        			}
        		}
        		
        	}
        	
        }
        neutrons.addAll(newNeutrons);
    }

	public static void makeXenon() {
			for(FuelCell f: convertToXenon) {
				f.makeXenon();
			}
	}
	
}
