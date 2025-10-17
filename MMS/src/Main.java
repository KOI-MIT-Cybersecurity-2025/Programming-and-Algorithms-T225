package src;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static GymManager manager = new GymManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Member Management System.");
        System.out.println("Please use option 1 to load a file and begin.");

        int choice = 0;
        while (choice != 9) { // Exit option is now 9
            displayMenu();
            try {
                System.out.print("Please choose an option: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        handleLoadFile();
                        break;
                    case 2:
                        handleViewMembers();
                        break;
                    case 3:
                        handleSearchMenu(); // New Search Sub-menu
                        break;
                    case 4:
                        handleAddMember();
                        break;
                    case 5:
                        handleAddPerformanceRecord();
                        break;
                    case 6:
                        handleUpdateMember();
                        break;
                    case 7:
                        handleDeleteMember();
                        break;
                    case 8:
                        handleSaveToFile();
                        break;
                    case 9:
                        System.out.println("Exiting system. Your changes have been saved to member_data.csv.");
                        manager.saveToFile("member_data.csv");
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 1 and 9.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n===== Member Management System =====");
        System.out.println("1. Load records from a file");
        System.out.println("2. View all members");
        System.out.println("3. Search / Filter Members..."); // New option
        System.out.println("4. Add a new member");
        System.out.println("5. Add Performance Record");
        System.out.println("6. Update member information");
        System.out.println("7. Delete a member");
        System.out.println("8. Save records to a new file");
        System.out.println("9. Exit and Save");
        System.out.println("====================================");
    }

    /**
     * NEW METHOD: Displays and handles the search sub-menu.
     */
    private static void handleSearchMenu() {
        int choice = 0;
        while (choice != 4) {
            System.out.println("\n--- Search & Filter Menu ---");
            System.out.println("1. Find members by name");
            System.out.println("2. Filter members by type");
            System.out.println("3. Filter members by performance");
            System.out.println("4. Back to Main Menu");
            System.out.println("----------------------------");
            System.out.print("Please choose an option: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        handleSearchByName();
                        break;
                    case 2:
                        handleFilterByType();
                        break;
                    case 3:
                        handleFilterByPerformance();
                        break;
                    case 4:
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 1 and 4.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * NEW METHOD: Handles searching for members by name.
     */
    private static void handleSearchByName() {
        System.out.print("Enter name to search for: ");
        String name = scanner.nextLine();
        List<Member> results = manager.findMembersByName(name);
        displaySearchResults(results, "name containing '" + name + "'");
    }

    /**
     * NEW METHOD: Handles filtering members by type.
     */
    private static void handleFilterByType() {
        System.out.print("Enter member type to filter by (Regular/Premium): ");
        String type = scanner.nextLine();
        if (!type.equalsIgnoreCase("Regular") && !type.equalsIgnoreCase("Premium")) {
            System.err.println("Invalid type. Please enter 'Regular' or 'Premium'.");
            return;
        }
        List<Member> results = manager.findMembersByType(type);
        displaySearchResults(results, "type '" + type + "'");
    }
    
    /**
     * NEW METHOD: Handles filtering members by performance.
     */
    private static void handleFilterByPerformance() {
        try {
            System.out.print("Enter performance month (1-12): ");
            int month = scanner.nextInt();
            System.out.print("Enter performance year (e.g., 2025): ");
            int year = scanner.nextInt();
            System.out.print("Filter by goal achieved? (yes/no): ");
            String goalStr = scanner.next();
            scanner.nextLine(); // consume newline
            boolean goalAchieved = goalStr.equalsIgnoreCase("yes");
            
            List<Member> results = manager.findMembersByPerformance(month, year, goalAchieved);
            String status = goalAchieved ? "Achieved" : "Not Achieved";
            displaySearchResults(results, "performance for " + month + "/" + year + " with status '" + status + "'");

        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter numbers for month and year.");
            scanner.nextLine(); // Clear bad input
        }
    }

    /**
     * NEW HELPER METHOD: Displays search results or a 'not found' message.
     */
    private static void displaySearchResults(List<Member> results, String criteria) {
        if (results.isEmpty()) {
            System.out.println("No members found matching criteria: " + criteria);
        } else {
            System.out.println("\n--- Search Results: " + results.size() + " member(s) found for " + criteria + " ---");
            for (Member member : results) {
                System.out.println(member.toString());
                System.out.println("--------------------------------------------------");
            }
        }
    }

    // --- Existing Methods (no changes below this line) ---

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
            System.out.println("-------------------");
        }
    }

    private static void handleAddMember() {
        System.out.print("Enter Member Type (Regular/Premium): ");
        String type = scanner.nextLine();
        System.out.print("Enter Member ID (e.g., M011): ");
        String id = scanner.nextLine();
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        if (manager.findMemberById(id) != null) {
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
                scanner.nextLine();
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

    private static void handleAddPerformanceRecord() {
        System.out.print("Enter the ID of the member to add a performance record for: ");
        String id = scanner.nextLine();
        Member member = manager.findMemberById(id);

        if (member == null) {
            System.err.println("Member with ID " + id + " not found.");
            return;
        }

        try {
            System.out.print("Enter performance month (1-12): ");
            int month = scanner.nextInt();
            System.out.print("Enter performance year (e.g., 2025): ");
            int year = scanner.nextInt();
            System.out.print("Did the member achieve their goal? (yes/no): ");
            String goalStr = scanner.next();
            scanner.nextLine();

            boolean goalAchieved = goalStr.equalsIgnoreCase("yes");

            if (month < 1 || month > 12 || year < 2000) {
                System.err.println("Invalid month or year. Record not added.");
                return;
            }

            member.addPerformanceRecord(new Performance(month, year, goalAchieved));
            System.out.println("Performance record added successfully for " + member.getFullName() + ".");

        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter numbers for month and year.");
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
                } catch (NumberFormatException e) {
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
