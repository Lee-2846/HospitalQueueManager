package hospital.queue.manager.logic;

import java.util.*;

public class HospitalManager {
    private Map<String, LinkedList<Patient>> departmentQueues;
    private List<Doctor> doctors;
    private Map<Doctor, Patient> assignments;
    private static HospitalManager instance;

    private HospitalManager() {
        departmentQueues = new HashMap<>();
        doctors = new ArrayList<>();
        assignments = new HashMap<>();

        String[] departments = {"General", "Neurology", "Orthopedics", "Cardiology", "Dermatology",
                "Gastroenterology", "Ophthalmology", "Dentistry", "Gynecology", "Pediatrics"};

        for (String dept : departments) {
            departmentQueues.put(dept, new LinkedList<>());
        }

        // 2 doctors per department
        doctors.add(new Doctor("Dr. Smith", "General"));
        doctors.add(new Doctor("Dr. Alice", "General"));
        doctors.add(new Doctor("Dr. John", "Neurology"));
        doctors.add(new Doctor("Dr. Clara", "Neurology"));
        doctors.add(new Doctor("Dr. Patel", "Orthopedics"));
        doctors.add(new Doctor("Dr. Kumar", "Orthopedics"));
        doctors.add(new Doctor("Dr. Lee", "Cardiology"));
        doctors.add(new Doctor("Dr. Gomez", "Cardiology"));
        doctors.add(new Doctor("Dr. Wang", "Dermatology"));
        doctors.add(new Doctor("Dr. Silva", "Dermatology"));
        doctors.add(new Doctor("Dr. Rao", "Gastroenterology"));
        doctors.add(new Doctor("Dr. Das", "Gastroenterology"));
        doctors.add(new Doctor("Dr. Roy", "Ophthalmology"));
        doctors.add(new Doctor("Dr. Sinha", "Ophthalmology"));
        doctors.add(new Doctor("Dr. Fernandes", "Dentistry"));
        doctors.add(new Doctor("Dr. Bhatt", "Dentistry"));
    }

    public static synchronized HospitalManager getInstance() {
        if (instance == null) instance = new HospitalManager();
        return instance;
    }

    // --- UPDATED Emergency logic ---
    public synchronized void bookAppointment(Patient patient) {
        LinkedList<Patient> queue = departmentQueues.get(patient.getDepartment());
        if (patient.isEmergency()) {
            // Find the last emergency
            int lastEmergencyIndex = -1;
            for (int i = 0; i < queue.size(); i++) {
                Patient p = queue.get(i);
                if (p.isEmergency() && !"Done".equals(p.getStatus())) {
                    lastEmergencyIndex = i;
                }
            }
            if (lastEmergencyIndex == -1) {
                queue.addFirst(patient); // no emergency yet
            } else {
                queue.add(lastEmergencyIndex + 1, patient); // right after last emergency
            }
        } else {
            queue.addLast(patient);
        }
        patient.setStatus("Waiting");
        patient.setAssignedDoctor("-");
        assignDoctors();
    }
    // ------------------------------

    private synchronized void assignDoctors() {
        List<Doctor> availableDoctors = new ArrayList<>();
        for (Doctor d : doctors) {
            if (d.isAvailable()) {
                availableDoctors.add(d);
            }
        }
        // Assign each available doctor in their department to first waiting patient in that department's queue
        for (Doctor doctor : availableDoctors) {
            LinkedList<Patient> queue = departmentQueues.get(doctor.getDepartment());
            Patient toAssign = null;
            for (Patient p : queue) {
                if ("Waiting".equals(p.getStatus())) {
                    toAssign = p;
                    break;
                }
            }
            if (toAssign != null) {
                doctor.setAvailable(false);
                toAssign.setStatus("In Consultation");
                toAssign.setAssignedDoctor(doctor.getName());
                assignments.put(doctor, toAssign);
            }
        }
    }

    public synchronized void doctorDone(Doctor doctor) {
        Patient patient = assignments.get(doctor);
        if (patient != null) {
            patient.setStatus("Done");
            LinkedList<Patient> queue = departmentQueues.get(patient.getDepartment());
            queue.remove(patient);
            assignments.remove(doctor);
        }
        doctor.setAvailable(true);
        assignDoctors();
    }

    public Map<String, LinkedList<Patient>> getDepartmentQueues() { return departmentQueues; }
    public List<Doctor> getDoctors() { return doctors; }
    public Map<Doctor, Patient> getAssignments() { return assignments; }
}
