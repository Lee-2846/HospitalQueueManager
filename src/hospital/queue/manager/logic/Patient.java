package hospital.queue.manager.logic;

import java.util.Objects;

public class Patient {
    private String name;
    private String department;
    private String contact;
    private boolean isEmergency;
    private String status; // "Waiting", "In Consultation", "Done"
    private String assignedDoctor;
    private String problem;

    public Patient(String name, String department, String contact, boolean isEmergency, String problem) {
        this.name = name;
        this.department = department;
        this.contact = contact;
        this.isEmergency = isEmergency;
        this.problem = problem;
        this.status = "Waiting";
        this.assignedDoctor = "-";
    }

    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getContact() { return contact; }
    public boolean isEmergency() { return isEmergency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignedDoctor() { return assignedDoctor; }
    public void setAssignedDoctor(String doctor) { this.assignedDoctor = doctor; }
    public String getProblem() { return problem; }
    public void setProblem(String problem) { this.problem = problem; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Patient)) return false;
        Patient other = (Patient) obj;
        return isEmergency == other.isEmergency
                && Objects.equals(name, other.name)
                && Objects.equals(department, other.department)
                && Objects.equals(contact, other.contact)
                && Objects.equals(problem, other.problem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, department, contact, isEmergency, problem);
    }
}
