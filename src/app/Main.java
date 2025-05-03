package app;

import javax.swing.SwingUtilities;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Properties;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import db.MongoLogger;
import db.UserManager;

import org.bson.Document;
import org.bson.types.ObjectId;

import ui.LaunchPrompt;
import ui.ReactorSimulatorUI;

public class Main {

    private static final Properties properties = new Properties();
	
    public static void main(String[] args) {
        // Load environment variables
        Dotenv dotenv = Dotenv.load();
        String username = dotenv.get("db.username");
        String password = dotenv.get("db.pass");

        if (username == null || password == null) {
            System.err.println("❌ Missing MongoDB credentials in .env file.");
            return;
        }

        // Build MongoDB connection string
        String connectionString = "mongodb+srv://" + username + ":" + password +
                "@cluster0.g1zsw5m.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

        // Set server API version (optional for newer MongoDB clients)
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        // Initialize DB, create user and logger
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("rbmk_simulator");

        try {
            database.runCommand(new Document("ping", 1));
            System.out.println("✅ Connected to MongoDB!");
        } catch (MongoException e) {
            System.err.println("❌ Failed to ping MongoDB:");
            e.printStackTrace();
            return;
        }

        // Create logger and user
        UserManager userManager = new UserManager(database);
        MongoLogger mongoLogger = new MongoLogger(database);

        // Launch UI to collect user + config
        LaunchPrompt.LaunchConfig config = LaunchPrompt.promptUser(userManager);

        // Start simulation using provided config
        ObjectId simulationId = mongoLogger.startSimulation(
            config.userId,
            config.targetNeutrons,
            config.fuelPercentage
        );



        // Launch UI
        SwingUtilities.invokeLater(() -> new ReactorSimulatorUI(mongoLogger, simulationId, config));
    }

}
