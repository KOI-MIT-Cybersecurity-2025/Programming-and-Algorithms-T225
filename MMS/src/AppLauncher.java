package src;

import java.util.Scanner;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the application.
 * Allows the user to choose between the Text-Based Interface (TBI)
 * or the Graphical User Interface (GUI).
 */
public class AppLauncher {

    public static void main(String[] args) {
        // Create ONE GymManager instance that will be shared by both UIs
        GymManager manager = new GymManager();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Member Management System");
        System.out.println("=======================================");
        System.out.println("Please choose your interface mode:");
        System.out.println("1. Text-Based Interface (Console)");
        System.out.println("2. Graphical User Interface (GUI)");
        System.out.print("Enter your choice (1 or 2): ");

        int choice = 0;
        try {
            choice = scanner.nextInt();
        } catch (Exception e) {
            choice = 1; // Default to text if input is bad
        }

        if (choice == 2) {
            // Run the GUI
            // SwingUtilities.invokeLater ensures the GUI runs on the correct thread
            SwingUtilities.invokeLater(() -> new GymGUI(manager));
        } else {
            // Run the Text-Based Interface
            GymApplication textApp = new GymApplication(manager, scanner);
            textApp.run();
        }
    }
}

