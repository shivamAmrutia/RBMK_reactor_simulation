package ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.Document;

import org.bson.types.ObjectId;

import com.mongodb.client.MongoCursor;

import db.MongoLogger;

public class SimulationManagerUI extends JFrame {
    private final MongoLogger mongoLogger;
    private final ObjectId userId;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> simulationList = new JList<>(listModel);
    private Map<String, ObjectId> simulationMap = new HashMap<>();

    public SimulationManagerUI(MongoLogger logger, ObjectId userId) {
        super("Simulation Manager");
        this.mongoLogger = logger;
        this.userId = userId;

        // Layout setup
        setLayout(new BorderLayout());
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        loadSimulations();

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteSelected());

        JButton downloadBtn = new JButton("Download Selected");
        downloadBtn.addActionListener(e -> downloadSelected());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteBtn);
        buttonPanel.add(downloadBtn);

        add(new JScrollPane(simulationList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadSimulations() {
        simulationMap.clear();
        listModel.clear();

        MongoCursor<org.bson.Document> cursor = mongoLogger.getSimulationsByUser(userId);
        while (cursor.hasNext()) {
            org.bson.Document doc = cursor.next();

            ObjectId id = doc.getObjectId("_id");
            Date startTime = doc.getDate("startTime");
            int fuel = doc.getInteger("initialFuelPercentage", -1);
            int target = doc.getInteger("targetNeutrons", -1);

            // Format timestamp
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String timeStr = formatter.format(startTime);

            String label = String.format("[%s] Fuel: %d%%, Target Neutrons: %d", timeStr, fuel, target);

            simulationMap.put(label, id);
            listModel.addElement(label);
        }
    }

    private void deleteSelected() {
        String selected = simulationList.getSelectedValue();
        ObjectId selectedId = simulationMap.get(selected);
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this simulation?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mongoLogger.deleteSimulation(selectedId);
                listModel.removeElement(selected);
            }
        }
    }

    private void downloadSelected() {
        String selected = simulationList.getSelectedValue();
        ObjectId selectedId = simulationMap.get(selected);
        if (selected != null) {
            org.bson.Document simulationDoc = mongoLogger.getFullSimulation(selectedId);  // ⬅️ Full doc, not just snapshots
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("simulation_" + selected + ".json"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileWriter writer = new FileWriter(chooser.getSelectedFile())) {
                    writer.write(simulationDoc.toJson());  // Save the full document as JSON
                    JOptionPane.showMessageDialog(this, "Saved full simulation successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error writing file.");
                }
            }
        }
    }

}
