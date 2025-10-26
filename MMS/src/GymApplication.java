package src;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * The main class for the Text-Based Interface (TBI) of the Gym Management
 * System.
 *
 * INDIVIDUAL PROJECT UPDATES:
 * - This class no longer has a main() method (AppLauncher does).
 * - It receives a GymManager and Scanner in its constructor.
 * - A new "Sort Members" sub-menu has been added.
 */
public class GymApplication {

    private GymManager manager;
    private Scanner scanner;

    /**
     * Constructor that accepts an existing GymManager instance and Scanner.
     * This allows the TBI and GUI to share the *same* data and scanner.
     */
    public GymApplication(GymManager manager, Scanner scanner) {
        this.manager = manager;
        this.scanner = scanner;
    }

    /**
     * The main run loop for the text-based application.
     */
    public void run() {
        // Load the default file on startup for the TBI session
        manager.loadFromFile("gym_records.csv");
        System.out.println("Welcome to the Member Management System (Text Mode).");
        System.out.println(manager.getAllMembers().size() + " records loaded from gym_records.csv.");

        int choice = 0;
        while (choice != 10) { // Exit option is now 10
            displayMainMenu();
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                processMainMenuChoice(choice);
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
        System.out.println("Exiting Text Mode...");
    }

    private void displayMainMenu() {
        System.out.println("\n===== Member Management System (Text Mode) =====");
        System.out.println("1. Load records from a file");
        System.out.println("2. View all members");
        System.out.println("3. Add a new member");
        System.out.println("4. Update a member's status (Freeze/Activate)");
        System.out.println("5. Add a performance record");
        System.out.println("6. Delete a member");
        System.out.println("7. Search / Filter Members...");
        System.out.println("8. Sort Members..."); // NEW SORT OPTION
        System.out.println("9. Save records to a new file");
        System.out.println("10. Exit and Save to gym_records.csv");
        System.out.println("==============================================");
        System.out.print("Please choose an option: ");
    }

    private void processMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleLoadFile();
                break;
            case 2:
                handleViewAllMembers();
                break;
            case 3:
                handleAddMember();
                break;
            case 4:
                handleUpdateStatus();
                break;
            case 5:
                handleAddPerformanceRecord();
                break;
            case 6:
                handleDeleteMember();
                break;
            case 7:
                handleSearchMenu();
                break;
            case 8:
                handleSortMenu(); // NEW
                break;
            case 9:
                handleSaveToFile();
                break;
            case 10:
                manager.saveToFile("gym_records.csv");
                System.out.println("Data saved to gym_records.csv.");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    /**
     * NEW METHOD: Displays and handles the sorting sub-menu.
     */
    private void handleSortMenu() {
        System.out.println("\n--- Sort Members Menu ---");
        System.out.println("1. Sort by Member ID (Default)");
        System.out.println("2. Sort by Name");
        System.out.println("3. Sort by Join Date");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose an option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    manager.sortMembersById();
                    System.out.println("Members sorted by ID.");
                    handleViewAllMembers(); // Show results
                    break;
                case 2:
                    manager.sortMembersByName();
                    System.out.println("Members sorted by Name.");
                    handleViewAllMembers(); // Show results
                    break;
                case 3:
                    manager.sortMembersByJoinDate();
                    System.out.println("Members sorted by Join Date.");
                    handleViewAllMembers(); // Show results
                    break;
                case 4:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        }
    }

    // --- All other handler methods (no changes) ---

    private void handleLoadFile() {
        System.out.print("Enter filename to load (e.g., gym_records.csv): ");
        String filename = scanner.nextLine();
        manager.loadFromFile(filename);
    }

