package db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MongoLogger {
    private final MongoCollection<Document> simulations;

    public MongoLogger(MongoDatabase database) {
        this.simulations = database.getCollection("simulations");
    }

    // Create a new simulation session
    public ObjectId startSimulation(ObjectId userId, int targetNeutrons, int fuelPercentage) {
        Document doc = new Document("userId", userId)
                .append("startTime", Instant.now())
                .append("targetNeutrons", targetNeutrons)
                .append("initialFuelPercentage", fuelPercentage)
                .append("snapshots", new ArrayList<>());

        simulations.insertOne(doc);
        return doc.getObjectId("_id");
    }

    // Append a snapshot to an existing simulation
    public void logSnapshot(ObjectId simulationId, long tick, int neutronCount, float temp, float power) {
        Document snapshot = new Document("tick", tick)
                .append("neutronCount", neutronCount)
                .append("avgTemp", temp)
                .append("power", power);

        simulations.updateOne(
                Filters.eq("_id", simulationId),
                Updates.push("snapshots", snapshot)
        );
    }

    // Optional: fetch all simulations by user
    public MongoCursor<Document> getSimulationsByUser(ObjectId userId) {
    	return simulations.find(Filters.eq("userId", userId)).iterator();
    }
    
    public void logTargetChange(ObjectId simulationId, long tick, int targetNeutrons) {
        Document change = new Document("tick", tick).append("value", targetNeutrons);
        simulations.updateOne(
            Filters.eq("_id", simulationId),
            Updates.push("targetChanges", change)
        );
    }
    
    public List<Document> getSnapshots(ObjectId simulationId) {
        Document sim = simulations.find(Filters.eq("_id", simulationId)).first();
        if (sim != null && sim.containsKey("snapshots")) {
            return (List<Document>) sim.get("snapshots");
        }
        return Collections.emptyList();
    }
    
    public Document getFullSimulation(ObjectId simulationId) {
        return simulations.find(Filters.eq("_id", simulationId)).first();
    }

    
    public boolean deleteSimulation(ObjectId simulationId) {
        long deletedCount = simulations.deleteOne(Filters.eq("_id", simulationId)).getDeletedCount();
        return deletedCount > 0;
    }



}
