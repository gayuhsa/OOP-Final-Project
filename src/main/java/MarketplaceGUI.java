import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas utama untuk aplikasi Marketplace GUI.
 * Kelas ini bertindak sebagai JFrame utama dan juga mengelola data.
 *
 * Untuk kesederhanaan, kelas Freelancer dan Project didefinisikan di file yang sama.
 */
public class MarketplaceGUI extends JFrame {
    // --- Bagian Data (Model) ---
    // Menggunakan ArrayList untuk menyimpan daftar freelancer dan proyek
    private List<Freelancer> freelancers = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private DatabaseManager mongoDriver = new DatabaseManager(this);
    
    // --- Bagian GUI (View) ---
    // Model untuk JList, ini memungkinkan kita menambah/menghapus item secara dinamis
    private DefaultListModel<Freelancer> freelancerListModel;
    private DefaultListModel<Project> projectListModel;

    // Komponen JList untuk menampilkan data
    private JList<Freelancer> jlistFreelancers;
    private JList<Project> jlistProjects;

    /**
     * Main method untuk menjalankan aplikasi.
     */
    public static void main(String[] args) {
        // Menjalankan GUI di Event Dispatch Thread (EDT) untuk keamanan thread Swing
        SwingUtilities.invokeLater(() -> {
            MarketplaceGUI frame = new MarketplaceGUI();
            frame.setVisible(true);
        });
    }

    /**
     * Konstruktor utama untuk MarketplaceGUI.
     * Menyiapkan frame dan komponen di dalamnya.
     */
    public MarketplaceGUI() {
        // --- Setup Window Utama (JFrame) ---
        setTitle("Marketplace Freelancer Mini");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Keluar saat ditutup
        setLocationRelativeTo(null); // Tampilkan di tengah layar
        setLayout(new BorderLayout(10, 10)); // Layout utama

        // --- Inisialisasi Komponen GUI ---
        initComponents();

        // --- Memuat Data Contoh ---
        // loadSampleData();
        
        for (Freelancer freelancer : mongoDriver.getAllFreelancers()) {
            freelancers.add(freelancer);
            freelancerListModel.addElement(freelancer);
        }
        
        for (Project project : mongoDriver.getAllProjects()) {
            projects.add(project);
            projectListModel.addElement(project);
        }
    }

    /**
     * Metode untuk menginisialisasi dan mengatur semua komponen GUI.
     */
    private void initComponents() {
        // Panel utama yang menampung semuanya
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Beri padding

        // --- Panel Tombol (Atas) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnRegisterFreelancer = new JButton("Registrasi Freelancer Baru");
        JButton btnPostProject = new JButton("Buat Proyek Baru");
        JButton btnFreelancerVisibility = new JButton("Sembunyikan Freelancer");
        JButton btnProjectVisibility = new JButton("Sembunyikan Proyek");
        buttonPanel.add(btnRegisterFreelancer);
        buttonPanel.add(btnPostProject);
        buttonPanel.add(btnFreelancerVisibility);
        buttonPanel.add(btnProjectVisibility);

        // --- Panel List (Tengah) ---
        // Menggunakan JSplitPane agar ukuran list bisa diatur
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5); // Bagi layar 50/50

        JPanel leftPanel = new JPanel(new GridBagLayout());
        JPanel rightPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.fill = GridBagConstraints.BOTH;
        gbcTop.weightx = 1.0;
        gbcTop.weighty = 1.0;
        
        GridBagConstraints gbcOther = new GridBagConstraints();
        gbcOther.gridx = 0;
        gbcOther.gridy = 1;
        gbcOther.fill = GridBagConstraints.BOTH;
        gbcOther.weightx = 1.0;
        gbcOther.weighty = 0.0;
        
        // Inisialisasi model dan list untuk Freelancer
        freelancerListModel = new DefaultListModel<>();
        jlistFreelancers = new JList<>(freelancerListModel);
        jlistFreelancers.setCellRenderer(new FreelancerRenderer()); // Pakai renderer kustom
        JScrollPane freelancerScrollPane = new JScrollPane(jlistFreelancers);
        // Tambahkan judul ke panel list
        freelancerScrollPane.setBorder(BorderFactory.createTitledBorder("Freelancer Tersedia"));
        leftPanel.add(freelancerScrollPane, gbcTop);
        
        JButton btnDeleteFreelancer = new JButton("Hapus");
        leftPanel.add(btnDeleteFreelancer, gbcOther);
        
        // Inisialisasi model dan list untuk Project
        projectListModel = new DefaultListModel<>();
        jlistProjects = new JList<>(projectListModel);
        jlistProjects.setCellRenderer(new ProjectRenderer()); // Pakai renderer kustom
        JScrollPane projectScrollPane = new JScrollPane(jlistProjects);
        projectScrollPane.setBorder(BorderFactory.createTitledBorder("Proyek Terbuka"));
        rightPanel.add(projectScrollPane, gbcTop);
        
