package ui;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.bson.types.ObjectId;

import db.UserManager;

public class LaunchPrompt {

	public static class LaunchConfig {
	    public int fuelPercentage;
	    public int targetNeutrons;
	    public ObjectId userId;

	    public LaunchConfig(int fuelPercentage, int targetNeutrons, ObjectId userId) {
	        this.fuelPercentage = fuelPercentage;
	        this.targetNeutrons = targetNeutrons;
	        this.userId = userId;
	    }
	}


    public static LaunchConfig promptUser(UserManager userManager) {
        JTextField fuelInput = new JTextField("30");
        JTextField neutronInput = new JTextField("30");
        JTextField userInput = new JTextField();

        Object[] message = {
            "Fissile Fuel %:", fuelInput,
            "Target Neutrons:", neutronInput,
            "Username:", userInput
        };

        int option = JOptionPane.showConfirmDialog(
            null, message, "Start Simulation", JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                int fuel = Integer.parseInt(fuelInput.getText());
                int target = Integer.parseInt(neutronInput.getText());

                if (fuel < 0 || fuel > 100 || target < 0) throw new NumberFormatException();

                String username = userInput.getText().trim();
                if (username.isEmpty()) throw new IllegalArgumentException();

                ObjectId userId = userManager.createOrGetUser(username);
                return new LaunchConfig(fuel, target, userId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid values.");
            }
        }

        System.exit(0); // Exit if cancelled or invalid
        return null;
    }

}

