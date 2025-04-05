package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class ReactorSimulatorUI {
    private JFrame frame;
    private JPanel reactorPanel;
    private JPanel controlsPanel;
    private JPanel fuelConfigPanel;
    private JPanel simulationPanel;
    private JButton generateButton;
    private JButton startButton;
    private JButton stopButton;
    private JTextField fissileInput;
    private JSlider controlRodSlider;
    private JLabel temperatureLabel;
    private FuelCellPanel[][] fuelCells;
    private final int GRID_SIZE = 20;

    public ReactorSimulatorUI() {
        frame = new JFrame("Chernobyl Reactor Simulator");
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Reactor core grid
        reactorPanel = new JPanel();
        reactorPanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE, 4, 4)); // gaps for control rods
        fuelCells = new FuelCellPanel[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                FuelCellPanel cell = new FuelCellPanel();
                reactorPanel.add(cell);
                fuelCells[i][j] = cell;
            }
        }

        frame.add(reactorPanel, BorderLayout.CENTER);

        // Controls panel (bottom)
        controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(1, 2));

        // Fuel config panel (bottom left)
        fuelConfigPanel = new JPanel();
        fissileInput = new JTextField("30", 5);
        generateButton = new JButton("Generate Grid");
        fuelConfigPanel.add(new JLabel("Fissile Fuel %: "));
        fuelConfigPanel.add(fissileInput);
        fuelConfigPanel.add(generateButton);

        // Simulation control panel (bottom right)
        simulationPanel = new JPanel();
        startButton = new JButton("Start Simulation");
        stopButton = new JButton("Stop Simulation");
        controlRodSlider = new JSlider(0, 100, 50);
        temperatureLabel = new JLabel("Temperature: 0 °C");
        simulationPanel.add(startButton);
        simulationPanel.add(stopButton);
        simulationPanel.add(new JLabel("Control Rod Position: "));
        simulationPanel.add(controlRodSlider);
        simulationPanel.add(temperatureLabel);

        controlsPanel.add(fuelConfigPanel);
        controlsPanel.add(simulationPanel);

        frame.add(controlsPanel, BorderLayout.SOUTH);

        // Action listener to generate grid
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateFuelGrid();
            }
        });

        frame.setVisible(true);
    }

    private void generateFuelGrid() {
        int percentage;
        try {
            percentage = Integer.parseInt(fissileInput.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Enter a valid percentage (0-100)");
            return;
        }

        Random rand = new Random();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                boolean isFissile = rand.nextInt(100) < percentage;
                int temp = rand.nextInt(100); // simulate water temp
                fuelCells[i][j].setState(isFissile, temp);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReactorSimulatorUI::new);
    }
}

class FuelCellPanel extends JPanel {
    private boolean isFissile;
    private int temperature;

    public FuelCellPanel() {
        setPreferredSize(new Dimension(30, 30));
    }

    public void setState(boolean isFissile, int temperature) {
        this.isFissile = isFissile;
        this.temperature = temperature;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Interpolate between #00CCFF (cold) and #FF5050 (hot)
        float ratio = Math.min(temperature / 100f, 1.0f);

        int r = (int)((1 - ratio) * 0 + ratio * 255);
        int gColor = (int)((1 - ratio) * 204 + ratio * 80);
        int b = (int)((1 - ratio) * 255 + ratio * 80);

        Color waterColor = new Color(r, gColor, b);
        setBackground(waterColor);

        // Draw round fuel pellet
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 10;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2d.setColor(isFissile ? Color.RED : Color.GRAY);
        g2d.fillOval(x, y, size, size);
    }

}
