package src;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Main Class (The "View")
 * This class is responsible for the text-based user interface.
 * It interacts with the user and tells the GymManager what to do.
 */
public class Main {

    // We declare these here so all methods in the class can access them.
    private static GymManager manager = new GymManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // The program now starts with an empty list. The user must load a file.
        System.out.println("Welcome to the Member Management System.");
        System.out.println("Please use option 1 to load a file and begin.");

        int choice = 0;
        while (choice != 7) { // The exit option is now 7
            displayMenu();
            try {
                System.out.print("Please choose an option: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character left by nextInt()

                switch (choice) {
                    case 1:
                        handleLoadFile(); // New option to manually load
                        break;
                    case 2:
                        handleViewMembers();
                        break;
                    case 3:
                        handleAddMember();
                        break;
                    case 4:
                        handleUpdateMember();
                        break;
                    case 5:
                        handleDeleteMember();
                        break;
                    case 6:
                        handleSaveToFile();
                        break;
                    case 7:
                        System.out.println("Exiting system. Your changes have been saved to latest_member_data.csv.");
                        manager.saveToFile("latest_member_data.csv");
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 1 and 7.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the bad input from the scanner
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n===== Member Management System =====");
        System.out.println("1. Load records from a file"); // New menu option
        System.out.println("2. View all members");
        System.out.println("3. Add a new member");
        System.out.println("4. Update member information");
        System.out.println("5. Delete a member");
        System.out.println("6. Save records to a new file");
        System.out.println("7. Exit and Save");
        System.out.println("====================================");
    }

    /**
     * NEW METHOD: Handles the manual loading of a member data file.
     */
    private static void handleLoadFile() {
        System.out.print("Enter the filename to load (e.g., member_data.csv): ");
        String filename = scanner.nextLine();

        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Error: Filename cannot be empty. Load cancelled.");
            return;
        }
        manager.loadFromFile(filename);
    }
    
    private static void handleViewMembers() {
        List<Member> members = manager.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("There are no members in the system. Please load a file or add a member.");
            return;
        }
        System.out.println("\n--- All Members ---");
        for (Member member : members) {
            System.out.println(member.toString());
        }
        System.out.println("-------------------");
    }

    private static void handleAddMember() {
        System.out.print("Enter Member Type (Regular/Premium): ");
        String type = scanner.nextLine();
        System.out.print("Enter Member ID (e.g., M011): ");
        String id = scanner.nextLine();
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        if(manager.findMemberById(id) != null) {
            System.err.println("Error: A member with this ID already exists.");
            return;
        }

        try {
            if (type.equalsIgnoreCase("Regular")) {
                manager.addMember(new RegularMember(id, name, LocalDate.now()));
                System.out.println("Regular member added successfully!");
            } else if (type.equalsIgnoreCase("Premium")) {
                System.out.print("Enter Personal Trainer Fee: ");
                double fee = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                manager.addMember(new PremiumMember(id, name, LocalDate.now(), fee));
                System.out.println("Premium member added successfully!");
            } else {
                System.err.println("Invalid member type. Please enter 'Regular' or 'Premium'.");
            }
        } catch (InputMismatchException e) {
            System.err.println("Invalid fee format. Please enter a number.");
            scanner.nextLine();
        }
    }

    private static void handleUpdateMember() {
        System.out.print("Enter the ID of the member to update: ");
        String id = scanner.nextLine();
        Member member = manager.findMemberById(id);

        if (member == null) {
            System.err.println("Member with ID " + id + " not found.");
            return;
        }

        System.out.print("Enter new Full Name (or press Enter to keep '" + member.getFullName() + "'): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            member.setFullName(newName);
            System.out.println("Name updated.");
        }
        
        if (member instanceof PremiumMember) {
            PremiumMember premium = (PremiumMember) member;
            System.out.print("Enter new Personal Trainer Fee (or press Enter to keep '" + premium.getPersonalTrainerFee() + "'): ");
            String newFeeStr = scanner.nextLine();
            if (!newFeeStr.isEmpty()) {
                try {
                    double newFee = Double.parseDouble(newFeeStr);
                    premium.setPersonalTrainerFee(newFee);
                    System.out.println("Fee updated.");
                } catch(NumberFormatException e) {
                    System.err.println("Invalid fee format. Fee was not updated.");
                }
            }
        }
        System.out.println("Update complete for member " + id + ".");
    }

    private static void handleDeleteMember() {
        System.out.print("Enter the ID of the member to delete: ");
        String id = scanner.nextLine();
        boolean deleted = manager.deleteMember(id);
        if (deleted) {
            System.out.println("Member " + id + " was successfully deleted.");
        } else {
            System.err.println("Member with ID " + id + " not found.");
        }
    }
    
    private static void handleSaveToFile() {
        System.out.print("Enter the filename to save to (e.g., members_backup.csv): ");
        String filename = scanner.nextLine();
        
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Error: Filename cannot be empty. Save cancelled.");
            return; 
        }

        manager.saveToFile(filename);
    }
}

