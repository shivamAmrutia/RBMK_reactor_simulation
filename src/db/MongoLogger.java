package db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;

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
    public Iterable<Document> getSimulationsByUser(ObjectId userId) {
        return simulations.find(Filters.eq("userId", userId));
    }
}
