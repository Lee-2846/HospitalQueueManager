package hospital.queue.manager.gui;

import hospital.queue.manager.logic.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PatientPanel extends JPanel {
    private JTextField nameField, contactField;
    private JComboBox<String> deptBox, problemBox, viewDeptBox;
    private JCheckBox emergencyBox;
    private JButton bookBtn, refreshBtn;
    private DefaultTableModel tableModel;
    private JTable queueTable;

    private static final int AVERAGE_APPOINTMENT_MINS = 10;
    private static HospitalManager hospitalManager = HospitalManager.getInstance();

    private static final String[] DEPARTMENTS = {
        "General", "Neurology", "Orthopedics", "Cardiology", "Dermatology",
        "Gastroenterology", "Ophthalmology", "Dentistry", "Gynecology", "Pediatrics"
    };

    private static final Map<String, String[]> PROBLEMS_BY_DEPT = new HashMap<String, String[]>() {{
        put("General", new String[]{"Fever", "Body ache", "Fatigue"});
        put("Cardiology", new String[]{"Chest pain", "Palpitations", "BP issues"});
        put("Pediatrics", new String[]{"Vaccination", "Child fever", "Child cold", "Growth Check-up"});
        put("Orthopedics", new String[]{"Fracture", "Sprain", "Joint pain"});
        put("Neurology", new String[]{"Headache", "Dizziness", "Numbness"});
        put("Dermatology", new String[]{"Rash", "Acne", "Allergy"});
        put("Gastroenterology", new String[]{"Stomach pain", "Acidity", "Diarrhea"});
        put("Ophthalmology", new String[]{"Eye pain", "Blurry vision", "Redness"});
        put("Dentistry", new String[]{"Cavity", "Toothache", "Braces"});
        put("Gynecology", new String[]{"Pregnancy Check-up", "Periods Issues", "PCOD", "Consultation"});
    }};

    public PatientPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(246,249,255));

        JPanel queueSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queueSelectPanel.setOpaque(false);
        queueSelectPanel.add(new JLabel("View Department Queue:"));

        viewDeptBox = new JComboBox<>(DEPARTMENTS);
        queueSelectPanel.add(viewDeptBox);

        refreshBtn = new JButton("Refresh");
        styleButton(refreshBtn);
        queueSelectPanel.add(refreshBtn);
        refreshBtn.addActionListener(e -> updateQueueTable());
        viewDeptBox.addActionListener(e -> updateQueueTable());
        add(queueSelectPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBackground(new Color(241, 246, 251));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Contact:"));
        contactField = new JTextField();
        inputPanel.add(contactField);

        inputPanel.add(new JLabel("Department:"));
        deptBox = new JComboBox<>(DEPARTMENTS);
        inputPanel.add(deptBox);

        inputPanel.add(new JLabel("Problem:"));
        problemBox = new JComboBox<>();
        inputPanel.add(problemBox);

        deptBox.addActionListener(e -> {
            String selectedDept = (String) deptBox.getSelectedItem();
            problemBox.removeAllItems();
            for (String p : PROBLEMS_BY_DEPT.getOrDefault(selectedDept, new String[]{})) {
                problemBox.addItem(p);
            }
        });
        deptBox.setSelectedIndex(0);
        deptBox.getActionListeners()[0].actionPerformed(null);

        emergencyBox = new JCheckBox("Emergency Case");
        inputPanel.add(emergencyBox);

        bookBtn = new JButton("Book Appointment");
        styleButton(bookBtn);
        inputPanel.add(bookBtn);

        add(inputPanel, BorderLayout.SOUTH);

        String[] columnNames = {"Queue #", "Patient Name", "Department", "Problem", "Status", "Assigned Doctor", "Wait Time", "Emergency"};
        tableModel = new DefaultTableModel(columnNames, 0);
        queueTable = new JTable(tableModel);
        styleTable(queueTable);
        queueTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("Yes".equals(value)) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD, 13f));
                } else {
                    c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                    c.setFont(c.getFont().deriveFont(Font.PLAIN, 13f));
                }
                return c;
            }
        });

        add(new JScrollPane(queueTable), BorderLayout.CENTER);
        bookBtn.addActionListener(e -> addNewPatient());

        updateQueueTable();
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(98, 156, 235));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 17, 7, 17));
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(new Color(248,250,255));
        table.setSelectionBackground(new Color(192,222,255));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(220,225,235));
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(224,238,255));
        header.setForeground(new Color(49,60,78));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void addNewPatient() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String department = (String) deptBox.getSelectedItem();
        String problem = (String) problemBox.getSelectedItem();
        boolean emergency = emergencyBox.isSelected();

        if (name.isEmpty() || contact.isEmpty() || problem == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        LinkedList<Patient> queue = hospitalManager.getDepartmentQueues().get(department);
        int queuePosition = 0;
        for (Patient p : queue) {
            if (!"Done".equals(p.getStatus())) {
                queuePosition++;
            }
        }

        int numDoctors = 0;
        for (Doctor d : hospitalManager.getDoctors()) {
            if (department.equals(d.getDepartment())) numDoctors++;
        }

        int patientsAhead = emergency ? 0 : queuePosition;
        int waitTimeCalculation = patientsAhead - numDoctors;
        int waitTime = (waitTimeCalculation < 0) ? 0 : (waitTimeCalculation + 1) * AVERAGE_APPOINTMENT_MINS;

        Patient patient = new Patient(name, department, contact, emergency, problem);
        hospitalManager.bookAppointment(patient);

        JOptionPane.showMessageDialog(this,
                "You are in position #" + (patientsAhead + 1) + ".\nPatients ahead: " + patientsAhead +
                        "\nEstimated wait time: " + waitTime + " minutes."
        );

        updateQueueTable();
        clearInput();
    }

    private void updateQueueTable() {
        tableModel.setRowCount(0);
        String viewDept = (String) viewDeptBox.getSelectedItem();
        LinkedList<Patient> queue = hospitalManager.getDepartmentQueues().get(viewDept);

        int queueNumber = 1;
        int numDoctors = 0;
        for (Doctor d : hospitalManager.getDoctors()) {
            if (viewDept.equals(d.getDepartment())) numDoctors++;
        }

        int waitingOrConsulting = 0;
        for (Patient p : queue) {
            String status = p.getStatus();
            String waitTimeStr;

            if ("Waiting".equals(status)) {
                int waitTimeCalculation = waitingOrConsulting - numDoctors;
                int waitTime = (waitTimeCalculation < 0) ? 0 : (waitTimeCalculation + 1) * AVERAGE_APPOINTMENT_MINS;
                waitTimeStr = waitTime + " min";
                waitingOrConsulting++;
            } else if ("In Consultation".equals(status)) {
                waitTimeStr = "With Doctor";
                waitingOrConsulting++;
            } else {
                waitTimeStr = "Completed";
            }

            tableModel.addRow(new Object[]{
                    queueNumber++,
                    p.getName(),
                    p.getDepartment(),
                    p.getProblem(),
                    p.getStatus(),
                    p.getAssignedDoctor(),
                    waitTimeStr,
                    p.isEmergency() ? "Yes" : "No"
            });
        }
    }

    private void clearInput() {
        nameField.setText("");
        contactField.setText("");
        deptBox.setSelectedIndex(0);
        emergencyBox.setSelected(false);
    }
}
