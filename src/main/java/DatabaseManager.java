import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

public class DatabaseManager {
    private static String connectionString = "";
    private static MarketplaceGUI gui;
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> freelancers;
    private static MongoCollection<Document> projects;
    private static boolean isError = false;

    private static ServerApi serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build();
    private static MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .serverApi(serverApi)
            .build();
    
    public DatabaseManager(MarketplaceGUI gui) {
        this.gui = gui;
        
        if (isError) {
            gui.displayDatabaseError();
        }
    }
    
    static {
        try {
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("oop");
            freelancers = database.getCollection("freelancers");
            projects = database.getCollection("projects");
        } catch (Throwable t) {
            isError = true;
        }
    }
    
    public static List<Freelancer> getAllFreelancers() {
        List<Freelancer> result = new ArrayList<>();

        for (Document doc : freelancers.find()) {
            Freelancer freelancer = new Freelancer(
                    doc.getObjectId("_id"),
                    doc.getString("name"),
                    doc.getString("skill"),
                    doc.getDouble("ratePerHour"),
                    doc.getDouble("rating")
            );
            result.add(freelancer);
        }

        return result;
    }
    
    public static List<Project> getAllProjects() {
        List<Project> result = new ArrayList<>();

        for (Document doc : projects.find()) {
            Project project = new Project(
                    doc.getObjectId("_id"),
                    doc.getString("title"),
                    doc.getString("description"),
                    doc.getDouble("budget"),
                    doc.getString("companyName")
            );
            result.add(project);
        }

        return result;
    }
    
    public void insertFreelancer(Freelancer freelancer) {
        Document doc = new Document("name", freelancer.getName())
                .append("skill", freelancer.getSkill())
                .append("ratePerHour", freelancer.getRatePerHour())
                .append("rating", freelancer.getRating());
        
        freelancers.insertOne(doc);
        gui.refreshFreelancers();
    }
    
    public void insertProject(Project project) {
        Document doc = new Document("title", project.getTitle())
                .append("description", project.getDescription())
                .append("budget", project.getBudget())
                .append("companyName", project.getCompanyName());
        
        projects.insertOne(doc);
        gui.refreshProjects();
    }
    
    public void deleteFreelancer(ObjectId id) {
        freelancers.deleteOne(new Document("_id", id));
        gui.refreshFreelancers();
    }
    
    public void deleteProject(ObjectId id) {
        projects.deleteOne(new Document("_id", id));
        gui.refreshProjects();
    }
}