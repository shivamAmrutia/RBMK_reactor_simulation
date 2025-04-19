package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import ui.InteractivePanel;

public class PhysicsEngine {

    private static final Random rand = new Random();

    public static void updatePhysics(ArrayList<Neutron> neutrons,
                                     ArrayList<FuelCell> fuelCells,
                                     ArrayList<Water> waters,
                                     ArrayList<ControlRod> controlRods) 
    {
        handleAbsorption(neutrons, fuelCells, waters, controlRods);
        handleFission(neutrons, fuelCells);
        // Future: handleXenonBuildUp(...)
    }

    public static void handleAbsorption(ArrayList<Neutron> neutrons,
                                        ArrayList<FuelCell> fuelCells,
                                        ArrayList<Water> waters,
                                        ArrayList<ControlRod> controlRods) 
    {
        Iterator<Neutron> iter = neutrons.iterator();
        while (iter.hasNext()) {
            Neutron n = iter.next();
            
            
            //should we handle control rod absorption here as well
            
            // Water Absorption
            for (Water water : waters) {
                if (water.absorbs(n)) {
                    water.heatUp();
                    iter.remove();
                    break;
                }
            }
        }
    }

    public static void handleFission(ArrayList<Neutron> neutrons, ArrayList<FuelCell> fuelCells) {
        ArrayList<Neutron> newNeutrons = new ArrayList<>();
        for (FuelCell cell : fuelCells) {
        	Iterator<Neutron> it = neutrons.iterator();
        	while(it.hasNext()){
        		Neutron n = it.next();
        		if (cell.absorbs(n)) {
        			it.remove();
        			int emitted = rand.nextInt(3) + 2; // emit 2-4 neutrons
        			for (int i = 0; i < emitted; i++) {
        				newNeutrons.add(cell.emitNeutron());
        			}
        		}        		
        	}
        	
        }
        neutrons.addAll(newNeutrons);
    }
}
