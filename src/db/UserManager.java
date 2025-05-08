package db;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;

public class UserManager {
    private final MongoCollection<Document> usersCollection;

    public UserManager(MongoDatabase database) {
        this.usersCollection = database.getCollection("users");
    }

    // Create user if not exists and return ObjectId
    public ObjectId createOrGetUser(String username) {
        Document userDoc = usersCollection.find(Filters.eq("username", username)).first();
        if (userDoc != null) {
            return userDoc.getObjectId("_id");
        }

        Document newUser = new Document("username", username)
                .append("createdAt", Instant.now());
        usersCollection.insertOne(newUser);
        return newUser.getObjectId("_id");
    }

    // Optional: fetch user by ID
    public Document getUserById(ObjectId userId) {
        return usersCollection.find(Filters.eq("_id", userId)).first();
    }
}
