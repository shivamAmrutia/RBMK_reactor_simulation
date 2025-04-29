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
import org.bson.Document;
import ui.ReactorSimulatorUI;

public class Main {

    private static final Properties properties = new Properties();
	
    public static void main(String[] args) {
    	
    	SwingUtilities.invokeLater(ReactorSimulatorUI::new);
    	
    	Dotenv dotenv = Dotenv.load();
        String username = dotenv.get("db.username");
        String password = dotenv.get("db.pass");
        String connectionString = "mongodb+srv://" + username + ":" + password + "@cluster0.g1zsw5m.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";      
        
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