        JButton btnDeleteProject = new JButton("Hapus");
        rightPanel.add(btnDeleteProject, gbcOther);
        
        // Masukkan kedua list ke split pane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        // --- Gabungkan semua panel ---
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);

        // --- Logika Tombol (Controller) ---
        // Aksi untuk tombol "Registrasi Freelancer"
        btnRegisterFreelancer.addActionListener((ActionEvent e) -> {
            showRegisterFreelancerDialog();
        });

        // Aksi untuk tombol "Buat Proyek"
        btnPostProject.addActionListener((ActionEvent e) -> {
            showPostProjectDialog();
        });
        
        btnDeleteFreelancer.addActionListener((ActionEvent e) -> {
            int index = jlistFreelancers.getSelectedIndex();
            if (jlistFreelancers.getSelectedIndex() >= 0) {
                showDeleteFreelancerConfirmation(index, freelancers.get(index).getName());
            }
        });
        
        btnDeleteProject.addActionListener((ActionEvent e) -> {
            int index = jlistProjects.getSelectedIndex();
            if (jlistProjects.getSelectedIndex() >= 0) {
                showDeleteProjectConfirmation(index, projects.get(index).getTitle());
            }
        });
        
        btnFreelancerVisibility.addActionListener((ActionEvent e) -> {
            freelancerScrollPane.setVisible(!freelancerScrollPane.isVisible());
            
            if (freelancerScrollPane.isVisible()) {
                splitPane.setDividerLocation(0.5);
                btnFreelancerVisibility.setText("Sembunyikan Freelancer");
            } else {
                splitPane.setDividerLocation(1.0);
                btnFreelancerVisibility.setText("Tampilkan Freelancer");
            }
            
            splitPane.revalidate();
            splitPane.repaint();
        });
        
        btnProjectVisibility.addActionListener((ActionEvent e) -> {
            projectScrollPane.setVisible(!projectScrollPane.isVisible());
            
            if (projectScrollPane.isVisible()) {
                splitPane.setDividerLocation(0.5);
                btnProjectVisibility.setText("Sembunyikan Proyek");
            } else {
                splitPane.setDividerLocation(1.0);
                btnProjectVisibility.setText("Sembunyikan Proyek");
            }
            
            splitPane.revalidate();
            splitPane.repaint();
        });
    }

    /**
     * Menampilkan dialog untuk mendaftarkan freelancer baru.
     */
    private void showRegisterFreelancerDialog() {
        // Buat panel kustom untuk dialog
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField skillField = new JTextField();
        JTextField rateField = new JTextField("0.0");
        JTextField ratingField = new JTextField("5.0"); // Rating awal

        panel.add(new JLabel("Nama:"));
        panel.add(nameField);
        panel.add(new JLabel("Skill Utama:"));
        panel.add(skillField);
        panel.add(new JLabel("Tarif per Jam ($):"));
        panel.add(rateField);
        panel.add(new JLabel("Rating Awal:"));
        panel.add(ratingField);

        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(this, panel, "Registrasi Freelancer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Ambil data dari field
                String name = nameField.getText();
                String skill = skillField.getText();
                double rate = Double.parseDouble(rateField.getText());
                double rating = Double.parseDouble(ratingField.getText());

                // Validasi input sederhana
                if (name.isEmpty() || skill.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nama dan Skill tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                mongoDriver.insertFreelancer(new Freelancer(name, skill, rate, rating));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tarif dan Rating harus angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Menampilkan dialog untuk membuat proyek baru.
     */
    private void showPostProjectDialog() {
        // Buat panel kustom
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField titleField = new JTextField();
        JTextField companyField = new JTextField();
        JTextField descField = new JTextField();
        JTextField budgetField = new JTextField("0.0");

        panel.add(new JLabel("Judul Proyek:"));
        panel.add(titleField);
        panel.add(new JLabel("Nama Perusahaan:"));
        panel.add(companyField);
        panel.add(new JLabel("Deskripsi Singkat:"));
        panel.add(descField);
        panel.add(new JLabel("Budget ($):"));
        panel.add(budgetField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Buat Proyek Baru",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText();
                String company = companyField.getText();
                String desc = descField.getText();
                double budget = Double.parseDouble(budgetField.getText());

                if (title.isEmpty() || company.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Judul dan Perusahaan tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                mongoDriver.insertProject(new Project(title, desc, budget, company));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Budget harus angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showDeleteFreelancerConfirmation(int index, String name) {
        int response = JOptionPane.showConfirmDialog(this,
                "Hapus " + name + "?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );

        if (response == JOptionPane.YES_OPTION) {
            if (index >= 0) {
                mongoDriver.deleteFreelancer(freelancers.get(index).getId());
            }
        }
    }
    
    private void showDeleteProjectConfirmation(int index, String title) {
        int response = JOptionPane.showConfirmDialog(this,
                "Hapus " + title + "?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );

        if (response == JOptionPane.YES_OPTION) {
            if (index >= 0) {
                mongoDriver.deleteProject(projects.get(index).getId());
            }
        }
    }
    
    public void refreshFreelancers() {
        freelancers.clear();
        freelancerListModel.clear();
        
        for (Freelancer freelancer : mongoDriver.getAllFreelancers()) {
            freelancers.add(freelancer);
            freelancerListModel.addElement(freelancer);
        }
    }
    
    public void refreshProjects() {
        projects.clear();
        projectListModel.clear();
        
        for (Project project : mongoDriver.getAllProjects()) {
            projects.add(project);
            projectListModel.addElement(project);
        }
    }

    /**
     * Memuat beberapa data contoh agar list tidak kosong saat aplikasi dimulai.
     */
    private void loadSampleData() {
        // Data Freelancer Contoh
        Freelancer f1 = new Freelancer("Budi Setiawan", "Programmer Java", 50, 4.8);
        Freelancer f2 = new Freelancer("Ani Lestari", "Desainer UI/UX", 45, 4.9);
        Freelancer f3 = new Freelancer("Rahmat Hidayat", "Penulis Konten", 30, 4.5);

        freelancers.add(f1);
        freelancers.add(f2);
        freelancers.add(f3);

        freelancerListModel.addElement(f1);
        freelancerListModel.addElement(f2);
        freelancerListModel.addElement(f3);

        // Data Proyek Contoh
        Project p1 = new Project("Sistem E-Commerce", "Membuat toko online", 5000, "Toko Maju Jaya");
        Project p2 = new Project("Desain Logo", "Desain logo untuk startup baru", 500, "MulaiApps");

        projects.add(p1);
        projects.add(p2);

        projectListModel.addElement(p1);
        projectListModel.addElement(p2);
    }
    
    public void displayDatabaseError() {
        JOptionPane.showMessageDialog(this, 
            "Terjadi kesalahan saat menghubungkan ke database.\n" +
            "Harap coba lagi atau periksa pengaturan koneksi.", 
            "Kesalahan Koneksi Database", 
            JOptionPane.ERROR_MESSAGE);
    }
}

// =====================================================================
// --- Kelas Renderer Kustom (View) ---
// Kelas ini digunakan untuk membuat tampilan JList lebih cantik
// daripada sekadar teks biasa.
// =====================================================================

/**
 * Custom Cell Renderer untuk JList Freelancer.
 * Ini membuat tampilan list lebih informatif dan rapi.
 */
class FreelancerRenderer extends JPanel implements ListCellRenderer<Freelancer> {
    private JLabel lblName = new JLabel();
    private JLabel lblSkill = new JLabel();
    private JLabel lblDetails = new JLabel();
    
    public FreelancerRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel westPanel = new JPanel(new GridLayout(2, 1));
        westPanel.add(lblName);
        westPanel.add(lblSkill);
        
        add(westPanel, BorderLayout.CENTER);
        add(lblDetails, BorderLayout.EAST);
        
        // Atur font
        lblName.setFont(new Font("Arial", Font.BOLD, 14));
        lblSkill.setFont(new Font("Arial", Font.ITALIC, 12));
        lblDetails.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Freelancer> list, Freelancer freelancer, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        
        // Set data ke label
        lblName.setText(freelancer.getName());
        lblSkill.setText(freelancer.getSkill());
        lblDetails.setText(String.format("$%.2f/jam | %.1f Bintang", 
                                        freelancer.getRatePerHour(), freelancer.getRating()));

        // Atur warna background saat item dipilih
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        return this;
    }
}

/**
 * Custom Cell Renderer untuk JList Project.
 */
class ProjectRenderer extends JPanel implements ListCellRenderer<Project> {
    private JLabel lblTitle = new JLabel();
    private JLabel lblCompany = new JLabel();
    private JLabel lblBudget = new JLabel();

    public ProjectRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel westPanel = new JPanel(new GridLayout(2, 1));
        westPanel.add(lblTitle);
        westPanel.add(lblCompany);

        add(westPanel, BorderLayout.CENTER);
        add(lblBudget, BorderLayout.EAST);
        
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblCompany.setFont(new Font("Arial", Font.ITALIC, 12));
        lblBudget.setFont(new Font("Arial", Font.BOLD, 12));
        lblBudget.setForeground(new Color(0, 100, 0)); // Warna hijau tua
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Project> list, Project project, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        
        lblTitle.setText(project.getTitle());
        lblCompany.setText("oleh " + project.getCompanyName());
        lblBudget.setText(String.format("Budget: $%.2f", project.getBudget()));

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        return this;
    }
}