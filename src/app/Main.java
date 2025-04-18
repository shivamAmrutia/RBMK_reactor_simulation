package app;

import javax.swing.SwingUtilities;

import core.TimerClock;
import ui.ReactorSimulatorUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReactorSimulatorUI::new);
        TimerClock.startTimer();
    }
}
