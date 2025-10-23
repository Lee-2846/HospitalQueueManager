package hospital.queue.manager.gui;

import hospital.queue.manager.logic.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class ManagerPanel extends JPanel {
    private JTable doctorTable;
    private JButton markDoneBtn, refreshBtn;
    private static HospitalManager hospitalManager = HospitalManager.getInstance();

    public ManagerPanel() {
        setLayout(new BorderLayout());

        doctorTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        markDoneBtn = new JButton("Mark Doctor Done");
        refreshBtn = new JButton("Refresh");

        buttonPanel.add(markDoneBtn);
        buttonPanel.add(refreshBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();

        markDoneBtn.addActionListener(e -> {
            int row = doctorTable.getSelectedRow();
            if (row >= 0) {
                Doctor doc = hospitalManager.getDoctors().get(row);
                hospitalManager.doctorDone(doc);
                refreshTable();
            }
        });

        refreshBtn.addActionListener(e -> refreshTable());
    }

    private void refreshTable() {
        String[] colNames = {"Doctor Name", "Department", "Availability", "Current Patient"};
        Object[][] data = new Object[hospitalManager.getDoctors().size()][4];

        Map<Doctor, Patient> assignments = hospitalManager.getAssignments();

        for (int i = 0; i < hospitalManager.getDoctors().size(); i++) {
            Doctor doc = hospitalManager.getDoctors().get(i);
            data[i][0] = doc.getName();
            data[i][1] = doc.getDepartment();
            data[i][2] = doc.isAvailable() ? "Available" : "Busy";
            Patient patient = assignments.get(doc);
            data[i][3] = (patient == null) ? "None" : patient.getName() + " (" + patient.getDepartment() + ")";
        }

        DefaultTableModel model = new DefaultTableModel(data, colNames) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        doctorTable.setModel(model);
    }
}
