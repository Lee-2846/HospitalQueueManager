package hospital.queue.manager.logic;

public class Doctor {
    private String name;
    private String department;
    private boolean available;

    public Doctor(String name, String department) {
        this.name = name;
        this.department = department;
        this.available = true;
    }

    public String getName() { return name; }
    public String getDepartment() { return department; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
