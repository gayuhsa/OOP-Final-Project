import org.bson.types.ObjectId;

public class Project {
    private ObjectId id;
    private String title;
    private String description;
    private double budget;
    private String companyName;
    
    public Project(ObjectId id, String title, String description, double budget, String companyName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.companyName = companyName;
    }

    public Project(String title, String description, double budget, String companyName) {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.companyName = companyName;
    }

    // Getters
    public ObjectId getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getBudget() { return budget; }
    public String getCompanyName() { return companyName; }

    @Override
    public String toString() {
        return title + " (" + companyName + ")";
    }
}