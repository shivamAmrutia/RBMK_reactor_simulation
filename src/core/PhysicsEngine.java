package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import ui.FuelCellPanel;
import ui.InteractivePanel;

public class PhysicsEngine {

    private static final Random rand = new Random();
    
    

    public static void updatePhysics(ArrayList<Neutron> neutrons,
                                     FuelCellPanel[][] fuelCellPanel,
                                     ArrayList<ControlRod> controlRods) 
    {
        handleAbsorption(neutrons, fuelCellPanel, controlRods);
        handleFission(neutrons, fuelCellPanel);
        // Future: handleXenonBuildUp(...)
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
            		if (fuelPanel.getWater().isInRange(n)) {
            			fuelPanel.getWater().heatUp();
            			if (fuelPanel.getWater().absorbs(n)){
            				iter.remove();  
            				isabsorbed = true;
            			}            				
            			break;   
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
        		while(it.hasNext()){
        			Neutron n = it.next();
        			if (fuelPanel.getFuelCell().absorbs(n)) {
        				it.remove();
        				fuelPanel.getFuelCell().updateFissile();
        				int emitted = rand.nextInt(3) + 2; // emit 2-4 neutrons
        				for (int i = 0; i < emitted; i++) {
        					newNeutrons.add(fuelPanel.getFuelCell().emitNeutron());
        				}
        			}        		
        		}        		
        	}
        	
        }
        neutrons.addAll(newNeutrons);
    }
}
