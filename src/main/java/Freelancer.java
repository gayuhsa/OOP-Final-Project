import org.bson.types.ObjectId;

public class Freelancer {
    // Atribut dibuat private (Encapsulation)
    private ObjectId id;
    private String name;
    private String skill;
    private double ratePerHour;
    private double rating;

    // Constructor untuk DatabaseManager
    public Freelancer(ObjectId id, String name, String skill, double ratePerHour, double rating) {
        this.id = id;
        this.name = name;
        this.skill = skill;
        this.ratePerHour = ratePerHour;
        this.rating = rating;
    }
    
    // Constructor untuk MarketplaceGUI
    // Tanpa id, karena id nantinya akan dibuat secara otomatis oleh MongoDB
    // Objek yang dibuat dengan constructor ini hanya digunakan untuk membuat document MongoDB
    public Freelancer(String name, String skill, double ratePerHour, double rating) {
        this.id = new ObjectId(); // Isi dengan id sementara
        this.name = name;
        this.skill = skill;
        this.ratePerHour = ratePerHour;
        this.rating = rating;
    }

    // Getter untuk mengakses data (Encapsulation)
    public ObjectId getId() { return id; }
    public String getName() { return name; }
    public String getSkill() { return skill; }
    public double getRatePerHour() { return ratePerHour; }
    public double getRating() { return rating; }

    /**
     * Override toString()
     * Ini PENTING agar JList tahu teks apa yang harus ditampilkan.
     * Tapi kita akan gunakan Cell Renderer kustom untuk tampilan lebih baik.
     */
    @Override
    public String toString() {
        return name + " (" + skill + ")";
    }
}