package hospital.queue.manager;

import javax.swing.*;
import hospital.queue.manager.gui.ManagerPanel;
import hospital.queue.manager.gui.PatientPanel;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hospital Queue Manager");
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Manager Section", new ManagerPanel());
        tabs.add("Patient Section", new PatientPanel());
        frame.add(tabs);
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
