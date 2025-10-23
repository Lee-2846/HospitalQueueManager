package hospital.queue.manager.gui;

import hospital.queue.manager.logic.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class ManagerPanel extends JPanel {
    private JTable doctorTable;
    private JButton markDoneBtn, refreshBtn;
    private static HospitalManager hospitalManager = HospitalManager.getInstance();

    public ManagerPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(246,249,255));

        doctorTable = new JTable();
        styleTable(doctorTable);
        doctorTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(246,249,255));
        markDoneBtn = new JButton("Mark Doctor Done");
        styleButton(markDoneBtn);
        refreshBtn = new JButton("Refresh");
        styleButton(refreshBtn);

        buttonPanel.add(markDoneBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();

        markDoneBtn.addActionListener(e -> {
            int row = doctorTable.getSelectedRow();
            if (row >= 0) {
                Object obj = doctorTable.getValueAt(row, 0);
                if (obj != null && obj.toString().startsWith("Dr.")) {
                    Doctor doc = getDoctorByName(obj.toString());
                    if (doc != null) {
                        hospitalManager.doctorDone(doc);
                        refreshTable();
                    }
                }
            }
        });

        refreshBtn.addActionListener(e -> refreshTable());
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

    private Doctor getDoctorByName(String name) {
        for (Doctor doc : hospitalManager.getDoctors()) {
            if (doc.getName().equals(name)) return doc;
        }
        return null;
    }

    private void refreshTable() {
        String[] colNames = {"Doctor Name", "Availability", "Current Patient"};

        List<Doctor> allDocs = new ArrayList<>(hospitalManager.getDoctors());
        allDocs.sort(Comparator.comparing(Doctor::getDepartment).thenComparing(Doctor::getName));
        Map<Doctor, Patient> assignments = hospitalManager.getAssignments();

        List<Object[]> rows = new ArrayList<>();
        String prevDept = null;
        for (Doctor doc : allDocs) {
            if (!doc.getDepartment().equals(prevDept)) {
                // Insert a department group header
                rows.add(new Object[]{doc.getDepartment(), "", ""});
                prevDept = doc.getDepartment();
            }
            Patient patient = assignments.get(doc);
            rows.add(new Object[]{
                doc.getName(),
                doc.isAvailable() ? "Available" : "Busy",
                (patient == null) ? "None" : patient.getName()
            });
        }

        DefaultTableModel model = new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        for (Object[] row : rows) model.addRow(row);
        doctorTable.setModel(model);

        // Custom renderer for group headers & "Busy" coloring
        doctorTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String nameCell = table.getValueAt(row, 0).toString();

                // Department header row
                if (!nameCell.startsWith("Dr.")) {
                    c.setFont(c.getFont().deriveFont(Font.BOLD, 15f));
                    c.setBackground(new Color(235, 240, 255));
                    c.setForeground(new Color(51, 77, 158));
                    return c;
                }

                // Red color for "Busy" in Availability column
                if (column == 1 && "Busy".equals(value)) {
                    c.setForeground(Color.RED);
                    c.setFont(table.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setFont(table.getFont());
                    c.setForeground(isSelected
                        ? table.getSelectionForeground()
                        : table.getForeground());
                }

                c.setBackground(isSelected
                    ? table.getSelectionBackground()
                    : table.getBackground());

                return c;
            }
        });
    }
}
