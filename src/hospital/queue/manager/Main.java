package hospital.queue.manager;

import javax.swing.*;
import hospital.queue.manager.gui.ManagerPanel;
import hospital.queue.manager.gui.PatientPanel;

public class Main {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback to default L&F
        }

        JFrame frame = new JFrame("Hospital Queue Manager");
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Manager Section", new ManagerPanel());
        tabs.add("Patient Section", new PatientPanel());
        frame.add(tabs);
        frame.setSize(950, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