    private void handleViewAllMembers() {
        List<Member> members = manager.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("There are no members in the system.");
        } else {
            System.out.println("\n--- All Members (" + members.size() + ") ---");
            members.forEach(member -> {
                System.out.println(member);
                System.out.println("-------------------");
            });
        }
    }

    private void handleAddMember() {
        try {
            System.out.print("Enter Member Type (Regular/Premium): ");
            String type = scanner.nextLine();
            if (!type.equalsIgnoreCase("Regular") && !type.equalsIgnoreCase("Premium")) {
                System.err.println("Invalid member type. Please enter 'Regular' or 'Premium'.");
                return;
            }
            System.out.print("Enter Member ID (e.g., M011): ");
            String id = scanner.nextLine();
            
            if (manager.findMemberById(id) != null) {
                System.err.println("Error: A member with this ID already exists.");
                return;
            }

            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine();

            Member member;
            if (type.equalsIgnoreCase("Regular")) {
                member = new RegularMember(id, name, LocalDate.now());
                manager.addMember(member);
                System.out.println("Regular member added successfully!");
            } else { // Premium
                System.out.print("Enter Personal Trainer Fee: ");
                double fee = scanner.nextDouble();
                scanner.nextLine();
                member = new PremiumMember(id, name, LocalDate.now(), fee);
                manager.addMember(member);
                System.out.println("Premium member added successfully!");
            }
        } catch (InputMismatchException e) {
            System.err.println("Invalid fee. Please enter a number.");
            scanner.nextLine();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleUpdateStatus() {
        System.out.print("Enter the Member ID to update status: ");
        String memberId = scanner.nextLine();
        Member member = manager.findMemberById(memberId);

        if (member == null) {
            System.err.println("Member not found.");
            return;
        }
        System.out.println("Current status for " + member.getFullName() + " is: " + member.getStatus());
        System.out.print("Enter new status (ACTIVE/FROZEN): ");
        String statusInput = scanner.nextLine().toUpperCase();
        try {
            MembershipStatus newStatus = MembershipStatus.valueOf(statusInput);
            member.setStatus(newStatus);
            System.out.println("Status updated successfully!");
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status. Please enter 'ACTIVE' or 'FROZEN'.");
        }
    }

    private void handleAddPerformanceRecord() {
        System.out.print("Enter Member ID for performance record: ");
        String memberId = scanner.nextLine();
        Member member = manager.findMemberById(memberId);

        if (member == null) {
            System.err.println("Member not found.");
            return;
        }

        try {
            System.out.print("Enter performance month (1-12): ");
            int month = scanner.nextInt();
            System.out.print("Enter performance year: ");
            int year = scanner.nextInt();
            System.out.print("Was the monthly goal achieved? (true/false): ");
            boolean achieved = scanner.nextBoolean();
            scanner.nextLine();

            member.addPerformanceRecord(new Performance(month, year, achieved));
            System.out.println("Performance record added for " + member.getFullName());

        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter the correct data type.");
            scanner.nextLine();
        }
    }

    private void handleDeleteMember() {
        System.out.print("Enter Member ID to delete: ");
        String memberId = scanner.nextLine();
        if (manager.deleteMember(memberId)) {
            System.out.println("Member deleted successfully.");
        } else {
            System.err.println("Member not found.");
        }
    }

    private void handleSaveToFile() {
        System.out.print("Enter filename to save to (e.g., members_backup.csv): ");
        String filename = scanner.nextLine();
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Filename cannot be empty. Save cancelled.");
            return;
        }
        manager.saveToFile(filename);
    }

    private void handleSearchMenu() {
        int choice = 0;
        while (choice != 4) {
            System.out.println("\n--- Search & Filter Menu ---");
            System.out.println("1. Search by Name");
            System.out.println("2. Filter by Member Type");
            System.out.println("3. Filter by Performance");
            System.out.println("4. Return to Main Menu");
            System.out.print("Choose an option: ");

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
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private void handleSearchByName() {
        System.out.print("Enter name to search for: ");
        String name = scanner.nextLine();
        displaySearchResults(manager.findMembersByName(name), "Search Results for '" + name + "'");
    }

    private void handleFilterByType() {
        System.out.print("Enter type to filter (Regular/Premium): ");
        String type = scanner.nextLine();
        displaySearchResults(manager.filterMembersByType(type), "Filter Results for Type: " + type);
    }

    private void handleFilterByPerformance() {
        try {
            System.out.print("Enter month (1-12): ");
            int month = scanner.nextInt();
            System.out.print("Enter year: ");
            int year = scanner.nextInt();
            System.out.print("Filter by goal achieved? (true/false): ");
            boolean achieved = scanner.nextBoolean();
            scanner.nextLine();

            String status = achieved ? "Achieved Goal" : "Did Not Achieve Goal";
            displaySearchResults(manager.findMembersByPerformance(month, year, achieved),
                    "Filter Results for Performance: " + status + " in " + month + "/" + year);
        } catch (InputMismatchException e) {
            System.err.println("Invalid input.");
            scanner.nextLine();
        }
    }

    private void displaySearchResults(List<Member> results, String header) {
        System.out.println("\n--- " + header + " ---");
        if (results.isEmpty()) {
            System.out.println("No members found matching your criteria.");
        } else {
            results.forEach(member -> {
                System.out.println(member);
                System.out.println("-------------------");
            });
        }
    }
}

